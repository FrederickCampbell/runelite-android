package org.lwjgl.system;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Pure-Java Android substitute for the MemoryUtil surface used by 117HD.
 *
 * The Zone renderer only uses raw addresses for its CPU model cache. Android
 * intentionally hides native pointers, so this implementation supplies virtual
 * addresses backed by direct ByteBuffers. Those addresses never cross into GLES.
 */
public final class MemoryUtil
{
	public static final long NULL = 0L;

	private static final long ADDRESS_ALIGNMENT = 1L << 20;
	private static final AtomicLong NEXT_ADDRESS = new AtomicLong(0x1_0000_0000L);
	private static final Object LOCK = new Object();
	private static final NavigableMap<Long, Block> BLOCKS = new TreeMap<>();
	private static final Map<Buffer, Long> BUFFER_ADDRESSES = new IdentityHashMap<>();

	private MemoryUtil() {}

	private static final class Block
	{
		final long base;
		final ByteBuffer bytes;

		Block(long base, ByteBuffer bytes)
		{
			this.base = base;
			this.bytes = bytes;
		}
	}

	private static long span(long size)
	{
		long s = Math.max(size, 1L);
		long rem = s % ADDRESS_ALIGNMENT;
		return rem == 0 ? s + ADDRESS_ALIGNMENT : s + (ADDRESS_ALIGNMENT - rem);
	}

	private static Block allocateBlock(int size)
	{
		ByteBuffer bytes = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
		long base = NEXT_ADDRESS.getAndAdd(span(size));
		Block block = new Block(base, bytes);
		synchronized (LOCK)
		{
			BLOCKS.put(base, block);
			BUFFER_ADDRESSES.put(bytes, base);
		}
		return block;
	}

	private static Block blockFor(long address, int byteCount)
	{
		synchronized (LOCK)
		{
			Map.Entry<Long, Block> entry = BLOCKS.floorEntry(address);
			if (entry == null)
			{
				throw new IllegalArgumentException("Unknown virtual address 0x" + Long.toHexString(address));
			}
			Block block = entry.getValue();
			long offset = address - block.base;
			if (offset < 0 || offset + byteCount > block.bytes.capacity())
			{
				throw new IndexOutOfBoundsException("Virtual address range exceeds allocation");
			}
			return block;
		}
	}

	private static ByteBuffer slice(long address, int byteCount)
	{
		Block block = blockFor(address, byteCount);
		int offset = Math.toIntExact(address - block.base);
		ByteBuffer dup = block.bytes.duplicate().order(ByteOrder.nativeOrder());
		dup.position(offset);
		dup.limit(offset + byteCount);
		return dup.slice().order(ByteOrder.nativeOrder());
	}

	private static void remember(Buffer view, long address)
	{
		synchronized (LOCK)
		{
			BUFFER_ADDRESSES.put(view, address);
		}
	}

	public static ByteBuffer memAlloc(int size)
	{
		return allocateBlock(size).bytes;
	}

	public static ByteBuffer memRealloc(ByteBuffer oldBuffer, int newSize)
	{
		if (oldBuffer == null) return memAlloc(newSize);

		int oldPosition = oldBuffer.position();
		ByteBuffer out = memAlloc(newSize);
		ByteBuffer src = oldBuffer.duplicate();
		src.clear();
		src.limit(Math.min(oldPosition, src.capacity()));
		out.put(src);
		out.position(Math.min(oldPosition, out.capacity()));
		memFree(oldBuffer);
		return out;
	}

	public static IntBuffer memAllocInt(int capacity)
	{
		Block block = allocateBlock(Math.multiplyExact(capacity, Integer.BYTES));
		IntBuffer view = block.bytes.asIntBuffer();
		remember(view, block.base);
		return view;
	}

	public static FloatBuffer memAllocFloat(int capacity)
	{
		Block block = allocateBlock(Math.multiplyExact(capacity, Float.BYTES));
		FloatBuffer view = block.bytes.asFloatBuffer();
		remember(view, block.base);
		return view;
	}

	public static void memFree(Buffer buffer)
	{
		if (buffer == null) return;

		synchronized (LOCK)
		{
			Long address = BUFFER_ADDRESSES.remove(buffer);
			if (address == null) return;

			Map.Entry<Long, Block> entry = BLOCKS.floorEntry(address);
			if (entry == null) return;

			Block block = entry.getValue();
			BLOCKS.remove(block.base);
			long end = block.base + block.bytes.capacity();
			BUFFER_ADDRESSES.entrySet().removeIf(e -> e.getValue() >= block.base && e.getValue() < end);
		}
	}

	public static long nmemAllocChecked(long byteCapacity)
	{
		if (byteCapacity < 0 || byteCapacity > Integer.MAX_VALUE)
		{
			throw new OutOfMemoryError("117HD allocation exceeds Android direct-buffer limit: " + byteCapacity);
		}
		return allocateBlock((int) byteCapacity).base;
	}

	public static void nmemFree(long address)
	{
		if (address == NULL) return;

		synchronized (LOCK)
		{
			Block block = BLOCKS.remove(address);
			if (block == null) return;
			long end = block.base + block.bytes.capacity();
			BUFFER_ADDRESSES.entrySet().removeIf(e -> e.getValue() >= block.base && e.getValue() < end);
		}
	}

	public static long memAddress0(Buffer buffer)
	{
		if (buffer == null) return NULL;

		synchronized (LOCK)
		{
			Long base = BUFFER_ADDRESSES.get(buffer);
			if (base == null) return NULL;
			int elementSize = (buffer instanceof IntBuffer || buffer instanceof FloatBuffer) ? Integer.BYTES : 1;
			return base + (long) buffer.position() * elementSize;
		}
	}

	public static IntBuffer memIntBuffer(long address, int capacity)
	{
		IntBuffer view = slice(address, Math.multiplyExact(capacity, Integer.BYTES)).asIntBuffer();
		remember(view, address);
		return view;
	}

	public static FloatBuffer memFloatBuffer(long address, int capacity)
	{
		FloatBuffer view = slice(address, Math.multiplyExact(capacity, Float.BYTES)).asFloatBuffer();
		remember(view, address);
		return view;
	}

	// Only unreachable OpenCL diagnostics need these in upstream 117HD.
	public static String memASCII(long address)
	{
		return address == NULL ? "" : "<android-native-string>";
	}

	public static String memUTF8(long address)
	{
		return memASCII(address);
	}
}

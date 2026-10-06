package org.lwjgl.system;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

/**
 * GC-managed replacement for LWJGL's thread-local native MemoryStack.
 * All buffers remain direct, which is what Android GLES requires.
 */
public final class MemoryStack implements AutoCloseable
{
	private MemoryStack() {}

	public static MemoryStack stackPush()
	{
		return new MemoryStack();
	}

	private static ByteBuffer bytes(int size)
	{
		return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
	}

	public ByteBuffer malloc(int size) { return bytes(size); }

	public ByteBuffer calloc(int size)
	{
		ByteBuffer out = bytes(size);
		for (int i = 0; i < size; i++) out.put(i, (byte) 0);
		return out;
	}

	public IntBuffer mallocInt(int count)
	{
		return bytes(Math.multiplyExact(count, Integer.BYTES)).asIntBuffer();
	}

	public IntBuffer callocInt(int count)
	{
		IntBuffer out = mallocInt(count);
		for (int i = 0; i < count; i++) out.put(i, 0);
		return out;
	}

	public LongBuffer mallocLong(int count)
	{
		return bytes(Math.multiplyExact(count, Long.BYTES)).asLongBuffer();
	}

	public LongBuffer callocLong(int count)
	{
		LongBuffer out = mallocLong(count);
		for (int i = 0; i < count; i++) out.put(i, 0L);
		return out;
	}

	public FloatBuffer mallocFloat(int count)
	{
		return bytes(Math.multiplyExact(count, Float.BYTES)).asFloatBuffer();
	}

	public ShortBuffer mallocShort(int count)
	{
		return bytes(Math.multiplyExact(count, Short.BYTES)).asShortBuffer();
	}

	@Override
	public void close()
	{
		// Direct buffers are reclaimed by ART.
	}
}

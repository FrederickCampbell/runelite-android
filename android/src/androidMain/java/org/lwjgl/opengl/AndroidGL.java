package org.lwjgl.opengl;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLES31;
import android.opengl.GLES32;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Native Android GLES implementation of the LWJGL OpenGL surface used by 117HD.
 *
 * This is an API backend, not desktop-GL emulation: supported calls go directly
 * to Android's system GLES bindings on RuneLite Android's existing EGL context.
 */
public class AndroidGL
{
	protected AndroidGL() {}

	public static final int GL_FALSE = 0;
	public static final int GL_TRUE = 1;
	public static final int GL_NONE = 0;
	public static final int GL_ZERO = 0;
	public static final int GL_ONE = 1;

	public static final int GL_COLOR_BUFFER_BIT = GLES20.GL_COLOR_BUFFER_BIT;
	public static final int GL_DEPTH_BUFFER_BIT = GLES20.GL_DEPTH_BUFFER_BIT;
	public static final int GL_NO_ERROR = GLES20.GL_NO_ERROR;
	public static final int GL_INVALID_ENUM = GLES20.GL_INVALID_ENUM;
	public static final int GL_INVALID_VALUE = GLES20.GL_INVALID_VALUE;
	public static final int GL_INVALID_OPERATION = GLES20.GL_INVALID_OPERATION;
	public static final int GL_INVALID_FRAMEBUFFER_OPERATION = GLES20.GL_INVALID_FRAMEBUFFER_OPERATION;
	public static final int GL_STACK_UNDERFLOW = 0x0504;
	public static final int GL_STACK_OVERFLOW = 0x0503;

	public static final int GL_RENDERER = GLES20.GL_RENDERER;
	public static final int GL_VENDOR = GLES20.GL_VENDOR;
	public static final int GL_VERSION = GLES20.GL_VERSION;
	public static final int GL_MAX_TEXTURE_SIZE = GLES20.GL_MAX_TEXTURE_SIZE;
	public static final int GL_MAX_TEXTURE_IMAGE_UNITS = GLES20.GL_MAX_TEXTURE_IMAGE_UNITS;
	public static final int GL_MAX_SAMPLES = GLES30.GL_MAX_SAMPLES;
	public static final int GL_SAMPLES = 0x80A9;
	public static final int GL_CURRENT_PROGRAM = GLES20.GL_CURRENT_PROGRAM;

	public static final int GL_ARRAY_BUFFER = GLES20.GL_ARRAY_BUFFER;
	public static final int GL_ARRAY_BUFFER_ARB = GL_ARRAY_BUFFER;
	public static final int GL_ELEMENT_ARRAY_BUFFER = GLES20.GL_ELEMENT_ARRAY_BUFFER;
	public static final int GL_PIXEL_UNPACK_BUFFER = GLES30.GL_PIXEL_UNPACK_BUFFER;
	public static final int GL_COPY_READ_BUFFER = GLES30.GL_COPY_READ_BUFFER;
	public static final int GL_COPY_WRITE_BUFFER = GLES30.GL_COPY_WRITE_BUFFER;
	public static final int GL_DRAW_INDIRECT_BUFFER = 0x8F3F;
	public static final int GL_SHADER_STORAGE_BUFFER = 0x90D2;
	public static final int GL_UNIFORM_BUFFER = GLES30.GL_UNIFORM_BUFFER;
	public static final int GL_BUFFER_SIZE = GLES20.GL_BUFFER_SIZE;

	public static final int GL_STATIC_DRAW = GLES20.GL_STATIC_DRAW;
	public static final int GL_DYNAMIC_DRAW = GLES20.GL_DYNAMIC_DRAW;
	public static final int GL_STREAM_DRAW = GLES20.GL_STREAM_DRAW;
	public static final int GL_STREAM_COPY = 0x88E2;

	public static final int GL_MAP_READ_BIT = GLES30.GL_MAP_READ_BIT;
	public static final int GL_MAP_WRITE_BIT = GLES30.GL_MAP_WRITE_BIT;
	public static final int GL_MAP_INVALIDATE_RANGE_BIT = GLES30.GL_MAP_INVALIDATE_RANGE_BIT;
	public static final int GL_MAP_INVALIDATE_BUFFER_BIT = GLES30.GL_MAP_INVALIDATE_BUFFER_BIT;
	public static final int GL_MAP_FLUSH_EXPLICIT_BIT = GLES30.GL_MAP_FLUSH_EXPLICIT_BIT;
	public static final int GL_MAP_UNSYNCHRONIZED_BIT = GLES30.GL_MAP_UNSYNCHRONIZED_BIT;
	public static final int GL_MAP_PERSISTENT_BIT = 0x0040;
	public static final int GL_DYNAMIC_STORAGE_BIT = 0x0100;
	public static final int GL_CLIENT_STORAGE_BIT = 0x0200;
	public static final int GL_READ_ONLY = 0x88B8;
	public static final int GL_WRITE_ONLY = 0x88B9;
	public static final int GL_READ_WRITE = 0x88BA;

	public static final int GL_TEXTURE0 = GLES20.GL_TEXTURE0;
	public static final int GL_TEXTURE_2D = GLES20.GL_TEXTURE_2D;
	public static final int GL_TEXTURE_2D_ARRAY = GLES30.GL_TEXTURE_2D_ARRAY;
	public static final int GL_TEXTURE_3D = GLES30.GL_TEXTURE_3D;
	public static final int GL_TEXTURE_CUBE_MAP = GLES20.GL_TEXTURE_CUBE_MAP;
	public static final int GL_TEXTURE_CUBE_MAP_POSITIVE_X = GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
	public static final int GL_TEXTURE_BUFFER = 0x8C2A;
	public static final int GL_TEXTURE_MIN_FILTER = GLES20.GL_TEXTURE_MIN_FILTER;
	public static final int GL_TEXTURE_MAG_FILTER = GLES20.GL_TEXTURE_MAG_FILTER;
	public static final int GL_TEXTURE_WRAP_S = GLES20.GL_TEXTURE_WRAP_S;
	public static final int GL_TEXTURE_WRAP_T = GLES20.GL_TEXTURE_WRAP_T;
	public static final int GL_TEXTURE_WRAP_R = GLES30.GL_TEXTURE_WRAP_R;
	public static final int GL_TEXTURE_BORDER_COLOR = 0x1004;
	public static final int GL_TEXTURE_COMPARE_MODE = GLES30.GL_TEXTURE_COMPARE_MODE;
	public static final int GL_TEXTURE_COMPARE_FUNC = GLES30.GL_TEXTURE_COMPARE_FUNC;
	public static final int GL_TEXTURE_MAX_ANISOTROPY_EXT = 0x84FE;
	public static final int GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT = 0x84FF;
	public static final int GL_CLAMP_TO_EDGE = GLES20.GL_CLAMP_TO_EDGE;
	public static final int GL_CLAMP_TO_BORDER = 0x812D;
	public static final int GL_REPEAT = GLES20.GL_REPEAT;
	public static final int GL_NEAREST = GLES20.GL_NEAREST;
	public static final int GL_LINEAR = GLES20.GL_LINEAR;
	public static final int GL_LINEAR_MIPMAP_LINEAR = GLES20.GL_LINEAR_MIPMAP_LINEAR;

	public static final int GL_RED = GLES30.GL_RED;
	public static final int GL_RED_INTEGER = GLES30.GL_RED_INTEGER;
	public static final int GL_RGB = GLES20.GL_RGB;
	public static final int GL_RGBA = GLES20.GL_RGBA;
	public static final int GL_BGRA = 0x80E1;
	public static final int GL_RGB8 = GLES30.GL_RGB8;
	public static final int GL_RGBA8 = GLES30.GL_RGBA8;
	public static final int GL_SRGB8 = GLES30.GL_SRGB8;
	public static final int GL_SRGB8_ALPHA8 = GLES30.GL_SRGB8_ALPHA8;
	public static final int GL_RGB32I = 0x8D83;
	public static final int GL_RGBA16UI = GLES30.GL_RGBA16UI;
	public static final int GL_RGBA16F = GLES30.GL_RGBA16F;
	public static final int GL_RGBA_INTEGER = GLES30.GL_RGBA_INTEGER;
	public static final int GL_R16I = GLES30.GL_R16I;
	public static final int GL_DEPTH_COMPONENT = GLES20.GL_DEPTH_COMPONENT;
	public static final int GL_DEPTH_COMPONENT16 = GLES20.GL_DEPTH_COMPONENT16;
	public static final int GL_DEPTH_COMPONENT24 = GLES30.GL_DEPTH_COMPONENT24;
	public static final int GL_DEPTH_COMPONENT32F = GLES30.GL_DEPTH_COMPONENT32F;

	public static final int GL_BYTE = GLES20.GL_BYTE;
	public static final int GL_UNSIGNED_BYTE = GLES20.GL_UNSIGNED_BYTE;
	public static final int GL_SHORT = GLES20.GL_SHORT;
	public static final int GL_UNSIGNED_SHORT = GLES20.GL_UNSIGNED_SHORT;
	public static final int GL_INT = GLES20.GL_INT;
	public static final int GL_UNSIGNED_INT = GLES20.GL_UNSIGNED_INT;
	public static final int GL_FLOAT = GLES20.GL_FLOAT;
	public static final int GL_HALF_FLOAT = GLES30.GL_HALF_FLOAT;
	public static final int GL_UNSIGNED_INT_8_8_8_8_REV = 0x8367;

	public static final int GL_POINTS = GLES20.GL_POINTS;
	public static final int GL_TRIANGLES = GLES20.GL_TRIANGLES;
	public static final int GL_TRIANGLE_FAN = GLES20.GL_TRIANGLE_FAN;

	public static final int GL_BLEND = GLES20.GL_BLEND;
	public static final int GL_CULL_FACE = GLES20.GL_CULL_FACE;
	public static final int GL_DEPTH_TEST = GLES20.GL_DEPTH_TEST;
	public static final int GL_POLYGON_OFFSET_FILL = GLES20.GL_POLYGON_OFFSET_FILL;
	public static final int GL_FRAMEBUFFER_SRGB = 0x8DB9;
	public static final int GL_MULTISAMPLE = 0x809D;
	public static final int GL_POINT_SPRITE = 0x8861;
	public static final int GL_PROGRAM_POINT_SIZE = 0x8642;

	public static final int GL_LESS = GLES20.GL_LESS;
	public static final int GL_LEQUAL = GLES20.GL_LEQUAL;
	public static final int GL_GEQUAL = GLES20.GL_GEQUAL;
	public static final int GL_GREATER = GLES20.GL_GREATER;
	public static final int GL_SRC_ALPHA = GLES20.GL_SRC_ALPHA;
	public static final int GL_ONE_MINUS_SRC_ALPHA = GLES20.GL_ONE_MINUS_SRC_ALPHA;

	public static final int GL_FRAMEBUFFER = GLES20.GL_FRAMEBUFFER;
	public static final int GL_READ_FRAMEBUFFER = GLES30.GL_READ_FRAMEBUFFER;
	public static final int GL_DRAW_FRAMEBUFFER = GLES30.GL_DRAW_FRAMEBUFFER;
	public static final int GL_RENDERBUFFER = GLES20.GL_RENDERBUFFER;
	public static final int GL_COLOR_ATTACHMENT0 = GLES20.GL_COLOR_ATTACHMENT0;
	public static final int GL_DEPTH_ATTACHMENT = GLES20.GL_DEPTH_ATTACHMENT;
	public static final int GL_FRAMEBUFFER_ATTACHMENT_ALPHA_SIZE = 0x8215;
	public static final int GL_FRAMEBUFFER_COMPLETE = GLES20.GL_FRAMEBUFFER_COMPLETE;
	public static final int GL_BACK = 0x0405;
	public static final int GL_BACK_LEFT = 0x0402;
	public static final int GL_FRONT = 0x0404;

	public static final int GL_VERTEX_SHADER = GLES20.GL_VERTEX_SHADER;
	public static final int GL_FRAGMENT_SHADER = GLES20.GL_FRAGMENT_SHADER;
	public static final int GL_GEOMETRY_SHADER = 0x8DD9;
	public static final int GL_COMPUTE_SHADER = 0x91B9;
	public static final int GL_COMPILE_STATUS = GLES20.GL_COMPILE_STATUS;
	public static final int GL_LINK_STATUS = GLES20.GL_LINK_STATUS;
	public static final int GL_VALIDATE_STATUS = GLES20.GL_VALIDATE_STATUS;
	public static final int GL_NUM_PROGRAM_BINARY_FORMATS = GLES30.GL_NUM_PROGRAM_BINARY_FORMATS;
	public static final int GL_PROGRAM_BINARY_LENGTH = GLES30.GL_PROGRAM_BINARY_LENGTH;

	public static final int GL_COMPARE_REF_TO_TEXTURE = GLES30.GL_COMPARE_REF_TO_TEXTURE;

	public static final int GL_SYNC_GPU_COMMANDS_COMPLETE = GLES30.GL_SYNC_GPU_COMMANDS_COMPLETE;
	public static final int GL_SYNC_FLUSH_COMMANDS_BIT = GLES30.GL_SYNC_FLUSH_COMMANDS_BIT;
	public static final long GL_TIMEOUT_IGNORED = GLES30.GL_TIMEOUT_IGNORED;

	public static final int GL_QUERY_RESULT = GLES30.GL_QUERY_RESULT;
	public static final int GL_QUERY_RESULT_AVAILABLE = GLES30.GL_QUERY_RESULT_AVAILABLE;
	public static final int GL_TIMESTAMP = 0x8E28;

	public static final int GL_DEBUG_SOURCE_API = 0x8246;
	public static final int GL_DEBUG_SOURCE_APPLICATION = 0x824A;
	public static final int GL_DEBUG_TYPE_OTHER = 0x8251;
	public static final int GL_DEBUG_TYPE_PERFORMANCE = 0x8250;
	public static final int GL_DEBUG_TYPE_PUSH_GROUP = 0x8269;
	public static final int GL_DEBUG_TYPE_POP_GROUP = 0x826A;
	public static final int GL_DEBUG_SEVERITY_NOTIFICATION = 0x826B;
	public static final int GL_DONT_CARE = 0x1100;
	public static final int GL_BUFFER = 0x82E0;
	public static final int GL_SHADER_STORAGE_BARRIER_BIT = 0x2000;
	public static final int GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS = 0x90EB;

	private static final Map<Integer, Integer> SHADER_TYPES = new ConcurrentHashMap<>();
	private static final AtomicInteger QUERY_IDS = new AtomicInteger(0x6000_0000);
	private static final Map<Integer, Long> QUERY_TIMES = new ConcurrentHashMap<>();

	private static int checkedInt(long value)
	{
		if (value < 0 || value > Integer.MAX_VALUE)
		{
			throw new IllegalArgumentException("GLES offset/size exceeds Android int range: " + value);
		}
		return (int) value;
	}

	private static int mobileWrap(int value)
	{
		return value == GL_CLAMP_TO_BORDER ? GL_CLAMP_TO_EDGE : value;
	}

	private static boolean desktopOnlyCapability(int cap)
	{
		return cap == GL_FRAMEBUFFER_SRGB ||
			cap == GL_MULTISAMPLE ||
			cap == GL_POINT_SPRITE ||
			cap == GL_PROGRAM_POINT_SIZE;
	}

	public static String glGetString(int name) { return GLES20.glGetString(name); }
	public static int glGetError() { return GLES20.glGetError(); }

	public static int glGetInteger(int pname)
	{
		int[] value = new int[1];
		GLES20.glGetIntegerv(pname, value, 0);
		return value[0];
	}

	public static void glGetIntegerv(int pname, int[] params) { GLES20.glGetIntegerv(pname, params, 0); }

	public static float glGetFloat(int pname)
	{
		float[] value = new float[1];
		GLES20.glGetFloatv(pname, value, 0);
		return value[0];
	}

	public static void glEnable(int cap) { if (!desktopOnlyCapability(cap)) GLES20.glEnable(cap); }
	public static void glDisable(int cap) { if (!desktopOnlyCapability(cap)) GLES20.glDisable(cap); }
	public static void glViewport(int x, int y, int width, int height) { GLES20.glViewport(x, y, width, height); }
	public static void glClear(int mask) { GLES20.glClear(mask); }
	public static void glClearColor(float r, float g, float b, float a) { GLES20.glClearColor(r, g, b, a); }
	public static void glClearDepth(double depth) { GLES20.glClearDepthf((float) depth); }
	public static void glDepthFunc(int func) { GLES20.glDepthFunc(func); }
	public static void glDepthMask(boolean flag) { GLES20.glDepthMask(flag); }
	public static void glColorMask(boolean r, boolean g, boolean b, boolean a) { GLES20.glColorMask(r, g, b, a); }
	public static void glCullFace(int mode) { GLES20.glCullFace(mode); }
	public static void glPolygonOffset(float factor, float units) { GLES20.glPolygonOffset(factor, units); }
	public static void glBlendFunc(int sfactor, int dfactor) { GLES20.glBlendFunc(sfactor, dfactor); }
	public static void glBlendFuncSeparate(int srgb, int drgb, int sa, int da) { GLES20.glBlendFuncSeparate(srgb, drgb, sa, da); }
	public static void glFinish() { GLES20.glFinish(); }
	public static void glFlush() { GLES20.glFlush(); }

	public static int glGenBuffers()
	{
		int[] id = new int[1];
		GLES20.glGenBuffers(1, id, 0);
		return id[0];
	}
	public static void glGenBuffers(int[] ids) { GLES20.glGenBuffers(ids.length, ids, 0); }
	public static void glDeleteBuffers(int id) { GLES20.glDeleteBuffers(1, new int[]{id}, 0); }
	public static void glDeleteBuffers(int[] ids) { GLES20.glDeleteBuffers(ids.length, ids, 0); }
	public static void glBindBuffer(int target, int buffer) { GLES20.glBindBuffer(target, buffer); }

	public static void glBufferData(int target, long size, int usage)
	{ GLES20.glBufferData(target, checkedInt(size), null, usage); }
	public static void glBufferData(int target, ByteBuffer data, int usage)
	{ GLES20.glBufferData(target, data.remaining(), data, usage); }
	public static void glBufferData(int target, IntBuffer data, int usage)
	{ GLES20.glBufferData(target, data.remaining() * Integer.BYTES, data, usage); }
	public static void glBufferData(int target, FloatBuffer data, int usage)
	{ GLES20.glBufferData(target, data.remaining() * Float.BYTES, data, usage); }
	public static void glBufferData(int target, ShortBuffer data, int usage)
	{ GLES20.glBufferData(target, data.remaining() * Short.BYTES, data, usage); }

	public static void glBufferSubData(int target, long offset, ByteBuffer data)
	{ GLES20.glBufferSubData(target, checkedInt(offset), data.remaining(), data); }
	public static void glBufferSubData(int target, long offset, IntBuffer data)
	{ GLES20.glBufferSubData(target, checkedInt(offset), data.remaining() * Integer.BYTES, data); }
	public static void glBufferSubData(int target, long offset, FloatBuffer data)
	{ GLES20.glBufferSubData(target, checkedInt(offset), data.remaining() * Float.BYTES, data); }
	public static void glBufferSubData(int target, long offset, ShortBuffer data)
	{ GLES20.glBufferSubData(target, checkedInt(offset), data.remaining() * Short.BYTES, data); }

	public static ByteBuffer glMapBufferRange(int target, long offset, long length, int access)
	{
		Buffer buffer = GLES30.glMapBufferRange(target, checkedInt(offset), checkedInt(length), access);
		return buffer instanceof ByteBuffer ? (ByteBuffer) buffer : null;
	}
	public static ByteBuffer glMapBufferRange(int target, long offset, long length, int access, ByteBuffer oldBuffer)
	{ return glMapBufferRange(target, offset, length, access); }

	public static ByteBuffer glMapBuffer(int target, int access)
	{
		int flags = access == GL_READ_ONLY ? GL_MAP_READ_BIT :
			access == GL_WRITE_ONLY ? GL_MAP_WRITE_BIT :
			GL_MAP_READ_BIT | GL_MAP_WRITE_BIT;
		return glMapBufferRange(target, 0, glGetBufferParameteri64(target, GL_BUFFER_SIZE), flags);
	}
	public static ByteBuffer glMapBuffer(int target, int access, ByteBuffer oldBuffer)
	{ return glMapBuffer(target, access); }

	public static boolean glUnmapBuffer(int target) { return GLES30.glUnmapBuffer(target); }
	public static void glFlushMappedBufferRange(int target, long offset, long length)
	{ GLES30.glFlushMappedBufferRange(target, checkedInt(offset), checkedInt(length)); }

	public static long glGetBufferParameteri64(int target, int pname)
	{
		long[] out = new long[1];
		GLES30.glGetBufferParameteri64v(target, pname, out, 0);
		return out[0];
	}

	public static void glCopyBufferSubData(int readTarget, int writeTarget, long readOffset, long writeOffset, long size)
	{ GLES30.glCopyBufferSubData(readTarget, writeTarget, checkedInt(readOffset), checkedInt(writeOffset), checkedInt(size)); }

	public static void glBufferStorage(int target, long size, int flags)
	{
		// Persistent storage is intentionally not advertised. This exists so ART
		// can resolve the desktop symbol if it verifies the cold path.
		GLES20.glBufferData(target, checkedInt(size), null, GL_DYNAMIC_DRAW);
	}

	public static void glBindBufferBase(int target, int index, int buffer) { GLES30.glBindBufferBase(target, index, buffer); }

	public static int glGenVertexArrays()
	{
		int[] id = new int[1];
		GLES30.glGenVertexArrays(1, id, 0);
		return id[0];
	}
	public static void glGenVertexArrays(int[] ids) { GLES30.glGenVertexArrays(ids.length, ids, 0); }
	public static void glDeleteVertexArrays(int id) { GLES30.glDeleteVertexArrays(1, new int[]{id}, 0); }
	public static void glDeleteVertexArrays(int[] ids) { GLES30.glDeleteVertexArrays(ids.length, ids, 0); }
	public static void glBindVertexArray(int id) { GLES30.glBindVertexArray(id); }
	public static void glEnableVertexAttribArray(int index) { GLES20.glEnableVertexAttribArray(index); }
	public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer)
	{ GLES20.glVertexAttribPointer(index, size, type, normalized, stride, checkedInt(pointer)); }
	public static void glVertexAttribIPointer(int index, int size, int type, int stride, long pointer)
	{ GLES30.glVertexAttribIPointer(index, size, type, stride, checkedInt(pointer)); }
	public static void glVertexAttribDivisor(int index, int divisor) { GLES30.glVertexAttribDivisor(index, divisor); }

	public static int glGenTextures()
	{
		int[] id = new int[1];
		GLES20.glGenTextures(1, id, 0);
		return id[0];
	}
	public static void glGenTextures(int[] ids) { GLES20.glGenTextures(ids.length, ids, 0); }
	public static void glDeleteTextures(int id) { GLES20.glDeleteTextures(1, new int[]{id}, 0); }
	public static void glDeleteTextures(int[] ids) { GLES20.glDeleteTextures(ids.length, ids, 0); }
	public static void glActiveTexture(int texture) { GLES20.glActiveTexture(texture); }
	public static void glBindTexture(int target, int texture) { GLES20.glBindTexture(target, texture); }

	public static void glTexParameteri(int target, int pname, int param)
	{
		if (pname == GL_TEXTURE_WRAP_S || pname == GL_TEXTURE_WRAP_T || pname == GL_TEXTURE_WRAP_R)
		{
			param = mobileWrap(param);
		}
		GLES20.glTexParameteri(target, pname, param);
	}
	public static void glTexParameterf(int target, int pname, float param) { GLES20.glTexParameterf(target, pname, param); }
	public static void glTexParameterfv(int target, int pname, float[] params)
	{
		// GLES has no border color because CLAMP_TO_BORDER is not core.
		if (pname != GL_TEXTURE_BORDER_COLOR) GLES20.glTexParameterfv(target, pname, params, 0);
	}

	public static void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels)
	{ GLES20.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels); }
	public static void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, IntBuffer pixels)
	{ GLES20.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels); }
	public static void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, FloatBuffer pixels)
	{ GLES20.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels); }

	public static void glTexSubImage2D(int target, int level, int x, int y, int width, int height, int format, int type, ByteBuffer pixels)
	{ GLES20.glTexSubImage2D(target, level, x, y, width, height, format, type, pixels); }
	public static void glTexSubImage2D(int target, int level, int x, int y, int width, int height, int format, int type, IntBuffer pixels)
	{ GLES20.glTexSubImage2D(target, level, x, y, width, height, format, type, pixels); }

	public static void glTexImage3D(int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels)
	{ GLES30.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, pixels); }
	public static void glTexImage3D(int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, IntBuffer pixels)
	{ GLES30.glTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, pixels); }
	public static void glTexSubImage3D(int target, int level, int x, int y, int z, int width, int height, int depth, int format, int type, ByteBuffer pixels)
	{ GLES30.glTexSubImage3D(target, level, x, y, z, width, height, depth, format, type, pixels); }
	public static void glTexSubImage3D(int target, int level, int x, int y, int z, int width, int height, int depth, int format, int type, IntBuffer pixels)
	{ GLES30.glTexSubImage3D(target, level, x, y, z, width, height, depth, format, type, pixels); }
	public static void glTexStorage3D(int target, int levels, int internalFormat, int width, int height, int depth)
	{ GLES30.glTexStorage3D(target, levels, internalFormat, width, height, depth); }
	public static void glTexBuffer(int target, int internalFormat, int buffer)
	{ GLES32.glTexBuffer(target, internalFormat, buffer); }
	public static void glGenerateMipmap(int target) { GLES20.glGenerateMipmap(target); }

	public static int glCreateShader(int type)
	{
		int shader = GLES20.glCreateShader(type);
		if (shader != 0) SHADER_TYPES.put(shader, type);
		return shader;
	}
	public static void glShaderSource(int shader, CharSequence source)
	{
		int type = SHADER_TYPES.getOrDefault(shader, GL_FRAGMENT_SHADER);
		GLES20.glShaderSource(shader, AndroidGLSL.translate(type, source == null ? null : source.toString()));
	}
	public static void glCompileShader(int shader) { GLES20.glCompileShader(shader); }
	public static int glGetShaderi(int shader, int pname)
	{
		int[] out = new int[1];
		GLES20.glGetShaderiv(shader, pname, out, 0);
		return out[0];
	}
	public static void glGetShaderiv(int shader, int pname, int[] params) { GLES20.glGetShaderiv(shader, pname, params, 0); }
	public static String glGetShaderInfoLog(int shader) { return GLES20.glGetShaderInfoLog(shader); }
	public static void glDeleteShader(int shader) { SHADER_TYPES.remove(shader); GLES20.glDeleteShader(shader); }

	public static int glCreateProgram() { return GLES20.glCreateProgram(); }
	public static void glAttachShader(int program, int shader) { GLES20.glAttachShader(program, shader); }
	public static void glDetachShader(int program, int shader) { GLES20.glDetachShader(program, shader); }
	public static void glLinkProgram(int program) { GLES20.glLinkProgram(program); }
	public static void glValidateProgram(int program) { GLES20.glValidateProgram(program); }
	public static int glGetProgrami(int program, int pname)
	{
		int[] out = new int[1];
		GLES20.glGetProgramiv(program, pname, out, 0);
		return out[0];
	}
	public static void glGetProgramiv(int program, int pname, int[] params) { GLES20.glGetProgramiv(program, pname, params, 0); }
	public static String glGetProgramInfoLog(int program) { return GLES20.glGetProgramInfoLog(program); }
	public static void glUseProgram(int program) { GLES20.glUseProgram(program); }
	public static void glDeleteProgram(int program) { GLES20.glDeleteProgram(program); }

	public static int glGetUniformLocation(int program, CharSequence name) { return GLES20.glGetUniformLocation(program, name.toString()); }
	public static int glGetUniformBlockIndex(int program, CharSequence name) { return GLES30.glGetUniformBlockIndex(program, name.toString()); }
	public static void glUniformBlockBinding(int program, int blockIndex, int blockBinding) { GLES30.glUniformBlockBinding(program, blockIndex, blockBinding); }
	public static void glUniform1i(int location, int v) { GLES20.glUniform1i(location, v); }
	public static void glUniform2i(int location, int x, int y) { GLES20.glUniform2i(location, x, y); }
	public static void glUniform3i(int location, int x, int y, int z) { GLES20.glUniform3i(location, x, y, z); }
	public static void glUniform4i(int location, int x, int y, int z, int w) { GLES20.glUniform4i(location, x, y, z, w); }
	public static void glUniform1f(int location, float v) { GLES20.glUniform1f(location, v); }
	public static void glUniform2f(int location, float x, float y) { GLES20.glUniform2f(location, x, y); }
	public static void glUniform3f(int location, float x, float y, float z) { GLES20.glUniform3f(location, x, y, z); }
	public static void glUniform4f(int location, float x, float y, float z, float w) { GLES20.glUniform4f(location, x, y, z, w); }
	public static void glUniform2iv(int location, int[] v) { GLES20.glUniform2iv(location, v.length / 2, v, 0); }
	public static void glUniform3iv(int location, int[] v) { GLES20.glUniform3iv(location, v.length / 3, v, 0); }
	public static void glUniform4iv(int location, int[] v) { GLES20.glUniform4iv(location, v.length / 4, v, 0); }
	public static void glUniform2fv(int location, float[] v) { GLES20.glUniform2fv(location, v.length / 2, v, 0); }
	public static void glUniform3fv(int location, float[] v) { GLES20.glUniform3fv(location, v.length / 3, v, 0); }
	public static void glUniform4fv(int location, float[] v) { GLES20.glUniform4fv(location, v.length / 4, v, 0); }
	public static void glUniformMatrix4fv(int location, boolean transpose, float[] value)
	{ GLES20.glUniformMatrix4fv(location, value.length / 16, transpose, value, 0); }

	public static int glGenFramebuffers()
	{
		int[] id = new int[1];
		GLES20.glGenFramebuffers(1, id, 0);
		return id[0];
	}
	public static void glGenFramebuffers(int[] ids) { GLES20.glGenFramebuffers(ids.length, ids, 0); }
	public static void glDeleteFramebuffers(int id) { GLES20.glDeleteFramebuffers(1, new int[]{id}, 0); }
	public static void glDeleteFramebuffers(int[] ids) { GLES20.glDeleteFramebuffers(ids.length, ids, 0); }
	public static void glBindFramebuffer(int target, int framebuffer) { GLES20.glBindFramebuffer(target, framebuffer); }
	public static void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level)
	{ GLES20.glFramebufferTexture2D(target, attachment, textarget, texture, level); }
	public static void glFramebufferTextureLayer(int target, int attachment, int texture, int level, int layer)
	{ GLES30.glFramebufferTextureLayer(target, attachment, texture, level, layer); }
	public static void glFramebufferTexture(int target, int attachment, int texture, int level)
	{ GLES32.glFramebufferTexture(target, attachment, texture, level); }
	public static int glCheckFramebufferStatus(int target) { return GLES20.glCheckFramebufferStatus(target); }

	public static int glGetFramebufferAttachmentParameteri(int target, int attachment, int pname)
	{
		int[] out = new int[1];
		GLES30.glGetFramebufferAttachmentParameteriv(target, attachment, pname, out, 0);
		return out[0];
	}

	public static int glGenRenderbuffers()
	{
		int[] id = new int[1];
		GLES20.glGenRenderbuffers(1, id, 0);
		return id[0];
	}
	public static void glDeleteRenderbuffers(int id) { GLES20.glDeleteRenderbuffers(1, new int[]{id}, 0); }
	public static void glBindRenderbuffer(int target, int renderbuffer) { GLES20.glBindRenderbuffer(target, renderbuffer); }
	public static void glRenderbufferStorage(int target, int internalFormat, int width, int height)
	{ GLES20.glRenderbufferStorage(target, internalFormat, width, height); }
	public static void glRenderbufferStorageMultisample(int target, int samples, int internalFormat, int width, int height)
	{ GLES30.glRenderbufferStorageMultisample(target, samples, internalFormat, width, height); }
	public static void glFramebufferRenderbuffer(int target, int attachment, int renderbufferTarget, int renderbuffer)
	{ GLES20.glFramebufferRenderbuffer(target, attachment, renderbufferTarget, renderbuffer); }

	public static void glDrawBuffer(int mode)
	{
		int mapped = mode == GL_BACK_LEFT ? GL_BACK : mode;
		GLES30.glDrawBuffers(1, new int[]{mapped}, 0);
	}
	public static void glReadBuffer(int mode) { GLES30.glReadBuffer(mode == GL_BACK_LEFT ? GL_BACK : mode); }
	public static void glBlitFramebuffer(int sx0, int sy0, int sx1, int sy1,
		int dx0, int dy0, int dx1, int dy1, int mask, int filter)
	{ GLES30.glBlitFramebuffer(sx0, sy0, sx1, sy1, dx0, dy0, dx1, dy1, mask, filter); }

	public static void glDrawArrays(int mode, int first, int count) { GLES20.glDrawArrays(mode, first, count); }
	public static void glDrawArraysInstanced(int mode, int first, int count, int instanceCount)
	{ GLES30.glDrawArraysInstanced(mode, first, count, instanceCount); }
	public static void glDrawElements(int mode, int count, int type, long indices)
	{ GLES20.glDrawElements(mode, count, type, checkedInt(indices)); }

	public static void glMultiDrawArrays(int mode, IntBuffer first, IntBuffer count)
	{
		IntBuffer f = first.duplicate();
		IntBuffer c = count.duplicate();
		while (f.hasRemaining() && c.hasRemaining()) GLES20.glDrawArrays(mode, f.get(), c.get());
	}

	public static void glDrawArraysIndirect(int mode, long indirect)
	{ GLES31.glDrawArraysIndirect(mode, checkedInt(indirect)); }
	public static void glDrawElementsIndirect(int mode, int type, long indirect)
	{ GLES31.glDrawElementsIndirect(mode, type, checkedInt(indirect)); }
	public static void glMultiDrawArraysIndirect(int mode, long indirect, int drawcount, int stride)
	{ throw new UnsupportedOperationException("Multi-draw indirect is not advertised on Android GLES"); }

	public static void glReadPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels)
	{ GLES20.glReadPixels(x, y, width, height, format, type, pixels); }
	public static void glReadPixels(int x, int y, int width, int height, int format, int type, IntBuffer pixels)
	{ GLES20.glReadPixels(x, y, width, height, format, type, pixels); }

	public static long glFenceSync(int condition, int flags) { return GLES30.glFenceSync(condition, flags); }
	public static int glClientWaitSync(long sync, int flags, long timeout) { return GLES30.glClientWaitSync(sync, flags, timeout); }
	public static void glDeleteSync(long sync) { if (sync != 0) GLES30.glDeleteSync(sync); }

	/**
	 * GL_TIMESTAMP is not core GLES. 117HD only uses these queries for its optional
	 * profiler, so expose monotonic CPU timestamps rather than requiring a driver
	 * extension for the renderer itself.
	 */
	public static int glGenQueries()
	{
		int id = QUERY_IDS.getAndIncrement();
		QUERY_TIMES.put(id, 0L);
		return id;
	}
	public static void glGenQueries(int[] ids) { for (int i = 0; i < ids.length; i++) ids[i] = glGenQueries(); }
	public static void glDeleteQueries(int id) { QUERY_TIMES.remove(id); }
	public static void glDeleteQueries(int[] ids) { for (int id : ids) QUERY_TIMES.remove(id); }
	public static void glQueryCounter(int id, int target) { QUERY_TIMES.put(id, System.nanoTime()); }
	public static void glGetQueryObjectiv(int id, int pname, int[] params)
	{ params[0] = pname == GL_QUERY_RESULT_AVAILABLE ? 1 : (int) (long) QUERY_TIMES.getOrDefault(id, 0L); }
	public static long glGetQueryObjectui64(int id, int pname) { return QUERY_TIMES.getOrDefault(id, 0L); }

	public static void glBindImageTexture(int unit, int texture, int level, boolean layered, int layer, int access, int format)
	{ GLES31.glBindImageTexture(unit, texture, level, layered, layer, access, format); }
	public static void glMemoryBarrier(int barriers) { GLES31.glMemoryBarrier(barriers); }
	public static void glDispatchCompute(int numGroupsX, int numGroupsY, int numGroupsZ)
	{ GLES31.glDispatchCompute(numGroupsX, numGroupsY, numGroupsZ); }

	public static void glGetProgramBinary(int program, int[] length, int[] binaryFormat, ByteBuffer binary)
	{
		if (length != null && length.length > 0) length[0] = 0;
		if (binaryFormat != null && binaryFormat.length > 0) binaryFormat[0] = 0;
	}

	public static void glObjectLabel(int identifier, int name, CharSequence label) {}
	public static void glPushDebugGroup(int source, int id, CharSequence message) {}
	public static void glPopDebugGroup() {}
	public static void glDebugMessageControl(int source, int type, int severity, int id, boolean enabled) {}
	public static void glDebugMessageControl(int source, int type, int severity, int[] ids, boolean enabled) {}
}

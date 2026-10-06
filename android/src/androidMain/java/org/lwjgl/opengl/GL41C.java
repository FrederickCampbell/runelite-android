package org.lwjgl.opengl;

import java.nio.ByteBuffer;

/** LWJGL 4.1 facade. Program binaries are developer diagnostics on Android. */
public class GL41C extends AndroidGL
{
	public static final int GL_NUM_PROGRAM_BINARY_FORMATS = AndroidGL.GL_NUM_PROGRAM_BINARY_FORMATS;
	public static final int GL_PROGRAM_BINARY_LENGTH = AndroidGL.GL_PROGRAM_BINARY_LENGTH;

	protected GL41C() {}

	public static void glGetProgramBinary(int program, int[] length, int[] binaryFormat, ByteBuffer binary)
	{
		AndroidGL.glGetProgramBinary(program, length, binaryFormat, binary);
	}
}

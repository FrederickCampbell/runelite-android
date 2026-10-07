package org.lwjgl.opengl;

/** GLES 3.1 image-load/store compatibility facade. */
public final class ARBShaderImageLoadStore
{
	public static final int GL_MAX_IMAGE_UNITS = 0x8F38;

	private ARBShaderImageLoadStore() {}

	public static void glBindImageTexture(
		int unit,
		int texture,
		int level,
		boolean layered,
		int layer,
		int access,
		int format)
	{
		AndroidGL.glBindImageTexture(unit, texture, level, layered, layer, access, format);
	}
}

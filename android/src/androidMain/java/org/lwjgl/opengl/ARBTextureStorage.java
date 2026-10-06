package org.lwjgl.opengl;

/**
 * Texture-storage compatibility facade. GLES 3.x provides immutable 3D texture
 * storage as a core operation.
 */
public final class ARBTextureStorage
{
	private ARBTextureStorage() {}

	public static void glTexStorage3D(int target, int levels, int internalFormat, int width, int height, int depth)
	{
		AndroidGL.glTexStorage3D(target, levels, internalFormat, width, height, depth);
	}
}

package org.lwjgl.opengl;

/** LWJGL 4.4 storage facade. Persistent storage is not advertised on GLES. */
public class GL44 extends AndroidGL
{
	public static final int GL_CLIENT_STORAGE_BIT = AndroidGL.GL_CLIENT_STORAGE_BIT;
	public static final int GL_DYNAMIC_STORAGE_BIT = AndroidGL.GL_DYNAMIC_STORAGE_BIT;
	public static final int GL_MAP_PERSISTENT_BIT = AndroidGL.GL_MAP_PERSISTENT_BIT;

	protected GL44() {}
}

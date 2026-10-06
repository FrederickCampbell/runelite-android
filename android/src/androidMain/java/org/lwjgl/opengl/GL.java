package org.lwjgl.opengl;

/** LWJGL GL facade backed by RuneLite Android's existing EGL/GLES context. */
public final class GL
{
	private static volatile GLCapabilities capabilities;

	private GL() {}

	public static GLCapabilities createCapabilities()
	{
		GLCapabilities caps = new GLCapabilities();
		capabilities = caps;
		return caps;
	}

	public static GLCapabilities getCapabilities()
	{
		GLCapabilities caps = capabilities;
		if (caps == null) caps = createCapabilities();
		return caps;
	}

	public static void setCapabilities(GLCapabilities caps)
	{
		capabilities = caps;
	}
}

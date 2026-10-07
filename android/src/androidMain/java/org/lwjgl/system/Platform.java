package org.lwjgl.system;

/** Platform facade for desktop plugins running under ART. */
public enum Platform
{
	WINDOWS,
	LINUX,
	MACOSX;

	public static Platform get()
	{
		return LINUX;
	}
}

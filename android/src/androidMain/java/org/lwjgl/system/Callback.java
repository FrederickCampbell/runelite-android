package org.lwjgl.system;

/** Native LWJGL callbacks are not allocated by the Android GLES backend. */
public class Callback
{
	public void free()
	{
		// no-op
	}
}

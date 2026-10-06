package org.lwjgl.opengl;

import org.lwjgl.system.Callback;

/** Android logcat remains the debug path; desktop KHR callbacks are optional. */
public final class GLUtil
{
	private GLUtil() {}

	public static Callback setupDebugMessageCallback()
	{
		return null;
	}
}

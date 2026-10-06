package net.runelite.rlawt;

import android.opengl.GLES32;
import java.awt.Canvas;
import net.runelite.client.plugins.gpugles.GlesHost;

/**
 * Android-native replacement for RuneLite rlawt's desktop GL context manager.
 *
 * 117HD is written against AWTContext/LWJGL on desktop. On Android the window,
 * surface and GL context are already owned by {@link GlesHost}; this class makes
 * that EGL/GLES context the implementation of the same lifecycle contract.
 */
public class AWTContext
{
	private final Canvas canvas;
	private boolean ownsGlesPresentation;

	public AWTContext(Canvas canvas)
	{
		this.canvas = canvas;
	}

	public static void loadNatives()
	{
		// EGL/GLES are Android framework APIs; there are no desktop natives to load.
	}

	public void configurePixelFormat(int alpha, int depth, int stencil)
	{
		// GlesHost selects an EGLConfig suitable for the SurfaceView. 117HD renders
		// its own scene/depth targets, so the desktop pixel-format hints are not
		// required here.
	}

	public void createGLContext()
	{
		// The Compose host only exposes the SurfaceView through the game-canvas
		// rectangle while this flag is set. GPU(GLES) normally owns the flag; 117HD
		// uses this AWTContext instead, so take presentation ownership here.
		Canvas.setRenderedByGles(true);
		ownsGlesPresentation = true;

		// Compose mounts the SurfaceView asynchronously after the Canvas ownership
		// flag changes. Desktop rlawt returns from createGLContext() with a usable
		// current context, and 117HD relies on that contract immediately for
		// capability probing and resource creation, so preserve it here.
		if (!GlesHost.get().awaitCurrent(3000))
		{
			Canvas.setRenderedByGles(false);
			ownsGlesPresentation = false;
			throw new IllegalStateException("Timed out waiting for Android GLES surface");
		}
	}

	public boolean makeCurrent()
	{
		return GlesHost.get().makeCurrent();
	}

	public void swapBuffers()
	{
		GlesHost.get().swapBuffers();
	}

	/** GLES default framebuffer for the EGL window surface. */
	public int getFramebuffer(boolean resolve)
	{
		return 0;
	}

	public int getBufferMode()
	{
		return GLES32.GL_BACK;
	}

	public int setSwapInterval(int interval)
	{
		GlesHost.get().setSwapInterval(interval);
		return interval;
	}

	public void destroy()
	{
		// Do not destroy GlesHost itself: it belongs to the app process and is reused
		// by the built-in GPU plugin. Only return the Canvas to software composition
		// when this AWTContext was the component that took it over.
		if (ownsGlesPresentation)
		{
			Canvas.setRenderedByGles(false);
			ownsGlesPresentation = false;
		}
	}
}

package net.runelite.rlawt;

import android.opengl.GLES32;
import java.awt.Canvas;
import java.awt.Component;
import net.runelite.client.plugins.gpugles.GlesHost;

/**
 * Android implementation of RuneLite rlawt's AWTContext ABI.
 *
 * Keep every public method descriptor identical to desktop rlawt: Plugin Hub jars
 * such as 117HD were compiled against that class, so changing a return type or
 * constructor parameter would produce NoSuchMethodError even when the Java source
 * looked otherwise compatible.
 *
 * Android does not create a second GL implementation here. The context, window
 * surface and presentation lifetime are owned by the app-wide {@link GlesHost},
 * which is backed directly by EGL14/GLES.
 */
public final class AWTContext
{
	private final Component component;
	private boolean ownsGlesPresentation;

	public AWTContext(Component component)
	{
		if (component == null)
		{
			throw new NullPointerException("component");
		}
		this.component = component;
	}

	public synchronized static void loadNatives()
	{
		// EGL/GLES are Android framework APIs; there are no rlawt desktop natives.
	}

	public void configurePixelFormat(int alpha, int depth, int stencil)
	{
		// GlesHost chooses the EGLConfig. 117HD uses its own off-screen render
		// targets for scene color/depth, so desktop pixel-format hints are not
		// required for correctness.
	}

	public void configureMultisamples(int samples)
	{
		GlesHost.get().setRequestedMsaa(samples);
	}

	/**
	 * Desktop rlawt returns the platform framebuffer object. Android renders to
	 * the EGL window surface directly, whose default framebuffer is name 0.
	 */
	public int getFramebuffer(boolean front)
	{
		return 0;
	}

	/**
	 * OpenGL desktop uses GL_FRONT for the default framebuffer. OpenGL ES uses
	 * GL_BACK for reads from a double-buffered EGL window surface.
	 */
	public int getBufferMode()
	{
		return GLES32.GL_BACK;
	}

	/**
	 * Preserve desktop rlawt's synchronous contract: on return a GL context is
	 * current on the calling thread. Compose mounts the SurfaceView only after the
	 * Canvas switches to GLES presentation, so wait for that lifecycle handoff.
	 */
	public void createGLContext()
	{
		Canvas.setRenderedByGles(true);
		ownsGlesPresentation = true;

		if (!GlesHost.get().awaitCurrent(3000))
		{
			Canvas.setRenderedByGles(false);
			ownsGlesPresentation = false;
			throw new IllegalStateException("Timed out waiting for Android GLES surface");
		}
	}

	public int setSwapInterval(int interval)
	{
		return GlesHost.get().setSwapInterval(interval) ? interval : -1;
	}

	public void makeCurrent()
	{
		if (!GlesHost.get().makeCurrent())
		{
			throw new IllegalStateException("Android GLES context is not available");
		}
	}

	public void detachCurrent()
	{
		GlesHost.get().detachCurrent();
	}

	public void swapBuffers()
	{
		if (!GlesHost.get().swapBuffers())
		{
			throw new IllegalStateException("Android GLES swap failed");
		}
	}

	// Desktop-only native handles used by 117HD's optional OpenCL interop path.
	// Android's supported renderer is the GLES ZoneRenderer, so no native OpenCL
	// sharing handles exist here.
	public long getGLContext() { return 0L; }
	public long getCGLShareGroup() { return 0L; }
	public long getGLXDisplay() { return 0L; }
	public long getWGLHDC() { return 0L; }

	public void destroy()
	{
		GlesHost.get().detachCurrent();
		if (ownsGlesPresentation)
		{
			Canvas.setRenderedByGles(false);
			ownsGlesPresentation = false;
		}
	}
}

package rs117.hd.renderer.legacy;

import javax.inject.Singleton;
import org.lwjgl.opengl.GLCapabilities;
import rs117.hd.renderer.Renderer;

/**
 * Android platform sentinel for 117HD's desktop legacy renderer.
 *
 * The Android port deliberately supports the modern ZoneRenderer only. Keeping a
 * type-compatible LegacyRenderer class lets upstream HdPlugin remain unmodified
 * while capability negotiation rejects the desktop OpenCL/GL4 renderer cleanly.
 */
@Singleton
public final class LegacyRenderer implements Renderer
{
	@Override
	public boolean supportsGpu(GLCapabilities glCaps)
	{
		return false;
	}
}

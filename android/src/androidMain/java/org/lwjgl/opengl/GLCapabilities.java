package org.lwjgl.opengl;

/**
 * Conservative capabilities exposed to desktop plugins on GLES 3.2.
 *
 * Optional desktop-only fast paths are deliberately disabled so upstream 117HD
 * selects the portable Zone renderer and ordinary buffer/draw paths.
 */
public final class GLCapabilities
{
	public final boolean OpenGL31 = false;
	public final boolean OpenGL33 = AndroidGL.isGlesAtLeast(3, 2);
	public final boolean OpenGL40 = false;
	public final boolean OpenGL43 = false;

	public final boolean GL_ARB_draw_indirect = false;
	public final boolean GL_ARB_multi_draw_indirect = false;
	public final boolean GL_ARB_buffer_storage = false;
	public final boolean GL_ARB_shader_image_load_store = false;
	public final boolean GL_ARB_copy_buffer = AndroidGL.isGlesAtLeast(3, 0);
	public final boolean GL_ARB_map_buffer_range = AndroidGL.isGlesAtLeast(3, 0);
	public final boolean GL_EXT_texture_filter_anisotropic = AndroidGL.hasExtension("GL_EXT_texture_filter_anisotropic");

	public final boolean forwardCompatible = true;

	public final long glTexStorage3D = AndroidGL.isGlesAtLeast(3, 0) ? 1L : 0L;
	public final long glDebugMessageControl = 0L;
}

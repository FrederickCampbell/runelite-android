package rs117.hd.utils.buffer;

/**
 * Android replacement for 117HD's GL/OpenCL shared buffer.
 *
 * ZoneRenderer never uses OpenCL. UniformBuffer's generic constructor still
 * references this type in common upstream code, so preserve the API as an
 * ordinary GLES buffer without manufacturing unsupported CL interop handles.
 */
public class SharedGLBuffer extends GLBuffer
{
	public final int clUsage;
	public long clId;

	public SharedGLBuffer(String name, int target, int glUsage, int clUsage)
	{
		super(name, target, glUsage);
		this.clUsage = clUsage;
	}
}

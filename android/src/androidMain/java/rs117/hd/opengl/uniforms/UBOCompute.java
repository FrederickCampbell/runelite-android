package rs117.hd.opengl.uniforms;

/**
 * The only common-code dependency on the legacy compute UBO is this shader
 * compile-time array bound. The Android ZoneRenderer has no compute UBO.
 */
public final class UBOCompute
{
	public static final int MAX_CHARACTER_POSITION_COUNT = 50;

	private UBOCompute() {}
}

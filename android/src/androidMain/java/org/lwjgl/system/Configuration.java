package org.lwjgl.system;

/** Small process-local subset of LWJGL Configuration used by RuneLite plugins. */
public final class Configuration
{
	public static final Option<String> SHARED_LIBRARY_EXTRACT_DIRECTORY = new Option<>();
	public static final Option<Boolean> OPENCL_EXPLICIT_INIT = new Option<>();

	private Configuration() {}

	public static final class Option<T>
	{
		private volatile T value;

		public void set(T value)
		{
			this.value = value;
		}

		public T get()
		{
			return value;
		}
	}
}

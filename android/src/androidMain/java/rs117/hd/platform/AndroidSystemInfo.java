package rs117.hd.platform;

import android.system.Os;
import android.system.OsConstants;

/**
 * Android platform information used by the native 117HD integration.
 *
 * Keep platform-specific process/OS queries out of upstream 117HD sources. The
 * desktop project normally reaches this information through com.sun.management,
 * which does not exist in Android's public Java API.
 */
public final class AndroidSystemInfo
{
	private AndroidSystemInfo() {}

	public static long totalPhysicalMemory()
	{
		try
		{
			long pages = Os.sysconf(OsConstants._SC_PHYS_PAGES);
			long pageSize = Os.sysconf(OsConstants._SC_PAGESIZE);
			if (pages > 0 && pageSize > 0 && pages <= Long.MAX_VALUE / pageSize)
			{
				return pages * pageSize;
			}
		}
		catch (Throwable ignored)
		{
			// Fall through to the conservative value below.
		}

		// 117HD only uses this to decide whether to enable lower-memory behavior.
		// Returning the runtime heap ceiling is a more useful Android fallback than
		// pretending the device has unlimited physical RAM.
		return Runtime.getRuntime().maxMemory();
	}
}

package rs117.hd.overlays;

import android.os.Debug;
import android.os.Process;
import java.util.ArrayDeque;
import java.util.Arrays;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.callback.ClientThread;
import org.lwjgl.opengl.GL43C;
import rs117.hd.HdPlugin;

import static org.lwjgl.opengl.GL33C.*;

/**
 * Android-native FrameTimer for 117HD.
 *
 * Desktop 117HD reads JVM ManagementFactory MXBeans, which Android does not
 * expose as a stable app API. Rendering/timer behavior stays identical; process
 * CPU load comes from android.os.Process and ART GC counters come from
 * android.os.Debug runtime stats when the runtime publishes them.
 */
@Slf4j
@Singleton
public class FrameTimer
{
	public static final int CPU_TIMER = 0;
	public static final int ASYNC_CPU_TIMER = 1;
	public static final int GPU_TIMER = 2;
	public static final int ASYNC_GPU_TIMER = 3;

	@Inject
	private ClientThread clientThread;

	@Inject
	private HdPlugin plugin;

	private static final int NUM_TIMERS = Timer.TIMERS.length;
	private static final int NUM_GPU_TIMERS = (int) Arrays.stream(Timer.TIMERS).filter(Timer::isGpuTimer).count();
	private static final int NUM_GPU_DEBUG_GROUPS = (int) Arrays.stream(Timer.TIMERS).filter(Timer::hasGpuDebugGroup).count();

	private final AutoTimer[] autoTimers = new AutoTimer[NUM_TIMERS];
	private final boolean[] activeTimers = new boolean[NUM_TIMERS];
	private final long[] timings = new long[NUM_TIMERS];
	private final int[] gpuQueries = new int[NUM_TIMERS * 2];
	private final ArrayDeque<Timer> glDebugGroupStack = new ArrayDeque<>(NUM_GPU_DEBUG_GROUPS);
	private final ArrayDeque<Listener> listeners = new ArrayDeque<>();

	private long lastCpuTimeMs = Process.getElapsedCpuTime();
	private long lastCpuWallNanos = System.nanoTime();
	private long lastGcCount;
	private long lastGcTimeMs;

	@RequiredArgsConstructor
	public class AutoTimer implements AutoCloseable
	{
		private final Timer timer;

		@Override
		public void close()
		{
			end(timer);
		}
	}

	@SuppressWarnings("resource")
	public FrameTimer()
	{
		for (int i = 0; i < NUM_TIMERS; i++)
			autoTimers[i] = new AutoTimer(Timer.TIMERS[i]);
	}

	@Getter
	private boolean isActive = false;

	public long cumulativeError;
	public long errorCompensation;

	private void initialize()
	{
		clientThread.invoke(() ->
		{
			int[] queryNames = new int[NUM_GPU_TIMERS * 2];
			glGenQueries(queryNames);
			int queryIndex = 0;
			for (var timer : Timer.TIMERS)
				if (timer.isGpuTimer())
					for (int j = 0; j < 2; ++j)
						gpuQueries[timer.ordinal() * 2 + j] = queryNames[queryIndex++];

			isActive = true;
			plugin.setupSyncMode();
			plugin.enableDetailedTimers = true;

			final int iterations = 100000;
			final int compensation = 1950000;
			for (int i = 0; i < 2; i++)
			{
				errorCompensation = 0;
				for (int j = 0; j < iterations; j++)
				{
					begin(Timer.DRAW_FRAME);
					end(Timer.DRAW_FRAME);
				}
				errorCompensation = (timings[Timer.DRAW_FRAME.ordinal()] + compensation) / iterations;
				timings[Timer.DRAW_FRAME.ordinal()] = 0;
			}
			log.debug("Estimated timer overhead around {} ns", errorCompensation);
		});
	}

	private void destroy()
	{
		clientThread.invoke(() ->
		{
			if (!isActive)
				return;

			isActive = false;
			plugin.setupSyncMode();
			plugin.enableDetailedTimers = false;

			glDeleteQueries(gpuQueries);
			Arrays.fill(gpuQueries, 0);
			reset();
		});
	}

	@FunctionalInterface
	public interface Listener
	{
		void onFrameCompletion(FrameTimings timings);
	}

	public void addTimingsListener(Listener listener)
	{
		if (listeners.isEmpty())
			initialize();
		listeners.add(listener);
	}

	public void removeTimingsListener(Listener listener)
	{
		listeners.remove(listener);
		if (listeners.isEmpty())
			destroy();
	}

	public void removeAllListeners()
	{
		listeners.clear();
		destroy();
	}

	public void reset()
	{
		Arrays.fill(timings, 0);
		Arrays.fill(activeTimers, false);
		cumulativeError = 0;
	}

	public AutoTimer begin(Timer timer)
	{
		int index = timer.ordinal();
		if (log.isDebugEnabled() && timer.hasGpuDebugGroup() && HdPlugin.GL_CAPS.OpenGL43)
		{
			if (glDebugGroupStack.contains(timer))
			{
				log.warn("The debug group {} is already on the stack", timer.name());
			}
			else
			{
				glDebugGroupStack.push(timer);
				GL43C.glPushDebugGroup(GL43C.GL_DEBUG_SOURCE_APPLICATION, index, timer.name);
			}
		}

		if (!isActive)
			return null;

		if (timer.isGpuTimer())
		{
			if (activeTimers[index])
				throw new UnsupportedOperationException("Cumulative GPU timing isn't supported");
			glQueryCounter(gpuQueries[index * 2], GL_TIMESTAMP);
		}
		else if (!activeTimers[index])
		{
			cumulativeError += errorCompensation + 1 >> 1;
			timings[index] -= System.nanoTime() - cumulativeError;
		}
		activeTimers[index] = true;

		return autoTimers[index];
	}

	public void end(Timer timer)
	{
		if (log.isDebugEnabled() && timer.hasGpuDebugGroup() && HdPlugin.GL_CAPS.OpenGL43)
		{
			if (glDebugGroupStack.peek() != timer)
			{
				if (glDebugGroupStack.contains(timer))
					log.warn("The debug group {} was popped out of order", timer.name());
			}
			else
			{
				glDebugGroupStack.pop();
				GL43C.glPopDebugGroup();
			}
		}

		if (!isActive || !activeTimers[timer.ordinal()])
			return;

		if (timer.isGpuTimer())
		{
			glQueryCounter(gpuQueries[timer.ordinal() * 2 + 1], GL_TIMESTAMP);
		}
		else
		{
			cumulativeError += errorCompensation >> 1;
			timings[timer.ordinal()] += System.nanoTime() - cumulativeError;
			activeTimers[timer.ordinal()] = false;
		}
	}

	public void add(Timer timer, long nanos)
	{
		if (isActive)
			timings[timer.ordinal()] += nanos;
	}

	public void endFrameAndReset()
	{
		if (HdPlugin.GL_CAPS.OpenGL43)
		{
			while (!glDebugGroupStack.isEmpty())
			{
				log.warn("The debug group {} was never popped", glDebugGroupStack.pop().name());
				GL43C.glPopDebugGroup();
			}
		}

		if (!isActive)
			return;

		long frameEndNanos = System.nanoTime();
		long frameEndTimestamp = System.currentTimeMillis();

		trackGarbageCollection();

		int[] available = { 0 };
		for (var timer : Timer.TIMERS)
		{
			int i = timer.ordinal();
			if (timer.isGpuTimer())
			{
				if (!activeTimers[i])
					continue;

				for (int j = 0; j < 2; j++)
				{
					while (available[0] == 0)
						glGetQueryObjectiv(gpuQueries[i * 2 + j], GL_QUERY_RESULT_AVAILABLE, available);
					timings[i] += (j * 2L - 1) * glGetQueryObjectui64(gpuQueries[i * 2 + j], GL_QUERY_RESULT);
				}
			}
			else if (activeTimers[i])
			{
				log.warn("Timer {} was never ended", timer);
				timings[i] += frameEndNanos;
			}
		}

		float cpuLoad = sampleProcessCpuLoad(frameEndNanos);
		var frameTimings = new FrameTimings(frameEndTimestamp, timings, cpuLoad);
		for (var listener : listeners)
			listener.onFrameCompletion(frameTimings);

		reset();
	}

	private float sampleProcessCpuLoad(long nowNanos)
	{
		long cpuMs = Process.getElapsedCpuTime();
		long cpuDeltaMs = Math.max(0L, cpuMs - lastCpuTimeMs);
		long wallDeltaNanos = Math.max(1L, nowNanos - lastCpuWallNanos);
		lastCpuTimeMs = cpuMs;
		lastCpuWallNanos = nowNanos;

		double wallMs = wallDeltaNanos / 1_000_000.0;
		double normalized = cpuDeltaMs / wallMs / Math.max(1, Runtime.getRuntime().availableProcessors());
		return (float) Math.max(0.0, Math.min(1.0, normalized));
	}

	private void trackGarbageCollection()
	{
		long gcCount = runtimeStat("art.gc.gc-count");
		long gcTimeMs = runtimeStat("art.gc.gc-time");

		if (gcCount >= 0)
		{
			plugin.garbageCollectionCount = gcCount;
			lastGcCount = gcCount;
		}

		if (gcTimeMs >= 0)
		{
			long deltaMs = Math.max(0L, gcTimeMs - lastGcTimeMs);
			lastGcTimeMs = gcTimeMs;
			add(Timer.GARBAGE_COLLECTION, deltaMs * 1_000_000L);
		}
	}

	private static long runtimeStat(String key)
	{
		try
		{
			String value = Debug.getRuntimeStat(key);
			return value == null ? -1L : Long.parseLong(value);
		}
		catch (Throwable ignored)
		{
			return -1L;
		}
	}
}

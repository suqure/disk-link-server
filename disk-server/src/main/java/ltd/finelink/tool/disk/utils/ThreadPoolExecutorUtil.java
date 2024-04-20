package ltd.finelink.tool.disk.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorUtil {

	private static ExecutorService executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
			Runtime.getRuntime().availableProcessors() * 2, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100));

	public static <T> Future<T> submit(Callable<T> call) {

		return executor.submit(call);

	}

	public static Future<?> submit(Runnable task) {

		return executor.submit(task);

	}

	public static <T> Future<T> submit(Runnable task, T result) {
		return executor.submit(task, result);
	}

}

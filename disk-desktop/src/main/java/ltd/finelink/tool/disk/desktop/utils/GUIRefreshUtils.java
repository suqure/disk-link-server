package ltd.finelink.tool.disk.desktop.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dev.onvoid.webrtc.RTCDataChannel;
import javafx.application.Platform;
import javafx.scene.control.TableView;

public class GUIRefreshUtils {

	private static ScheduledExecutorService restoreExecutor = Executors
			.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
 
	public static ConcurrentHashMap<String, TableView> refreshViews = new ConcurrentHashMap<>();

	public static void refreshCombineSeconds(TableView view, long seconds) {
		if (!refreshViews.containsKey(view.getId())) { 
			refreshViews.put(view.getId(), view);
			restoreExecutor.schedule(() -> {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						view.refresh();
					}
				});
				refreshViews.remove(view.getId());
			}, seconds, TimeUnit.SECONDS);
		}
	}

	public static void refreshCombinePerSecond(TableView view) {
		refreshCombineSeconds(view, 1);
	}

}

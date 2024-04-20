package ltd.finelink.tool.disk.desktop.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import ltd.finelink.tool.disk.client.enums.NotifyType;
import ltd.finelink.tool.disk.client.listener.NotifyHandler;
import ltd.finelink.tool.disk.client.vo.NotifyEvent;

@Service
public class ClientNotifyHanler implements NotifyHandler {

	private Map<NotifyType, List<NotifyListener>> listeners = new ConcurrentHashMap<>();

	@Override
	public void onNotify(NotifyEvent event) {
		List<NotifyListener> list = listeners.get(event.getType());
		if(list!=null&&!list.isEmpty()) {
			for (NotifyListener listener : list ) {
				listener.onEvent(event);
			}
		} 
	}

	public void addListener(NotifyType type, NotifyListener listener) {
		List<NotifyListener> list = listeners.get(type);
		if (list == null) {
			list = new ArrayList<>();
			listeners.put(type, list);
		}
		list.add(listener);
	}

	public void removeListener(NotifyType type, NotifyListener listener) {
		List<NotifyListener> list = listeners.get(type);
		if (list != null && list.contains(listener)) {
			list.remove(listener);
		}
	}
	
	public void clearListener(NotifyType type) {
		listeners.remove(type);
	}

}

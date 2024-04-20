package ltd.finelink.tool.disk.client.listener;

import ltd.finelink.tool.disk.client.vo.NotifyEvent;

public interface NotifyHandler {
	
	void onNotify(NotifyEvent event);

}

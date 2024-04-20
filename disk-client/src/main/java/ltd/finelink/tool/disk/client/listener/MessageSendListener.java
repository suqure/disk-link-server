package ltd.finelink.tool.disk.client.listener;

import ltd.finelink.tool.disk.client.vo.ChannelMessage;

public interface MessageSendListener {

	void onSuccess(ChannelMessage message);

	void onFailed(ChannelMessage message, Exception e);

}

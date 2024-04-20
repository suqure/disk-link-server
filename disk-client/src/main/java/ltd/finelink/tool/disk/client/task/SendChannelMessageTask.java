package ltd.finelink.tool.disk.client.task;

import java.io.IOException;

import dev.onvoid.webrtc.RTCDataChannel;
import dev.onvoid.webrtc.RTCDataChannelState;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.client.context.UserContext;
import ltd.finelink.tool.disk.client.enums.ChannelBasicType;
import ltd.finelink.tool.disk.client.enums.ChannelMessageType;
import ltd.finelink.tool.disk.client.listener.MessageSendListener;
import ltd.finelink.tool.disk.client.utils.ClientMessageUtil;
import ltd.finelink.tool.disk.client.vo.ChannelMessage;

@Slf4j
public class SendChannelMessageTask implements Runnable {

	private ChannelMessage message;

	private String device;

	private long maxBuffer;

	private MessageSendListener listener;

	private final static long DEFAUL_BUFFER = 15 * 1024 * 1024;

	public SendChannelMessageTask(ChannelMessage message, String device, long maxBuffer) {
		this(message, device, maxBuffer, null);
	}

	public SendChannelMessageTask(ChannelMessage message, String device, long maxBuffer, MessageSendListener listener) {
		this.message = message;
		this.device = device;
		this.listener = listener;
		if (maxBuffer <= 0) {
			this.maxBuffer = DEFAUL_BUFFER;
		} else {
			this.maxBuffer = maxBuffer;
		}

	}

	public SendChannelMessageTask(ChannelMessage message, String device) {
		this(message, device, 0, null);
	}

	public SendChannelMessageTask(ChannelMessage message, String device, MessageSendListener listener) {

		this(message, device, 0, listener);

	}

	public void addListener(MessageSendListener listener) {
		this.listener = listener;
	}

	@Override
	public void run() {
		if (message == null) {
			return;
		}
		RTCDataChannel channel = UserContext.localChannels.get(device);
		if (channel == null) {
			return;
		} 
		try { 
			while (true) { 
				if (RTCDataChannelState.CLOSED.equals(channel.getState())
						|| RTCDataChannelState.CLOSING.equals(channel.getState())) {
					break;
				}
				if (channel.getBufferedAmount() < maxBuffer) {
					channel.send(ClientMessageUtil.buildChannelBuffer(message));
					if (listener != null) {
						listener.onSuccess(message);
					}
				} else {
					Thread.sleep(10);
				} 
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			listener.onFailed(message, e);
		}

	}

}

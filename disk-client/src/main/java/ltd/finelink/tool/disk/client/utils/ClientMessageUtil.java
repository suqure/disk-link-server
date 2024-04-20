package ltd.finelink.tool.disk.client.utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;

import dev.onvoid.webrtc.RTCDataChannelBuffer;
import dev.onvoid.webrtc.RTCIceCandidate;
import ltd.finelink.tool.disk.client.enums.ChannelBasicType;
import ltd.finelink.tool.disk.client.enums.ChannelDirType;
import ltd.finelink.tool.disk.client.enums.ChannelFileType;
import ltd.finelink.tool.disk.client.enums.ChannelMessageType;
import ltd.finelink.tool.disk.client.enums.NotifyType;
import ltd.finelink.tool.disk.client.vo.ChannelData;
import ltd.finelink.tool.disk.client.vo.ChannelMessage;
import ltd.finelink.tool.disk.client.vo.NotifyEvent;
import ltd.finelink.tool.disk.client.vo.SubcribeVo;
import ltd.finelink.tool.disk.client.vo.WebRTCVo;
import ltd.finelink.tool.disk.protobuf.ClientBody;
import ltd.finelink.tool.disk.protobuf.ClientMessage;
import ltd.finelink.tool.disk.protobuf.ClientMessage.ClientMsgType;

public class ClientMessageUtil {

	public static ClientMessage buildWebRTCMessage(int type, String userId, String channel, WebRTCVo content) {
		ClientMessage.Builder builder = ClientMessage.newBuilder().setType(ClientMsgType.WEBRTC).setBiz("WEBRTC")
				.setCid(generatMessageId()).setTimestamp(System.currentTimeMillis());
		ClientBody.Builder bodyBuidler = ClientBody.newBuilder().setType(type)
				.setContent(ByteString.copyFrom(JSON.toJSONString(content).getBytes())).setToId(userId);
		if (StringUtils.isNotBlank(channel)) {
			bodyBuidler.setSub(channel);
		}
		builder.addBody(bodyBuidler.build());
		return builder.build();
	}

	public static ClientMessage buildSubscribMessage(int type, SubcribeVo content) {
		ClientMessage.Builder builder = ClientMessage.newBuilder().setType(ClientMsgType.SUBSCRIBE).setBiz("SUBSCRIBE")
				.setCid(generatMessageId()).setTimestamp(System.currentTimeMillis());

		ClientBody.Builder bodyBuidler = ClientBody.newBuilder().setType(type);
		if (content != null) {
			bodyBuidler.setContent(ByteString.copyFrom(JSON.toJSONString(content).getBytes()));
		}
		builder.addBody(bodyBuidler.build());
		return builder.build();
	}

	public static ChannelMessage buildChannelMessage(ChannelMessageType type, ChannelData data) {
		ChannelMessage channelMessage = new ChannelMessage();
		channelMessage.setType(type.getCode());
		channelMessage.setData(data);
		return channelMessage;
	}

	public static ChannelData buildBasicData(ChannelBasicType type, Object message) {
		ChannelData data = new ChannelData();
		data.setType(type.getCode());
		data.setData(message);
		return data;
	}

	public static ChannelData buildFileData(ChannelFileType type, Object message) {
		ChannelData data = new ChannelData();
		data.setType(type.getCode());
		data.setData(message);
		return data;
	}
	
	public static ChannelData buildDirData(ChannelDirType type, Object message) {
		ChannelData data = new ChannelData();
		data.setType(type.getCode());
		data.setData(message);
		return data;
	}

	public static RTCDataChannelBuffer buildChannelBuffer(ChannelMessage message) {
		ByteBuffer data = ByteBuffer.wrap(JSON.toJSONString(message).getBytes(StandardCharsets.UTF_8));
		RTCDataChannelBuffer buffer = new RTCDataChannelBuffer(data, false);
		return buffer;
	}
	
	public static RTCDataChannelBuffer buildByteBuffer(byte[] bytes) {
		ByteBuffer data = ByteBuffer.wrap(bytes);
		RTCDataChannelBuffer buffer = new RTCDataChannelBuffer(data, true);
		return buffer;
	}

	public static ChannelMessage decodeChannelMessage(ByteBuffer byteBuffer) {
		byte[] payload = null;

		if (byteBuffer.hasArray()) {
			payload = byteBuffer.array();
		} else {
			payload = new byte[byteBuffer.limit()];
			byteBuffer.get(payload);
		}
		String text = new String(payload, StandardCharsets.UTF_8);
		return JSON.parseObject(text, ChannelMessage.class);
	}
	
	public static byte[] decodeBytes(ByteBuffer byteBuffer) {
		byte[] payload = null;
		if (byteBuffer.hasArray()) {
			payload = byteBuffer.array();
		} else {
			payload = new byte[byteBuffer.limit()];
			byteBuffer.get(payload);
		}
		 
		return payload;
	}

	public static String generatMessageId() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static NotifyEvent buildNotifyEvent(NotifyType type, String code, String message, Object data) {
		NotifyEvent event = new NotifyEvent();
		event.setType(type);
		event.setCode(code);
		event.setMessage(message);
		event.setData(data);
		return event;
	}

	public static String parseIceCandidate(RTCIceCandidate candidate) {
		JSONObject data = new JSONObject();
		data.put("sdb", candidate.sdp);
		data.put("sdpMid", candidate.sdpMid);
		data.put("sdpMLineIndex", candidate.sdpMLineIndex);
		data.put("serverUrl", candidate.serverUrl);
		return data.toJSONString();
	}
}

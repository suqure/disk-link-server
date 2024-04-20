package ltd.finelink.tool.disk.utils;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.google.protobuf.ByteString;

import ltd.finelink.tool.disk.enums.CustomBodyType;
import ltd.finelink.tool.disk.enums.LogoutMessage;
import ltd.finelink.tool.disk.enums.SubscribeBodyType;
import ltd.finelink.tool.disk.enums.SystemBodyType;
import ltd.finelink.tool.disk.enums.WebRTCBodyType;
import ltd.finelink.tool.disk.enums.WebRTCRelyMessage;
import ltd.finelink.tool.disk.protobuf.ClientBody;
import ltd.finelink.tool.disk.protobuf.ClientMessage;
import ltd.finelink.tool.disk.protobuf.ClientMessage.ClientMsgType;
import ltd.finelink.tool.disk.protobuf.ServerBody;
import ltd.finelink.tool.disk.protobuf.ServerMessage;
import ltd.finelink.tool.disk.protobuf.ServerMessage.ServerMsgType;

public class MessageUtils {

	public static ServerMessage buildLogoutMessage(LogoutMessage message) {
		ServerBody body = ServerBody.newBuilder().setSid(generatMessageId()).setType(SystemBodyType.LOGOUT.getCode())
				.setCode(String.valueOf(message.getCode())).setMsg(message.getMessage()).build();
		return ServerMessage.newBuilder().setType(ServerMsgType.SYSTEM).setTimestamp(System.currentTimeMillis())
				.addBody(body).build();
	}

	public static ServerMessage buildRejectMessage(ClientMessage message) {
		ServerBody body = ServerBody.newBuilder().setSid(generatMessageId()).setType(SystemBodyType.REJECT.getCode())
				.setCid(message.getCid()).setCode("100").setMsg("invalidate message").build();
		return ServerMessage.newBuilder().setType(ServerMsgType.SYSTEM).setBiz(message.getBiz())
				.setTimestamp(System.currentTimeMillis()).addBody(body).build();
	}

	public static ServerMessage buildNotifyMessage(String msgType, String content) {
		ServerBody body = ServerBody.newBuilder().setSid(generatMessageId()).setType(SystemBodyType.NOTIFY.getCode())
				.setCid(generatMessageId()).setMsgType(msgType).setContent(ByteString.copyFrom(content.getBytes()))
				.build();
		return ServerMessage.newBuilder().setType(ServerMsgType.SYSTEM).setBiz("NOTIFY")
				.setTimestamp(System.currentTimeMillis()).addBody(body).build();
	}

	public static ServerMessage buildTorrentMessage(String hashId, byte[] content) {
		ServerBody body = ServerBody.newBuilder().setSid(generatMessageId()).setType(CustomBodyType.TORRENT.getCode())
				.setFromId(hashId).setContent(ByteString.copyFrom(content)).build();
		return ServerMessage.newBuilder().setType(ServerMsgType.CUSTOM).setBiz("magnet")
				.setTimestamp(System.currentTimeMillis()).addBody(body).build();
	}

	public static ServerMessage buildRoomMessage(String msgType, String content) {
		ServerBody body = ServerBody.newBuilder().setSid(generatMessageId()).setType(CustomBodyType.ROOM.getCode())
				.setMsgType(msgType).setContent(ByteString.copyFrom(content.getBytes())).build();
		return ServerMessage.newBuilder().setType(ServerMsgType.CUSTOM).setBiz("room")
				.setTimestamp(System.currentTimeMillis()).addBody(body).build();
	}

	public static ServerMessage buildRoomErrorMessage(String msgType, String code, String message) {
		ServerBody body = ServerBody.newBuilder().setSid(generatMessageId()).setType(CustomBodyType.ROOM.getCode())
				.setMsgType(msgType).setCode(code).setMsg(message).build();
		return ServerMessage.newBuilder().setType(ServerMsgType.CUSTOM).setBiz("room")
				.setTimestamp(System.currentTimeMillis()).addBody(body).build();
	}

	public static ServerMessage buildHeartBeatMessage(ClientMessage message) {
		ServerBody body = ServerBody.newBuilder().setSid(generatMessageId()).setType(SystemBodyType.HEATBEAT.getCode())
				.setCid(message.getCid()).build();
		return ServerMessage.newBuilder().setType(ServerMsgType.SYSTEM).setBiz(message.getBiz())
				.setTimestamp(System.currentTimeMillis()).addBody(body).build();
	}

	public static ServerMessage buildWebRTCMessage(String userId, String type, ClientMessage message) {
		ClientBody clientBody = message.getBody(0);
		ServerBody.Builder bodyBuilder = ServerBody.newBuilder().setSub(type).setCid(message.getCid())
				.setSid(generatMessageId()).setFromId(userId).setToId(clientBody.getToId())
				.setType(clientBody.getType()).setMsgType(clientBody.getMsgType()).setTimestamp(message.getTimestamp());
		if (clientBody.getContent() != null) {
			bodyBuilder.setContent(clientBody.getContent());
		}
		if (clientBody.getType() == WebRTCBodyType.REJECT.getCode()) {
			bodyBuilder.setCode(String.valueOf(WebRTCRelyMessage.FORBIDDEN.getCode()))
					.setMsg(WebRTCRelyMessage.FORBIDDEN.getMessage());
		}
		return ServerMessage.newBuilder().setType(ServerMsgType.WEBRTC).setBiz(message.getBiz())
				.setTimestamp(System.currentTimeMillis()).addBody(bodyBuilder.build()).build();
	}

	public static ServerMessage buildSubscribeRespMessage(String content, ClientMessage message) {
		ClientBody clientBody = message.getBody(0);
		ServerBody.Builder bodyBuilder = ServerBody.newBuilder().setCid(message.getCid()).setSid(generatMessageId())
				.setType(clientBody.getType()).setTimestamp(message.getTimestamp());
		if (StringUtils.isNotBlank(content)) {
			bodyBuilder.setContent(ByteString.copyFrom(content.getBytes()));
		} else {
			bodyBuilder.setCode("404").setMsg("subcribe code not exist!");
		}
		return ServerMessage.newBuilder().setType(ServerMsgType.SUBSCRIBE).setBiz(message.getBiz())
				.setTimestamp(System.currentTimeMillis()).addBody(bodyBuilder.build()).build();
	}

	public static ServerMessage buildRejectSubscribeRespMessage(ClientMessage message) {
		ClientBody clientBody = message.getBody(0);
		ServerBody.Builder bodyBuilder = ServerBody.newBuilder().setCid(message.getCid()).setSid(generatMessageId())
				.setType(clientBody.getType()).setTimestamp(message.getTimestamp()).setContent(clientBody.getContent())
				.setCode("403").setMsg("No permission!");
		return ServerMessage.newBuilder().setType(ServerMsgType.SUBSCRIBE).setBiz(message.getBiz())
				.setTimestamp(System.currentTimeMillis()).addBody(bodyBuilder.build()).build();
	}

	public static ServerMessage buildUnSubscribeMessage(String content) {

		return buildUnSubscribeMessage(null, content);
	}

	public static ServerMessage buildUnSubscribeMessage(String msgType, String content) {
		ServerBody.Builder bodyBuilder = ServerBody.newBuilder().setCid(generatMessageId()).setSid(generatMessageId())
				.setType(SubscribeBodyType.UNSUB.getCode()).setTimestamp(System.currentTimeMillis())
				.setContent(ByteString.copyFrom(content.getBytes()));
		if (StringUtils.isNotBlank(msgType)) {
			bodyBuilder.setMsgType(msgType);
		}
		return ServerMessage.newBuilder().setType(ServerMsgType.SUBSCRIBE).setBiz("SUBSCRIBE")
				.setTimestamp(System.currentTimeMillis()).addBody(bodyBuilder.build()).build();
	}

	public static ServerMessage buildSubscribeMessage(String content) {
		return buildSubscribeMessage(null, content);
	}

	public static ServerMessage buildSubscribeMessage(String msgType, String content) {
		ServerBody.Builder bodyBuilder = ServerBody.newBuilder().setCid(generatMessageId()).setSid(generatMessageId())
				.setType(SubscribeBodyType.MESSAGE.getCode()).setTimestamp(System.currentTimeMillis())
				.setContent(ByteString.copyFrom(content.getBytes()));
		if (StringUtils.isNotBlank(msgType)) {
			bodyBuilder.setMsgType(msgType);
		}
		return ServerMessage.newBuilder().setType(ServerMsgType.SUBSCRIBE).setBiz("SUBSCRIBE")
				.setTimestamp(System.currentTimeMillis()).addBody(bodyBuilder.build()).build();
	}

	public static ServerMessage buildSubscribeRespMessage(String content) {
		return buildSubscribeRespMessage(null, content);
	}

	public static ServerMessage buildSubscribeRespMessage(String msgType, String content) {
		ServerBody.Builder bodyBuilder = ServerBody.newBuilder().setCid(generatMessageId()).setSid(generatMessageId())
				.setType(SubscribeBodyType.SUB.getCode()).setTimestamp(System.currentTimeMillis())
				.setContent(ByteString.copyFrom(content.getBytes()));
		if (StringUtils.isNotBlank(msgType)) {
			bodyBuilder.setMsgType(msgType);
		}
		if (StringUtils.isNotBlank(content)) {
			bodyBuilder.setContent(ByteString.copyFrom(content.getBytes()));
		}
		return ServerMessage.newBuilder().setType(ServerMsgType.SUBSCRIBE).setBiz("SUBSCRIBE")
				.setTimestamp(System.currentTimeMillis()).addBody(bodyBuilder.build()).build();
	}

	public static ServerMessage buildWebRTCOffLineMessage(ClientMessage message) {
		ClientBody clientBody = message.getBody(0);
		ServerBody.Builder bodyBuilder = ServerBody.newBuilder().setCid(message.getCid())
				.setType(WebRTCBodyType.REPLY.getCode()).setSid(generatMessageId()).setToId(clientBody.getToId())
				.setSub(clientBody.getSub()).setMsgType(clientBody.getMsgType()).setTimestamp(message.getTimestamp())
				.setCode(String.valueOf(WebRTCRelyMessage.OFFLINE.getCode()))
				.setMsg(WebRTCRelyMessage.OFFLINE.getMessage()).setContent(clientBody.getContent());
		return ServerMessage.newBuilder().setType(ServerMsgType.WEBRTC).setBiz(message.getBiz())
				.setTimestamp(System.currentTimeMillis()).addBody(bodyBuilder.build()).build();
	}

	public static String generatMessageId() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	public static boolean isChat(ClientMessage message) {
		if (ClientMsgType.CHAT.equals(message.getType())) {
			return true;
		}
		return false;
	}

	public static boolean isComfirm(ClientMessage message) {
		if (ClientMsgType.CONFIRM.equals(message.getType())) {
			return true;
		}
		return false;
	}

	public static boolean isWEBRTC(ClientMessage message) {
		if (ClientMsgType.WEBRTC.equals(message.getType())) {
			return true;
		}
		return false;
	}

	public static boolean isCustom(ClientMessage message) {
		if (ClientMsgType.CUSTOM.equals(message.getType())) {
			return true;
		}
		return false;
	}

	public static boolean isHeartbeat(ClientMessage message) {
		if (ClientMsgType.HEARTBEAT.equals(message.getType())) {
			return true;
		}
		return false;
	}

	public static boolean isSubscribe(ClientMessage message) {
		if (ClientMsgType.SUBSCRIBE.equals(message.getType())) {
			return true;
		}
		return false;
	}

}

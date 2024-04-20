package ltd.finelink.tool.disk.client.listener;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import dev.onvoid.webrtc.CreateSessionDescriptionObserver;
import dev.onvoid.webrtc.PeerConnectionFactory;
import dev.onvoid.webrtc.PeerConnectionObserver;
import dev.onvoid.webrtc.RTCAnswerOptions;
import dev.onvoid.webrtc.RTCConfiguration;
import dev.onvoid.webrtc.RTCDataChannel;
import dev.onvoid.webrtc.RTCDataChannelBuffer;
import dev.onvoid.webrtc.RTCDataChannelInit;
import dev.onvoid.webrtc.RTCDataChannelObserver;
import dev.onvoid.webrtc.RTCIceCandidate;
import dev.onvoid.webrtc.RTCIceConnectionState;
import dev.onvoid.webrtc.RTCOfferOptions;
import dev.onvoid.webrtc.RTCPeerConnection;
import dev.onvoid.webrtc.RTCPeerConnectionState;
import dev.onvoid.webrtc.RTCSdpType;
import dev.onvoid.webrtc.RTCSessionDescription;
import dev.onvoid.webrtc.SetSessionDescriptionObserver;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.client.context.UserContext;
import ltd.finelink.tool.disk.client.entity.DownloadFile;
import ltd.finelink.tool.disk.client.entity.ShareDir;
import ltd.finelink.tool.disk.client.entity.ShareFile;
import ltd.finelink.tool.disk.client.entity.UploadFile;
import ltd.finelink.tool.disk.client.enums.ChannelBasicType;
import ltd.finelink.tool.disk.client.enums.ChannelDirType;
import ltd.finelink.tool.disk.client.enums.ChannelFileType;
import ltd.finelink.tool.disk.client.enums.ChannelMessageType;
import ltd.finelink.tool.disk.client.enums.DownloadFileType;
import ltd.finelink.tool.disk.client.enums.FileReadType;
import ltd.finelink.tool.disk.client.enums.NotifyType;
import ltd.finelink.tool.disk.client.enums.WebRTCType;
import ltd.finelink.tool.disk.client.service.DownloadFileService;
import ltd.finelink.tool.disk.client.service.ShareDirService;
import ltd.finelink.tool.disk.client.service.ShareFileService;
import ltd.finelink.tool.disk.client.service.UploadFileService;
import ltd.finelink.tool.disk.client.service.UserInfoService;
import ltd.finelink.tool.disk.client.task.FileUploadBufferTask;
import ltd.finelink.tool.disk.client.utils.ClientMessageUtil;
import ltd.finelink.tool.disk.client.utils.FileInfoUtil;
import ltd.finelink.tool.disk.client.vo.CandidateVo;
import ltd.finelink.tool.disk.client.vo.ChannelData;
import ltd.finelink.tool.disk.client.vo.ChannelMessage;
import ltd.finelink.tool.disk.client.vo.ConnectionMessage;
import ltd.finelink.tool.disk.client.vo.DirInfo;
import ltd.finelink.tool.disk.client.vo.DirMessage;
import ltd.finelink.tool.disk.client.vo.FileInfo;
import ltd.finelink.tool.disk.client.vo.FileReadMessage;
import ltd.finelink.tool.disk.client.vo.IceServer;
import ltd.finelink.tool.disk.client.vo.SubcribeMessage;
import ltd.finelink.tool.disk.client.vo.SubcribeVo;
import ltd.finelink.tool.disk.client.vo.WebRTCMessage;
import ltd.finelink.tool.disk.client.vo.WebRTCVo;
import ltd.finelink.tool.disk.enums.SubscribeBodyType;
import ltd.finelink.tool.disk.enums.WebRTCBodyType;
import ltd.finelink.tool.disk.protobuf.ClientMessage;
import ltd.finelink.tool.disk.protobuf.ServerBody;
import ltd.finelink.tool.disk.protobuf.ServerMessage;
import ltd.finelink.tool.disk.protobuf.ServerMessage.ServerMsgType;
import okhttp3.WebSocket;
import okio.ByteString;

@Slf4j
public class DiskServerListener implements MessageListener {

	private NotifyHandler notifyHandler;

	private UserInfoService userInfoService;

	private ShareFileService shareFileService;

	private DownloadFileService downloadFileService;

	private UploadFileService uploadFileService;

	private PeerConnectionFactory peerConnectionFactory;

	private ShareDirService shareDirService;

	private ExecutorService executor = Executors.newFixedThreadPool(10);

	private ScheduledExecutorService restoreExecutor = Executors
			.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

	public final ConcurrentHashMap<String, RandomAccessFile> writeFile = new ConcurrentHashMap<>();

	public final ConcurrentHashMap<String, ShareFile> sendFile = new ConcurrentHashMap<>();

	public final ConcurrentHashMap<String, ShareFile> dirFile = new ConcurrentHashMap<>();

	public final ConcurrentHashMap<String, FileInfo> currentDowload = new ConcurrentHashMap<>();

	public final ConcurrentHashMap<String, FileInfo> currentSend = new ConcurrentHashMap<>();

	public DiskServerListener(NotifyHandler notifyHandler, UserInfoService userInfoService,
			ShareFileService shareFileService, DownloadFileService downloadFileService,
			UploadFileService uploadFileService, ShareDirService shareDirService,
			PeerConnectionFactory peerConnectionFactory) {
		this.notifyHandler = notifyHandler;
		this.userInfoService = userInfoService;
		this.peerConnectionFactory = peerConnectionFactory;
		this.shareFileService = shareFileService;
		this.downloadFileService = downloadFileService;
		this.uploadFileService = uploadFileService;
		this.shareDirService = shareDirService;
	}

	@Override
	public void onMessage(WebSocket webSocket, ServerMessage message) {
		if (message.getType().equals(ServerMsgType.SUBSCRIBE)) {
			handleSubscribeMessage(webSocket, message);
		} else if (message.getType().equals(ServerMsgType.WEBRTC)) {
			handleWebRTCMessage(webSocket, message);
		} else if (message.getType().equals(ServerMsgType.CHAT)) {
			handleChatMessage(message);
		} else if (message.getType().equals(ServerMsgType.CUSTOM)) {
			handleCustomMessage(message);
		} else if (message.getType().equals(ServerMsgType.SYSTEM)) {
			handleSystemMessage(message);
		}

	}

	@Override
	public void onMessage(WebSocket webSocket, String message) {

		log.debug("reciever message {}", message);
	}

	@Override
	public void onOpen(WebSocket webSocket) {
		if (UserContext.currentUser != null) {
			SubcribeVo vo = new SubcribeVo();
			vo.setChannel(UserContext.currentUser.getChannel());
			vo.setCode(UserContext.currentUser.getCode());
			vo.setUserId(UserContext.currentUser.getUsername());
			webSocket.send(ByteString
					.of(ClientMessageUtil.buildSubscribMessage(SubscribeBodyType.CREATE.getCode(), vo).toByteArray()));
			List<IceServer> iceServers = userInfoService.getIceServer(UserContext.currentUser.getUsername(),
					UserContext.currentUser.getToken());
			if (iceServers != null) {
				UserContext.iceServers.addAll(iceServers);
			}
		}
	}

	private void handleSubscribeMessage(WebSocket webSocket, ServerMessage message) {
		SubcribeMessage notify = new SubcribeMessage();
		ServerBody body = message.getBody(0);
		SubcribeVo vo = null;
		if (body.getContent() != null) {
			try {
				String data = new String(body.getContent().toByteArray(), "UTF-8");
				vo = JSONObject.parseObject(data, SubcribeVo.class);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		notify.setType(body.getType());
		notify.setVo(vo);
		if (StringUtils.isNotBlank(body.getCode())) {
			if (notifyHandler != null) {
				notify.setVo(vo);
				notifyHandler.onNotify(
						ClientMessageUtil.buildNotifyEvent(NotifyType.SUBCRIBE, body.getCode(), body.getMsg(), notify));
			} else {
				log.warn("subscribe code {}, msg {}", body.getCode(), body.getMsg());
			}

		} else {
			if (body.getType() == SubscribeBodyType.CREATE.getCode()) {
				UserContext.currentUser.setCode(vo.getCode());
				userInfoService.updateUserCode(UserContext.currentUser.getUsername(), vo.getCode());
			} else if (body.getType() == SubscribeBodyType.SUB.getCode()) {
				if ("local".equals(body.getMsgType())) {
					vo.setLocal(true);
				} else {
					vo.setLocal(false);
				}
				vo.setStatus(0);
				UserContext.deviceMap.put(vo.getCode(), vo);
			} else if (body.getType() == SubscribeBodyType.UNSUB.getCode()) {
				UserContext.deviceMap.remove(vo.getCode());
			} else if (body.getType() == SubscribeBodyType.MESSAGE.getCode()) {
				SubcribeVo update = UserContext.deviceMap.get(vo.getCode());
				if (update != null) {
					update.setOnline(vo.getOnline());
				} else {
					if ("local".equals(body.getMsgType())) {
						vo.setLocal(true);
					} else {
						vo.setLocal(false);
					}
					vo.setStatus(0);
					UserContext.deviceMap.put(vo.getCode(), vo);
				}
			} else {
				UserContext.deviceMap.clear();
				webSocket.send(ByteString.of(ClientMessageUtil
						.buildSubscribMessage(SubscribeBodyType.CREATE.getCode(), new SubcribeVo()).toByteArray()));

			}
			notify.setVo(vo);
			if (notifyHandler != null) {
				notifyHandler.onNotify(ClientMessageUtil.buildNotifyEvent(NotifyType.SUBCRIBE, null, null, notify));
			}
		}
	}

	private void handleWebRTCMessage(WebSocket webSocket, ServerMessage message) {
		WebRTCMessage notify = new WebRTCMessage();
		ServerBody body = message.getBody(0);
		WebRTCVo vo = null;
		if (body.getContent() != null) {
			try {
				String data = new String(body.getContent().toByteArray(), "UTF-8");
				vo = JSONObject.parseObject(data, WebRTCVo.class);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		notify.setType(body.getType());
		notify.setVo(vo);
		if (StringUtils.isNotBlank(body.getCode())) {
			notifyHandler.onNotify(
					ClientMessageUtil.buildNotifyEvent(NotifyType.WEBRTC, body.getCode(), body.getMsg(), notify));
		} else {
			SubcribeVo device = UserContext.deviceMap.get(vo.getCode());
			String userId = device.getUserId();
			String channel = device.getChannel();
			if (body.getType() == WebRTCBodyType.APPLY.getCode()) {
				initWebRTC(webSocket, vo.getCode());

				if (UserContext.currentUser.isRequirePwd()
						&& StringUtils.isNotBlank(UserContext.currentUser.getPassword())) {

					if (WebRTCType.APPLY.getValue().equals(vo.getType())
							|| (WebRTCType.AUTH.getValue().equals(vo.getType())
									&& !UserContext.currentUser.getPassword().equals(vo.getPwd()))) {
						WebRTCVo auth = new WebRTCVo();
						auth.setCode(UserContext.currentUser.getCode());
						auth.setType(WebRTCType.AUTH.getValue());
						ClientMessage authReply = ClientMessageUtil.buildWebRTCMessage(WebRTCBodyType.REPLY.getCode(),
								userId, channel, auth);
						webSocket.send(ByteString.of(authReply.toByteArray()));
					} else {
						RTCPeerConnection connection = UserContext.connections.get(vo.getCode());

						connection.createOffer(new RTCOfferOptions(), new CreateSessionDescriptionObserver() {

							@Override
							public void onFailure(String msg) {
								log.error("create offer error {}", msg);
							}

							@Override
							public void onSuccess(RTCSessionDescription description) {
								WebRTCVo offer = new WebRTCVo();
								offer.setCode(UserContext.currentUser.getCode());
								offer.setSdp(description.sdp);
								offer.setType(WebRTCType.OFFER.getValue());
								ClientMessage message = ClientMessageUtil
										.buildWebRTCMessage(WebRTCBodyType.REPLY.getCode(), userId, channel, offer);
								webSocket.send(ByteString.of(message.toByteArray()));
								connection.setLocalDescription(description, new SetSessionDescriptionObserver() {

									@Override
									public void onFailure(String msg) {
										log.error("local desc error {}", msg);

									}

									@Override
									public void onSuccess() {
										log.debug("set local desc success");
									}
								});
							}
						});
					}
				} else {
					RTCPeerConnection connection = UserContext.connections.get(vo.getCode());

					connection.createOffer(new RTCOfferOptions(), new CreateSessionDescriptionObserver() {

						@Override
						public void onFailure(String msg) {
							log.error("create offer error {}", msg);
						}

						@Override
						public void onSuccess(RTCSessionDescription description) {
							WebRTCVo offer = new WebRTCVo();
							offer.setCode(UserContext.currentUser.getCode());
							offer.setSdp(description.sdp);
							offer.setType(WebRTCType.OFFER.getValue());
							ClientMessage message = ClientMessageUtil.buildWebRTCMessage(WebRTCBodyType.REPLY.getCode(),
									userId, channel, offer);
							webSocket.send(ByteString.of(message.toByteArray()));
							connection.setLocalDescription(description, new SetSessionDescriptionObserver() {

								@Override
								public void onFailure(String msg) {
									log.error("local desc error {}", msg);

								}

								@Override
								public void onSuccess() {
									log.debug("set local desc success");
								}
							});
						}
					});
				}
			} else if (body.getType() == WebRTCBodyType.REPLY.getCode()) {
				RTCPeerConnection connection = UserContext.connections.get(vo.getCode());
				if (WebRTCType.AUTH.getValue().equals(vo.getType())) {
					device.setStatus(3);
				}
				if (WebRTCType.OFFER.getValue().equals(vo.getType())) {
					connection.setRemoteDescription(new RTCSessionDescription(RTCSdpType.OFFER, vo.getSdp()),
							new SetSessionDescriptionObserver() {

								@Override
								public void onFailure(String msg) {

									log.error("remote desc error {}", msg);
								}

								@Override
								public void onSuccess() {
									log.debug("set remote desc success");

								}
							});
					connection.createAnswer(new RTCAnswerOptions(), new CreateSessionDescriptionObserver() {

						@Override
						public void onFailure(String msg) {

							log.error("create answer error {}", msg);
						}

						@Override
						public void onSuccess(RTCSessionDescription description) {
							WebRTCVo answer = new WebRTCVo();
							answer.setCode(UserContext.currentUser.getCode());
							answer.setSdp(description.sdp);
							answer.setType(WebRTCType.ANSWER.getValue());
							ClientMessage message = ClientMessageUtil.buildWebRTCMessage(WebRTCBodyType.REPLY.getCode(),
									userId, channel, answer);
							webSocket.send(ByteString.of(message.toByteArray()));
							connection.setLocalDescription(description, new SetSessionDescriptionObserver() {

								@Override
								public void onFailure(String msg) {
									log.error("local desc error {}", msg);

								}

								@Override
								public void onSuccess() {
									log.debug("set local desc success");
								}
							});
						}
					});
				}
				if (WebRTCType.ANSWER.getValue().equals(vo.getType())) {
					connection.setRemoteDescription(new RTCSessionDescription(RTCSdpType.ANSWER, vo.getSdp()),
							new SetSessionDescriptionObserver() {

								@Override
								public void onFailure(String msg) {

									log.error("remote desc error {}", msg);
								}

								@Override
								public void onSuccess() {
									log.debug("set remote desc success");

								}
							});
				}
				if (WebRTCType.ICE.getValue().equals(vo.getType())) {
					if (StringUtils.isNotBlank(vo.getIceCandidate().getCandidate())) {
						connection.addIceCandidate(vo.getIceCandidate().to());
					}
				}

			} else if (body.getType() == WebRTCBodyType.MESSAGE.getCode()) {

			} else if (body.getType() == WebRTCBodyType.CLOSE.getCode()) {
				UserContext.closeConnection(vo.getCode());
			}
			if (notifyHandler != null) {
				notifyHandler.onNotify(ClientMessageUtil.buildNotifyEvent(NotifyType.WEBRTC, null, null, notify));
			}
		}

	}

	private void handleCustomMessage(ServerMessage message) {

	}

	private void handleChatMessage(ServerMessage message) {

	}

	private void handleSystemMessage(ServerMessage message) {

	}

	public void initWebRTC(WebSocket webSocket, String deviceCode) {
		SubcribeVo device = UserContext.deviceMap.get(deviceCode);
		String userId = device.getUserId();
		String channel = device.getChannel();
		RTCConfiguration config = new RTCConfiguration();
		if (UserContext.iceServers.isEmpty()) {
			config.iceServers.add(IceServer.defalut().tranfer());
		} else {
			for (IceServer server : UserContext.iceServers) {
				config.iceServers.add(server.tranfer());
			}
		}
		RTCPeerConnection peer = peerConnectionFactory.createPeerConnection(config, new PeerConnectionObserver() {

			@Override
			public void onIceConnectionChange(RTCIceConnectionState state) {
				switch (state) {
				case NEW:
					break;
				case CHECKING:
					break;
				case CONNECTED:
					break;
				case COMPLETED:
					break;
				case FAILED:
					break;
				case DISCONNECTED:
					break;
				case CLOSED:
					UserContext.closeConnection(deviceCode);
					break;
				default:
					break;
				}
				if (notifyHandler != null) {
					notifyHandler.onNotify(ClientMessageUtil.buildNotifyEvent(NotifyType.CONNECT, null, null,
							new ConnectionMessage(deviceCode, state)));
				}

			}

			@Override
			public void onIceCandidate(RTCIceCandidate candidate) {
				WebRTCVo vo = new WebRTCVo();
				vo.setCode(UserContext.currentUser.getCode());
				vo.setType(WebRTCType.ICE.getValue());
				vo.setIceCandidate(CandidateVo.transfer(candidate));
				webSocket.send(ByteString.of(ClientMessageUtil
						.buildWebRTCMessage(WebRTCBodyType.REPLY.getCode(), userId, channel, vo).toByteArray()));
			}

			@Override
			public void onDataChannel(RTCDataChannel dataChannel) {
				if (dataChannel.getLabel().equals(UserContext.currentUser.getCode())) {
					UserContext.remoteChannels.put(deviceCode, dataChannel);
					dataChannel.registerObserver(new RTCDataChannelObserver() {

						@Override
						public void onBufferedAmountChange(long previousAmount) {

						}

						@Override
						public void onStateChange() {
							switch (dataChannel.getState()) {
							case CONNECTING:
								break;
							case OPEN:
								sendChannelOpenMessage(deviceCode);
								break;
							case CLOSING:
								break;
							case CLOSED:
								RTCPeerConnection connection = UserContext.connections.get(deviceCode);
								if (connection != null
										&& connection.getConnectionState().equals(RTCPeerConnectionState.CLOSED)) {
									UserContext.closeConnection(deviceCode);
									updateDownloadStatus(deviceCode);
								} else {
									dataChannel.unregisterObserver();
									dataChannel.dispose();
									UserContext.remoteChannels.remove(deviceCode);
								}
								break;
							default:
								break;
							}

						}

						@Override
						public void onMessage(RTCDataChannelBuffer buffer) {
							if (!buffer.binary) {
								ChannelMessage message = ClientMessageUtil.decodeChannelMessage(buffer.data);
								handleChannelMessage(deviceCode, message);
							} else {
								handleFileBytes(deviceCode, ClientMessageUtil.decodeBytes(buffer.data));
							}

						}
					});
				} else if (dataChannel.getLabel().equals("file")) {
					dataChannel.registerObserver(new RTCDataChannelObserver() {

						@Override
						public void onBufferedAmountChange(long previousAmount) {

						}

						@Override
						public void onStateChange() {
							switch (dataChannel.getState()) {
							case CONNECTING:
								break;
							case OPEN:
								break;
							case CLOSING:
								break;
							case CLOSED:
								RTCPeerConnection connection = UserContext.connections.get(deviceCode);
								if (connection != null
										&& connection.getConnectionState().equals(RTCPeerConnectionState.CLOSED)) {
									UserContext.closeConnection(deviceCode);
									updateDownloadStatus(deviceCode);
								} else {
									dataChannel.unregisterObserver();
									dataChannel.dispose();
								}
								break;
							default:
								break;
							}

						}

						@Override
						public void onMessage(RTCDataChannelBuffer buffer) {
							if (buffer.binary) {
								handleFileBytes(deviceCode, ClientMessageUtil.decodeBytes(buffer.data));
							} else {
								ChannelMessage message = ClientMessageUtil.decodeChannelMessage(buffer.data);
								handleChannelMessage(deviceCode, message);
							}
						}
					});
				}

			}
		});
		UserContext.connections.put(deviceCode, peer);
		UserContext.localChannels.put(deviceCode, peer.createDataChannel(deviceCode, new RTCDataChannelInit()));
		UserContext.fileChannels.put(deviceCode, peer.createDataChannel("file", new RTCDataChannelInit()));

	}

	public void sendFileMessage() {
		List<ShareFile> files = shareFileService.findAllShareFile();
		for (String deviceCode : UserContext.localChannels.keySet()) {
			sendFileMessage(files, deviceCode);
		}
	}

	private void sendChannelOpenMessage(String deviceCode) {
		sendPingMessage(deviceCode);
		List<ShareFile> files = shareFileService.findAllShareFile();
		sendFileMessage(files, deviceCode);
	}

	private void sendPingMessage(String deviceCode) {
		RTCDataChannel channel = UserContext.localChannels.get(deviceCode);
		if (channel != null) {
			JSONObject ping = new JSONObject();
			ping.put("code", UserContext.currentUser.getCode());
			ping.put("time", System.currentTimeMillis());
			ChannelMessage pingMessage = ClientMessageUtil.buildChannelMessage(ChannelMessageType.BASIC,
					ClientMessageUtil.buildBasicData(ChannelBasicType.BASIC, ping));
			try {
				channel.send(ClientMessageUtil.buildChannelBuffer(pingMessage));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}

		}
	}

	private void sendFileMessage(List<ShareFile> files, String deviceCode) {
		RTCDataChannel channel = UserContext.localChannels.get(deviceCode);
		if (channel != null) {
			List<FileInfo> list = new ArrayList<>();
			if (files != null && !files.isEmpty()) {
				for (ShareFile file : files) {
					FileInfo fileInfo = new FileInfo();
					fileInfo.setDevice(UserContext.currentUser.getCode());
					fileInfo.setName(file.getName());
					fileInfo.setSize(file.getSize());
					fileInfo.setId(file.getKey());
					fileInfo.setStatus(0);
					fileInfo.setFormat(file.getFormat());
					list.add(fileInfo);
				}
			}
			ChannelMessage fileList = ClientMessageUtil.buildChannelMessage(ChannelMessageType.BASIC,
					ClientMessageUtil.buildBasicData(ChannelBasicType.FILELIST, list));
			try {
				channel.send(ClientMessageUtil.buildChannelBuffer(fileList));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public void sendConfirmFileChannel(ChannelFileType type, FileInfo file) {
		if (ChannelFileType.ACCEPT.equals(type)) {
			DownloadFile download = downloadFileService.findFileByKey(file.getId());
			if (download == null) {
				download = new DownloadFile();
				download.setKey(file.getId());
				download.setChunks(0);
				download.setChunkSize(file.getChunkSize());
				download.setSize(file.getSize());
				download.setDevice(file.getDevice());
				download.setName(file.getName());
				download.setStartTime(new Date());
				download.setTotal(file.getTotal());
				download.setStatus(0);
				download.setType(DownloadFileType.ACCEPT.getCode());
				download.setPath(UserContext.getDownloadPath() + file.getName());
			} else {
				if (download.getStatus() == 2) {
					return;
				}
				if (download.getStatus() == 3) {
					download.setStatus(0);
				}
				if (download.getChunks() > 0) {
					file.setChunks(download.getChunks() - 1);
				}
				file.setChunkSize(download.getChunkSize());
			}
			downloadFileService.saveDownloadFile(download);
			UserContext.writeTask.put(file.getId(), download);
			if (currentDowload.get(file.getDevice()) != null) {
				type = ChannelFileType.CONFIRM;
			}
		}
		RTCDataChannel channel = UserContext.localChannels.get(file.getDevice());
		if (channel != null) {
			ChannelMessage message = ClientMessageUtil.buildChannelMessage(ChannelMessageType.FILE,
					ClientMessageUtil.buildFileData(type, file));
			try {
				channel.send(ClientMessageUtil.buildChannelBuffer(message));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	private boolean sendAcceptFileChannel(FileInfo file) {
		RTCDataChannel channel = UserContext.localChannels.get(file.getDevice());
		if (channel != null) {
			ChannelMessage message = ClientMessageUtil.buildChannelMessage(ChannelMessageType.FILE,
					ClientMessageUtil.buildFileData(ChannelFileType.ACCEPT, file));
			try {
				channel.send(ClientMessageUtil.buildChannelBuffer(message));
				return true;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return false;
	}

	private boolean sendDirFileChannel(FileInfo file) {
		RTCDataChannel channel = UserContext.localChannels.get(file.getDevice());
		if (channel != null) {
			ChannelMessage message = ClientMessageUtil.buildChannelMessage(ChannelMessageType.FILE,
					ClientMessageUtil.buildFileData(ChannelFileType.DIR, file));
			try {
				channel.send(ClientMessageUtil.buildChannelBuffer(message));
				return true;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return false;
	}

	public boolean sendStopDownloadChannel(FileInfo file) {

		boolean result = sendFileChannelMessage(ChannelBasicType.STOP, file);
		if (result) {
			DownloadFile download = UserContext.writeTask.get(file.getId());
			download.setStatus(3);
			downloadFileService.saveDownloadFile(download);
			UserContext.writeTask.remove(file.getId());
			RandomAccessFile f = writeFile.remove(file.getId());
			if (f != null) {
				try {
					f.close();
				} catch (IOException e) {

				}
			}
			FileInfo taskInfo = currentDowload.get(file.getDevice());
			if (taskInfo == null || file.getId().equals(taskInfo.getId())) {
				downloadNext(file.getDevice());
			}
		}
		return result;
	}

	public void removeUploadFile(String device, String key) {
		UploadFile upload = UserContext.readTask.remove(device + key);
		if (upload == null) {
			upload = uploadFileService.findFileByDeviceAndKey(device, key);
		}
		if (upload != null) {
			uploadFileService.deleteUploadFile(upload.getId());
		}
		FileInfo file = currentSend.get(device);
		if (file != null) {
			if (file.getId().equals(key)) {
				sendNextFileRequest(device);
			} else if (!UserContext.readTask.containsKey(device + file.getId())) {
				sendNextFileRequest(device);
			}

		}
	}

	public boolean sendDownloadChannel(FileInfo file) {
		DownloadFile download = downloadFileService.findFileByKey(file.getId());
		if (download.getStatus() == 2) {
			return false;
		}
		download.setStatus(0);
		downloadFileService.saveDownloadFile(download);
		UserContext.writeTask.put(file.getId(), download);
		if (currentDowload.get(file.getDevice()) != null) {
			return true;
		}
		if (download.getChunks() > 0) {
			file.setChunks(download.getChunks() - 1);
		}
		if (download.getType() == DownloadFileType.ACCEPT.getCode()) {
			return sendAcceptFileChannel(file);
		} else if (download.getType() == DownloadFileType.MANUAL.getCode()) {
			return sendFileChannelMessage(ChannelBasicType.DOWNLOAD, file);
		} else {
			return sendDirFileChannel(file);
		}

	}

	private boolean sendFileChannelMessage(ChannelBasicType type, FileInfo file) {

		RTCDataChannel channel = UserContext.localChannels.get(file.getDevice());
		if (channel != null) {
			ChannelMessage message = ClientMessageUtil.buildChannelMessage(ChannelMessageType.BASIC,
					ClientMessageUtil.buildBasicData(type, file));
			try {
				channel.send(ClientMessageUtil.buildChannelBuffer(message));
				return true;
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return false;
	}

	private void handleChannelMessage(String deviceCode, ChannelMessage message) {
		ChannelData data = message.getData();
		if (message.getType() == ChannelMessageType.BASIC.getCode()) {
			if (data.getType() == ChannelBasicType.BASIC.getCode()) {
				Map<String, Object> ping = (Map<String, Object>) data.getData();
				String code = (String) ping.get("code");
				long time = (Long) ping.get("time");
				if (UserContext.currentUser.getCode().equals(code)) {
					long rtt = System.currentTimeMillis() - time;
					UserContext.deviceMap.get(deviceCode).setRtt(rtt);
				} else {
					RTCDataChannel channel = UserContext.localChannels.get(deviceCode);
					try {
						channel.send(ClientMessageUtil.buildChannelBuffer(message));
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			} else if (data.getType() == ChannelBasicType.FILELIST.getCode()) {
				JSONArray array = (JSONArray) data.getData();
				UserContext.deviceMap.get(deviceCode).getFiles().clear();
				List<FileInfo> files = array.toJavaList(FileInfo.class);
				for (FileInfo file : files) {
					DownloadFile download = downloadFileService.findFileByKey(file.getId());
					if (download != null) {
						if (download.getStatus() == 1) {
							file.setStatus(2);
						} else {
							file.setStatus(1);
						}
					}
				}
				UserContext.deviceMap.get(deviceCode).getFiles().addAll(files);
			} else if (data.getType() == ChannelBasicType.DOWNLOAD.getCode()) {
				JSONObject obj = (JSONObject) data.getData();
				FileInfo file = obj.toJavaObject(FileInfo.class);
				ShareFile shareFile = shareFileService.findShareFileByKey(file.getId());
				if (shareFile != null) {
					UploadFile uf = UserContext.readTask.get(deviceCode + file.getId());
					if (uf == null) {
						uf = uploadFileService.findFileByDeviceAndKey(deviceCode, file.getId());
						if (uf == null) {
							uf = new UploadFile();
							uf.setChunks(0);
							uf.setDevice(deviceCode);
							uf.setStartTime(new Date());
							uf.setName(shareFile.getName());
							uf.setKey(shareFile.getKey());
							uf.setPath(shareFile.getPath());
							uf.setSize(shareFile.getSize());
							uf.setStatus(0);
							uf.setType(DownloadFileType.MANUAL.getCode());
							uploadFileService.saveUploadFile(uf);
						}
						UserContext.readTask.put(uf.getDevice() + uf.getKey(), uf);
					}
					executor.execute(new FileUploadBufferTask(shareFile, deviceCode, file.getChunkSize(),
							file.getChunks(), new ChunkReadListener() {

								@Override
								public void onRead(FileInfo chunk) {
									UploadFile uploadFile = UserContext.readTask.get(deviceCode + file.getId());
									uploadFile.setChunks(chunk.getCurrent() + 1);
									uploadFile.setTotal(chunk.getTotal());
									uploadFile.setChunkSize(chunk.getChunkSize());
									FileReadMessage fr = new FileReadMessage();
									fr.setFile(chunk);
									fr.setType(FileReadType.READ.getCode());
									if (notifyHandler != null) {
										notifyHandler.onNotify(
												ClientMessageUtil.buildNotifyEvent(NotifyType.UPLOAD, null, null, fr));
									}
								}

								@Override
								public void onClose(FileInfo chunk) {
									UploadFile uploadFile = UserContext.readTask.get(deviceCode + file.getId());
									uploadFile.setChunks(chunk.getCurrent());
									uploadFile.setTotal(chunk.getTotal());
									uploadFile.setChunkSize(chunk.getChunkSize());
									if (chunk.getCurrent() == chunk.getTotal()) {
										uploadFile.setStatus(2);
									} else {
										uploadFile.setStatus(3);
									}
									uploadFileService.saveUploadFile(uploadFile);
									UserContext.readTask.remove(deviceCode + file.getId());
									FileReadMessage fr = new FileReadMessage();
									fr.setFile(chunk);
									fr.setType(FileReadType.CLOSE.getCode());
									if (notifyHandler != null) {
										notifyHandler.onNotify(
												ClientMessageUtil.buildNotifyEvent(NotifyType.UPLOAD, null, null, fr));
									}
								}
							}));
				}
			} else if (data.getType() == ChannelBasicType.FILECHUNK.getCode()) {
				JSONObject obj = (JSONObject) data.getData();
				FileInfo file = obj.toJavaObject(FileInfo.class);
				currentDowload.put(deviceCode, file);
				DownloadFile downloadFile = UserContext.writeTask.get(file.getId());
				downloadFile.setChunkSize(file.getChunkSize());
				downloadFile.setTotal(file.getTotal());
				downloadFile.setStatus(1);
				downloadFile.setChunks(file.getChunks());
				downloadFileService.saveDownloadFile(downloadFile);
			} else if (data.getType() == ChannelBasicType.STOP.getCode()) {
				JSONObject obj = (JSONObject) data.getData();
				FileInfo file = obj.toJavaObject(FileInfo.class);
				if (file != null) {
					UserContext.uploadTasks.remove(deviceCode + file.getId());
				}
			}

		} else if (message.getType() == ChannelMessageType.FILE.getCode()) {
			JSONObject obj = (JSONObject) data.getData();
			FileInfo file = obj.toJavaObject(FileInfo.class);
			if (data.getType() == ChannelFileType.CHUNK.getCode()) {
				writeFileChunk(deviceCode, file);
			} else if (data.getType() == ChannelFileType.SEND.getCode()) {
				if (UserContext.currentUser.getConfirmType() == 1) {
					sendConfirmFileChannel(ChannelFileType.ACCEPT, file);
				} else if (UserContext.currentUser.getConfirmType() == 2) {
					sendConfirmFileChannel(ChannelFileType.REJECT, file);
				}
			} else if (data.getType() == ChannelFileType.ACCEPT.getCode()) {
				ShareFile shareFile = sendFile.get(file.getId());
				if (shareFile != null) {
					UploadFile uf = UserContext.readTask.get(deviceCode + file.getId());
					if (uf == null) {
						uf = uploadFileService.findFileByDeviceAndKey(deviceCode, file.getId());
						if (uf != null) {
							uf.setStatus(0);
						} else {
							return;
						}
						UserContext.readTask.put(uf.getDevice() + uf.getKey(), uf);
					}
					uploadFileService.saveUploadFile(uf);
					sendNextFileRequest(deviceCode);
					executor.execute(new FileUploadBufferTask(shareFile, deviceCode, file.getChunkSize(),
							file.getChunks(), new ChunkReadListener() {

								@Override
								public void onRead(FileInfo chunk) {
									UploadFile uploadFile = UserContext.readTask.get(deviceCode + file.getId());
									uploadFile.setChunks(chunk.getCurrent() + 1);
									uploadFile.setTotal(chunk.getTotal());
									uploadFile.setChunkSize(chunk.getChunkSize());
									FileReadMessage fr = new FileReadMessage();
									fr.setFile(chunk);
									fr.setType(FileReadType.READ.getCode());
									if (notifyHandler != null) {
										notifyHandler.onNotify(
												ClientMessageUtil.buildNotifyEvent(NotifyType.UPLOAD, null, null, fr));
									}
								}

								@Override
								public void onClose(FileInfo chunk) {
									UploadFile uploadFile = UserContext.readTask.get(deviceCode + file.getId());
									uploadFile.setChunks(chunk.getCurrent());
									uploadFile.setTotal(chunk.getTotal());
									uploadFile.setChunkSize(chunk.getChunkSize());
									if (chunk.getTotal() == chunk.getCurrent()) {
										uploadFile.setStatus(2);
										sendFile.remove(file.getId());
									} else {
										uploadFile.setStatus(3);
									}
									uploadFileService.saveUploadFile(uploadFile);
									UserContext.readTask.remove(deviceCode + file.getId());
									FileReadMessage fr = new FileReadMessage();
									fr.setFile(chunk);
									fr.setType(FileReadType.CLOSE.getCode());
									if (notifyHandler != null) {
										notifyHandler.onNotify(
												ClientMessageUtil.buildNotifyEvent(NotifyType.UPLOAD, null, null, fr));
									}
								}
							}));
				}
			} else if (data.getType() == ChannelFileType.REJECT.getCode()) {
				sendFile.remove(file.getId());
				UploadFile uf = UserContext.readTask.remove(deviceCode + file.getId());
				if (uf != null) {
					uploadFileService.deleteUploadFile(uf.getId());
				}
				sendNextFileRequest(deviceCode);
			} else if (data.getType() == ChannelFileType.CONFIRM.getCode()) {
				UploadFile uf = UserContext.readTask.get(deviceCode + file.getId());
				if (uf == null) {
					uf = uploadFileService.findFileByDeviceAndKey(deviceCode, file.getId());
					if (uf != null) {
						uf.setStatus(0);
					} else {
						return;
					}
					UserContext.readTask.put(uf.getDevice() + uf.getKey(), uf);
				}
				uploadFileService.saveUploadFile(uf);
				sendNextFileRequest(deviceCode);
			} else if (data.getType() == ChannelFileType.DIR.getCode()) {
				ShareFile shareFile = dirFile.get(file.getId());
				if (shareFile != null) {
					UploadFile uf = UserContext.readTask.get(deviceCode + file.getId());
					if (uf == null) {
						uf = uploadFileService.findFileByDeviceAndKey(deviceCode, file.getId());
						if (uf != null) {
							uf.setStatus(0);
						} else {
							return;
						}
						UserContext.readTask.put(uf.getDevice() + uf.getKey(), uf);
					}
					uploadFileService.saveUploadFile(uf);
					sendNextFileRequest(deviceCode);
					executor.execute(new FileUploadBufferTask(shareFile, deviceCode, file.getChunkSize(),
							file.getChunks(), new ChunkReadListener() {

								@Override
								public void onRead(FileInfo chunk) {
									UploadFile uploadFile = UserContext.readTask.get(deviceCode + file.getId());
									uploadFile.setChunks(chunk.getCurrent() + 1);
									uploadFile.setTotal(chunk.getTotal());
									uploadFile.setChunkSize(chunk.getChunkSize());
									FileReadMessage fr = new FileReadMessage();
									fr.setFile(chunk);
									fr.setType(FileReadType.READ.getCode());
									if (notifyHandler != null) {
										notifyHandler.onNotify(
												ClientMessageUtil.buildNotifyEvent(NotifyType.UPLOAD, null, null, fr));
									}
								}

								@Override
								public void onClose(FileInfo chunk) {
									UploadFile uploadFile = UserContext.readTask.get(deviceCode + file.getId());
									uploadFile.setChunks(chunk.getCurrent());
									uploadFile.setTotal(chunk.getTotal());
									uploadFile.setChunkSize(chunk.getChunkSize());
									if (chunk.getTotal() == chunk.getCurrent()) {
										uploadFile.setStatus(2);
										sendFile.remove(file.getId());
									} else {
										uploadFile.setStatus(3);
									}
									uploadFileService.saveUploadFile(uploadFile);
									UserContext.readTask.remove(deviceCode + file.getId());
									FileReadMessage fr = new FileReadMessage();
									fr.setFile(chunk);
									fr.setType(FileReadType.CLOSE.getCode());
									if (notifyHandler != null) {
										notifyHandler.onNotify(
												ClientMessageUtil.buildNotifyEvent(NotifyType.UPLOAD, null, null, fr));
									}
								}
							}));
				}
			}
		} else if (message.getType() == ChannelMessageType.DIR.getCode()) {
			handleDirMessage(deviceCode, data);
		} else if(message.getType() == ChannelMessageType.MEDIA.getCode()) {
			
		} else if(message.getType() == ChannelMessageType.CHAT.getCode()) {
			
		}
		if (notifyHandler != null) {
			notifyHandler.onNotify(ClientMessageUtil.buildNotifyEvent(NotifyType.CHANNEL, null, null, message));
		}

	}

	private void handleDirMessage(String deviceCode, ChannelData data) {
		try {
			JSONObject obj = (JSONObject) data.getData();
			DirMessage dirMsg = obj.toJavaObject(DirMessage.class);
			DirInfo info = dirMsg.getData();
			if (data.getType() == ChannelDirType.REQ.getCode()) {
				DirMessage res = new DirMessage();
				ChannelDirType type = ChannelDirType.DIR;
				if (StringUtils.isAllBlank(info.getRoot(), info.getPath())) {
					List<ShareDir> shareDirs = shareDirService.findAllShareDir();
					if (shareDirs != null && !shareDirs.isEmpty()) {
						List<DirInfo> children = new ArrayList<>();
						for (ShareDir dir : shareDirs) {
							children.add(DirInfo.transferToDirInfo(dir));
						}
						info.setChildren(children);
					}
				} else if (StringUtils.isNoneBlank(info.getRoot(), info.getPath())) {
					ShareDir root = shareDirService.findShareDirByKey(info.getRoot());
					if (root != null) {
						info.setChildren(FileInfoUtil.getDirInfoChildren(root, info));
					} else {
						res.setCode("404");
						res.setMessage("directory not exit");
						type = ChannelDirType.ERROR;
					}
				}
				res.setData(info);
				RTCDataChannel channel = UserContext.localChannels.get(deviceCode);
				try {
					channel.send(ClientMessageUtil.buildChannelBuffer(ClientMessageUtil
							.buildChannelMessage(ChannelMessageType.DIR, ClientMessageUtil.buildDirData(type, res))));
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			} else if (data.getType() == ChannelDirType.DIR.getCode()) {
				SubcribeVo vo = UserContext.deviceMap.get(deviceCode);
				if (StringUtils.isAllBlank(info.getRoot(), info.getPath())) {
					vo.getDir().setChildren(info.getChildren());
				} else {
					for (DirInfo root : vo.getDir().getChildren()) {
						if (root.getId().equals(info.getRoot())) {
							appendChild(root, info);
							break;
						}

					}
				}

			} else if (data.getType() == ChannelDirType.FILE.getCode()) {
				DirMessage res = new DirMessage();
				ChannelDirType type = ChannelDirType.CONFIRM;
				if (StringUtils.isNoneBlank(info.getRoot(), info.getPath())) {
					ShareDir root = shareDirService.findShareDirByKey(info.getRoot());
					if (root != null) {
						ShareFile shareFile = FileInfoUtil.getDirShareFile(root, info);
						if (shareFile != null) {
							dirFile.put(shareFile.getKey(), shareFile);
							UploadFile uf = uploadFileService.findFileByDeviceAndKey(deviceCode, shareFile.getKey());
							if (uf == null) {
								uf = new UploadFile();
							}
							uf.setChunks(0);
							uf.setDevice(deviceCode);
							uf.setStartTime(new Date());
							uf.setName(shareFile.getName());
							uf.setKey(shareFile.getKey());
							uf.setPath(shareFile.getPath());
							uf.setSize(shareFile.getSize());
							uf.setStatus(0);
							uf.setType(DownloadFileType.DIR.getCode());
							uploadFileService.saveUploadFile(uf);
							UserContext.readTask.put(deviceCode + uf.getKey(), uf);
						} else {
							res.setCode("404");
							res.setMessage("directory not exit");
							type = ChannelDirType.ERROR;
						}
					} else {
						res.setCode("404");
						res.setMessage("directory not exit");
						type = ChannelDirType.ERROR;
					}
				} else {
					res.setCode("404");
					res.setMessage("directory not exit");
					type = ChannelDirType.ERROR;
				}
				res.setData(info);
				RTCDataChannel channel = UserContext.localChannels.get(deviceCode);
				try {
					channel.send(ClientMessageUtil.buildChannelBuffer(ClientMessageUtil
							.buildChannelMessage(ChannelMessageType.DIR, ClientMessageUtil.buildDirData(type, res))));
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			} else if (data.getType() == ChannelDirType.CONFIRM.getCode()) {
				FileInfo file = new FileInfo();
				file.setChunks(0);
				file.setCurrent(0);
				file.setTotal(0);
				file.setDevice(info.getDevice());
				file.setId(info.getId());
				file.setName(info.getName());
				file.setSize(info.getSize());
				DownloadFile download = downloadFileService.findFileByKey(info.getId());
				if (download == null) {
					download = new DownloadFile();
					download.setKey(info.getId());
					download.setChunks(0);
					download.setChunkSize(0);
					download.setSize(info.getSize());
					download.setDevice(info.getDevice());
					download.setName(info.getName());
					download.setStartTime(new Date());
					download.setTotal(0);
					download.setStatus(0);
					download.setType(DownloadFileType.DIR.getCode());
					download.setPath(UserContext.getDownloadPath() + info.getName());
				} else {
					if (download.getStatus() == 2) {
						return;
					}
					if (download.getStatus() == 3) {
						download.setStatus(0);
					}
					if (download.getChunks() > 0) {
						file.setChunks(download.getChunks() - 1);
					}
					file.setChunkSize(download.getChunkSize());
				}
				downloadFileService.saveDownloadFile(download);
				UserContext.writeTask.put(info.getId(), download);
				if (currentDowload.get(info.getDevice()) == null) {
					RTCDataChannel channel = UserContext.localChannels.get(deviceCode);
					try {
						channel.send(ClientMessageUtil.buildChannelBuffer(ClientMessageUtil.buildChannelMessage(
								ChannelMessageType.FILE, ClientMessageUtil.buildFileData(ChannelFileType.DIR, file))));
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}

			} else if (data.getType() == ChannelDirType.ERROR.getCode()) {
				log.warn("dir error msg {}", data);

			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	public void sendDirMessage(ChannelDirType type, DirInfo dir) {
		RTCDataChannel channel = UserContext.localChannels.get(dir.getDevice());
		DirMessage res = new DirMessage();
		res.setData(dir);
		try {
			channel.send(ClientMessageUtil.buildChannelBuffer(ClientMessageUtil
					.buildChannelMessage(ChannelMessageType.DIR, ClientMessageUtil.buildDirData(type, res))));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private void appendChild(DirInfo parent, DirInfo node) {
		if (node.getPath().startsWith(parent.getPath())) {
			if (parent.getId().equals(node.getId())) {
				parent.setChildren(node.getChildren());
			} else {
				for (DirInfo child : parent.getChildren()) {
					appendChild(child, node);
				}
			}
		}

	}

	private void handleFileBytes(String deviceCode, byte[] buffer) {
		FileInfo info = currentDowload.get(deviceCode);
		if (info == null) {
			return;
		}
		DownloadFile file = UserContext.writeTask.get(info.getId());
		if (file == null) {
			file = downloadFileService.findFileByKey(info.getId());
			if (file == null) {
				return;
			}
			UserContext.writeTask.put(file.getKey(), file);
		} else if (file.getStatus() == 3) {
			return;
		}
		RandomAccessFile f = writeFile.get(info.getId());
		try {
			if (f == null) {
				f = new RandomAccessFile(file.getPath() + ".part", "rw");
				writeFile.put(info.getId(), f);
				long offset = info.getChunkSize() * info.getCurrent();
				if (offset > 0) {
					f.seek(offset);
				}
				file.setTotal(info.getTotal());
				info.setStatus(1);
			}
			f.write(buffer);
			info.setCurrent(info.getCurrent() + 1);
			info.setChunks(info.getChunks() + 1);
			file.setChunks(file.getChunks() + 1);
			if (file.getChunks() == file.getTotal()) {
				file.setFinishTime(new Date());
				file.setStatus(2);
				downloadFileService.saveDownloadFile(file);
				UserContext.writeTask.remove(info.getId());
				f.close();
				writeFile.remove(info.getId());
				File finishFile = new File(file.getPath() + ".part");
				finishFile.renameTo(new File(file.getPath()));
				updateSubscribVoFile(deviceCode, info);
				info.setStatus(2);
				downloadNext(deviceCode);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			try {
				if (f != null) {
					f.close();
				}
			} catch (IOException e1) {

			}
			writeFile.remove(info.getId());
			file.setStatus(3);
			info.setStatus(3);
			downloadFileService.saveDownloadFile(file);
		}
		if (notifyHandler != null) {

			notifyHandler.onNotify(ClientMessageUtil.buildNotifyEvent(NotifyType.CHANNEL, null, null,
					ClientMessageUtil.buildChannelMessage(ChannelMessageType.FILE,
							ClientMessageUtil.buildFileData(ChannelFileType.CHUNK, JSONObject.toJSON(info)))));
		}
	}

	private void downloadNext(String code) {
		currentDowload.remove(code);
		List<DownloadFile> files = downloadFileService.findDownloadFileByDevice(code, 0);
		if (files != null && !files.isEmpty()) {
			for (DownloadFile download : files) {
				DownloadFile data = UserContext.writeTask.get(download.getKey());
				FileInfo file = new FileInfo();
				file.setChunks(data.getChunks());
				file.setCurrent(data.getChunks() - 1);
				file.setTotal(data.getTotal());
				file.setDevice(data.getDevice());
				file.setId(data.getKey());
				file.setName(data.getName());
				file.setSize(data.getSize());
				downloadFileService.saveDownloadFile(data);
				if (download.getType() == DownloadFileType.ACCEPT.getCode()) {
					sendAcceptFileChannel(file);
				} else if (download.getType() == DownloadFileType.MANUAL.getCode()) {
					sendFileChannelMessage(ChannelBasicType.DOWNLOAD, file);
				} else {
					sendDirFileChannel(file);
				}
				break;
			}

		}
	}

	public void sendFileRequest(String deviceCode, File file) {
		ShareFile share = FileInfoUtil.transfer(file);
		sendFile.put(share.getKey(), share);
		UploadFile uf = new UploadFile();
		uf.setChunks(0);
		uf.setDevice(deviceCode);
		uf.setStartTime(new Date());
		uf.setName(share.getName());
		uf.setKey(share.getKey());
		uf.setPath(share.getPath());
		uf.setSize(share.getSize());
		uf.setStatus(-1);
		uf.setType(DownloadFileType.ACCEPT.getCode());
		uploadFileService.saveUploadFile(uf);
		FileInfo fileInfo = new FileInfo();
		fileInfo.setName(share.getName());
		fileInfo.setSize(share.getSize());
		fileInfo.setId(share.getKey());
		fileInfo.setStatus(0);
		fileInfo.setFormat(share.getFormat());
		if (currentSend.get(deviceCode) == null) {
			RTCDataChannel channel = UserContext.localChannels.get(deviceCode);
			fileInfo.setDevice(UserContext.currentUser.getCode());
			currentSend.put(deviceCode, fileInfo);
			if (channel != null) {
				try {
					ChannelMessage message = ClientMessageUtil.buildChannelMessage(ChannelMessageType.FILE,
							ClientMessageUtil.buildFileData(ChannelFileType.SEND, fileInfo));
					channel.send(ClientMessageUtil.buildChannelBuffer(message));
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
		UserContext.readTask.put(deviceCode + uf.getKey(), uf);
		if (notifyHandler != null) {
			FileReadMessage fr = new FileReadMessage();
			fileInfo.setDevice(deviceCode);
			fr.setFile(fileInfo);
			fr.setType(FileReadType.CREATE.getCode());
			notifyHandler.onNotify(ClientMessageUtil.buildNotifyEvent(NotifyType.UPLOAD, null, null, fr));
		}
	}

	private void sendNextFileRequest(String deviceCode) {
		restoreExecutor.schedule(() -> {
			currentSend.remove(deviceCode);
			List<UploadFile> files = uploadFileService.findUploadFileByDevice(deviceCode, -1);
			if (files != null && !files.isEmpty()) {
				RTCDataChannel channel = UserContext.localChannels.get(deviceCode);
				for (UploadFile uf : files) {
					ShareFile share = sendFile.get(uf.getKey());
					FileInfo fileInfo = new FileInfo();
					fileInfo.setName(share.getName());
					fileInfo.setSize(share.getSize());
					fileInfo.setId(share.getKey());
					fileInfo.setStatus(0);
					fileInfo.setDevice(UserContext.currentUser.getCode());
					fileInfo.setFormat(share.getFormat());
					currentSend.put(deviceCode, fileInfo);
					if (channel != null) {
						try {
							ChannelMessage message = ClientMessageUtil.buildChannelMessage(ChannelMessageType.FILE,
									ClientMessageUtil.buildFileData(ChannelFileType.SEND, fileInfo));
							channel.send(ClientMessageUtil.buildChannelBuffer(message));
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}
					break;
				}
			}
		}, 1, TimeUnit.SECONDS);

	}

	public void dowanloadFile(String deviceCode, FileInfo info) {
		DownloadFile file = downloadFileService.findFileByKey(info.getId());
		if (file == null) {
			file = new DownloadFile();
			file.setKey(info.getId());
			file.setChunks(0);
			file.setChunkSize(info.getChunkSize());
			file.setSize(info.getSize());
			file.setDevice(deviceCode);
			file.setName(info.getName());
			file.setStartTime(new Date());
			file.setTotal(info.getTotal());
			file.setStatus(0);
			file.setType(DownloadFileType.MANUAL.getCode());
			file.setPath(UserContext.getDownloadPath() + file.getName());
			downloadFileService.saveDownloadFile(file);
			UserContext.writeTask.put(file.getKey(), file);
		}
		if (currentDowload.get(deviceCode) == null) {
			RTCDataChannel channel = UserContext.localChannels.get(deviceCode);
			if (channel != null) {
				try {
					ChannelMessage message = ClientMessageUtil.buildChannelMessage(ChannelMessageType.BASIC,
							ClientMessageUtil.buildBasicData(ChannelBasicType.DOWNLOAD, info));
					channel.send(ClientMessageUtil.buildChannelBuffer(message));
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	private void writeFileChunk(String deviceCode, FileInfo info) {
		if (info == null) {
			return;
		}
		DownloadFile file = UserContext.writeTask.get(info.getId());
		if (file == null) {
			file = downloadFileService.findFileByKey(info.getId());
			if (file == null) {
				return;
			}
			UserContext.writeTask.put(file.getKey(), file);
		} else if (file.getStatus() == 2 || file.getStatus() == 3) {
			return;
		}
		RandomAccessFile f = writeFile.get(info.getId());
		try {
			if (f == null) {
				f = new RandomAccessFile(file.getPath() + ".part", "rw");
				writeFile.put(info.getId(), f);
				long offset = info.getChunkSize() * info.getCurrent();
				if (offset > 0) {
					f.seek(offset);
				}
				file.setTotal(info.getTotal());
			}
			f.write(Base64.decodeBase64(info.getChunk()));
			file.setChunks(file.getChunks() + 1);
			if (file.getChunks() == file.getTotal()) {
				file.setFinishTime(new Date());
				file.setStatus(2);
				downloadFileService.saveDownloadFile(file);
				UserContext.writeTask.remove(info.getId());
				f.close();
				writeFile.remove(info.getId());
				File finishFile = new File(file.getPath() + ".part");
				finishFile.renameTo(new File(file.getPath()));
				updateSubscribVoFile(deviceCode, info);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			try {
				if (f != null) {
					f.close();
				}
			} catch (IOException e1) {

			}
			writeFile.remove(info.getId());
			file.setStatus(3);
			downloadFileService.saveDownloadFile(file);
		}

	}

	private void updateSubscribVoFile(String deviceCode, FileInfo info) {
		SubcribeVo vo = UserContext.deviceMap.get(deviceCode);
		if (vo != null && vo.getFiles() != null) {
			for (FileInfo f : vo.getFiles()) {
				if (f.getId().equals(info.getId())) {
					f.setStatus(2);
				}
			}
		}
	}

	private void updateDownloadStatus(String deviceCode) {
		List<DownloadFile> files = downloadFileService.findDownloadFileByDevice(deviceCode, 1);
		List<DownloadFile> updates = new ArrayList<>();
		if (files != null && files.isEmpty()) {
			for (DownloadFile file : files) {
				file.setStatus(3);
				RandomAccessFile f = writeFile.remove(file.getKey());
				try {
					if (f != null) {
						f.close();
					}
				} catch (IOException e) {
				}
				DownloadFile update = UserContext.writeTask.remove(file.getKey());
				if (update != null) {
					update.setStatus(3);
					updates.add(update);
				} else {
					updates.add(file);
				}
			}
			downloadFileService.saveDownloadFiles(updates);
		}
	}
}

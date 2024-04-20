package ltd.finelink.tool.disk.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.context.ChannelContext;
import ltd.finelink.tool.disk.entity.torrent.MetaData;
import ltd.finelink.tool.disk.enums.CustomBodyType;
import ltd.finelink.tool.disk.enums.RoomType;
import ltd.finelink.tool.disk.enums.SubscribeBodyType;
import ltd.finelink.tool.disk.enums.WebRTCBodyType;
import ltd.finelink.tool.disk.protobuf.ClientBody;
import ltd.finelink.tool.disk.protobuf.ClientMessage;
import ltd.finelink.tool.disk.protobuf.ServerMessage;
import ltd.finelink.tool.disk.service.MessageService;
import ltd.finelink.tool.disk.service.TorrentService;
import ltd.finelink.tool.disk.service.torrent.IMetaDataService;
import ltd.finelink.tool.disk.utils.BTUtil;
import ltd.finelink.tool.disk.utils.IPUtil;
import ltd.finelink.tool.disk.utils.MessageUtils;
import ltd.finelink.tool.disk.utils.RandomStrUtils;
import ltd.finelink.tool.disk.utils.ThreadPoolExecutorUtil;
import ltd.finelink.tool.disk.vo.RoomInfoVo;
import ltd.finelink.tool.disk.vo.RoomUserVo;
import ltd.finelink.tool.disk.vo.UserChannelVo;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

	private Cache<String, UserChannelVo> subcribeCache = Caffeine.newBuilder().expireAfterWrite(2, TimeUnit.HOURS)
			.build();

	private Cache<String, RoomInfoVo> roomCache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

	private final Cache<String, Set<String>> magnetTask;

	private final TorrentService torrentService;

	private final IMetaDataService metaDataService;

	@Override
	public boolean receiveMessage(String userId, String type, ClientMessage message) {
		if (MessageUtils.isHeartbeat(message)) {
			Channel channel = ChannelContext.get(userId, type);
			channel.writeAndFlush(MessageUtils.buildHeartBeatMessage(message));
			return true;
		} else if (MessageUtils.isWEBRTC(message)) {
			handleWebRTC(userId, type, message);
		} else if (MessageUtils.isSubscribe(message)) {
			handleSubscribe(userId, type, message);
		} else if (MessageUtils.isCustom(message)) {
			handleCustom(userId, type, message);
		}

		return true;
	}

	@Override
	public boolean sendMessage(String userId, ServerMessage message) {
		Map<String, Channel> channels = ChannelContext.getMap(userId);
		if (channels == null) {
			return false;
		}
		for (String key : channels.keySet()) {
			Channel channel = channels.get(key);
			if (channel.isOpen()) {
				channel.writeAndFlush(message);
			} else {
				hanlerOfflineNotify(userId, key);
				channels.remove(key);
			}
		}
		return true;
	}

	@Override
	public boolean sendMessage(String userId, String type, ServerMessage message) {
		Channel ch = ChannelContext.get(userId, type);
		if (ch == null) {
			return false;
		} else if (!ch.isOpen()) {
			hanlerOfflineNotify(userId, type);
			ChannelContext.remove(userId, type);
			return false;
		}
		ch.writeAndFlush(message);
		return true;
	}

	private void sendIgnoreOffline(String userId, String type, ServerMessage message) {
		Channel ch = ChannelContext.get(userId, type);
		if (ch == null || !ch.isOpen()) {
			return;
		}
		ch.writeAndFlush(message);
	}

	private void handleWebRTC(String userId, String channelType, ClientMessage message) {

		if (message.getBodyCount() > 0) {
			ClientBody clientBody = message.getBody(0);
			ServerMessage sendMessage = MessageUtils.buildWebRTCMessage(userId, channelType, message);
			boolean sendResult = false;
			if (StringUtils.isBlank(clientBody.getSub()) && clientBody.getType() == WebRTCBodyType.APPLY.getCode()) {
				sendResult = sendMessage(clientBody.getToId(), sendMessage);
			} else {
				sendResult = sendMessage(clientBody.getToId(), clientBody.getSub(), sendMessage);
			}
			if (!sendResult) {

				sendMessage(userId, channelType, MessageUtils.buildWebRTCOffLineMessage(message));
			}

		}
	}

	private void handleSubscribe(String userId, String channelType, ClientMessage message) {
		String code = null;
		String result = null;
		if (message.getBodyCount() > 0) {
			ClientBody clientBody = message.getBody(0);
			if (clientBody.getContent() != null && clientBody.getContent().size() > 0) {
				try {
					String content = new String(clientBody.getContent().toByteArray(), "UTF-8");
					JSONObject data = JSONObject.parseObject(content);
					code = data.getString("code");
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
			if (clientBody.getType() == SubscribeBodyType.CREATE.getCode()) {
				if (StringUtils.isBlank(code)) {
					code = RandomStrUtils.randomStr(6);
				}else {
					unSubMyDevice(code,userId);
				}
				UserChannelVo vo = new UserChannelVo();
				vo.setCode(code);
				vo.setUserId(userId);
				vo.setChannel(channelType);
				vo.setOnline(1);
				vo.setVersion(ChannelContext.getVersion(userId, channelType));
				vo.setDevice(ChannelContext.getDeviceType(userId, channelType));
				ChannelContext.setDeviceCode(userId, channelType, code);
				result = JSON.toJSONString(vo);
				subcribeCache.put(code, vo);
				hanlerOnlineNotify(userId, channelType);
			} else if (clientBody.getType() == SubscribeBodyType.SUB.getCode()) {
				UserChannelVo vo = subcribeCache.getIfPresent(code);
				if (vo != null) {
					result = JSON.toJSONString(vo);
					String deviceCode = ChannelContext.getDeviceCode(userId, channelType);
					if (StringUtils.isNotBlank(deviceCode)) {
						ChannelContext.addSubcribeDevice(code, deviceCode);
						ChannelContext.addSubcribeDevice(deviceCode, code);
						UserChannelVo subVo = subcribeCache.getIfPresent(deviceCode);
						if (subVo != null) {
							sendMessage(vo.getUserId(), vo.getChannel(),
									MessageUtils.buildSubscribeRespMessage(JSON.toJSONString(subVo), message));
						}
					}
				}

			} else if (clientBody.getType() == SubscribeBodyType.UNSUB.getCode()) {
				UserChannelVo vo = subcribeCache.getIfPresent(code);
				String deviceCode = ChannelContext.getDeviceCode(userId, channelType);
				ChannelContext.removeSubscribeDevice(code, ChannelContext.getDeviceCode(userId, deviceCode));
				result = JSON.toJSONString(vo);
				if (StringUtils.isNotBlank(deviceCode)) {
					ChannelContext.removeSubscribeDevice(deviceCode, code);
					UserChannelVo subVo = subcribeCache.getIfPresent(deviceCode);
					if (subVo != null) {
						sendMessage(vo.getUserId(), vo.getChannel(),
								MessageUtils.buildSubscribeRespMessage(JSON.toJSONString(subVo), message));
					}
				}
			} else if (clientBody.getType() == SubscribeBodyType.CLOSE.getCode()) {
				UserChannelVo vo = subcribeCache.getIfPresent(code);
				if (vo != null && vo.getChannel().equals(channelType) && vo.getUserId().equals(userId)) {
					Set<String> codes = ChannelContext.getSubscribeDevice(code);
					if (codes != null) {
						for (String device : codes) {
							UserChannelVo sub = subcribeCache.getIfPresent(device);
							if (sub != null) {
								ChannelContext.removeSubscribeDevice(device, code);
								if (sub.getOnline() == 1) {
									sendMessage(sub.getUserId(), sub.getChannel(),
											MessageUtils.buildUnSubscribeMessage(JSON.toJSONString(vo)));

								}
								sendMessage(userId, channelType,
										MessageUtils.buildUnSubscribeMessage(JSON.toJSONString(sub)));
							}
						}
					}
					String ip = ChannelContext.getClientIp(userId, channelType);
					Set<String> locals = ChannelContext.getIpSubscribeDevice(ip);
					if (locals != null) {
						for (String local : locals) {
							if (code.equals(local)) {
								continue;
							}
							UserChannelVo sub = subcribeCache.getIfPresent(local);
							if (sub != null) {
								if (sub.getOnline() == 1) {
									sendMessage(sub.getUserId(), sub.getChannel(),
											MessageUtils.buildUnSubscribeMessage("local", JSON.toJSONString(vo)));
								}
							}

						}
					}
					Set<String> myDevice = ChannelContext.getOtherDeviceCode(userId, channelType);
					if (myDevice != null&&!myDevice.isEmpty()) {
						for (String local : myDevice) { 
							UserChannelVo sub = subcribeCache.getIfPresent(local);
							if (sub != null) {
								if (sub.getOnline() == 1) {
									sendMessage(sub.getUserId(), sub.getChannel(),
											MessageUtils.buildUnSubscribeMessage("myself", JSON.toJSONString(vo)));
								}
							}

						}
					}
					subcribeCache.invalidate(code);
					ChannelContext.removeSubcribe(code);
					ChannelContext.removeIpDevice(ip, code);
					result = JSON.toJSONString(vo);
				} else {
					sendMessage(userId, channelType, MessageUtils.buildRejectSubscribeRespMessage(message));
					return;
				}

			}
			sendMessage(userId, channelType, MessageUtils.buildSubscribeRespMessage(result, message));
		}
	}
	
	private void unSubMyDevice(String code,String userId) {
		UserChannelVo vo = subcribeCache.getIfPresent(code);
		if(vo!=null&&!vo.getUserId().equals(userId)) {
			Set<String> myDevice = ChannelContext.getOtherDeviceCode(vo.getUserId(), vo.getChannel());
			if (myDevice != null&&!myDevice.isEmpty()) {
				for (String local : myDevice) { 
					UserChannelVo sub = subcribeCache.getIfPresent(local);
					if (sub != null) {
						if (sub.getOnline() == 1) {
							sendMessage(sub.getUserId(), sub.getChannel(),
									MessageUtils.buildUnSubscribeMessage("myself", JSON.toJSONString(vo)));
						}
					}

				}
			}
		}
	}

	@Override
	public boolean hanlerOnlineNotify(String userId, String type) {
		String deviceCode = ChannelContext.getDeviceCode(userId, type);
		if (StringUtils.isNotBlank(deviceCode)) {
			UserChannelVo vo = subcribeCache.getIfPresent(deviceCode);
			if (vo != null) {
				vo.setOnline(1);
				subcribeCache.put(deviceCode, vo);
				Set<String> codes = ChannelContext.getSubscribeDevice(deviceCode);
				if (codes != null) {
					for (String code : codes) {
						UserChannelVo sub = subcribeCache.getIfPresent(code);
						if (sub != null) {
							if (sub.getOnline() == 1) {
								sendMessage(sub.getUserId(), sub.getChannel(),
										MessageUtils.buildSubscribeMessage(JSON.toJSONString(vo)));
							}
							sendMessage(userId, type, MessageUtils.buildSubscribeRespMessage(JSON.toJSONString(sub)));
						}
					}
				}
				Set<String> locals = ChannelContext.getIpSubscribeDevice(ChannelContext.getClientIp(userId, type));
				if (locals != null) {
					for (String code : locals) {
						if (code.equals(deviceCode)) {
							continue;
						}
						UserChannelVo sub = subcribeCache.getIfPresent(code);
						if (sub != null) {
							if (sub.getOnline() == 1) {
								sendMessage(sub.getUserId(), sub.getChannel(),
										MessageUtils.buildSubscribeMessage("local", JSON.toJSONString(vo)));
							}
							sendMessage(userId, type,
									MessageUtils.buildSubscribeMessage("local", JSON.toJSONString(sub)));
						}

					}
				}
				Set<String> myDevices = ChannelContext.getOtherDeviceCode(userId, type);
				if (myDevices != null&&!myDevices.isEmpty()) {
					for (String code : myDevices) {
						UserChannelVo sub = subcribeCache.getIfPresent(code);
						if (sub != null) {
							if (sub.getOnline() == 1) {
								sendMessage(sub.getUserId(), sub.getChannel(),
										MessageUtils.buildSubscribeMessage("myself", JSON.toJSONString(vo)));
							}
							sendMessage(userId, type,
									MessageUtils.buildSubscribeMessage("myself", JSON.toJSONString(sub)));
						}

					}
				}
				return true;
			}

		}
		return false;
	}

	@Override
	public boolean hanlerOfflineNotify(String userId, String type) {
		String deviceCode = ChannelContext.getDeviceCode(userId, type);
		if (StringUtils.isNotBlank(deviceCode)) {
			UserChannelVo vo = subcribeCache.getIfPresent(deviceCode);
			if (vo != null) {
				vo.setOnline(0);
				subcribeCache.put(deviceCode, vo);
				Set<String> codes = ChannelContext.getSubscribeDevice(deviceCode);
				if (codes != null) {
					for (String code : codes) {
						UserChannelVo sub = subcribeCache.getIfPresent(code);
						if (sub != null) {
							sendIgnoreOffline(sub.getUserId(), sub.getChannel(),
									MessageUtils.buildSubscribeMessage(JSON.toJSONString(vo)));
						}
					}
				}
				ChannelContext.removeIpDevice(ChannelContext.getClientIp(userId, type), deviceCode);
				Set<String> locals = ChannelContext.getIpSubscribeDevice(ChannelContext.getClientIp(userId, type));
				if (locals != null) {
					for (String code : locals) {
						UserChannelVo sub = subcribeCache.getIfPresent(code);
						if (sub != null) {
							if (sub.getOnline() == 1) {
								sendIgnoreOffline(sub.getUserId(), sub.getChannel(),
										MessageUtils.buildSubscribeMessage("local", JSON.toJSONString(vo)));
							}
							sendIgnoreOffline(userId, type,
									MessageUtils.buildSubscribeMessage("local", JSON.toJSONString(sub)));
						}

					}
				}
				Set<String> myDevices = ChannelContext.getOtherDeviceCode(userId, type);
				if (myDevices != null&&!myDevices.isEmpty()) {
					for (String code : myDevices) {
						UserChannelVo sub = subcribeCache.getIfPresent(code);
						if (sub != null) {
							if (sub.getOnline() == 1) {
								sendIgnoreOffline(sub.getUserId(), sub.getChannel(),
										MessageUtils.buildSubscribeMessage("myself", JSON.toJSONString(vo)));
							}
							sendIgnoreOffline(userId, type,
									MessageUtils.buildSubscribeMessage("myself", JSON.toJSONString(sub)));
						}

					}
				}
				return true;
			}

		}
		return false;
	}

	@Override
	public boolean broadcastMessage(ServerMessage message) {
		if (message != null) {
			ThreadPoolExecutorUtil.submit(() -> {
				for (String userId : ChannelContext.getAllUserId()) {
					Map<String, Channel> channels = ChannelContext.getMap(userId);
					if (channels == null) {
						return;
					}
					for (String key : channels.keySet()) {
						Channel channel = channels.get(key);
						if (channel.isOpen()) {
							channel.writeAndFlush(message);
						} else {
							hanlerOfflineNotify(userId, key);
							channels.remove(key);
						}
					}
				}
			});
			return true;
		}
		return false;
	}

	private void handleCustom(String userId, String channel, ClientMessage message) {
		if (message.getBodyCount() > 0) {
			ClientBody clientBody = message.getBody(0);
			if (clientBody.getType() == CustomBodyType.TORRENT.getCode()) {
				String hashId = clientBody.getToId();
				MetaData data = metaDataService.findByHash(hashId);
				if (data == null) {
					addMagnetTask(userId, channel, hashId);
				} else {
					sendMessage(userId, channel,
							MessageUtils.buildTorrentMessage(hashId, BTUtil.pareseMetadata(data.getData())));
				}

			} else if (clientBody.getType() == CustomBodyType.ROOM.getCode()) {
				RoomInfoVo vo = null;
				try {
					String content = new String(clientBody.getContent().toByteArray(), "UTF-8");
					JSONObject data = JSONObject.parseObject(content);
					vo = data.toJavaObject(RoomInfoVo.class);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
				if (vo == null) {
					return;
				}
				String nickname = ChannelContext.getDeviceCode(userId, channel);
				if (RoomType.CREATE.getCode().equals(clientBody.getMsgType())) {
					if (StringUtils.isNotBlank(vo.getName())) {
						vo.setOwnerId(userId);
						vo.setCode(RandomStrUtils.randomIntStr(8));
						List<RoomUserVo> users = new ArrayList<>();
						RoomUserVo user = new RoomUserVo();
						user.setChannel(channel);
						user.setUserId(userId);
						user.setDevice(ChannelContext.getDeviceType(userId, channel));
						user.setRegion(IPUtil.getCityInfo(ChannelContext.getClientIp(userId, channel)));
						user.setNickname(nickname);
						user.setLeave(true);
						users.add(user);
						vo.setUsers(users);
						roomCache.put(vo.getCode(), vo);
						sendMessage(userId, channel,
								MessageUtils.buildRoomMessage(RoomType.INFO.getCode(), JSON.toJSONString(vo)));
					}
				} else if (RoomType.VERIFY.getCode().equals(clientBody.getMsgType())) {
					if (StringUtils.isNoneBlank(vo.getCode(), vo.getPassword())) {
						RoomInfoVo room = roomCache.getIfPresent(vo.getCode());
						boolean update = false;
						if (room != null) {
							if (vo.getPassword().equals(room.getPassword())) {
								for (RoomUserVo user : room.getUsers()) {
									if (user.getUserId().equals(userId) && user.getChannel().equals(channel)) {
										user.setDevice(ChannelContext.getDeviceType(userId, channel));
										user.setRegion(IPUtil.getCityInfo(ChannelContext.getClientIp(userId, channel)));
										user.setLeave(false);
										if (StringUtils.isNotBlank(nickname)) {
											user.setNickname(nickname);
										}
										update = true;
										break;
									}
								}
								if (!update) {
									RoomUserVo user = new RoomUserVo();
									user.setChannel(channel);
									user.setUserId(userId);
									user.setDevice(ChannelContext.getDeviceType(userId, channel));
									user.setRegion(IPUtil.getCityInfo(ChannelContext.getClientIp(userId, channel)));
									user.setNickname(nickname);
									user.setLeave(false);
									room.getUsers().add(user);
									notifyRoomUpdate(room);
								} else {
									sendMessage(userId, channel, MessageUtils.buildRoomMessage(RoomType.INFO.getCode(),
											JSON.toJSONString(room)));
								}
								roomCache.put(vo.getCode(), room);
							} else {
								sendMessage(userId, channel, MessageUtils.buildRoomMessage(RoomType.VERIFY.getCode(),
										JSON.toJSONString(vo)));
							}
						} else {
							sendMessage(userId, channel, MessageUtils.buildRoomErrorMessage(RoomType.VERIFY.getCode(),
									"404", "ROOM NOT EXIST"));
						}
					}

				} else if (RoomType.JOIN.getCode().equals(clientBody.getMsgType())) {
					if (StringUtils.isNoneBlank(vo.getCode())) {
						RoomInfoVo room = roomCache.getIfPresent(vo.getCode());
						boolean update = false;
						if (room != null) {
							for (RoomUserVo user : room.getUsers()) {
								if (user.getUserId().equals(userId) && user.getChannel().equals(channel)) {
									user.setDevice(ChannelContext.getDeviceType(userId, channel));
									user.setRegion(IPUtil.getCityInfo(ChannelContext.getClientIp(userId, channel)));
									if (StringUtils.isNotBlank(nickname)) {
										user.setNickname(nickname);
									}
									user.setLeave(false);
									update = true;
									break;
								}
							}
							if (StringUtils.isNotBlank(room.getPassword())) {
								if (update) {
									sendMessage(userId, channel, MessageUtils.buildRoomMessage(RoomType.INFO.getCode(),
											JSON.toJSONString(room)));
								} else {
									sendMessage(userId, channel, MessageUtils
											.buildRoomMessage(RoomType.VERIFY.getCode(), JSON.toJSONString(vo)));
								}
							} else {
								if (update) {
									sendMessage(userId, channel, MessageUtils.buildRoomMessage(RoomType.INFO.getCode(),
											JSON.toJSONString(room)));
								} else {
									RoomUserVo user = new RoomUserVo();
									user.setChannel(channel);
									user.setUserId(userId);
									user.setDevice(ChannelContext.getDeviceType(userId, channel));
									user.setRegion(IPUtil.getCityInfo(ChannelContext.getClientIp(userId, channel)));
									user.setNickname(nickname);
									user.setLeave(false);
									room.getUsers().add(user);
									notifyRoomUpdate(room);
								}

							}
							roomCache.put(vo.getCode(), room);
						} else {
							sendMessage(userId, channel, MessageUtils.buildRoomErrorMessage(RoomType.JOIN.getCode(),
									"404", "ROOM NOT EXIST"));
						}
					}
				} else if (RoomType.LEAVE.getCode().equals(clientBody.getMsgType())) {
					if (StringUtils.isNoneBlank(vo.getCode())) {
						RoomInfoVo room = roomCache.getIfPresent(vo.getCode());
						boolean update = false;
						if (room != null) {
							for (RoomUserVo user : room.getUsers()) {
								if (user.getUserId().equals(userId) && user.getChannel().equals(channel)) {
									user.setDevice(ChannelContext.getDeviceType(userId, channel));
									user.setRegion(IPUtil.getCityInfo(ChannelContext.getClientIp(userId, channel)));
									user.setLeave(true);
									if (StringUtils.isNotBlank(nickname)) {
										user.setNickname(nickname);
									}
									update = true;
									break;
								}
							}
							if (update) {
								notifyRoomUpdate(room);
								roomCache.put(vo.getCode(), room);
							}
						}
					}
				} else if (RoomType.EXIT.getCode().equals(clientBody.getMsgType())) {
					if (StringUtils.isNoneBlank(vo.getCode())) {
						RoomInfoVo room = roomCache.getIfPresent(vo.getCode());
						boolean exist = false;
						if (room != null) {
							int index = 0;
							for (RoomUserVo user : room.getUsers()) {
								if (user.getUserId().equals(userId) && user.getChannel().equals(channel)) {
									exist = true;
									break;
								}
								index++;
							}
							if (exist) {
								room.getUsers().remove(index);
								sendMessage(userId, channel,
										MessageUtils.buildRoomMessage(RoomType.EXIT.getCode(), JSON.toJSONString(vo)));
								notifyRoomUpdate(room);
							}
							roomCache.put(vo.getCode(), room);
						} else {
							sendMessage(userId, channel, MessageUtils.buildRoomErrorMessage(RoomType.EXIT.getCode(),
									"404", "ROOM NOT EXIST"));
						}
					}
				} else if (RoomType.CLOSE.getCode().equals(clientBody.getMsgType())) {
					if (StringUtils.isNoneBlank(vo.getCode())) {
						RoomInfoVo room = roomCache.getIfPresent(vo.getCode());
						if (room != null) {
							if (room.getOwnerId().equals(userId)) {
								ServerMessage serverMessage = MessageUtils.buildRoomMessage(RoomType.CLOSE.getCode(),
										JSON.toJSONString(room));
								for (RoomUserVo user : room.getUsers()) {
									sendMessage(user.getUserId(), user.getChannel(), serverMessage);
								}
								roomCache.invalidate(vo.getCode());
							} else {
								sendMessage(userId, channel, MessageUtils
										.buildRoomErrorMessage(RoomType.CLOSE.getCode(), "403", "No Permission"));
							}
						} else {
							sendMessage(userId, channel, MessageUtils.buildRoomErrorMessage(RoomType.CLOSE.getCode(),
									"404", "ROOM NOT EXIST"));
						}
					}
				}
			}

		}
	}

	private void notifyRoomUpdate(RoomInfoVo room) {
		if (room != null && !room.getUsers().isEmpty()) {
			List<RoomUserVo> offline = new ArrayList<>();
			ServerMessage message = MessageUtils.buildRoomMessage(RoomType.INFO.getCode(), JSON.toJSONString(room));
			for (RoomUserVo user : room.getUsers()) {
				boolean result = sendMessage(user.getUserId(), user.getChannel(), message);
				if (!result) {
					offline.add(user);
				}
			}
			if (!offline.isEmpty()) {
				room.getUsers().removeAll(offline);
			}
		}

	}

	private void addMagnetTask(String userId, String channel, String hashId) {
		if (StringUtils.isNotBlank(hashId) && (hashId.length() == 40 || hashId.length() == 32)) {
			Set<String> users = magnetTask.getIfPresent(hashId);
			String task = userId + ":" + channel;
			if (users == null) {
				users = new HashSet<>();
			}
			if (users.contains(hashId)) {
				return;
			}
			users.add(task);
			magnetTask.put(hashId, users);
			torrentService.fetchMagnetTask(hashId);
		}
	}

}

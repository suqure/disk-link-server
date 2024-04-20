package ltd.finelink.tool.disk.context;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.constants.ConstantsKey;

@Slf4j
public class ChannelContext {

	private static final AttributeKey<String> USER_ID = AttributeKey.valueOf(ConstantsKey.USER_ID);
	private static final AttributeKey<String> TYPE = AttributeKey.valueOf(ConstantsKey.TYPE);

	private static final AttributeKey<String> VERSION = AttributeKey.valueOf(ConstantsKey.SDK_VERSION);

	private static final AttributeKey<String> REAL_IP = AttributeKey.valueOf(ConstantsKey.REAL_IP);

	private static final AttributeKey<String> DEVICE_CODE = AttributeKey.valueOf(ConstantsKey.DEVICE_CODE);

	private static final AttributeKey<String> DEVICE_TYPE = AttributeKey.valueOf(ConstantsKey.DEVICE_TYPE);

	private static ConcurrentHashMap<String, ConcurrentHashMap<String, Channel>> channelMap = new ConcurrentHashMap<>();

	private static Cache<String, Set<String>> subDevices = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
			.build();

	private static Cache<String, Integer> feebackRecord = Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES)
			.build();

	private static Cache<String, Set<String>> ipSubDevices = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
			.build();

	private static volatile Channel dhtChannel;

	public static void setAttribute(String userId, String type, String version, Channel channel) {
		if (channel != null) {
			channel.attr(USER_ID).set(userId);
			channel.attr(TYPE).set(type);
			channel.attr(VERSION).set(version);
		}
	}

	public static void setRealIp(String realIp, Channel channel) {
		setAttr(channel, REAL_IP, realIp);
	}

	public static void setRealIp(String userId, String type, String realIp) {
		setAttr(get(userId, type), REAL_IP, realIp);
	}

	public static void setDeviceType(String deviceType, Channel channel) {
		setAttr(channel, DEVICE_TYPE, deviceType);
	}

	public static void setDeviceType(String userId, String type, String deviceType) {
		setAttr(get(userId, type), DEVICE_TYPE, deviceType);
	}

	public static void setDeviceCode(String deviceCode, Channel channel) {
		setAttr(channel, DEVICE_CODE, deviceCode);
	}

	public static boolean addSubcribeDevice(String deviceCode, String subDeviceCode) {
		Set<String> subList = subDevices.getIfPresent(deviceCode);
		if (subList == null) {
			subList = new HashSet<>();
		}

		subList.add(subDeviceCode);
		subDevices.put(deviceCode, subList);
		return true;
	}

	public static boolean addIpDevice(String ip, String subDeviceCode) {
		Set<String> devices = ipSubDevices.getIfPresent(ip);
		if (devices == null) {
			devices = new HashSet<>();
		}
		devices.add(subDeviceCode);
		ipSubDevices.put(ip, devices);
		return true;
	}

	public static boolean removeSubcribe(String deviceCode) {
		subDevices.invalidate(deviceCode);
		return true;
	}

	public static boolean removeIpDevice(String ip) {
		ipSubDevices.invalidate(ip);
		return true;
	}

	public static boolean removeSubscribeDevice(String deviceCode, String subDeviceCode) {
		Set<String> subList = subDevices.getIfPresent(deviceCode);
		if (subList != null) {
			return subList.remove(subDeviceCode);
		}
		return false;
	}

	public static boolean removeIpDevice(String ip, String subDeviceCode) {
		Set<String> subList = ipSubDevices.getIfPresent(ip);
		if (subList != null) {
			return subList.remove(subDeviceCode);
		}
		return false;
	}

	public static Set<String> getSubscribeDevice(String deviceCode) {
		return subDevices.getIfPresent(deviceCode);
	}

	public static Set<String> getIpSubscribeDevice(String ip) {
		return ipSubDevices.getIfPresent(ip);
	}

	public static boolean setDeviceCode(String userId, String type, String deviceCode) {
		addIpDevice(getClientIp(userId, type), deviceCode);
		return setAttr(get(userId, type), DEVICE_CODE, deviceCode);
	}

	public static String getUserId(Channel channel) {
		return getAttr(channel, USER_ID);
	}

	public static String getType(Channel channel) {
		return getAttr(channel, TYPE);
	}

	public static String getVersion(Channel channel) {
		return getAttr(channel, VERSION);
	}

	public static String getVersion(String userId, String type) {
		return getAttr(get(userId, type), VERSION);
	}

	public static String getRealIp(Channel channel) {
		return getAttr(channel, REAL_IP);
	}

	public static String getRealIp(String userId, String type) {
		return getAttr(get(userId, type), REAL_IP);
	}

	public static String getDeviceType(Channel channel) {
		return getAttr(channel, DEVICE_TYPE);
	}

	public static String getDeviceType(String userId, String type) {
		return getAttr(get(userId, type), DEVICE_TYPE);
	}

	public static String getDeviceCode(Channel channel) {
		return getAttr(channel, DEVICE_CODE);
	}

	public static String getDeviceCode(String userId, String type) {
		return getAttr(get(userId, type), DEVICE_CODE);
	}

	public static Set<String> getOtherDeviceCode(String userId, String type) {
		Set<String> myDevices = new HashSet<>();
		ConcurrentHashMap<String, Channel> channels = channelMap.get(userId);
		if (channels!=null&&!channels.isEmpty()) {
			for (String key : channels.keySet()) {
				if (!key.equals(type)) {
					myDevices.add(getDeviceCode(channels.get(key)));
				}
			}
		}
		return myDevices;
	}

	public static <T> T getAttr(Channel channel, AttributeKey<T> attrKey) {
		if (channel != null) {
			Attribute<T> attr = channel.attr(attrKey);
			if (attr != null) {
				return attr.get();
			}
		}
		return null;
	}

	public static <T> boolean setAttr(Channel channel, AttributeKey<T> attrKey, T value) {
		if (channel != null) {
			channel.attr(attrKey).set(value);
			return true;
		}
		return false;
	}

	public static String getClientAddress(Channel channel) {
		if (channel != null) {
			return channel.remoteAddress().toString().replaceFirst("/", "");
		}
		return null;

	}

	public static String getClientIp(String userId, String type) {

		return getClientIp(get(userId, type));
	}

	public static String getClientIp(Channel channel) {
		String realIp = getRealIp(channel);
		if (StringUtils.isNotBlank(realIp)) {
			return realIp;
		}
		String address = getClientAddress(channel);
		if (address.contains(":")) {
			return address.substring(0, address.lastIndexOf(":"));
		}
		return address;
	}

	public static void add(String userId, String type, Channel channel) {
		ConcurrentHashMap<String, Channel> map = channelMap.get(userId);
		if (map == null) {
			map = new ConcurrentHashMap<>();
			channelMap.put(userId, map);
		}
		map.put(type, channel);
	}

	public static void remove(String userId, String type) {
		ConcurrentHashMap<String, Channel> map = channelMap.get(userId);
		if (!CollectionUtils.isEmpty(map)) {
			Channel channel = map.remove(type);
			if (channel != null) {
				channel.close();
			}
			if (CollectionUtils.isEmpty(map)) {
				channelMap.remove(userId);
			}
		}
	}

	public static void remove(String userId, String type, String version) {
		ConcurrentHashMap<String, Channel> map = channelMap.get(userId);
		if (!map.isEmpty() && map.containsKey(type) && map.get(type).attr(VERSION).get().equals(version)) {
			Channel channel = map.remove(type);
			if (channel != null) {
				channel.close();
			}
			if (CollectionUtils.isEmpty(map)) {
				channelMap.remove(userId);
			}
		}
	}

	public static void remove(String userId) {
		ConcurrentHashMap<String, Channel> map = channelMap.remove(userId);
		if (map != null && !map.isEmpty()) {
			for (Channel channel : map.values()) {
				channel.close();
			}
		}
	}

	public static Channel get(String userId, String type) {
		ConcurrentHashMap<String, Channel> map = channelMap.get(userId);
		if (!CollectionUtils.isEmpty(map)) {
			return map.get(type);
		}
		return null;
	}

	public static Channel get(String userId, String type, String version) {
		Channel channel = get(userId, type);
		if (null != channel && channel.attr(VERSION).get().equals(version)) {
			return channel;
		}
		return null;
	}

	public static ConcurrentHashMap<String, Channel> getMap(String userId) {
		return channelMap.get(userId);
	}

	public static Set<String> getAllUserId() {
		return channelMap.keySet();
	}

	public static boolean hasChannel(String userId) {
		return channelMap.containsKey(userId);
	}

	public static boolean verifyCanFeedback(String userId) {
		if (hasChannel(userId)) {
			if (feebackRecord.getIfPresent(userId) != null) {
				return false;
			}
			feebackRecord.put(userId, 1);
			return true;
		} else {
			return false;
		}
	}

	public static Map<String, List<String>> getAllKeys() {
		Map<String, List<String>> data = new HashMap<>();
		for (String key : channelMap.keySet()) {
			List<String> types = new ArrayList<>();
			types.addAll(channelMap.get(key).keySet());
			data.put(key, types);
		}
		return data;
	}

	public static String getCurrentServerIp() {
		try {
			InetAddress inet = InetAddress.getLocalHost();
			return inet.getHostAddress();
		} catch (UnknownHostException e) {
			log.error("get host error:", e);
		}
		return null;
	}

	public static void setDHTChannel(Channel channel) {
		dhtChannel = channel;
	}

	public static Channel getDHTChannel() {
		return dhtChannel;
	}

}

package ltd.finelink.tool.disk.utils;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.Type;

import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.constants.ConstantsKey;
import ltd.finelink.tool.disk.dto.MessageInfo;
import ltd.finelink.tool.disk.dto.NodeInfo;
import ltd.finelink.tool.disk.enums.MethodEnum;
import ltd.finelink.tool.disk.enums.YEnum;
import ltd.finelink.tool.disk.exception.BizException;

@Slf4j
@Component
public class BTUtil {

	// 用于递增消息ID
	private static AtomicInteger messageIDGenerator = new AtomicInteger(1);
	private static AtomicInteger getPeersMessageIDGenerator = new AtomicInteger(1);
	// 递增刷新阈值
	private static int maxMessageID = 1 << 15;

	private static final String[][] announceList = { { "http://sukebei.tracker.wf:8888/announce" },
			{ "http://1337.abcvg.info/announce" }, { "http://bt.okmp3.ru:2710/announce" },
			{ "http://bvarf.tracker.sh:2086/announce" }, { "http://nyaa.tracker.wf:7777/announce" },
			{ "http://open.acgnxtracker.com/announce" }, { "http://retracker.211.ru/announce" },
			{ "http://share.camoe.cn:8080/announce" }, { "http://t.acg.rip:6699/announce" },
			{ "udp://107.182.30.76.16clouds.com:6969/announce" }, { "udp://1c.premierzal.ru:6969/announce" },
			{ "udp://6.pocketnet.app:6969/announce" }, { "udp://aarsen.me:6969/announce" },
			{ "udp://acxx.de:6969/announce" }, { "udp://bigfoot1942.sektori.org:6969/announce" },
			{ "udp://d40969.acod.regrucolo.ru:6969/announce" },
			{ "udp://ec2-18-191-163-220.us-east-2.compute.amazonaws.com:6969/announce" },
			{ "udp://epider.me:6969/announce" }, { "udp://evan.im:6969/announce" },
			{ "udp://tracker1.bt.moack.co.kr:80/announce" }, { "udp://tracker2.dler.com:80/announce" },
			{ "udp://ttk2.nbaonlineservice.com:6969/announce" }, { "udp://u4.trakx.crim.ist:1337/announce" },
			{ "udp://u6.trakx.crim.ist:1337/announce" }, { "udp://uploads.gamecoast.net:6969/announce" },
			{ "udp://wepzone.net:6969/announce" }, { "udp://y.paranoid.agency:6969/announce" },
			{ "udp://yahor.of.by:6969/announce" }, { "wss://peertube2.cpy.re/tracker/socket" },
			{ "wss://tracker.webtorrent.dev" } };

	/**
	 * 从channel中获取到当前通道的id
	 */
	public static String getChannelId(Channel channel) {
		return channel.id().asShortText();
	}

	/**
	 * 生成一个随机的nodeId
	 */
	public static byte[] generateNodeId() {
		return RandomUtils.nextBytes(20);
	}

	/**
	 * 生成一个随机的nodeID
	 */
	public static String generateNodeIdString() {
		return new String(generateNodeId(), CharsetUtil.ISO_8859_1);
	}

	/**
	 * 生成一个递增的t,相当于消息id 使用指定生成器
	 */
	private static String generateMessageID(AtomicInteger generator) {
		int result;
		// 当大于阈值时,重置
		if ((result = generator.getAndIncrement()) > maxMessageID) {
			generator.lazySet(1);
		}
		return new String(CodeUtil.int2TwoBytes(result), CharsetUtil.ISO_8859_1);
	}

	/**
	 * 生成一个递增的t,相当于消息id
	 */
	public static String generateMessageID() {
		return generateMessageID(messageIDGenerator);
	}

	/**
	 * 生成一个递增的t,相当于消息id 用于get_peers请求
	 */
	public static String generateMessageIDOfGetPeers() {
		return generateMessageID(getPeersMessageIDGenerator);
	}

	/**
	 * 根据解析后的消息map,获取消息信息,例如 消息方法(ping/find_node等)/ 消息状态(请求/回复/异常)
	 */
	public static MessageInfo getMessageInfo(Map<String, Object> map) throws Exception {
		MessageInfo messageInfo = new MessageInfo();

		/**
		 * 状态 请求/回复/异常
		 */
		String y = getParamString(map, "y", "y属性不存在.map:" + map);
		Optional<YEnum> yEnumOptional = EnumUtil.getByCode(y, YEnum.class);
		messageInfo.setStatus(yEnumOptional.orElseThrow(() -> new BizException("y属性值不正确.map:" + map)));

		/**
		 * 消息id
		 */
		String t = getParamString(map, "t", "t属性不存在.map:" + map);
		messageInfo.setMessageId(t);

		/**
		 * 获取方法 ping/find_node等
		 */
		// 如果是请求, 直接从请求主体获取其方法
		if (EnumUtil.equals(messageInfo.getStatus().getCode(), YEnum.QUERY)) {
			String q = getParamString(map, "q", "q属性不存在.map:" + map);

			Optional<MethodEnum> qEnumOptional = EnumUtil.getByCode(q, MethodEnum.class);
			messageInfo.setMethod(qEnumOptional.orElseThrow(() -> new BizException("q属性值不正确.map:" + map)));

		} else if (EnumUtil.equals(messageInfo.getStatus().getCode(), YEnum.RECEIVE)) {
			Map<String, Object> rMap = BTUtil.getParamMap(map, "r", "r属性不存在.map:" + map);

			if (rMap.get("token") != null) {
				messageInfo.setMethod(MethodEnum.GET_PEERS);
			} else if (rMap.get("nodes") != null) {
				messageInfo.setMethod(rMap.get("token") == null ? MethodEnum.FIND_NODE : MethodEnum.GET_PEERS);
			} else {
				throw new BizException("未知类型的回复消息.消息:" + map);
			}
		}
		return messageInfo;
	}

	/**
	 * 从Map中获取Object属性
	 */
	public static Object getParam(Map<String, Object> map, String key, String log) {
		Object obj = map.get(key);
		if (obj == null)
			throw new BizException(log);
		return obj;
	}

	/**
	 * 从Map中获取String属性
	 */
	public static String getParamString(Map<String, Object> map, String key, String log) {
		Object obj = getParam(map, key, log);
		return (String) obj;
	}

	/**
	 * 从Map中获取Integer属性
	 */
	public static Integer getParamInteger(Map<String, Object> map, String key, String log) {
		Object obj = getParam(map, key, log);
		return (Integer) obj;
	}

	/**
	 * 从Map中获取List属性
	 */

	@SuppressWarnings("unchecked")
	public static List<String> getParamList(Map<String, Object> map, String key, String log) {
		Object obj = getParam(map, key, log);
		return (List<String>) obj;
	}

	/**
	 * 从Map中获取Map属性
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getParamMap(Map<String, Object> map, String key, String log) {
		Object obj = getParam(map, key, log);
		return (Map<String, Object>) obj;
	}

	/**
	 * 从udp返回的sender属性中,提取出ip
	 */
	public static String getIpBySender(InetSocketAddress sender) {
		String address = sender.getAddress().toString().substring(1);
		if (address.contains("/")) {
			return address.substring(address.indexOf("/") + 1);
		} else {
			return address;
		}

	}

	/**
	 * 从回复的r对象中取出nodes
	 */
	public static List<NodeInfo> getNodeListByRMap(Map<String, Object> rMap) {
		byte[] nodesBytes = BTUtil.getParamString(rMap, "nodes", "FIND_NODE,找不到nodes参数.rMap:" + rMap)
				.getBytes(CharsetUtil.ISO_8859_1);
		List<NodeInfo> nodeList = new LinkedList<>();
		for (int i = 0; i + ConstantsKey.NODE_BYTES_LEN < nodesBytes.length; i += ConstantsKey.NODE_BYTES_LEN) {
			// byte[26] 转 Node
			NodeInfo node = new NodeInfo(ArrayUtils.subarray(nodesBytes, i, i + ConstantsKey.NODE_BYTES_LEN));
			nodeList.add(node);
		}
		return nodeList;
	}

	public static byte[] pareseMetadata(byte[] data) {
		Bencode bencode = new  Bencode(CharsetUtil.UTF_8);
		Map<String, Object> info = bencode.decode(data, Type.DICTIONARY);
		Map<String, Object> torrent = new HashMap<>();
		torrent.put("info", info); 
		List<List<String>> announces = new ArrayList<>();
		for(String[] arr:announceList) { 
			announces.add(Arrays.asList(arr));
		}
		torrent.put("announce-list",announces ); 
		torrent.put("announce", "https://tracker.cloudit.top:443/announce");
		return bencode.encode(torrent);
	}

}

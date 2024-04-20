package ltd.finelink.tool.disk.service.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.dto.AnnouncePeer;
import ltd.finelink.tool.disk.dto.GetPeersSendInfo;
import ltd.finelink.tool.disk.dto.MessageInfo;
import ltd.finelink.tool.disk.dto.NodeInfo;
import ltd.finelink.tool.disk.dto.Peer;
import ltd.finelink.tool.disk.entity.torrent.Hash;
import ltd.finelink.tool.disk.entity.torrent.MetaData;
import ltd.finelink.tool.disk.entity.torrent.Node;
import ltd.finelink.tool.disk.enums.MethodEnum;
import ltd.finelink.tool.disk.enums.NodeRankEnum;
import ltd.finelink.tool.disk.enums.YEnum;
import ltd.finelink.tool.disk.service.DHTMessageService;
import ltd.finelink.tool.disk.service.RoutingTable;
import ltd.finelink.tool.disk.service.TorrentService;
import ltd.finelink.tool.disk.service.torrent.IHashService;
import ltd.finelink.tool.disk.service.torrent.IMetaDataService;
import ltd.finelink.tool.disk.service.torrent.INodeService;
import ltd.finelink.tool.disk.task.FetchMetadataByPeerTask;
import ltd.finelink.tool.disk.task.FindNodeTask;
import ltd.finelink.tool.disk.utils.BTUtil;
import ltd.finelink.tool.disk.utils.CodeUtil;

@Slf4j

@Service
@RequiredArgsConstructor
public class TorrentServiceImpl implements TorrentService {

	private final RoutingTable routingTable;

	private final DHTMessageService dhtMessageService;

	private final FindNodeTask findNodeTask;

	private final FetchMetadataByPeerTask fetchMetadataByPeerTask;

	private final IHashService hashService;

	private final INodeService nodeService;
	
	private final IMetaDataService metaDataService;

	private Cache<String, GetPeersSendInfo> getPeersCache = Caffeine.newBuilder()
			.expireAfterWrite(300, TimeUnit.SECONDS).build();

	@Override
	public void fetchMagnetTask(String hashId) {

		Hash hash = hashService.findByInfoHash(hashId);
		if (hash != null) {
			fetchMetadataByPeerTask.put(hash);
		} else {
			String messageId = BTUtil.generateNodeIdString();
			List<NodeInfo> nodeList = routingTable.getForTop8(CodeUtil.hexStr2Bytes(hashId));
			List<byte[]> nodeIdList = new ArrayList<>();
			nodeIdList.addAll(nodeList.stream().map(NodeInfo::getNodeIdBytes).collect(Collectors.toList()));
			getPeersCache.put(messageId, new GetPeersSendInfo(hashId).put(nodeIdList));
			List<InetSocketAddress> addresses = nodeList.stream().map(NodeInfo::toAddress).collect(Collectors.toList());
			dhtMessageService.sendGetPeers(addresses, messageId, hashId);
		}

	}

	@Override
	public void handleUDPMessage(InetSocketAddress sender, Map<String, Object> data, MessageInfo messageInfo) {
		if (MethodEnum.PING.equals(messageInfo.getMethod())) {
			handlePing(sender, data, messageInfo);
		} else if (MethodEnum.FIND_NODE.equals(messageInfo.getMethod())) {
			handleFindNode(sender, data, messageInfo);
		} else if (MethodEnum.ANNOUNCE_PEER.equals(messageInfo.getMethod())) {
			handleAnnouncePeer(sender, data, messageInfo);
		} else if (MethodEnum.GET_PEERS.equals(messageInfo.getMethod())) {
			handleGetPeers(sender, data, messageInfo);
		} else {
			log.info("handle erro message {}", sender.getHostString());
		}

	}

	private void handlePing(InetSocketAddress sender, Map<String, Object> data, MessageInfo messageInfo) {
		if (YEnum.QUERY.equals(messageInfo.getStatus())) {
			dhtMessageService.responsePing(sender, messageInfo.getMessageId());
		} else {
			log.info("handle ping resopnse {}", sender.getHostString());
		}
	}

	private void handleFindNode(InetSocketAddress sender, Map<String, Object> data, MessageInfo messageInfo) {
		if (YEnum.QUERY.equals(messageInfo.getStatus())) {
			Map<String, Object> aMap = BTUtil.getParamMap(data, "a", "FIND_NODE,找不到a参数.map:" + data);
			byte[] targetNodeId = BTUtil.getParamString(aMap, "target", "FIND_NODE,找不到target参数.map:" + data)
					.getBytes(CharsetUtil.ISO_8859_1);
			byte[] id = BTUtil.getParamString(aMap, "id", "FIND_NODE,找不到id参数.map:" + data)
					.getBytes(CharsetUtil.ISO_8859_1);
			List<NodeInfo> nodes = routingTable.getForTop8(targetNodeId);
			dhtMessageService.responseFindNode(sender, messageInfo.getMessageId(), nodes);
			routingTable.put(new NodeInfo(id, sender, NodeRankEnum.FIND_NODE.getCode()));
		} else {
			Map<String, Object> rMap = BTUtil.getParamMap(data, "r", "FIND_NODE,找不到r参数.map:" + data);
			List<NodeInfo> nodeList = BTUtil.getNodeListByRMap(rMap);
			// 为空退出
			if (CollectionUtils.isEmpty(nodeList))
				return;
			// 去重
			NodeInfo[] nodes = nodeList.stream().distinct().toArray(NodeInfo[]::new);
			// 将nodes加入发送队列
			for (NodeInfo node : nodes) {
				findNodeTask.put(node.toAddress());
			}
			byte[] id = BTUtil.getParamString(rMap, "id", "FIND_NODE,找不到id参数.map:" + data)
					.getBytes(CharsetUtil.ISO_8859_1);
			// 将发送消息的节点加入路由表
			routingTable.put(new NodeInfo(id, sender, NodeRankEnum.FIND_NODE_RECEIVE.getCode()));
		}
	}

	private void handleAnnouncePeer(InetSocketAddress sender, Map<String, Object> data, MessageInfo messageInfo) {
		if (YEnum.QUERY.equals(messageInfo.getStatus())) {
			AnnouncePeer.RequestContent requestContent = new AnnouncePeer.RequestContent(data, sender.getPort());
 
			saveHash(requestContent.getInfo_hash(),BTUtil.getIpBySender(sender) + ":" + requestContent.getPort() + ";");
			// 回复
			dhtMessageService.responseAnnouncePeer(sender, messageInfo.getMessageId());
			NodeInfo node = new NodeInfo(CodeUtil.hexStr2Bytes(requestContent.getId()), sender,
					NodeRankEnum.ANNOUNCE_PEER.getCode());
			// 加入路由表
			routingTable.put(node);
			// 入库
			saveNode(node); 
			// 加入任务队列
			// 加入findNode任务队列
			findNodeTask.put(sender);
		} else {
			log.info("handle announce peer response {}", sender.getHostString());
		}
	}

	private void saveHash(String infoHash, String address) {
		Hash hash = hashService.findByInfoHash(infoHash);
		if (hash == null) {
			hash = new Hash();
			hash.setCreateTime(System.currentTimeMillis());
			hash.setInfoHash(infoHash);
			hash.setPeerAddress(address);
			hash.setUpdateTime(System.currentTimeMillis());
			hashService.save(hash);
			fetchMetadataByPeerTask.put(hash);
		} else if (!hash.getPeerAddress().contains(address)) {
			hash.setUpdateTime(System.currentTimeMillis());
			hash.setPeerAddress(hash.getPeerAddress() + address);
			hashService.updateById(hash);
			MetaData data = metaDataService.findByHash(infoHash);
			if(data==null||data.getData()==null) {
				fetchMetadataByPeerTask.put(hash);
			} 
		}

	}
	
	private void saveNode(NodeInfo info) {
		 Node node = nodeService.findByNodeId(info.getNodeId());
		 if(node!=null) {
			 Node update = info.toNode();
			 update.setId(node.getId());
			 update.setCreateTime(node.getCreateTime());
			 nodeService.updateById(update);
		 }else {
			 nodeService.save(info.toNode());
		 }
	}

	private void handleGetPeers(InetSocketAddress sender, Map<String, Object> data, MessageInfo messageInfo) {
		if (YEnum.QUERY.equals(messageInfo.getStatus())) {
			Map<String, Object> aMap = BTUtil.getParamMap(data, "a", "GET_PEERS,找不到a参数.map:" + data);
			byte[] infoHash = BTUtil.getParamString(aMap, "info_hash", "GET_PEERS,找不到info_hash参数.map:" + data)
					.getBytes(CharsetUtil.ISO_8859_1);
			byte[] id = BTUtil.getParamString(aMap, "id", "GET_PEERS,找不到id参数.map:" + data)
					.getBytes(CharsetUtil.ISO_8859_1);
			List<NodeInfo> nodes = routingTable.getForTop8(infoHash);
			routingTable.put(new NodeInfo(id, sender, NodeRankEnum.GET_PEERS.getCode()));
			dhtMessageService.responseGetPeers(sender, messageInfo.getMessageId(), nodes);
		} else {
			Map<String, Object> rMap = BTUtil.getParamMap(data, "r", "");
			byte[] id = BTUtil.getParamString(rMap, "id", "GET_PEERS-RECEIVE,找不到id参数.map:" + rMap)
					.getBytes(CharsetUtil.ISO_8859_1);
			GetPeersSendInfo getPeersSendInfo = getPeersCache.getIfPresent(messageInfo.getMessageId());
			if (getPeersSendInfo == null)
				return;
			if (rMap.get("nodes") != null) {
				List<NodeInfo> nodeList = BTUtil.getNodeListByRMap(rMap);
				// 如果nodes为空
				if (CollectionUtils.isEmpty(nodeList)) {
					routingTable.delete(id);
					return;
				}
				// 向新节点发送消息
				nodeList.forEach(
						item -> dhtMessageService.sendFindNode(item.toAddress(), BTUtil.generateNodeIdString()));
				// 将消息发送者加入路由表.
				routingTable.put(new NodeInfo(id, sender, NodeRankEnum.GET_PEERS_RECEIVE.getCode()));
				// 取出未发送过请求的节点

				List<NodeInfo> unSentNodeList = nodeList.stream()
						.filter(node -> !getPeersSendInfo.contains(node.getNodeIdBytes())).collect(Collectors.toList());
				// 为空退出
				if (CollectionUtils.isEmpty(unSentNodeList)) {
					log.info("发送者:{},info_hash:{},消息id:{},所有节点已经发送过请求.", sender, getPeersSendInfo.getInfoHash(),
							messageInfo.getMessageId());
					return;
				}
				// 未发送过请求的节点id
				List<byte[]> unSentNodeIdList = unSentNodeList.stream().map(NodeInfo::getNodeIdBytes)
						.collect(Collectors.toList());
				// 将其加入已发送队列
				getPeersSendInfo.put(unSentNodeIdList);
				// 未发送过请求节点的地址
				List<InetSocketAddress> unSentAddressList = unSentNodeList.stream().map(NodeInfo::toAddress)
						.collect(Collectors.toList());
				String messageId = BTUtil.generateNodeIdString();
				dhtMessageService.sendGetPeers(unSentAddressList, messageId, getPeersSendInfo.getInfoHash());
			} else if (rMap.get("values") != null) {
				List<String> rawPeerList;
				try {
					rawPeerList = BTUtil.getParamList(rMap, "values", "GET_PEERS-RECEIVE,找不到values参数.map:" + data);
				} catch (Exception e) {
					// 如果发生异常,说明该values参数可能是string类型的
					String values = BTUtil.getParamString(data, "values", "GET_PEERS-RECEIVE,找不到values参数.map:" + data);
					rawPeerList = Collections.singletonList(values);
				}
				if (CollectionUtils.isEmpty(rawPeerList)) {
					return;
				}

				List<Peer> peerList = new LinkedList<>();
				for (String rawPeer : rawPeerList) {
					// byte[6] 转 Peer
					Peer peer = new Peer(rawPeer.getBytes(CharsetUtil.ISO_8859_1));
					peerList.add(peer);
				}
				// 将peers连接为字符串
				final StringBuilder peersInfoBuilder = new StringBuilder();
				peerList.forEach(
						peer -> peersInfoBuilder.append(peer.getIp()).append(":").append(peer.getPort()).append(";"));

				// 清除该任务缓存 和 连接peer任务
				getPeersCache.invalidate(messageInfo.getMessageId());
				// 入库
				Hash hash = new Hash();
				hash.setCreateTime(System.currentTimeMillis());
				hash.setInfoHash(getPeersSendInfo.getInfoHash());
				hash.setPeerAddress(peersInfoBuilder.toString());
				hash.setUpdateTime(System.currentTimeMillis());
				hashService.save(hash);
				// 节点入库

				routingTable.put(new NodeInfo(id, sender, NodeRankEnum.GET_PEERS_RECEIVE_OF_VALUE.getCode()));
				fetchMetadataByPeerTask.put(hash);
				// 并向该节点发送findNode请求
				findNodeTask.put(sender);

			}
		}
	}

}

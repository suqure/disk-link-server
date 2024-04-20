package ltd.finelink.tool.disk.service.impl;

import java.net.InetSocketAddress;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dampcake.bencode.Bencode;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.config.DHTProperties;
import ltd.finelink.tool.disk.context.ChannelContext;
import ltd.finelink.tool.disk.dto.AnnouncePeer;
import ltd.finelink.tool.disk.dto.FindNode;
import ltd.finelink.tool.disk.dto.GetPeers;
import ltd.finelink.tool.disk.dto.NodeInfo;
import ltd.finelink.tool.disk.dto.Ping;
import ltd.finelink.tool.disk.service.DHTMessageService;
import ltd.finelink.tool.disk.utils.BTUtil;
import ltd.finelink.tool.disk.utils.BeanUtil;
import ltd.finelink.tool.disk.utils.CodeUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class DHTMessageServiceImpl implements DHTMessageService {

	private Bencode bencode = new Bencode(CharsetUtil.ISO_8859_1);

	private final DHTProperties dhtProperties;

	@Override
	public void sendMessage(InetSocketAddress address, byte[] data) {
		if (dhtProperties.getIp().equals(address.getHostName())) {
			return;
		}
		Channel channel = ChannelContext.getDHTChannel();
		if (channel != null) {
			channel.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(data), address));
		}

	}

	@Override
	public void sendPing(InetSocketAddress address) {
		Ping.Request request = new Ping.Request(dhtProperties.getNodeId());
		this.sendMessage(address, bencode.encode(BeanUtil.beanToMap(request)));

	}

	@Override
	public void responsePing(InetSocketAddress address, String messageId) {
		Ping.Response response = new Ping.Response(dhtProperties.getNodeId(), messageId);
		this.sendMessage(address, bencode.encode(BeanUtil.beanToMap(response)));

	}

	@Override
	public void sendFindNode(InetSocketAddress address, String targetId) {
		FindNode.Request request = new FindNode.Request(dhtProperties.getNodeId(), BTUtil.generateNodeIdString());
		this.sendMessage(address, bencode.encode(BeanUtil.beanToMap(request)));

	}

	@Override
	public void responseFindNode(InetSocketAddress address, String messageId, List<NodeInfo> nodeList) {
		FindNode.Response response = new FindNode.Response(dhtProperties.getNodeId(),
				new String(NodeInfo.toBytes(nodeList), CharsetUtil.ISO_8859_1), messageId);
		this.sendMessage(address, bencode.encode(BeanUtil.beanToMap(response)));
	}

	@Override
	public void sendGetPeers(List<InetSocketAddress> addresses, String messageId, String infoHash) {
		GetPeers.Request request = new GetPeers.Request(dhtProperties.getNodeId(),
				new String(CodeUtil.hexStr2Bytes(infoHash), CharsetUtil.ISO_8859_1), messageId);
		byte[] encode = bencode.encode(BeanUtil.beanToMap(request));
		for (InetSocketAddress address : addresses) {
			try {
				this.sendMessage(address, encode);
			} catch (Exception e) {
				log.error("发送GET_PEERS,失败.e:{}", e.getMessage());
			}
		}

	}

	@Override
	public void responseGetPeers(InetSocketAddress address, String messageId, List<NodeInfo> nodeList) {
		GetPeers.Response response = new GetPeers.Response(dhtProperties.getNodeId(), dhtProperties.getToken(),
				new String(NodeInfo.toBytes(nodeList), CharsetUtil.ISO_8859_1), messageId);
		this.sendMessage(address, bencode.encode(BeanUtil.beanToMap(response)));
	}

	@Override
	public void responseAnnouncePeer(InetSocketAddress address, String messageId) {
		AnnouncePeer.Response response = new AnnouncePeer.Response(dhtProperties.getNodeId(), messageId);
		this.sendMessage(address, bencode.encode(BeanUtil.beanToMap(response)));
	}

}

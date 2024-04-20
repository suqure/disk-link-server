package ltd.finelink.tool.disk.channel;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dampcake.bencode.Bencode;
import com.dampcake.bencode.BencodeException;
import com.dampcake.bencode.Type;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.config.DHTProperties;
import ltd.finelink.tool.disk.context.ChannelContext;
import ltd.finelink.tool.disk.dto.MessageInfo;
import ltd.finelink.tool.disk.entity.torrent.Node;
import ltd.finelink.tool.disk.exception.BizException;
import ltd.finelink.tool.disk.service.DHTMessageService;
import ltd.finelink.tool.disk.service.TorrentService;
import ltd.finelink.tool.disk.service.torrent.INodeService;
import ltd.finelink.tool.disk.utils.BTUtil;

@Slf4j
@Component
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class DHTServerChannelHandler extends SimpleChannelInboundHandler<DatagramPacket> {

	private final DHTProperties dHtProperties;

	private final TorrentService torrentService;

	private Bencode utfBencode = new Bencode(CharsetUtil.UTF_8);

	private Bencode isoBencode = new Bencode(CharsetUtil.ISO_8859_1);

	private final DHTMessageService dhtMessageService;

	private final INodeService nodeService;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {

		byte[] bytes = getBytes(packet);
		InetSocketAddress sender = packet.sender();
		// 解码为map
		Map<String, Object> map;
		try {
			map = isoBencode.decode(bytes, Type.DICTIONARY);
		} catch (BencodeException e) {
			try {
				map = utfBencode.decode(bytes, Type.DICTIONARY);
			} catch (BencodeException e1) {
				log.error("消息解码异常.发送者:{}.异常:{}", sender, e1.getMessage());
				return;
			} 
		} catch (Exception e) {
			log.error("消息解码异常.发送者:{}.异常:{}", sender, e.getMessage(), e);
			return;
		}
		// 解析出MessageInfo
		MessageInfo messageInfo;
		try {
			messageInfo = BTUtil.getMessageInfo(map);
			torrentService.handleUDPMessage(sender, map, messageInfo);
		} catch (BizException e) {
			log.error("解析MessageInfo异常.异常:{}", e.getMessage());
			return;
		} catch (Exception e) {
			log.error("解析MessageInfo异常.异常:{}", e.getMessage(), e);
			return;
		}

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ChannelContext.setDHTChannel(ctx.channel());
		List<String> nodes = dHtProperties.getNodes();

		if (nodes != null && !nodes.isEmpty()) {
			for (String node : nodes) {
				String[] split = node.split(":");
				dhtMessageService.sendFindNode(new InetSocketAddress(split[0], Integer.parseInt(split[1])),
						BTUtil.generateNodeIdString());
			}

		}
		List<Node> list = nodeService.findLastNode();
		if (list != null && !list.isEmpty()) {
			for (Node node : list) {
				dhtMessageService.sendFindNode(new InetSocketAddress(node.getIp(), node.getPort()),
						BTUtil.generateNodeIdString());
			}
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("发生异常:{}", cause.getMessage());
	}

	private byte[] getBytes(DatagramPacket packet) {
		// 读取消息到byte[]
		byte[] bytes = new byte[packet.content().readableBytes()];
		packet.content().readBytes(bytes);
		return bytes;
	}

}

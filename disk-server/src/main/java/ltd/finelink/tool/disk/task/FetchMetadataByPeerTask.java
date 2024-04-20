package ltd.finelink.tool.disk.task;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import com.dampcake.bencode.Bencode;
import com.github.benmanes.caffeine.cache.Cache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.CharsetUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.config.BootstrapFactory;
import ltd.finelink.tool.disk.constants.ConstantsKey;
import ltd.finelink.tool.disk.context.ChannelContext;
import ltd.finelink.tool.disk.entity.torrent.Hash;
import ltd.finelink.tool.disk.entity.torrent.MetaData;
import ltd.finelink.tool.disk.protobuf.ServerMessage;
import ltd.finelink.tool.disk.service.torrent.IMetaDataService;
import ltd.finelink.tool.disk.utils.BTUtil;
import ltd.finelink.tool.disk.utils.CodeUtil;
import ltd.finelink.tool.disk.utils.MessageUtils;

/**
 * author:ZhengXing datetime:2018/3/6 0006 14:46
 * 根据bep-009/bep-010协议获取metadata信息任务
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class FetchMetadataByPeerTask {
	private static final String LOG = "[FetchMetadataByPeerTask]";

	private final BootstrapFactory bootstrapFactory;
	private Bencode bencode = new Bencode();
	private final Cache<String, Set<String>> magnetTask;
	private final IMetaDataService metaDataService;
	private boolean isRunning;

	// 等待连接peers的infoHash队列
	private BlockingDeque<Hash> queue = new LinkedBlockingDeque<>(10240);

	/**
	 * 入队
	 */
	public void put(Hash infoHash) {
		queue.offer(infoHash);
		if (!isRunning) {
			start();
		}
	}

	/**
	 * 删除某个任务
	 */
	public void remove(String infoHash) {
		queue.removeIf(item -> item.getInfoHash().equals(infoHash));
	}

	/**
	 * 队列长度
	 */
	public int size() {
		return queue.size();
	}

	/**
	 * 异步任务
	 */
	public void start() {
		isRunning = true;
		new Thread(() -> {
			while (true) {
				try {
					Hash hash = queue.take();
					log.info("{}开始新任务.infoHash:{}", LOG, hash.getInfoHash());
					fetchMetadata(hash);
				} catch (Exception e) {
					log.error("{}异常.e:{}", e.getMessage(), e);
				}
			}
		}).start();

	}

	/**
	 * 获取metadata
	 */
	@SneakyThrows
	private void fetchMetadata(Hash infoHash) {
		CountDownLatch latch = new CountDownLatch(1);
		// peer地址
		String[] addressArr = infoHash.getPeerAddress().split(";");

		// 每次最多同时建立5个连接,如果peers数量超过5个,则分批获取
		int maxNum = 5;
		for (int i = 0; i < addressArr.length; i += maxNum) {
			List<Result> results = new LinkedList<>();
			// 向5个peer执行发送任务
			for (int j = 0; j < 5; j++) {
				if (addressArr.length <= i + j)
					break;
				String[] ipPort = addressArr[i + j].split(":");
				final Result result = new Result(latch);
				results.add(result);
				bootstrapFactory.build().handler(new CustomChannelInitializer(infoHash.getInfoHash(), result))
						.connect(new InetSocketAddress(ipPort[0], Integer.parseInt(ipPort[1])))
						.addListener(new ConnectListener(infoHash.getInfoHash(), BTUtil.generateNodeId()));
			}
			// 暂停10s 或 被唤醒
			latch.await(10, TimeUnit.SECONDS);
			// 尝试解析
			MetaData meta = new MetaData();
			meta.setCreateTime(System.currentTimeMillis());
			meta.setUpdateTime(System.currentTimeMillis());
			meta.setHash(infoHash.getInfoHash());
			for (Result result : results) {

				if (result.getResult() != null) {
					if (result.getSize() == result.getResult().length) {
						sendTorrentMessage(infoHash.getInfoHash(), result.getResult());
						meta.setData(result.getResult());
						metaDataService.save(meta);
						return;
					}

				}
			}

		}
	}

	private void sendTorrentMessage(String hashId, byte[] torrent) {
		if (torrent != null) {
			ServerMessage message = MessageUtils.buildTorrentMessage(hashId, torrent);
			Set<String> receivers = magnetTask.getIfPresent(hashId);
			if (receivers != null) {
				for (String user : receivers) {
					String[] data = user.split(":");
					Channel ch = ChannelContext.get(data[0], data[1]);
					if (ch != null && ch.isOpen()) {
						ch.writeAndFlush(message);
					}
				}
				magnetTask.invalidate(hashId);
			}
		}
	}

	public void sendMessage(String userId, String type, ServerMessage message) {
		Channel ch = ChannelContext.get(userId, type);
		if (ch == null || !ch.isOpen()) {
			return;
		}
		ch.writeAndFlush(message);
	}

	/**
	 * 消息处理类
	 */
	@Getter
	@NoArgsConstructor
	private class FetchMetadataHandler extends SimpleChannelInboundHandler<ByteBuf> {
		private String infoHashHexStr;
		private Result result;

		@Override
		protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
			byte[] bytes = new byte[msg.readableBytes()];
			msg.readBytes(bytes);

			String messageStr = new String(bytes, CharsetUtil.ISO_8859_1);

			// 收到握手消息回复
			if (bytes[0] == (byte) 19) {
				// 发送扩展消息
				SendExtendMessage(ctx);
			}

			// 如果收到的消息中包含ut_metadata,提取出ut_metadata的值
			String utMetadataStr = "ut_metadata";
			String metadataSizeStr = "metadata_size";
			String utPexStr = "ut_pex";
			if (messageStr.contains(utMetadataStr) && messageStr.contains(metadataSizeStr)) {
				log.info("发送metadata请求:{}", infoHashHexStr);
				sendMetadataRequest(ctx, messageStr, utMetadataStr, metadataSizeStr);
			} else if (messageStr.contains(utPexStr)) {
				// 只支持ut_pex 关闭连接
				log.info("不支持ut_metadata,关闭连接{}",infoHashHexStr);
				ctx.close();
				return;
			}


			// 如果是分片信息
			if (messageStr.contains("msg_type")) {
				log.info("收到分片消息:{}", infoHashHexStr);
				fetchMetadataBytes(messageStr);
			}
		}

		/***
		 * 获取metadataBytes
		 */
		private void fetchMetadataBytes(String messageStr) {
			String resultStr = messageStr.substring(messageStr.indexOf("ee") + 2, messageStr.length());
			byte[] resultStrBytes = resultStr.getBytes(CharsetUtil.ISO_8859_1);
			if (result.getResult() != null) {
				result.setResult(ArrayUtils.addAll(result.getResult(), resultStrBytes));
			} else {
				result.setResult(resultStrBytes);
			}
			// 唤醒latch
			if(result.getResult().length>=result.size) {
				result.getLatch().countDown();
			}
			
		}

		/**
		 * 发送 metadata 请求消息
		 */
		private void sendMetadataRequest(ChannelHandlerContext ctx, String messageStr, String utMetadataStr,
				String metadataSizeStr) {
			int utMetadataIndex = messageStr.indexOf(utMetadataStr) + utMetadataStr.length() + 1;
			// ut_metadata值
			int utMetadataValue = Integer.parseInt(messageStr.substring(utMetadataIndex, utMetadataIndex + 1));
			int metadataSizeIndex = messageStr.indexOf(metadataSizeStr) + metadataSizeStr.length() + 1;
			String otherStr = messageStr.substring(metadataSizeIndex);
			// metadata_size值
			int metadataSize = Integer.parseInt(otherStr.substring(0, otherStr.indexOf("e")));
			// 分块数
			int blockSum = (int) Math.ceil((double) metadataSize / ConstantsKey.METADATA_PIECE_SIZE);
//				log.info("该种子metadata大小:{},分块数:{}",metadataSize,blockSum);
			result.setSize(metadataSize);
			// 发送metadata请求
			for (int i = 0; i < blockSum; i++) {
				Map<String, Object> metadataRequestMap = new LinkedHashMap<>();
				metadataRequestMap.put("msg_type", 0);
				metadataRequestMap.put("piece", i);
				byte[] metadataRequestMapBytes = bencode.encode(metadataRequestMap);
				byte[] metadataRequestBytes = new byte[metadataRequestMapBytes.length + 6];
				metadataRequestBytes[4] = 20;
				metadataRequestBytes[5] = (byte) utMetadataValue;
				byte[] lenBytes = CodeUtil.int2Bytes(metadataRequestMapBytes.length + 2);
				System.arraycopy(lenBytes, 0, metadataRequestBytes, 0, 4);
				System.arraycopy(metadataRequestMapBytes, 0, metadataRequestBytes, 6, metadataRequestMapBytes.length);
				ctx.channel().writeAndFlush(Unpooled.copiedBuffer(metadataRequestBytes));
			}
		}

		/**
		 * 发送扩展消息
		 *
		 * @param ctx
		 */
		private void SendExtendMessage(ChannelHandlerContext ctx) {
			Map<String, Object> extendMessageMap = new LinkedHashMap<>();
			Map<String, Object> extendMessageMMap = new LinkedHashMap<>();
			extendMessageMMap.put("ut_metadata", 1);
			extendMessageMap.put("m", extendMessageMMap);
			byte[] tempExtendBytes = bencode.encode(extendMessageMap);
			byte[] extendMessageBytes = new byte[tempExtendBytes.length + 6];
			extendMessageBytes[4] = 20;
			extendMessageBytes[5] = 0;
			byte[] lenBytes = CodeUtil.int2Bytes(tempExtendBytes.length + 2);
			System.arraycopy(lenBytes, 0, extendMessageBytes, 0, 4);
			System.arraycopy(tempExtendBytes, 0, extendMessageBytes, 6, tempExtendBytes.length);
			ctx.channel().writeAndFlush(Unpooled.copiedBuffer(extendMessageBytes));
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			log.error("{}{}异常:", LOG, infoHashHexStr, cause);
			// 关闭
			ctx.close();
		}

		public FetchMetadataHandler(String infoHashHexStr, Result result) {
			this.infoHashHexStr = infoHashHexStr;
			this.result = result;
		}
	}

	/**
	 * 连接监听器
	 */
	@AllArgsConstructor
	private class ConnectListener implements ChannelFutureListener {
		private String infoHashHexStr;
		// 自己的peerId,直接定义为和nodeId相同即可
		private byte[] selfPeerId;

		@Override
		public void operationComplete(ChannelFuture future) throws Exception {
			if (future.isSuccess()) {
				// 连接成功发送握手消息
				SendHandshakeMessage(future);
				return;
			}
			// 如果失败 ,不做任何操作
			future.channel().close();
		}

		/**
		 * 发送握手消息
		 */
		private void SendHandshakeMessage(ChannelFuture future) {
			
			byte[] infoHash = CodeUtil.hexStr2Bytes(infoHashHexStr);
			byte[] sendBytes = new byte[68];
			System.arraycopy(ConstantsKey.GET_METADATA_HANDSHAKE_PRE_BYTES, 0, sendBytes, 0, 28);
			System.arraycopy(infoHash, 0, sendBytes, 28, 20);
			System.arraycopy(selfPeerId, 0, sendBytes, 48, 20);
			future.channel().writeAndFlush(Unpooled.copiedBuffer(sendBytes));
		}
	}

	/**
	 * 通道初始化器
	 */
	@AllArgsConstructor
	private class CustomChannelInitializer extends ChannelInitializer {
		private String infoHashHexStr;
		private final Result result;

		@Override
		protected void initChannel(Channel ch) throws Exception {
			ch.pipeline().addLast(new ReadTimeoutHandler(10)).addLast(new FetchMetadataHandler(infoHashHexStr, result));

		}
	}

	/**
	 * 返回对象
	 */
	@Data
	private class Result {
		private byte[] result;
		private int size;
		private final CountDownLatch latch;

		public Result(CountDownLatch latch) {
			this.latch = latch;
		}
	}

}

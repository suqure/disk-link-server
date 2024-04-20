package ltd.finelink.tool.disk.handler;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import ltd.finelink.tool.disk.protobuf.ClientMessage;

public class MessageDecodeHander extends MessageToMessageDecoder<BinaryWebSocketFrame> {

	@Override
	protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out) throws Exception {
		ByteBuf byteBuf = msg.content();
		final byte[] array; 
		final int length = byteBuf.readableBytes();
		if (byteBuf.hasArray()) {
			array = byteBuf.array(); 
		} else {
			array = ByteBufUtil.getBytes(byteBuf, byteBuf.readerIndex(), length, false); 
		}
		out.add(ClientMessage.parseFrom(array));
	}

}

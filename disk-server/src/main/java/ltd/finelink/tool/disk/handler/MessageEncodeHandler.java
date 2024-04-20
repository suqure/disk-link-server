package ltd.finelink.tool.disk.handler;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import ltd.finelink.tool.disk.protobuf.ServerMessage;

public class MessageEncodeHandler extends MessageToMessageEncoder<ServerMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, ServerMessage msg, List<Object> out) throws Exception {
		ByteBuf byteBuf = Unpooled.wrappedBuffer(msg.toByteArray());
		WebSocketFrame frame = new BinaryWebSocketFrame(byteBuf);
        out.add(frame);
	}

}

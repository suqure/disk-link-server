package ltd.finelink.tool.disk.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.context.ChannelContext;
import ltd.finelink.tool.disk.service.MessageService;

@ChannelHandler.Sharable
@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

	private final MessageService messageService;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {

		handleWebSocket(ctx, msg);

	}

	private void handleWebSocket(ChannelHandlerContext ctx, WebSocketFrame frame) {
		if (frame instanceof CloseWebSocketFrame) {
			String type = ChannelContext.getType(ctx.channel());
			String userId = ChannelContext.getUserId(ctx.channel());
			messageService.hanlerOfflineNotify(userId, type);
			if (StringUtils.isNoneBlank(userId, type)) {
				ChannelContext.remove(userId, type); 
			}
			close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
			return;
		}
		// 判断是否是Ping消息
		if (frame instanceof PingWebSocketFrame) {
			ctx.channel().writeAndFlush(new PongWebSocketFrame());
			return;
		}
		// 不支持文本消息
		if (frame instanceof TextWebSocketFrame) {
			String msg = ((TextWebSocketFrame) frame).text();
			ctx.channel().writeAndFlush(new TextWebSocketFrame("当前通道不支持文本消息"));
			log.warn("receive text msg :{}", msg);
		}

	}


	private void close(ChannelOutboundInvoker ctx, CloseWebSocketFrame frame) {
		ctx.writeAndFlush(frame, ctx.newPromise()).addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error(cause.getMessage(), cause);
		Channel channel = ctx.channel(); 
		String userId = ChannelContext.getUserId(channel);
		String type = ChannelContext.getType(channel);
		if (StringUtils.isNoneBlank(userId, type)) {
			messageService.hanlerOfflineNotify(userId, type);
			ChannelContext.remove(userId, type); 
		}
		ctx.close();
	}

}

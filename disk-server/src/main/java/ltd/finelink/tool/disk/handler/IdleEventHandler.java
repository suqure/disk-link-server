package ltd.finelink.tool.disk.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.context.ChannelContext;
import ltd.finelink.tool.disk.service.MessageService;

@Slf4j
@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class IdleEventHandler extends ChannelInboundHandlerAdapter {

	private final MessageService messageService;
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

		if (evt instanceof IdleStateEvent || evt instanceof ChannelInputShutdownReadComplete) {
			log.warn("no heartbeat,close channel");
			String type = ChannelContext.getType(ctx.channel());
			String userId = ChannelContext.getUserId(ctx.channel()); 
			if (StringUtils.isNoneBlank(userId, type)) {
				messageService.hanlerOfflineNotify(userId, type);
				ChannelContext.remove(userId, type);
			}
			if (ctx.channel().isOpen()) {
				ctx.channel().writeAndFlush(new CloseWebSocketFrame());
				ctx.close();
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

}

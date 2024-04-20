package ltd.finelink.tool.disk.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.context.ChannelContext;
import ltd.finelink.tool.disk.protobuf.ClientMessage;
import ltd.finelink.tool.disk.service.MessageService;

@Slf4j
@Component
@ChannelHandler.Sharable
@RequiredArgsConstructor
public class MessageHandler extends SimpleChannelInboundHandler<ClientMessage> {

	private final MessageService messageService;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ClientMessage msg) throws Exception {
		Channel channel = ctx.channel();
		String userId = ChannelContext.getUserId(channel);
		String type = ChannelContext.getType(channel);
		if (StringUtils.isNoneBlank(userId, type)) {
			messageService.receiveMessage(userId, type, msg);
		} else {
			log.warn("获取用户id失败 channel id = {}", channel.id().toString());
		}
	}

}

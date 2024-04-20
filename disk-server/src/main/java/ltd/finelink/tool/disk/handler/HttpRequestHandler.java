package ltd.finelink.tool.disk.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.protobuf.ServiceException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.config.NettyProperties;
import ltd.finelink.tool.disk.constants.ConstantsKey;
import ltd.finelink.tool.disk.context.ChannelContext;
import ltd.finelink.tool.disk.service.MessageService;
import ltd.finelink.tool.disk.service.UserService;
import ltd.finelink.tool.disk.utils.IPUtil;

@ChannelHandler.Sharable
@Component
@Slf4j
@RequiredArgsConstructor
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private final NettyProperties nettyProperties;

	private final UserService userService;

	private final MessageService messageService; 
	
	private final ApiRequestHandler apiHandler;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {

		handleHttpRequest(ctx, msg);

	}

	private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		String uri = request.uri();

		log.debug("handleHttpRequest uri -->{}", uri);

		if (uri.startsWith(ConstantsKey.HEALTH_CHECK_PATH)) {
			sendHttpResponse(ctx, request, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));
			return;
		}
		if (uri.equals(ConstantsKey.DEVICE_PATH)) {
			apiHandler.genrateDeviceResponse(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.FEEBACK_PATH)) {
			apiHandler.genratefeebackResponse(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.ICE_SERVER_PATH)) {
			apiHandler.genrateIceServerResponse(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.EMAIL_VERIFY_PATH)) {
			apiHandler.genrateEmailVerifyResponse(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.SIGN_UP_PATH)) {
			apiHandler.genrateSignupResponse(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.LOGIN_PATH)) {
			apiHandler.genrateLoginResponse(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.VERIFY_PATH)) {
			apiHandler.genrateVerifyResponse(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.RESET_PASSWORD_PATH)) {
			apiHandler.genrateResetResponse(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.UPDATE_USER_PATH)) {
			apiHandler.genrateUpdateUserResponse(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.USER_COMMENT_PATH)) {
			apiHandler.genrateUserCommentResponse(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.SHOW_COMMENT_PATH)) {
			apiHandler.genrateShowCommentResponse(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.FILE_TOKEN_PATH)) {
			apiHandler.genrateFileTokenResponse(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.FILE_SHARE_PATH)) {
			apiHandler.genrateShareResponse(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.FILE_INFO_PATH)) {
			apiHandler.genrateFileInfoResponse(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.QINIU_CALLBACK_PATH)) {
			apiHandler.genrateQiniuCallback(ctx, request);
			return;
		}
		if (uri.equals(ConstantsKey.AI_CHAT_PATH)) {
			apiHandler.genrateAIChatResponse(ctx, request);
			return;
		}
		String token = request.headers().get(ConstantsKey.TOKEN);
		String protocol = request.headers().get(ConstantsKey.WEBSOCKET_PROTOCOL);
		String version = request.headers().get(ConstantsKey.SDK_VERSION);
		if (StringUtils.isBlank(token)) {
			token = protocol;
		}
		// websocket 握手
		if (!request.decoderResult().isSuccess()
				|| (!ConstantsKey.WEBSOCKET.equals(request.headers().get(ConstantsKey.UPGRADE)))) {
			sendHttpResponse(ctx, request,
					new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
			return;
		}
		// 第一次握手连接，校验是否登入
		String[] split = uri.split(ConstantsKey.SLASH_VAL);
		if (split.length != 4) {
			sendHttpResponse(ctx, request,
					new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
			return;
		}
		String path = split[1];
		String userName = split[2];
		String type = split[3];
		String realIp = IPUtil.getRealIp(request);
		if (StringUtils.isNotBlank(realIp)) {
			ChannelContext.setRealIp(realIp, ctx.channel());
		}
		ChannelContext.setDeviceType(getDeviceType(request), ctx.channel());

		if (nettyProperties.getBasePath().equals(path)) { 
			if (!userService.verifyToken(userName, token)) {
				sendHttpResponse(ctx, request,
						new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
				return;
			}
		} else if (nettyProperties.getAnonymousPath().equals(path)) {
			if (!ConstantsKey.ANONYMOUS_CHANNELS.contains(type) || !userService.existAnonymousInfo(userName)) {
				sendHttpResponse(ctx, request,
						new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
				return;
			}
		} else {
			sendHttpResponse(ctx, request,
					new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
			return;
		}
		// 正常WebSocket的Http连接请求，构造握手响应返回
		WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("", null, false,
				nettyProperties.getMaxFrameLength());
		WebSocketServerHandshaker handShaker = wsFactory.newHandshaker(request);
		if (handShaker == null) { // 无法处理的websocket版本
			WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
		} else {
			// 记录管道处理上下文，便于服务器推送数据到客户端
			Channel channel = ChannelContext.get(userName, type);
			if (channel != null) {
				log.debug("remove channel ........");
				// 通知登出
				channel.writeAndFlush(new CloseWebSocketFrame());
				channel.close().addListener((ChannelFutureListener) channelFuture -> {
					log.debug("Listener channel close finished");
					if (channelFuture.isSuccess()) {
						log.debug("channel connection success");
					} else {
						log.debug("channel connection error");
						throw new ServiceException("关闭连接异常");
					}
				});

			}

			initChannelContext(userName, type, ctx.channel(), version);
			// 向客户端发送websocket握手,完成握手
			if (StringUtils.isNotBlank(protocol)) {
				HttpHeaders responseHeaders = new DefaultHttpHeaders();
				responseHeaders.add(ConstantsKey.WEBSOCKET_PROTOCOL, protocol);
				handShaker.handshake(ctx.channel(), request, responseHeaders, ctx.channel().newPromise());
			} else {
				handShaker.handshake(ctx.channel(), request);
			}

		}
	}

	private void initChannelContext(String userId, String type, Channel channel, String version) {
		ChannelContext.setAttribute(userId, type, version, channel);
		ChannelContext.add(userId, type, channel);

	}

	/**
	 * Http返回
	 *
	 * @param ctx
	 * @param request
	 * @param response
	 */
	private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request,
			FullHttpResponse response) {
		// 返回应答给客户端
		if (response.status().code() != 200) {
			ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
			response.content().writeBytes(buf);
			buf.release();
			HttpUtil.setContentLength(response, response.content().readableBytes());
		}

		// 如果是非Keep-Alive，关闭连接
		ChannelFuture f = ctx.channel().writeAndFlush(response);
		if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
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

	public String getDeviceType(FullHttpRequest request) {
		String deviceType = "UNKNOWN";
		String userAgent = request.headers().get("user-agent");
		if (StringUtils.isNotBlank(userAgent)) {
			if (userAgent.indexOf("Android") > -1) {
				deviceType = "Android";
			} else if (userAgent.indexOf("iPhone") > -1) {
				deviceType = "iPhone";
			} else if (userAgent.indexOf("iPad") > -1) {
				deviceType = "iPad";
			} else if (userAgent.indexOf("iOS") > -1) {
				deviceType = "iOS Device";
			} else if (userAgent.indexOf("Mac OS X") > -1) {
				deviceType = "MAC Device";
			} else if (userAgent.indexOf("phone") > -1) {
				deviceType = "phone";
			} else if (userAgent.indexOf("Windows") > -1) {
				deviceType = "Windows Device";
			} else if (userAgent.indexOf("Linux") > -1) {
				deviceType = "Linux Device";
			} else if (userAgent.indexOf("Desktop") > -1) {
				deviceType = "Desktop Client";
			}
		}

		return deviceType;
	}

	 

}

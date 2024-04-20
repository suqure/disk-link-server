package ltd.finelink.tool.disk.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.qiniu.util.Auth;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http2.HttpConversionUtil.ExtensionHeaderNames;
import io.netty.util.CharsetUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.base.IResult;
import ltd.finelink.tool.disk.config.NettyProperties;
import ltd.finelink.tool.disk.constants.ConstantsKey;
import ltd.finelink.tool.disk.context.ChannelContext;
import ltd.finelink.tool.disk.entity.system.EmailVerify;
import ltd.finelink.tool.disk.entity.user.AnonymousInfo;
import ltd.finelink.tool.disk.entity.user.Comment;
import ltd.finelink.tool.disk.enums.EmailVerifyType;
import ltd.finelink.tool.disk.enums.NotifyType;
import ltd.finelink.tool.disk.enums.ResultMessage;
import ltd.finelink.tool.disk.protobuf.ServerMessage;
import ltd.finelink.tool.disk.service.MessageService;
import ltd.finelink.tool.disk.service.OssService;
import ltd.finelink.tool.disk.service.UserService;
import ltd.finelink.tool.disk.utils.DateUtil;
import ltd.finelink.tool.disk.utils.IPUtil;
import ltd.finelink.tool.disk.utils.MD5Util;
import ltd.finelink.tool.disk.utils.MessageUtils;
import ltd.finelink.tool.disk.utils.OKHttpClientUtil;
import ltd.finelink.tool.disk.utils.StringTemplateUtils;
import ltd.finelink.tool.disk.utils.ThreadPoolExecutorUtil;
import ltd.finelink.tool.disk.vo.AiChatResVo;
import ltd.finelink.tool.disk.vo.AiChatVo;
import ltd.finelink.tool.disk.vo.AiMessage;
import ltd.finelink.tool.disk.vo.CommentQueryVo;
import ltd.finelink.tool.disk.vo.EmailVerifyVo;
import ltd.finelink.tool.disk.vo.FeebackVo;
import ltd.finelink.tool.disk.vo.FileVo;
import ltd.finelink.tool.disk.vo.IceServer;
import ltd.finelink.tool.disk.vo.LoginVo;
import ltd.finelink.tool.disk.vo.ResetPasswordVo;
import ltd.finelink.tool.disk.vo.SignupVo;
import ltd.finelink.tool.disk.vo.UserCommentVo;
import ltd.finelink.tool.disk.vo.UserInfoVo;
import ltd.finelink.tool.disk.vo.VerifyVo;
import ltd.finelink.tool.disk.vo.updateUserInfoVo;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiRequestHandler {

	private final NettyProperties nettyProperties;

	private final UserService userService;

	private final JavaMailSender mailSender;

	private final MessageService messageService;

	private final OssService ossService;

	private Cache<String, List<AiMessage>> chatCache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
			.build();

	public void genratefeebackResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String username = request.headers().get("username");
		if (request.method().equals(HttpMethod.POST) && StringUtils.isNotBlank(username)
				&& ChannelContext.verifyCanFeedback(username)) {
			JSONObject body = parseJsonBody(request);
			if (body != null) {
				FeebackVo vo = body.toJavaObject(FeebackVo.class);
				if (StringUtils.isNoneBlank(vo.getContent(), vo.getTitle())) {
					String text = "标题:" + vo.getTitle() + "\n";
					text += "具体内容:\n" + vo.getContent() + "\n ";
					if (StringUtils.isNotBlank(vo.getEmail())) {
						text += "联系邮箱:" + vo.getEmail();
					}
					SimpleMailMessage message = new SimpleMailMessage();
					message.setSubject("用户" + username + "反馈邮件");
					message.setText(text);
					message.setFrom(nettyProperties.getMailSender());
					message.setTo(nettyProperties.getFeebackMail());
					mailSender.send(message);
				}
			}
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.ok())));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
					HttpResponseStatus.BAD_REQUEST,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.fail("error parameter"))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			f.addListener(ChannelFutureListener.CLOSE);
		}

	}

	public void genrateIceServerResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String username = request.headers().get("username");
		String token = request.headers().get(ConstantsKey.TOKEN);
		List<IceServer> servers = new ArrayList<>();
		servers.add(IceServer.defalut());
		if (ChannelContext.hasChannel(username) && userService.verifyToken(username, token)) {
			IceServer server = new IceServer();
			server.setCredential("credential");
			server.setUrls("turn:stun.xxx.com:5349");
			server.setUsername("username");
			servers.add(server);
		}
		Map<String, Object> data = new HashMap<>();
		data.put("iceServers", servers);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
				Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.ok(data))));
		response.headers().set("Content-Type", "application/json");
		HttpUtil.setContentLength(response, response.content().readableBytes());
		ChannelFuture f = ctx.channel().writeAndFlush(response);
		if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}

	}

	public void genrateEmailVerifyResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String username = request.headers().get("username");
		String lang = request.headers().get("lang");
		if (request.method().equals(HttpMethod.POST) && ChannelContext.hasChannel(username)) {
			IResult result = null;
			JSONObject body = parseJsonBody(request);
			if (body != null) {
				EmailVerifyVo vo = body.toJavaObject(EmailVerifyVo.class);
				EmailVerifyType type = EmailVerifyType.parese(vo.getType());
				if (type != null && StringUtils.isNoneBlank(vo.getEmail())
						&& StringTemplateUtils.verifyEmailAddress(vo.getEmail())) {
					String code = ConstantsKey.VERIFY_CODE_CN;
					String subject = "随心传验证码";
					if (StringUtils.isNotBlank(lang) && lang.contains("en")) {
						code = ConstantsKey.VERIFY_CODE_EN;
						subject = "Disk Link single-use code";
					}
					EmailVerify verify = userService.createEmailVerify(type, vo.getEmail());
					if (verify != null) {
						Map<String, String> params = new HashMap<>();
						params.put("name", vo.getEmail());
						params.put("code", verify.getCode());
						String text = userService.renderMessage(code, params);
						SimpleMailMessage message = new SimpleMailMessage();
						message.setSubject(subject);
						message.setText(text);
						message.setFrom(nettyProperties.getMailSender());
						message.setTo(vo.getEmail());
						try {
							mailSender.send(message);
							result = IResult.ok();
						} catch (Exception e) {
							result = IResult.fail(e.getMessage());
						}
					} else {
						result = IResult.fail("invalidated email address");
					}
				} else {
					result = IResult.fail(ResultMessage.PARAMETER_ERROR);
				}
			} else {
				result = IResult.fail(ResultMessage.PARAMETER_ERROR);
			}
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(result)));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.fail("no permission"))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public void genrateSignupResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String username = request.headers().get("username");
		if (request.method().equals(HttpMethod.POST) && ChannelContext.hasChannel(username)) {
			IResult result = null;
			JSONObject body = parseJsonBody(request);
			if (body != null) {
				SignupVo vo = body.toJavaObject(SignupVo.class);
				if (StringUtils.isNoneBlank(vo.getUsername(), vo.getEmail(), vo.getPassword(), vo.getVerify())) {
					vo.setRegisterIp(IPUtil.getRealIp(request));
					try {
						UserInfoVo user = userService.userSiginup(vo);
						user.setChannel(username);
						result = IResult.ok(user);
					} catch (Exception e) {
						result = IResult.fail(e.getMessage());
					}

				} else {
					result = IResult.fail(ResultMessage.PARAMETER_ERROR);
				}
			} else {
				result = IResult.fail(ResultMessage.PARAMETER_ERROR);
			}
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(result)));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.fail("no permission"))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public void genrateLoginResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String username = request.headers().get("username");
		if (request.method().equals(HttpMethod.POST) && ChannelContext.hasChannel(username)) {
			IResult result = null;
			JSONObject body = parseJsonBody(request);
			if (body != null) {
				LoginVo vo = body.toJavaObject(LoginVo.class);
				if (StringUtils.isNoneBlank(vo.getLogin(), vo.getVerify())) {
					try {
						UserInfoVo user = userService.userLogin(vo);
						user.setChannel(username);
						result = IResult.ok(user);
					} catch (Exception e) {
						result = IResult.fail(e.getMessage());
					}
				} else {
					result = IResult.fail(ResultMessage.PARAMETER_ERROR);
				}
			} else {
				result = IResult.fail(ResultMessage.PARAMETER_ERROR);
			}
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(result)));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.fail("no permission"))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public void genrateUserCommentResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String username = request.headers().get("username");
		if (request.method().equals(HttpMethod.POST) && ChannelContext.hasChannel(username)) {
			IResult result = null;
			JSONObject body = parseJsonBody(request);
			if (body != null) {
				UserCommentVo vo = body.toJavaObject(UserCommentVo.class);
				if (StringUtils.isNoneBlank(vo.getContent(), vo.getCode())) {
					vo.setDeviceId(username);
					try {
						Comment comment = userService.saveUserComment(vo);
						result = IResult.ok(comment);
						ThreadPoolExecutorUtil.submit(() -> {
							ServerMessage message = MessageUtils.buildNotifyMessage(NotifyType.COMMENT.getCode(),
									JSON.toJSONString(comment));
							messageService.broadcastMessage(message);
						});
					} catch (Exception e) {
						result = IResult.fail(e.getMessage());
					}
				} else {
					result = IResult.fail(ResultMessage.PARAMETER_ERROR);
				}
			} else {
				result = IResult.fail(ResultMessage.PARAMETER_ERROR);
			}
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(result)));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.fail("no permission"))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public void genrateShowCommentResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String username = request.headers().get("username");
		if (request.method().equals(HttpMethod.POST) && ChannelContext.hasChannel(username)) {
			IResult result = null;
			JSONObject body = parseJsonBody(request);
			if (body != null) {
				CommentQueryVo vo = body.toJavaObject(CommentQueryVo.class);
				try {
					List<Comment> comments = userService.queryUserComment(vo);
					result = IResult.ok(comments);
				} catch (Exception e) {
					result = IResult.fail(e.getMessage());
				}
			} else {
				result = IResult.fail(ResultMessage.PARAMETER_ERROR);
			}
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(result)));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.fail("no permission"))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public void genrateUpdateUserResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String username = request.headers().get("username");
		String token = request.headers().get(ConstantsKey.TOKEN);
		if (request.method().equals(HttpMethod.POST) && StringUtils.isNoneBlank(token, username)
				&& ChannelContext.hasChannel(username) && userService.verifyToken(username, token)) {
			IResult result = null;
			JSONObject body = parseJsonBody(request);
			if (body != null) {
				updateUserInfoVo vo = body.toJavaObject(updateUserInfoVo.class);
				if (StringUtils.isNotBlank(vo.getNickname()) || StringUtils.isNotBlank(vo.getAvatar())) {
					try {
						vo.setUsername(username);
						userService.updateUserInfo(vo);
						result = IResult.ok();
					} catch (Exception e) {
						result = IResult.fail(e.getMessage());
					}
				} else {
					result = IResult.fail(ResultMessage.PARAMETER_ERROR);
				}
			} else {
				result = IResult.fail(ResultMessage.PARAMETER_ERROR);
			}
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(result)));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.fail("no permission"))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public void genrateResetResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String username = request.headers().get("username");
		if (request.method().equals(HttpMethod.POST) && ChannelContext.hasChannel(username)) {
			IResult result = null;
			JSONObject body = parseJsonBody(request);
			if (body != null) {
				ResetPasswordVo vo = body.toJavaObject(ResetPasswordVo.class);
				if (StringUtils.isNoneBlank(vo.getLogin(), vo.getPassword(), vo.getVerify())) {
					try {
						if (userService.resetPassword(vo)) {
							result = IResult.ok();
						} else {
							result = IResult.fail(ResultMessage.PARAMETER_ERROR);
						}
					} catch (Exception e) {
						result = IResult.fail(e.getMessage());
					}
				} else {
					result = IResult.fail(ResultMessage.PARAMETER_ERROR);
				}
			} else {
				result = IResult.fail(ResultMessage.PARAMETER_ERROR);
			}
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(result)));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.fail("no permission"))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public void genrateVerifyResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String username = request.headers().get("username");
		if (request.method().equals(HttpMethod.POST) && ChannelContext.hasChannel(username)) {
			IResult result = null;
			JSONObject body = parseJsonBody(request);
			if (body != null) {
				VerifyVo vo = body.toJavaObject(VerifyVo.class);
				if (StringUtils.isNotBlank(vo.getVerify())) {
					Map<String, Object> data = new HashMap<>();
					data.put("result", userService.verify(vo));
					result = IResult.ok(data);
				} else {
					result = IResult.fail(ResultMessage.PARAMETER_ERROR);
				}
			} else {
				result = IResult.fail(ResultMessage.PARAMETER_ERROR);
			}
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(result)));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.fail("no permission"))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public void genrateDeviceResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String realIp = IPUtil.getRealIp(request);
		if (StringUtils.isBlank(realIp)) {
			realIp = ChannelContext.getClientIp(ctx.channel());
		}
		String userAgent = request.headers().get("user-agent");
		String deviceInfo = request.headers().get("device-info");
		String deviceId = null;
		String channel = ConstantsKey.ANONYMOUS_CHANNELS.get(0);
		if (StringUtils.isNotBlank(deviceInfo)) {
			deviceId = MD5Util.encrypt(deviceInfo + realIp);
		} else {
			deviceId = MD5Util.encrypt(userAgent + realIp);
		}
		if (userService.existAnonymousInfo(deviceId)) {
			channel = ConstantsKey.ANONYMOUS_CHANNELS.get(1);
		} else {
			AnonymousInfo info = new AnonymousInfo();
			info.setDeviceId(deviceId);
			info.setUserAgent(userAgent);
			info.setIp(realIp);
			info.setDeviceInfo(deviceInfo);
			userService.saveAnonymousInfo(info);
		}
		Map<String, String> data = new HashMap<>();
		data.put("deviceId", deviceId);
		data.put("channel", channel);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
				Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.ok(data))));
		response.headers().set("Content-Type", "application/json");
		HttpUtil.setContentLength(response, response.content().readableBytes());
		ChannelFuture f = ctx.channel().writeAndFlush(response);
		if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}

	}

	public void genrateQiniuCallback(ChannelHandlerContext ctx, FullHttpRequest request) {
		String callbackAuthHeader = request.headers().get("Authorization");
		Auth auth = Auth.create(nettyProperties.getQiniuAk(), nettyProperties.getQiniuSk());
		ByteBuf jsonBuf = request.content();
		byte[] body = new byte[jsonBuf.readableBytes()];
		jsonBuf.readBytes(body);
		boolean result = auth.isValidCallback(callbackAuthHeader, nettyProperties.getQiniuCb(), body,
				"application/json");
		JSONObject data = JSON.parseObject(new String(body));
		log.info("qiniu callback {}", data);
		if (result) {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.ok())));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
					HttpResponseStatus.BAD_REQUEST,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.fail("validate error"))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	public void genrateFileTokenResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String username = request.headers().get("username");
		String token = request.headers().get(ConstantsKey.TOKEN);
		if (ChannelContext.hasChannel(username) && userService.verifyToken(username, token)) {
			Map<String, Object> data = new HashMap<>();
			String fileName = request.headers().get("fileName");
			String key = nettyProperties.getKeyPerfix() + DateUtil.format(new Date(), "YYYYMMDD") + "/"
					+ System.currentTimeMillis();
			if (StringUtils.isNotBlank(fileName) && fileName.contains(".")) {
				key += fileName.substring(fileName.lastIndexOf("."));
			}
			String result = ossService.getQiniuWebToken(nettyProperties.getQiniuAk(), nettyProperties.getQiniuSk(),
					nettyProperties.getQiniuBucket(), key, nettyProperties.getQiniuCb());
			data.put("token", result);
			data.put("key", key);
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.ok(data))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.fail("no permission"))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			f.addListener(ChannelFutureListener.CLOSE);
		}

	}

	public void genrateShareResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String username = request.headers().get("username");
		String token = request.headers().get(ConstantsKey.TOKEN);
		if (request.method().equals(HttpMethod.POST) && ChannelContext.hasChannel(username)
				&& userService.verifyToken(username, token)) {
			IResult result = null;
			JSONObject body = parseJsonBody(request);
			if (body != null) {
				FileVo vo = body.toJavaObject(FileVo.class);
				if (StringUtils.isNoneBlank(vo.getFullPath(), vo.getFileName(), vo.getDevice())) {
					userService.shareFile(vo);
					result = IResult.ok(vo);
				} else {
					result = IResult.fail(ResultMessage.PARAMETER_ERROR);
				}
			} else {
				result = IResult.fail(ResultMessage.PARAMETER_ERROR);
			}
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(result)));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.fail("no permission"))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			f.addListener(ChannelFutureListener.CLOSE);
		}

	}

	public void genrateFileInfoResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String username = request.headers().get("username");
		String code = request.headers().get("code");
		if (ChannelContext.hasChannel(username) && StringUtils.isNotBlank(code)) {
			FileVo vo = userService.getShareFileByCode(code);
			IResult result = null;
			if (vo != null) {
				result = IResult.ok(vo);
			} else {
				result = IResult.fail(ResultMessage.EXPIRED);
			}
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(result)));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.fail("no permission"))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			f.addListener(ChannelFutureListener.CLOSE);
		}

	}

	public void genrateAIChatResponse(ChannelHandlerContext ctx, FullHttpRequest request) {
		String username = request.headers().get("username");
		String token = request.headers().get(ConstantsKey.TOKEN);
		if (ChannelContext.hasChannel(username) && userService.verifyToken(username, token)) {
			IResult result = null;
			JSONObject body = parseJsonBody(request);
			if (body != null) {
				AiChatVo chat = body.toJavaObject(AiChatVo.class);
				if (StringUtils.isNotBlank(chat.getModel()) && chat.getMessages() != null
						&& !chat.getMessages().isEmpty()) {
					if (chat.isStream()) {
						handleStreamChat(ctx, request, chat, username);
						return;
					}
					AiChatResVo res = handleChat(username, chat);
					result = IResult.ok(res.getResponse());
				} else {
					result = IResult.fail(ResultMessage.PARAMETER_ERROR);
				}
			} else {
				result = IResult.fail(ResultMessage.PARAMETER_ERROR);
			}
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(result)));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			if (!HttpUtil.isKeepAlive(request) || response.status().code() != 200) {
				f.addListener(ChannelFutureListener.CLOSE);
			}
		} else {
			FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN,
					Unpooled.wrappedBuffer(JSON.toJSONBytes(IResult.fail("no permission"))));
			response.headers().set("Content-Type", "application/json");
			HttpUtil.setContentLength(response, response.content().readableBytes());
			ChannelFuture f = ctx.channel().writeAndFlush(response);
			f.addListener(ChannelFutureListener.CLOSE);
		}

	}

	private JSONObject parseJsonBody(FullHttpRequest request) {
		ByteBuf jsonBuf = request.content();
		String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
		if (StringUtils.isNotBlank(jsonStr)) {
			return JSON.parseObject(jsonStr);
		}
		return null;
	}

	private AiChatResVo handleChat(String username, AiChatVo chat) {
		AiChatResVo res = new AiChatResVo();
		List<AiMessage> messages = chatCache.getIfPresent(username);
		if (messages == null) {
			messages = new ArrayList<>();
		}
		if (messages.size() > nettyProperties.getChatToken() * 2) {
			res.setResponse("资源有限，请1小时后再次尝试");
			return res;
		}
		messages.addAll(chat.getMessages());
		chat.setMessages(messages);
		try {
			String result = OKHttpClientUtil.postJSON(nettyProperties.getAiApi(), null, JSON.toJSONString(chat));
			if (StringUtils.isNotBlank(result)) {
				JSONObject chatRsp = JSON.parseObject(result);
				JSONArray array = chatRsp.getJSONArray("choices");
				if (array != null) {
					AiMessage response = array.getJSONObject(0).getObject("message", AiMessage.class);
					messages.add(response);
					chatCache.put(username, messages);
					res.setSuccess(true);
					res.setResponse(response.getContent());
				}
			} else {
				res.setResponse("AI服务异常，请稍后再试");
			}
		} catch (Exception e) {
			res.setResponse("AI服务器下线了，有问题请联系管理员");
			log.error("接口异常", e);
		}
		return res;
	}

	public void handleStreamChat(ChannelHandlerContext ctx, FullHttpRequest request, AiChatVo chat, String username) {

		List<AiMessage> messages = chatCache.getIfPresent(username);
		if (messages == null) {
			messages = new ArrayList<>();
		} 
		messages.addAll(chat.getMessages());
		chat.setMessages(messages);
		try {
			final DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
			final String streamId = request.headers().get(ExtensionHeaderNames.STREAM_ID.text());
			if (null != streamId) {
				response.headers().set(ExtensionHeaderNames.STREAM_ID.text(), streamId);
			} 
			response.headers().set("Content-Type", "text/event-stream");
			response.headers().set("Connection", "keep-alive");
			response.headers().set("Cache-Control", "no-cache");
			response.headers().set("Access-Control-Allow-Origin", "*");
			ctx.writeAndFlush(response);
			if (messages.size() > nettyProperties.getChatToken() * 2) {
				final ByteBuf buffer = Unpooled.copiedBuffer(prepareData("资源有限，请1小时后再次尝试") , StandardCharsets.UTF_8);
				ChannelFuture future = ctx.writeAndFlush(new DefaultHttpContent(buffer));
				future.addListener(ChannelFutureListener.CLOSE);
				return;
			}
			AiMessage complete = new AiMessage();
			complete.setRole("assistant");
			complete.setContent("");
			OKHttpClientUtil.ssePostRequest(nettyProperties.getAiApi(), null, JSON.toJSONString(chat),
					new EventSourceListener() {
						private ChannelFuture f;

						@Override
						public void onClosed(EventSource eventSource) {
							super.onClosed(eventSource);
							if (f != null) {
								f.addListener(ChannelFutureListener.CLOSE);
							}else {
								final ByteBuf buffer = Unpooled.copiedBuffer(prepareData("AI服务异常，请稍后再试"), StandardCharsets.UTF_8);
								f = ctx.writeAndFlush(new DefaultHttpContent(buffer));
								f.addListener(ChannelFutureListener.CLOSE);
							}
							if(StringUtils.isNotBlank(complete.getContent())) {
								List<AiMessage> messages =chat.getMessages();
								messages.add(complete);
								chatCache.put(username, messages);
							}
							log.info("close ");
						}

						@Override
						public void onEvent(EventSource eventSource, String id, String type, String data) {
							AiMessage message = parseAIMessage(data);
							if (message != null&&StringUtils.isNotBlank(message.getContent())) {
								complete.setContent(complete.getContent() + message.getContent());
							}
							final StringBuilder msg = new StringBuilder(1024);
							msg.append("event:").append(type).append("\n");
							msg.append("data:").append(data).append("\n\n");
							final ByteBuf buffer = Unpooled.copiedBuffer(msg.toString(), StandardCharsets.UTF_8);
							f = ctx.writeAndFlush(new DefaultHttpContent(buffer));
						}

						@Override
						public void onFailure(EventSource eventSource, Throwable t, Response res) {
							if (f != null) {
								f.addListener(ChannelFutureListener.CLOSE);
							} else { 
								final ByteBuf buffer = Unpooled.copiedBuffer(prepareData("AI服务异常，请稍后再试"), StandardCharsets.UTF_8);
								ChannelFuture future = ctx.writeAndFlush(new DefaultHttpContent(buffer));
								future.addListener(ChannelFutureListener.CLOSE);
							}
							log.info("fail {}", res);
						}

					});
		} catch (IOException e) {
			log.error("请求错误", e); 
			final ByteBuf buffer = Unpooled.copiedBuffer(prepareData("AI服务异常，请稍后再试") , StandardCharsets.UTF_8);
			ChannelFuture future = ctx.writeAndFlush(new DefaultHttpContent(buffer));
			future.addListener(ChannelFutureListener.CLOSE);
		}

	}

	private AiMessage parseAIMessage(String msg) {
		if (StringUtils.isNotBlank(msg) && !msg.equals("[DONE]")) {
			JSONObject chatRsp = JSON.parseObject(msg);
			JSONArray array = chatRsp.getJSONArray("choices");
			if (array != null) {
				return array.getJSONObject(0).getObject("delta", AiMessage.class);

			}
		}
		return null;
	}
	
	private String prepareData(String data) {
		final StringBuilder msg = new StringBuilder(1024);
		JSONObject chat = new JSONObject();
		JSONObject delta = new JSONObject();
		JSONArray choices = new JSONArray();
		AiMessage message = new AiMessage();
		message.setContent(data);
		delta.put("delta", message);
		choices.add(delta);
		chat.put("choices", choices);
		msg.append("event:").append("null").append("\n");
		msg.append("data:").append(chat.toJSONString()).append("\n\n");
		return msg.toString();
	}
	

}

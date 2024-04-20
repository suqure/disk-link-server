package ltd.finelink.tool.disk.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.RequiredArgsConstructor;
import ltd.finelink.tool.disk.entity.system.EmailVerify;
import ltd.finelink.tool.disk.entity.system.MessageTemplate;
import ltd.finelink.tool.disk.entity.user.AnonymousInfo;
import ltd.finelink.tool.disk.entity.user.Auth;
import ltd.finelink.tool.disk.entity.user.Comment;
import ltd.finelink.tool.disk.entity.user.Info;
import ltd.finelink.tool.disk.enums.AuthType;
import ltd.finelink.tool.disk.enums.BizErrorCode;
import ltd.finelink.tool.disk.enums.EmailVerifyType;
import ltd.finelink.tool.disk.enums.LoginType;
import ltd.finelink.tool.disk.enums.ResetType;
import ltd.finelink.tool.disk.enums.VerifyType;
import ltd.finelink.tool.disk.exception.BizException;
import ltd.finelink.tool.disk.service.UserService;
import ltd.finelink.tool.disk.service.system.IEmailVerifyService;
import ltd.finelink.tool.disk.service.system.IMessageTemplateService;
import ltd.finelink.tool.disk.service.user.IAnonymousInfoService;
import ltd.finelink.tool.disk.service.user.IAuthService;
import ltd.finelink.tool.disk.service.user.ICommentService;
import ltd.finelink.tool.disk.service.user.IInfoService;
import ltd.finelink.tool.disk.utils.DateUtil;
import ltd.finelink.tool.disk.utils.MD5Util;
import ltd.finelink.tool.disk.utils.RandomStrUtils;
import ltd.finelink.tool.disk.utils.StringTemplateUtils;
import ltd.finelink.tool.disk.vo.CommentQueryVo;
import ltd.finelink.tool.disk.vo.FileVo;
import ltd.finelink.tool.disk.vo.LoginVo;
import ltd.finelink.tool.disk.vo.ResetPasswordVo;
import ltd.finelink.tool.disk.vo.SignupVo;
import ltd.finelink.tool.disk.vo.UserCommentVo;
import ltd.finelink.tool.disk.vo.UserInfoVo;
import ltd.finelink.tool.disk.vo.VerifyVo;
import ltd.finelink.tool.disk.vo.updateUserInfoVo;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final IAnonymousInfoService anonymousInfoService;

	private final IAuthService authService;

	private final IInfoService infoService;

	private final ICommentService commentService;

	private final IEmailVerifyService emailVerifyService;

	private final IMessageTemplateService messageTemplateService;

	private Cache<String, Auth> authCache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

	private Cache<String, Boolean> deviceCache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build();

	private Cache<String, FileVo> fileCache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build();

	@Override
	public AnonymousInfo findAnonymousInfoByDeviceId(String deviceId) {
		if (StringUtils.isNotBlank(deviceId)) {
			return anonymousInfoService.findByDeviceId(deviceId);
		}
		return null;
	}

	@Override
	public void saveAnonymousInfo(AnonymousInfo info) {
		anonymousInfoService.save(info);
		deviceCache.put(info.getDeviceId(), true);
	}

	@Override
	public boolean existAnonymousInfo(String deviceId) {
		if (StringUtils.isNotBlank(deviceId)) {
			Boolean exist = deviceCache.getIfPresent(deviceId);
			if (exist == null) {
				exist = anonymousInfoService.existByDeviceId(deviceId);
				deviceCache.put(deviceId, exist);
			}
			return exist;
		}
		return false;
	}

	@Override
	public Auth findAuthByUserName(String userName) {
		Auth auth = null;
		if (StringUtils.isNotBlank(userName)) {
			auth = authCache.getIfPresent(userName);
		}
		if (auth == null) {
			auth = authService.findByTypeAndAuthName(AuthType.USERNAME.getCode(), userName);
			if (auth != null) {
				authCache.put(userName, auth);
			}

		}
		return auth;
	}

	@Transactional
	@Override
	public EmailVerify createEmailVerify(EmailVerifyType type, String email) {
		boolean exit = infoService.checkEmailExist(email);
		EmailVerify verify = null;
		if (type.equals(EmailVerifyType.REG)) {
			if (!exit) {
				verify = new EmailVerify();
				verify.setCode(RandomStrUtils.randomIntStr(6));
				verify.setEmail(email);
				verify.setCreateTime(System.currentTimeMillis());
				verify.setEffectiveTime(DateUtil.addMinute(new Date(), 15).getTime());
				verify.setType(type.getCode());
				verify.setUsed(false);
				emailVerifyService.save(verify);
			}
		} else if (exit) {
			verify = new EmailVerify();
			verify.setCode(RandomStrUtils.randomIntStr(6));
			verify.setEmail(email);
			verify.setCreateTime(System.currentTimeMillis());
			verify.setEffectiveTime(DateUtil.addMinute(new Date(), 15).getTime());
			verify.setType(type.getCode());
			verify.setUsed(false);
			emailVerifyService.save(verify);
		}
		return verify;
	}

	@Transactional
	@Override
	public UserInfoVo userSiginup(SignupVo vo) {
		if (infoService.checkUsernameExist(vo.getUsername()) || infoService.checkEmailExist(vo.getEmail())) {
			throw new BizException(BizErrorCode.USER_EXIST);
		}
		if (!emailVerifyService.verifySignupEmail(vo.getEmail(), vo.getVerify())) {
			throw new BizException(BizErrorCode.USER_AUTH_PARAM_ERROR);
		}
		Info info = new Info();
		info.setAvatar(vo.getAvatar());
		info.setUserName(vo.getUsername());
		info.setEmail(vo.getEmail());
		info.setStatus(1);
		info.setRegisterTime(System.currentTimeMillis());
		info.setUpdateTime(System.currentTimeMillis());
		info.setRegisterIp(vo.getRegisterIp());
		info.setNickName(vo.getNickname());
		infoService.save(info);
		Auth auth = new Auth();
		auth.setUserId(info.getId());
		auth.setToken(MD5Util.encryptWithStal(vo.getPassword(), vo.getUsername()));
		auth.setType(AuthType.USERNAME.getCode());
		auth.setCreateTime(System.currentTimeMillis());
		auth.setAuthName(vo.getUsername());
		auth.setUpdateTime(System.currentTimeMillis());
		authService.save(auth);
		UserInfoVo user = new UserInfoVo();
		user.setAvatar(info.getAvatar());
		user.setEmail(info.getEmail());
		user.setToken(MD5Util.encryptWithStal(auth.getToken(), auth.getAuthName()));
		user.setUsername(info.getUserName());
		user.setNickname(info.getNickName());
		user.setCreateTime(info.getRegisterTime());
		return user;
	}

	@Override
	public UserInfoVo userLogin(LoginVo vo) {
		if (vo.getType() == LoginType.USERNAME.getCode()) {
			Info info = infoService.findByUsername(vo.getLogin());
			if (info == null) {
				throw new BizException(BizErrorCode.USER_NOT_EXIST);
			}
			Auth auth = findAuthByUserName(vo.getLogin());
			if (auth != null && auth.getToken().equals(MD5Util.encryptWithStal(vo.getVerify(), vo.getLogin()))) {
				UserInfoVo user = new UserInfoVo();
				user.setAvatar(info.getAvatar());
				user.setEmail(info.getEmail());
				user.setToken(MD5Util.encryptWithStal(auth.getToken(), auth.getAuthName()));
				user.setUsername(info.getUserName());
				user.setNickname(info.getNickName());
				user.setCreateTime(info.getRegisterTime());
				return user;
			}
		} else if (vo.getType() == LoginType.EMAIL.getCode()) {
			Info info = infoService.findByEmail(vo.getLogin());
			if (info == null) {
				throw new BizException(BizErrorCode.USER_NOT_EXIST);
			}
			if (!emailVerifyService.verifyLoginEmail(vo.getLogin(), vo.getVerify())) {
				throw new BizException(BizErrorCode.USER_AUTH_PARAM_ERROR);
			}
			Auth auth = findAuthByUserName(info.getUserName());
			UserInfoVo user = new UserInfoVo();
			user.setAvatar(info.getAvatar());
			user.setEmail(info.getEmail());
			user.setToken(MD5Util.encryptWithStal(auth.getToken(), auth.getAuthName()));
			user.setUsername(info.getUserName());
			user.setNickname(info.getNickName());
			user.setCreateTime(info.getRegisterTime());
			return user;
		}
		return null;
	}

	@Override
	public boolean verify(VerifyVo vo) {
		if (vo.getType() == VerifyType.USERNAME.getCode()) {
			return infoService.checkUsernameExist(vo.getVerify());
		} else if (vo.getType() == VerifyType.EMAIL.getCode()) {
			return infoService.checkEmailExist(vo.getVerify());
		}
		return false;
	}

	@Override
	public boolean resetPassword(ResetPasswordVo vo) {
		if (vo.getType() == ResetType.PASSWORD.getCode()) {
			Auth auth = findAuthByUserName(vo.getLogin());
			if (auth != null && auth.getToken().equals(MD5Util.encryptWithStal(vo.getVerify(), vo.getLogin()))) {
				auth.setToken(MD5Util.encryptWithStal(vo.getPassword(), auth.getAuthName()));
				auth.setUpdateTime(System.currentTimeMillis());
				authService.updateById(auth);
				return true;
			}
		} else if (vo.getType() == ResetType.EMAIL.getCode()) {
			if (!emailVerifyService.verifyResetEmail(vo.getLogin(), vo.getVerify())) {
				throw new BizException(BizErrorCode.USER_AUTH_PARAM_ERROR);
			}
			Info info = infoService.findByEmail(vo.getLogin());
			Auth auth = findAuthByUserName(info.getUserName());
			auth.setToken(MD5Util.encryptWithStal(vo.getPassword(), auth.getAuthName()));
			auth.setUpdateTime(System.currentTimeMillis());
			authService.updateById(auth);
			return true;
		}
		return false;
	}

	@Override
	public String renderMessage(String code, Map<String, String> params) {
		MessageTemplate template = messageTemplateService.findByCode(code);
		if (template != null && StringTemplateUtils.validateParams(template.getParams(), params)) {
			return StringTemplateUtils.renderMessage(template.getContent(), params);
		}
		return null;
	}

	@Override
	public boolean verifyToken(String username, String token) {
		Auth auth = findAuthByUserName(username);
		if (auth != null && MD5Util.encryptWithStal(auth.getToken(), username).equals(token)) {
			return true;
		}
		return false;
	}

	@Override
	public void updateUserInfo(updateUserInfoVo userInfo) {
		Info info = infoService.findByUsername(userInfo.getUsername());
		if (info != null) {
			if (StringUtils.isNotBlank(userInfo.getNickname())) {
				info.setNickName(userInfo.getNickname());
			}
			if (StringUtils.isNotBlank(userInfo.getAvatar())) {
				info.setAvatar(userInfo.getAvatar());
			}
			info.setUpdateTime(System.currentTimeMillis());
			infoService.updateById(info);
		}

	}

	@Override
	public Comment saveUserComment(UserCommentVo vo) {
		Comment comment = new Comment();
		comment.setType(vo.getType());
		comment.setCode(vo.getCode());
		comment.setComment(vo.getContent());
		comment.setDeviceId(vo.getDeviceId());
		comment.setCreateTime(System.currentTimeMillis());
		commentService.save(comment);
		return comment;

	}

	@Override
	public List<Comment> queryUserComment(CommentQueryVo vo) {

		return commentService.findLastComment(vo.getLastId(), vo.getRows());
	}

	@Override
	public void shareFile(FileVo file) {
		String code = RandomStrUtils.randomStr(8);
		file.setCode(code);
		fileCache.put(code, file);
	}

	@Override
	public FileVo getShareFileByCode(String code) {

		return fileCache.getIfPresent(code);
	}

}

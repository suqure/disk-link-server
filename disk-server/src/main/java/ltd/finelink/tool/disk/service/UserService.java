package ltd.finelink.tool.disk.service;

import java.util.List;
import java.util.Map;

import ltd.finelink.tool.disk.entity.system.EmailVerify;
import ltd.finelink.tool.disk.entity.user.AnonymousInfo;
import ltd.finelink.tool.disk.entity.user.Auth;
import ltd.finelink.tool.disk.entity.user.Comment;
import ltd.finelink.tool.disk.enums.EmailVerifyType;
import ltd.finelink.tool.disk.vo.CommentQueryVo;
import ltd.finelink.tool.disk.vo.FileVo;
import ltd.finelink.tool.disk.vo.LoginVo;
import ltd.finelink.tool.disk.vo.ResetPasswordVo;
import ltd.finelink.tool.disk.vo.SignupVo;
import ltd.finelink.tool.disk.vo.UserCommentVo;
import ltd.finelink.tool.disk.vo.UserInfoVo;
import ltd.finelink.tool.disk.vo.VerifyVo;
import ltd.finelink.tool.disk.vo.updateUserInfoVo;

public interface UserService {

	AnonymousInfo findAnonymousInfoByDeviceId(String deviceId);

	void saveAnonymousInfo(AnonymousInfo info);

	boolean existAnonymousInfo(String deviceId);

	Auth findAuthByUserName(String userName);
	
	EmailVerify createEmailVerify(EmailVerifyType type,String email);
	
	UserInfoVo userSiginup(SignupVo vo);
	
	UserInfoVo userLogin(LoginVo vo);
	
	boolean verify(VerifyVo vo);
	
	boolean resetPassword(ResetPasswordVo vo);
	
	String renderMessage(String code,Map<String,String> params);
	
	boolean verifyToken(String username,String token);
	
	void updateUserInfo(updateUserInfoVo userInfo);
	
	Comment saveUserComment(UserCommentVo vo);
	
	List<Comment> queryUserComment(CommentQueryVo vo);
	
	void shareFile(FileVo file);
	
	FileVo getShareFileByCode(String code);

}

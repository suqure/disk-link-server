package ltd.finelink.tool.disk.constants;

import java.util.Arrays;
import java.util.List;

public class ConstantsKey {

	public static final String USER_CHANNEL_PFIX = "U_C:";

	public static final String USER_CHANNEL = "USER_CHANNEL";

	public static final String USER_ID = "userId";

	public static final String TYPE = "type";

	public static final String APP_ID = "appId";
	
	public static final String DEVICE_CODE = "device-code";

	public static final String USERNAME = "username";

	public static final String WEBSOCKET = "websocket";

	public static final String UPGRADE = "Upgrade";

	public static final String WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";

	public static final String TOKEN = "token";

	public static final String SLASH_VAL = "/";

	public static final String COLON_VAL = ":";

	public static final String HTTP_PROTOL = "http://";

	public static final String SERVER_MESSAGE_ID = "sid";

	public static final String CLIENT_MESSAGE_ID = "cid";

	public static final String MESSAGE_TYPE = "msgType";

	public static final String SDK_VERSION = "sdk-version";

	public static final String REAL_IP = "real-ip";
	
	public static final String DEVICE_TYPE = "device-type";

	public static final String HEALTH_CHECK_PATH = "/health";

	public static final String DEVICE_PATH = "/device";
	
	public static final String FEEBACK_PATH = "/feeback";
	
	public static final String ICE_SERVER_PATH = "/iceServer";
	
	public static final String EMAIL_VERIFY_PATH = "/emailVerify";
	
	public static final String SIGN_UP_PATH = "/signup";
	
	public static final String LOGIN_PATH = "/login";
	
	public static final String VERIFY_PATH = "/verify";
	
	public static final String RESET_PASSWORD_PATH = "/resetPassword";
	
	public static final String UPDATE_USER_PATH = "/updateInfo";
	
	public static final String USER_COMMENT_PATH = "/userComment";
	
	public static final String SHOW_COMMENT_PATH = "/showComment";
	
	public static final String FILE_TOKEN_PATH = "/fileToken";
	
	public static final String FILE_SHARE_PATH = "/fileShare";
	
	public static final String FILE_INFO_PATH = "/fileInfo";
	
	public static final String AI_CHAT_PATH = "/aiChat";
	
	public static final String QINIU_CALLBACK_PATH = "/qiniuCb";
	
	public static final String VERIFY_CODE_CN = "verify_cn";
	
	public static final String VERIFY_CODE_EN = "verify_en";
	
	public static final List<String> ANONYMOUS_CHANNELS = Arrays.asList("DEFALUT", "SECOND");
	 
    public static final Integer NODE_BYTES_LEN = 26;
 
    public static final Integer PEER_BYTES_LEN = 6;

 
    public static final Integer BASIC_HASH_LEN = 20;
 
    public static final byte[] GET_METADATA_HANDSHAKE_PRE_BYTES = {19, 66, 105, 116, 84, 111, 114, 114, 101, 110, 116, 32, 112, 114,
            111, 116, 111, 99, 111, 108, 0, 0, 0, 0, 0, 16, 0, 1};
 
    public static final long METADATA_PIECE_SIZE = 16 << 10;

}
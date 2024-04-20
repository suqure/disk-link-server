package ltd.finelink.tool.disk.client.context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.lang3.StringUtils;

import dev.onvoid.webrtc.RTCDataChannel;
import dev.onvoid.webrtc.RTCPeerConnection;
import ltd.finelink.tool.disk.client.entity.DownloadFile;
import ltd.finelink.tool.disk.client.entity.UploadFile;
import ltd.finelink.tool.disk.client.entity.UserInfo;
import ltd.finelink.tool.disk.client.vo.IceServer;
import ltd.finelink.tool.disk.client.vo.SubcribeVo;

public class UserContext {

	public static volatile UserInfo currentUser;

	private static String DEFALUT_PATH;

	public static ConcurrentHashMap<String, SubcribeVo> deviceMap = new ConcurrentHashMap<>();

	public static ConcurrentHashMap<String, RTCPeerConnection> connections = new ConcurrentHashMap<>();

	public static volatile List<IceServer> iceServers = new ArrayList<>();

	public static ConcurrentHashMap<String, RTCDataChannel> localChannels = new ConcurrentHashMap<>();
	
	public static ConcurrentHashMap<String, RTCDataChannel> fileChannels = new ConcurrentHashMap<>();

	public static ConcurrentHashMap<String, RTCDataChannel> remoteChannels = new ConcurrentHashMap<>();
	
	public static final ConcurrentSkipListSet<String> uploadTasks = new ConcurrentSkipListSet<>();
	
	public static final ConcurrentHashMap<String, DownloadFile> writeTask = new ConcurrentHashMap<>();
	
	public static final ConcurrentHashMap<String, UploadFile> readTask = new ConcurrentHashMap<>();

	public static void closeConnection(String deviceCode) {
		RTCPeerConnection connection = connections.remove(deviceCode);
		RTCDataChannel localChannel = localChannels.remove(deviceCode);
		RTCDataChannel fileChannel = fileChannels.remove(deviceCode);
		RTCDataChannel remoteChannel = remoteChannels.remove(deviceCode);

		if (localChannel != null) {
			localChannel.unregisterObserver();
			localChannel.close();
			localChannel.dispose();
		}
		if (fileChannel != null) {
			fileChannel.unregisterObserver();
			fileChannel.close();
			fileChannel.dispose();
		}
		if (remoteChannel != null) {
			remoteChannel.unregisterObserver();
			remoteChannel.close();
			remoteChannel.dispose();
		}
		if (connection != null) {
			connection.close();
		}
	}
	
	public static void closeAllConnection() {
		for(String device:deviceMap.keySet()) {
			closeConnection(device);
		}
	}

	public static String getDownloadPath() {
		if (currentUser != null && currentUser.getPath() != null) {
			return currentUser.getPath();
		}
		if (StringUtils.isBlank(DEFALUT_PATH)) {
			String path = System.getProperty("user.home");
			File file = new File(path + File.separator + "disklink" + File.separator + "file" + File.separator);
			if (!file.exists()) {
				file.mkdirs();
			}
			DEFALUT_PATH = file.getPath() + File.separator;
		}

		return DEFALUT_PATH;
	}

}

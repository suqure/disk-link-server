package ltd.finelink.tool.disk.service;

import java.net.InetSocketAddress;
import java.util.Map;

import ltd.finelink.tool.disk.dto.MessageInfo;

public interface TorrentService {

	void fetchMagnetTask(String hashId);
	
	void handleUDPMessage(InetSocketAddress sender,Map<String,Object> data,MessageInfo messageInfo); 

}

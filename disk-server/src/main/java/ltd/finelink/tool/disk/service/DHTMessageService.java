package ltd.finelink.tool.disk.service;

import java.net.InetSocketAddress;
import java.util.List;

import ltd.finelink.tool.disk.dto.NodeInfo;

public interface DHTMessageService {
	
	void sendMessage(InetSocketAddress address,byte[] data);
	
	void sendPing(InetSocketAddress address);
	
	void responsePing(InetSocketAddress address,String messageId);
	
	void sendFindNode(InetSocketAddress address,String targetId);
	
	void responseFindNode(InetSocketAddress address,String messageId,List<NodeInfo> nodeList);
	
	void sendGetPeers(List<InetSocketAddress> addresses,String messageId,String infoHash);
	
	void responseGetPeers(InetSocketAddress address, String messageId,List<NodeInfo> nodeList);
	
	void responseAnnouncePeer(InetSocketAddress address, String messageId);

}

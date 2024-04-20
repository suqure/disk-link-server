package ltd.finelink.tool.disk.service;



import ltd.finelink.tool.disk.protobuf.ClientMessage;
import ltd.finelink.tool.disk.protobuf.ServerMessage;

public interface MessageService {
	/**
	 * 接收消息
	 * @param userId 发送用户id
	 * @param type 通道类型
	 * @param appId 应用id
	 * @param message 消息内容
	 * @return
	 */
	boolean receiveMessage(String userId,String type,ClientMessage message);
 
	
	/**
	 * 推送消息
	 * @param userId 接收用户id
	 * @param message 消息内容
	 * @return
	 */
	boolean sendMessage(String userId,ServerMessage message);
	
	/**
	 * 
	 * @param userId 接收用户id
	 * @param type 通道类型
	 * @param message 消息内容
	 * @return
	 */
	boolean sendMessage(String userId,String type,ServerMessage message);
	
	
	/**
	 * 处理上线通知
	 * @param userId
	 * @param type
	 * @return
	 */
	boolean hanlerOnlineNotify(String userId,String type);
	
	/**
	 * 处理下线通知
	 * @param userId
	 * @param type
	 * @return
	 */
	boolean hanlerOfflineNotify(String userId,String type);
	
	/**
	 * 广播消息
	 * @param message
	 * @return
	 */
	boolean broadcastMessage(ServerMessage message);
}

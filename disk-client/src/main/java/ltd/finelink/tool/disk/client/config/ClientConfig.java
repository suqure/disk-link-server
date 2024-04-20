package ltd.finelink.tool.disk.client.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import dev.onvoid.webrtc.PeerConnectionFactory;
import ltd.finelink.tool.disk.client.ClientListener;
import ltd.finelink.tool.disk.client.WebsocketClient;
import ltd.finelink.tool.disk.client.WsCredentials;
import ltd.finelink.tool.disk.client.context.UserContext;
import ltd.finelink.tool.disk.client.entity.UserInfo;
import ltd.finelink.tool.disk.client.listener.DiskServerListener;
import ltd.finelink.tool.disk.client.listener.MessageListener;
import ltd.finelink.tool.disk.client.listener.NotifyHandler;
import ltd.finelink.tool.disk.client.service.DownloadFileService;
import ltd.finelink.tool.disk.client.service.ShareDirService;
import ltd.finelink.tool.disk.client.service.ShareFileService;
import ltd.finelink.tool.disk.client.service.UploadFileService;
import ltd.finelink.tool.disk.client.service.UserInfoService;
import okhttp3.WebSocketListener;

@Configuration
@ComponentScan(value = { "ltd.finelink.tool.disk.client.*" })
@EnableJpaRepositories(basePackages = "ltd.finelink.tool.disk.client.*")
@EntityScan(basePackages = "ltd.finelink.tool.disk.client.*")
public class ClientConfig {

	@Bean
	@ConditionalOnMissingBean
	public WebSocketListener getWebSocketListenser(MessageListener messageListener) {
		ClientListener listener = new ClientListener(messageListener);
		return listener;
	}

	@Bean
	@ConditionalOnMissingBean
	public MessageListener getMessageListenser(ObjectProvider<NotifyHandler> notifyHandler,
			UserInfoService userInfoService, ShareFileService shareFileService, DownloadFileService downloadFileService,
			UploadFileService uploadFileService,ShareDirService shareDirService, PeerConnectionFactory peerConnectionFactory) {
		DiskServerListener messageListener = new DiskServerListener(notifyHandler.getIfAvailable(), userInfoService,
				shareFileService, downloadFileService,uploadFileService,shareDirService, peerConnectionFactory);
		return messageListener;
	}

	@Bean
	public WebsocketClient getWebSocketClient(ClientProperties properties, WebSocketListener listenser,
			UserInfoService userInfoService) {
		String username = properties.getUserId();
		String channel = properties.getChannel();
		String token =  properties.getToken();
		if (StringUtils.isBlank(username)) {
			UserInfo user = userInfoService.initUserInfo();
			username = user.getUsername();
			channel = user.getChannel();
			token = user.getToken();
			UserContext.currentUser = user;
		} else {
			UserInfo user = userInfoService.findByUsername(username);
			if (user == null) {
				user = new UserInfo();
				user.setChannel(channel);
				user.setUsername(username);
				user.setToken(properties.getToken());
				userInfoService.saveUser(user);
			}
			UserContext.currentUser = user;
		}
		String query = StringUtils.isNoneBlank(token)?properties.getAuth():properties.getQuery();
		WsCredentials credentials = new WsCredentials(username, channel, properties.getToken(), properties.getHost(),
				properties.getPort(), properties.getProtocol(), query);
		WebsocketClient client = new WebsocketClient(credentials);
		client.start(listenser);
		return client;
	}

	@Bean
	public PeerConnectionFactory peerConnectionFactory() {
		PeerConnectionFactory peerConnectionFactory = new PeerConnectionFactory();
		return peerConnectionFactory;
	}

}

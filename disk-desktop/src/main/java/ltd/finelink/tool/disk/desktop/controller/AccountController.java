package ltd.finelink.tool.disk.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.client.WebsocketClient;
import ltd.finelink.tool.disk.client.WsCredentials;
import ltd.finelink.tool.disk.client.config.ClientProperties;
import ltd.finelink.tool.disk.client.context.UserContext;
import ltd.finelink.tool.disk.client.entity.UserInfo;
import ltd.finelink.tool.disk.client.service.UserInfoService;

@Slf4j
@FXMLController
public class AccountController implements Initializable {

	@FXML
	public Button logoutBtn;
 
	@FXML
	public Label username;

	@FXML
	public Label nickname;
	
	@FXML
	public Label email;
	
	private boolean init;
	
	@Autowired
	private WebsocketClient client;
	@Autowired
	private ClientProperties properties;
	 

	@Autowired
	private UserInfoService userInfoService;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initAccount();
		logoutBtn.setOnAction(event -> {
			userInfoService.logout(); 
			changeCredentials();
			closeWindow();
		});
		 
		init = true;
	}
	
	private void initAccount() {
		UserInfo user= UserContext.currentUser;
		username.setText(user.getUsername());
		nickname.setText(user.getNickname());
		email.setText(user.getEmail());
		 
	}
	
	public void refreshAccount() {
		changeCredentials();
		if(init) {
			initAccount();
		}
	}

	private void changeCredentials() {
		UserInfo user =UserContext.currentUser;
		String query = StringUtils.isNoneBlank(user.getToken())?properties.getAuth():properties.getQuery();
		WsCredentials credentials = new WsCredentials(user.getUsername(), user.getChannel(),user.getToken(), properties.getHost(),
				properties.getPort(), properties.getProtocol(),query);
		client.changeCredentials(credentials);
		UserContext.deviceMap.clear();
	}
	
	public void closeWindow() {
		Stage stage = (Stage) logoutBtn.getScene().getWindow();
		stage.close();
	}
	
	

}

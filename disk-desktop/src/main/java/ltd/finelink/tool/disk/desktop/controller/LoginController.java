package ltd.finelink.tool.disk.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.client.context.UserContext;
import ltd.finelink.tool.disk.client.entity.UserInfo;
import ltd.finelink.tool.disk.client.service.UserInfoService;

@Slf4j
@FXMLController
public class LoginController implements Initializable {

	@FXML
	public Button loginBtn;

	@FXML
	public Button cancelBtn;
	 
	@FXML
	public TextField userAcc;

	@FXML
	public PasswordField userPwd;
	
	@FXML
	public CheckBox remCheck;

	@Autowired
	private UserInfoService userInfoService;
	@Autowired
	private AccountController accountController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loginBtn.setOnAction(event -> {
			if ( StringUtils.isBlank(userAcc.getText())|| StringUtils.isBlank(userPwd.getText())) {
				Alert alert = new Alert(Alert.AlertType.WARNING, "请输入用户名密码不允许为空!");
				alert.initOwner(loginBtn.getScene().getWindow());
				alert.showAndWait();
			} else { 
				UserInfo user = userInfoService.loginUser(UserContext.currentUser.getUsername(), userAcc.getText(), userPwd.getText(),remCheck.isSelected());
				if(user==null) {
					Alert alert = new Alert(Alert.AlertType.WARNING, "登录失败，用户名或密码错误");
					alert.initOwner(loginBtn.getScene().getWindow());
					alert.showAndWait();
				} else {
					userAcc.setText(null);
					userPwd.setText(null);
					remCheck.setSelected(false);
					accountController.refreshAccount();
					closeWindow();
				} 
			}
		});
		cancelBtn.setOnAction(event -> { 
			closeWindow();
		});

	}

	public void closeWindow() {
		Stage stage = (Stage) cancelBtn.getScene().getWindow();
		stage.close();
	}
	
	

}

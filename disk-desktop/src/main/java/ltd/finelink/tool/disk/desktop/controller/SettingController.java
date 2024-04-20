package ltd.finelink.tool.disk.desktop.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.client.context.UserContext;
import ltd.finelink.tool.disk.client.entity.UserInfo;
import ltd.finelink.tool.disk.client.service.UserInfoService;

@Slf4j
@FXMLController
public class SettingController implements Initializable {

	@FXML
	public Button saveButton;

	@FXML
	public Button cancelButton;
	@FXML
	public Button pathButton;

	@FXML
	public Text downloadPath;

	@FXML
	public PasswordField authPwd;

	@FXML
	public CheckBox authCheck;

	@FXML
	public RadioButton manual;

	@FXML
	public RadioButton autoConfirm;

	@FXML
	public RadioButton autoReject;

	@Autowired
	private UserInfoService userInfoService;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		ToggleGroup tg = new ToggleGroup();
		manual.setToggleGroup(tg);
		autoConfirm.setToggleGroup(tg);
		autoReject.setToggleGroup(tg);
		initSetting();
		
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("选择下载目录");
		pathButton.setOnAction(event -> {
			File file = chooser.showDialog(pathButton.getScene().getWindow());
			if (file != null) {
				downloadPath.setText(file.getPath() + File.separator);
			}
		});
		authCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
				authPwd.setVisible(authCheck.isSelected());
			}
		});
		saveButton.setOnAction(event -> {
			if (authCheck.isSelected() && StringUtils.isBlank(authPwd.getText())) {
				Alert alert = new Alert(Alert.AlertType.WARNING, "请输入认证密码，或者取消设备验证勾选!");
				alert.initOwner(saveButton.getScene().getWindow());
				alert.showAndWait();
			} else {
				UserInfo user = UserContext.currentUser;
				user.setRequirePwd(authCheck.isSelected());
				user.setPath(downloadPath.getText());
				user.setPassword(authPwd.getText());
				RadioButton rb = (RadioButton) tg.getSelectedToggle();
				String id = rb.getId();
				if ("manual".equals(id)) {
					user.setConfirmType(0);
				} else if ("autoConfirm".equals(id)) {
					user.setConfirmType(1);
				} else {
					user.setConfirmType(2);
				}
				userInfoService.saveUser(user);
				closeWindow();
			}
		});
		cancelButton.setOnAction(event -> {
			initSetting();
			closeWindow();
		});

	}

	public void closeWindow() {
		Stage stage = (Stage) cancelButton.getScene().getWindow();
		stage.close();
	}
	
	public void initSetting() {
		if (StringUtils.isNotBlank(UserContext.currentUser.getPassword())) {
			authPwd.setText(UserContext.currentUser.getPassword());
		}
		authPwd.setVisible(UserContext.currentUser.isRequirePwd());
		authCheck.setSelected(UserContext.currentUser.isRequirePwd());
		downloadPath.setText(UserContext.getDownloadPath());
		int type = UserContext.currentUser.getConfirmType();
		switch (type) {
		case 0:
			manual.setSelected(true);
			break;
		case 1:
			autoConfirm.setSelected(true);
			break;
		case 2:
			autoReject.setSelected(true);
			break;
		}
	}

}

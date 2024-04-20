package ltd.finelink.tool.disk.desktop.controller;

import java.net.URL;
import java.util.ResourceBundle;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@FXMLController
public class AboutController implements Initializable {

	@FXML
	public TextFlow  content;

	@FXML
	public ImageView logo;
	 

	@Override
	public void initialize(URL location, ResourceBundle resources) {
  
		logo.setImage(new Image("images/logo.png"));
		Text t1 = new Text("Disk Link APP是一款基于P2P的协议的文件传输客户端工具。\n\n");
		Text t2 = new Text("文件上传下载不经过服务器基于客户端点对点直接传输，保障文件的私密性。\n支持web端与APP端互传。\n\n");
		Text t3 = new Text("Web端地址 https://disk.finelink.ltd/ \n\n");
		Text t4 = new Text("产品由广州快链网络科技有限公司开发 官网：https://finelink.ltd/ \n\n");	
		Text t5 = new Text("联系邮箱support@finelink.ltd");
		content.getChildren().add(t1);
		content.getChildren().add(t2);
		content.getChildren().add(t3);
		content.getChildren().add(t4);
		content.getChildren().add(t5);
		
	}

	 

}

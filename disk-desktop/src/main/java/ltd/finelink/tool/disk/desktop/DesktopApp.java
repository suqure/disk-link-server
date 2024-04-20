package ltd.finelink.tool.disk.desktop;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;

@SpringBootApplication
public class DesktopApp extends AbstractJavaFxApplicationSupport {

	@Override
	public void stop() throws Exception {
		 System.exit(0);
	}

	public static void main(String[] args) {
		launch(DesktopApp.class, MainView.class,new CustomScreen(), args); 
	}

}

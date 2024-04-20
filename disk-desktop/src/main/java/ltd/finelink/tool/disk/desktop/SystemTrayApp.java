package ltd.finelink.tool.disk.desktop;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class SystemTrayApp {

	private static SystemTrayApp instance;
	private static MenuItem showItem;
	private static MenuItem exitItem;
	private static TrayIcon trayIcon;
	private static ActionListener showListener;
	private static ActionListener exitListener;
	private static MouseListener mouseListener;

	static {
		// 执行stage.close()方法,窗口不直接退出
		Platform.setImplicitExit(false);
		showItem = new MenuItem("Display");
		// 菜单项(退出)
		exitItem = new MenuItem("Exit");
		// 此处不能选择ico格式的图片,要使用16*16的png格式的图片
		InputStream img = SystemTrayApp.class.getClassLoader().getResourceAsStream("images/icon.png");
		Image image = null;
		try {
			image = ImageIO.read(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 系统托盘图标
		trayIcon = new TrayIcon(image);
		// 初始化监听事件(空)
		showListener = e -> Platform.runLater(() -> {
		});
		exitListener = e -> {
		};
		mouseListener = new MouseAdapter() {
		};
	}
	
	public static SystemTrayApp getInstance(Stage stage) {
        if (instance == null) {
            synchronized (SystemTrayApp.class){
                if (instance == null) {
                    instance = new SystemTrayApp(stage);
                }
            }
        }
        return instance;
    }
	
	private SystemTrayApp(Stage stage) {
        try {
            //检查系统是否支持托盘
            if (!SystemTray.isSupported()) {
                //系统托盘不支持
                return;
            }
            //设置图标尺寸自动适应
            trayIcon.setImageAutoSize(true);
            //系统托盘
            SystemTray tray = SystemTray.getSystemTray();
            //弹出式菜单组件
            final PopupMenu popup = new PopupMenu();
            popup.add(showItem);
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);
            //鼠标移到系统托盘,会显示提示文本
            trayIcon.setToolTip("Disk Link");
            listen(stage);
            tray.add(trayIcon);
        } catch (Exception e) {
            //系统托盘添加失败
        	log.error("添加系统失败",e); 
        }
    }
	
	/**
     * 更改系统托盘所监听的Stage
     */
    public void listen(Stage stage) {
        //防止报空指针异常
        if (showListener == null || exitListener == null || mouseListener == null || showItem == null || exitItem == null || trayIcon == null) {
            return;
        }
        //移除原来的事件
        showItem.removeActionListener(showListener);
        exitItem.removeActionListener(exitListener);
        trayIcon.removeMouseListener(mouseListener);
        //行为事件: 点击"打开"按钮,显示窗口
        showListener = e -> Platform.runLater(() -> showStage(stage));
        //行为事件: 点击"退出"按钮, 就退出系统
        exitListener = e -> {
            Platform.runLater(()->{
                System.exit(0);
            });
        };
        //鼠标行为事件: 单机显示stage
        mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //鼠标左键
                if (e.getButton() == MouseEvent.BUTTON1) {
                    showStage(stage);
                }
            }
        };
        //给菜单项添加事件
        showItem.addActionListener(showListener);
        exitItem.addActionListener(exitListener);
        //给系统托盘添加鼠标响应事件
        trayIcon.addMouseListener(mouseListener);
    }

    /**
     * 关闭窗口
     */
    public void hide(Stage stage) {
        Platform.runLater(() -> {
            //如果支持系统托盘,就隐藏到托盘,不支持就直接退出
            if (SystemTray.isSupported()) {
                //stage.hide()与stage.close()等价
                stage.hide();
            } else {
                System.exit(0);
            }
        });
    }

    /**
     * 点击系统托盘,显示界面(并且显示在最前面,将最小化的状态设为false)
     */
    private void showStage(Stage stage) {

        //点击系统托盘,
        Platform.runLater(() -> {
            if (stage.isIconified()) {
                stage.setIconified(false);
            }
            if (!stage.isShowing()) {
                stage.show();
            }
            stage.toFront();
        });
    }


}

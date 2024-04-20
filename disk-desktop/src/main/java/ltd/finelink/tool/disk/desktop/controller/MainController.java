package ltd.finelink.tool.disk.desktop.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;

import de.felixroske.jfxsupport.FXMLController;
import dev.onvoid.webrtc.RTCIceConnectionState;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.client.WebsocketClient;
import ltd.finelink.tool.disk.client.context.UserContext;
import ltd.finelink.tool.disk.client.entity.DownloadFile;
import ltd.finelink.tool.disk.client.entity.ShareDir;
import ltd.finelink.tool.disk.client.entity.ShareFile;
import ltd.finelink.tool.disk.client.entity.UploadFile;
import ltd.finelink.tool.disk.client.enums.ChannelDirType;
import ltd.finelink.tool.disk.client.enums.ChannelFileType;
import ltd.finelink.tool.disk.client.enums.ChannelMessageType;
import ltd.finelink.tool.disk.client.enums.FileReadType;
import ltd.finelink.tool.disk.client.enums.NotifyType;
import ltd.finelink.tool.disk.client.enums.WebRTCType;
import ltd.finelink.tool.disk.client.listener.DiskServerListener;
import ltd.finelink.tool.disk.client.service.DownloadFileService;
import ltd.finelink.tool.disk.client.service.ShareDirService;
import ltd.finelink.tool.disk.client.service.ShareFileService;
import ltd.finelink.tool.disk.client.service.UploadFileService;
import ltd.finelink.tool.disk.client.utils.ClientMessageUtil;
import ltd.finelink.tool.disk.client.utils.FileInfoUtil;
import ltd.finelink.tool.disk.client.vo.ChannelData;
import ltd.finelink.tool.disk.client.vo.ChannelMessage;
import ltd.finelink.tool.disk.client.vo.ConnectionMessage;
import ltd.finelink.tool.disk.client.vo.FileInfo;
import ltd.finelink.tool.disk.client.vo.FileReadMessage;
import ltd.finelink.tool.disk.client.vo.NotifyEvent;
import ltd.finelink.tool.disk.client.vo.SubcribeMessage;
import ltd.finelink.tool.disk.client.vo.SubcribeVo;
import ltd.finelink.tool.disk.client.vo.WebRTCVo;
import ltd.finelink.tool.disk.desktop.AboutView;
import ltd.finelink.tool.disk.desktop.AccountView;
import ltd.finelink.tool.disk.desktop.DesktopApp;
import ltd.finelink.tool.disk.desktop.LoginView;
import ltd.finelink.tool.disk.desktop.SettingView;
import ltd.finelink.tool.disk.desktop.ShareView;
import ltd.finelink.tool.disk.desktop.SystemTrayApp;
import ltd.finelink.tool.disk.desktop.config.ClientNotifyHanler;
import ltd.finelink.tool.disk.desktop.config.NotifyListener;
import ltd.finelink.tool.disk.desktop.utils.GUIRefreshUtils;
import ltd.finelink.tool.disk.desktop.vo.DownloadVo;
import ltd.finelink.tool.disk.desktop.vo.ShareDirVo;
import ltd.finelink.tool.disk.desktop.vo.ShareFileVo;
import ltd.finelink.tool.disk.desktop.vo.UploadVo;
import ltd.finelink.tool.disk.enums.SubscribeBodyType;
import ltd.finelink.tool.disk.enums.WebRTCBodyType;

@Slf4j
@FXMLController
public class MainController implements Initializable {

	private Stage primaryStage;

	@FXML
	public Button button;

	@FXML
	public Button refreshButton;
	@FXML
	public TextField deviceCode;

	@FXML
	public Text currentCode;

	@FXML
	public TableView<SubcribeVo> tableView;

	@Autowired
	private WebsocketClient websocketClient;

	@Autowired
	private DiskServerListener listener;

	@Autowired
	private ClientNotifyHanler notifyHanlder;

	@Autowired
	private ShareFileService shareFileService;

	@Autowired
	private DownloadFileService downloadFileService;

	@Autowired
	private UploadFileService uploadFileService;
	@Autowired
	private ShareDirService shareDirService;

	@Autowired
	private ShareController shareController;

	@FXML
	public TableColumn<SubcribeVo, String> deviceCol;

	@FXML
	public TableColumn<SubcribeVo, String> typeCol;

	@FXML
	public TableColumn<SubcribeVo, String> statusCol;

	@FXML
	public TableColumn<SubcribeVo, String> rttCol;

	@FXML
	public TableColumn<SubcribeVo, Integer> actionCol;

	@FXML
	public TableView<ShareFileVo> shareView;

	@FXML
	public TableColumn<ShareFileVo, String> nameCol;

	@FXML
	public TableColumn<ShareFileVo, String> sizeCol;

	@FXML
	public TableColumn<ShareFileVo, String> pathCol;

	@FXML
	public TableColumn<ShareFileVo, Boolean> checkCol;

	@FXML
	public Button fileButton;

	@FXML
	public Button deleteButton;

	@FXML
	public TableView<ShareDirVo> shareDirView;

	@FXML
	public TableColumn<ShareDirVo, String> nameCol2;

	@FXML
	public TableColumn<ShareDirVo, String> pathCol2;

	@FXML
	public TableColumn<ShareDirVo, Boolean> checkCol2;

	@FXML
	public Button dirButton;

	@FXML
	public Button dirDelButton;

	@FXML
	public TableView<DownloadVo> downloadView;

	@FXML
	public TableColumn<DownloadVo, String> fDeviceCol;

	@FXML
	public TableColumn<DownloadVo, String> fNameCol;

	@FXML
	public TableColumn<DownloadVo, String> fSizeCol;

	@FXML
	public TableColumn<DownloadVo, String> fPathCol;

	@FXML
	public TableColumn<DownloadVo, String> fStatusCol;

	@FXML
	public TableColumn<DownloadVo, Integer> fActionCol;

	@FXML
	public TableView<UploadVo> uploadView;

	@FXML
	public TableColumn<UploadVo, String> uDeviceCol;

	@FXML
	public TableColumn<UploadVo, String> uNameCol;

	@FXML
	public TableColumn<UploadVo, String> uSizeCol;

	@FXML
	public TableColumn<UploadVo, String> uPathCol;

	@FXML
	public TableColumn<UploadVo, String> uStatusCol;

	@FXML
	public TableColumn<UploadVo, Integer> uActionCol;
	@FXML
	public MenuItem infoItem;
	@FXML
	public MenuItem settingItem;
	@FXML
	public MenuItem aboutItem;
	
	@FXML
	public MenuItem accountItem;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		primaryStage = DesktopApp.getStage();
		SystemTrayApp.getInstance(primaryStage);
		primaryStage.setTitle("Disk Link");
		primaryStage.getIcons().add(new Image("images/icon.png"));
		initTableView();
		initShareView();
		initShareDirView();
		initDownloadView();
		initUploadView();
		infoItem.setOnAction(event -> {
			openDownloadDir();
		});
		settingItem.setOnAction(event -> {
			showSetting();
		});
		aboutItem.setOnAction(event -> {
			showAbout();
		});
		accountItem.setOnAction(event -> {
			showAccount();
		});
		refreshButton.setOnAction(event -> {
			String info = "更换设备码会断开连接，同时清除所有订阅的设备信息，请确定是否更换??";
			Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, info);
			confirm.setTitle("更换设备码");
			confirm.setHeaderText("更换设备码确认");
			Optional<ButtonType> result = confirm.showAndWait();
			if (result.get() == ButtonType.OK) {
				UserContext.closeAllConnection();
				SubcribeVo vo = new SubcribeVo();
				vo.setChannel(UserContext.currentUser.getChannel());
				vo.setCode(UserContext.currentUser.getCode());
				vo.setUserId(UserContext.currentUser.getUsername());
				websocketClient.sendClientMessage(
						ClientMessageUtil.buildSubscribMessage(SubscribeBodyType.CLOSE.getCode(), vo));
			}
		});
		notifyHanlder.addListener(NotifyType.CHANNEL, new NotifyListener() {

			@Override
			public void onEvent(NotifyEvent event) {

				if (event.getCode() == null && event.getData() != null) {
					ChannelMessage message = (ChannelMessage) event.getData();
					ChannelData data = message.getData();
					if (message.getType() == ChannelMessageType.FILE.getCode()) {
						JSONObject obj = (JSONObject) data.getData();
						FileInfo file = obj.toJavaObject(FileInfo.class);
						if (data.getType() == ChannelFileType.SEND.getCode()) {
							if (UserContext.currentUser.getConfirmType() == 0) {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										String info = "是否接受设备:" + file.getDevice() + "发送的文件'" + file.getName() + "'(大小:"
												+ FileInfoUtil.formatSize(file.getSize()) + ")?";
										Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, info);
										confirm.setTitle("确认信息");
										confirm.setHeaderText("文件接收确认");
										Optional<ButtonType> result = confirm.showAndWait();
										if (result.get() == ButtonType.OK) {
											listener.sendConfirmFileChannel(ChannelFileType.ACCEPT, file);
										} else {
											listener.sendConfirmFileChannel(ChannelFileType.REJECT, file);
										}
									}

								});
							}
							GUIRefreshUtils.refreshCombinePerSecond(downloadView);
						} else if (data.getType() == ChannelFileType.REJECT.getCode()) {
							for (int i = 0; i < uploadView.getItems().size(); i++) {
								UploadVo vo = uploadView.getItems().get(i);
								if (vo.getKey().equals(file.getId())) {
									uploadView.getItems().remove(i);
									break;
								}
							}
							GUIRefreshUtils.refreshCombinePerSecond(uploadView);

						} else if (data.getType() == ChannelFileType.ACCEPT.getCode()
								|| data.getType() == ChannelFileType.CONFIRM.getCode()) {
							for (UploadVo vo : uploadView.getItems()) {
								if (vo.getKey().equals(file.getId())) {
									vo.setStatus(0);
									break;
								}
							}
							GUIRefreshUtils.refreshCombinePerSecond(uploadView);
						} else if (data.getType() == ChannelFileType.CHUNK.getCode()) {
							boolean update = false;
							boolean refreshTable = false;
							for (DownloadVo vo : downloadView.getItems()) {
								if (vo.getKey().equals(file.getId())) {
									if (vo.getStatus() != 3) {
										vo.setChunks(file.getCurrent() + 1);
									}
									if (file.getTotal() == file.getCurrent() + 1) {
										vo.setStatus(2);
										refreshTable = true;
									}
									update = true;
									break;
								}
							}
							if (!update) {
								DownloadFile dowload = downloadFileService.findFileByKey(file.getId());
								dowload.setChunks(file.getCurrent() + 1);
								dowload.setTotal(file.getTotal());
								downloadView.getItems().add(DownloadVo.createVo(dowload));
							}
							GUIRefreshUtils.refreshCombinePerSecond(downloadView);
							if (refreshTable) {
								GUIRefreshUtils.refreshCombinePerSecond(tableView);
							}
						}
					} else if (message.getType() == ChannelMessageType.BASIC.getCode()) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								tableView.refresh();
							}

						});
					} else if (message.getType() == ChannelMessageType.DIR.getCode()) {
						shareController.refresh();
					}

				} else {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							Alert alert = new Alert(Alert.AlertType.WARNING, event.getMessage());
							alert.initOwner(primaryStage);
							alert.showAndWait();
						}

					});
				}

			}
		});

		notifyHanlder.addListener(NotifyType.UPLOAD, new NotifyListener() {

			@Override
			public void onEvent(NotifyEvent event) {

				if (event.getCode() == null && event.getData() != null) {
					FileReadMessage message = (FileReadMessage) event.getData();
					FileInfo file = message.getFile();
					boolean update = false;
					for (UploadVo vo : uploadView.getItems()) {
						if (vo.getDevice().equals(file.getDevice()) && vo.getKey().equals(file.getId())) {
							update = true;
							if (message.getType() == FileReadType.CLOSE.getCode()) {
								vo.setStatus(3);
								vo.setChunks(file.getCurrent());
							} else {
								vo.setChunks(file.getCurrent() + 1);
								vo.setStatus(1);
								vo.setTotal(file.getTotal());
							}
							if (vo.getTotal() == vo.getChunks()) {
								vo.setStatus(2);
							}
							break;
						}
					}
					if (!update) {
						UploadFile upload = uploadFileService.findFileByDeviceAndKey(file.getDevice(), file.getId());
						if (upload != null) {
							upload.setChunks(file.getCurrent() + 1);
							upload.setTotal(file.getTotal());
							uploadView.getItems().add(UploadVo.createVo(upload));
						}

					}
					GUIRefreshUtils.refreshCombinePerSecond(uploadView);
				}

			}
		});
	}

	public void showSetting() {
		DesktopApp.showView(SettingView.class, Modality.WINDOW_MODAL);
	}

	public void showAbout() {
		DesktopApp.showView(AboutView.class, Modality.WINDOW_MODAL);
	}
	
	public void showAccount() {
		if(StringUtils.isBlank(UserContext.currentUser.getToken())) {
			DesktopApp.showView(LoginView.class, Modality.WINDOW_MODAL);
		}else {
			DesktopApp.showView(AccountView.class, Modality.WINDOW_MODAL);
		}
		 
	}

	public void openDownloadDir() {
		String path = UserContext.getDownloadPath();
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.contains("win")) {
				Runtime.getRuntime().exec("explorer " + path);
			} else if (os.contains("mac")) {
				Runtime.getRuntime().exec("open " + path);

			} else if (os.contains("nix") || os.contains("nux") || os.contains("bsd")) {
				Runtime.getRuntime().exec("nautilus " + path);
			}
			return;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		Alert alert = new Alert(Alert.AlertType.ERROR, "不支持该系统目录打开，请自行打开系统目录：" + path);
		alert.initOwner(primaryStage);
		alert.showAndWait();

	}

	public void initTableView() {
		if (UserContext.currentUser != null) {
			currentCode.setText(UserContext.currentUser.getCode());
		}
		button.setOnAction(event -> {
			String value = deviceCode.getText();
			String message = "";
			if (StringUtils.isBlank(value) || UserContext.currentUser.getCode().equals(value)) {
				message = "请输入正确的设备码";
			} else if (UserContext.deviceMap.containsKey(value)) {
				message = "设备已经在列表中，无需添加";
			}
			if (StringUtils.isNotBlank(message)) {
				Alert alert = new Alert(Alert.AlertType.WARNING, message);
				alert.initOwner(primaryStage);
				alert.showAndWait();
			} else {
				SubcribeVo vo = new SubcribeVo();
				vo.setCode(value);
				websocketClient
						.sendClientMessage(ClientMessageUtil.buildSubscribMessage(SubscribeBodyType.SUB.getCode(), vo));
			}
		});

		ObservableList<SubcribeVo> items = FXCollections.observableArrayList();
		deviceCol.setCellValueFactory(new PropertyValueFactory<SubcribeVo, String>("code"));
		typeCol.setCellValueFactory(new PropertyValueFactory<SubcribeVo, String>("device"));

		statusCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<SubcribeVo, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<SubcribeVo, String> param) {
						SubcribeVo vo = param.getValue();
						String status = "";
						if (vo.getOnline() == 1) {
							status = "在线";
						} else if (vo.getOnline() == 0) {
							status = "离线";
						}
						if (vo.getStatus() != null) {
							if (vo.getStatus() == 1) {
								status = "已连接";
							}
							if (vo.getStatus() == 2) {
								status = "连接中";
							}
						}
						return new SimpleStringProperty(status);
					}
				});

		rttCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<SubcribeVo, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<SubcribeVo, String> param) {
						SubcribeVo vo = param.getValue();
						String status = "";
						if (vo.getStatus() != null && vo.getStatus() == 1 && vo.getRtt() != null) {
							status = vo.getRtt() + "ms";
						}
						return new SimpleStringProperty(status);
					}
				});
		actionCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<SubcribeVo, Integer>, ObservableValue<Integer>>() {

					@Override
					public ObservableValue<Integer> call(CellDataFeatures<SubcribeVo, Integer> param) {
						Integer status = param.getValue().getOnline() == 1 ? param.getValue().getStatus() : -1;
						return new SimpleIntegerProperty(status).asObject();
					}

				});
		actionCol.setCellFactory(new Callback<TableColumn<SubcribeVo, Integer>, TableCell<SubcribeVo, Integer>>() {
			@Override
			public TableCell<SubcribeVo, Integer> call(TableColumn<SubcribeVo, Integer> buttonTableColumn) {

				return new ButtonCell();
			}
		});
		tableView.setRowFactory(tv -> new TableRow<SubcribeVo>() {
			TableView<FileInfo> fileTable;

			@Override
			protected double computePrefHeight(double width) {
				if (getItem() != null) {
					if (fileTable == null) {
						fileTable = constructSubTable(getItem());
					}
					if (getItem().getStatus() == 1) {
						if (!getChildren().contains(fileTable)) {
							getChildren().add(fileTable);
						}
						return super.computePrefHeight(width) + fileTable.prefHeight(60);
					} else {
						this.getChildren().remove(fileTable);
					}

				}
				return super.computePrefHeight(width);
			}

			@Override
			protected void layoutChildren() {
				super.layoutChildren();
				if (fileTable != null && getItem() != null && getItem().getStatus() == 1) {
					double width = getWidth();
					double paneHeight = fileTable.prefHeight(width);
					fileTable.resizeRelocate(0, getHeight() - paneHeight, width, paneHeight);
				}
			}
		});
		tableView.setItems(items);
		items.addAll(UserContext.deviceMap.values());
		notifyHanlder.addListener(NotifyType.SUBCRIBE, new NotifyListener() {

			@Override
			public void onEvent(NotifyEvent event) {

				if (event.getCode() == null && event.getData() != null) {
					SubcribeMessage notify = (SubcribeMessage) event.getData();
					if (notify.getVo() == null) {
						return;
					}
					if (notify.getType() == SubscribeBodyType.CREATE.getCode()) {
						currentCode.setText(notify.getVo().getCode());
					} else if (notify.getType() == SubscribeBodyType.SUB.getCode()) { 
						boolean update = false;
						for (SubcribeVo vo : items) {
							if (vo.getCode().equals(notify.getVo().getCode())) {
								vo.setOnline(notify.getVo().getOnline());
								vo.setUserId(notify.getVo().getUserId());
								vo.setChannel(notify.getVo().getChannel());
								update = true;
								break;
							}
						}
						if (!update) {
							items.add(notify.getVo());
						}
					} else if (notify.getType() == SubscribeBodyType.MESSAGE.getCode()) {
						boolean update = false;
						for (SubcribeVo vo : items) {
							if (vo.getCode().equals(notify.getVo().getCode())) {
								vo.setOnline(notify.getVo().getOnline());
								vo.setUserId(notify.getVo().getUserId());
								vo.setChannel(notify.getVo().getChannel());
								update = true;
								break;
							}
						}
						if (!update) {
							items.add(notify.getVo());
						}
					} else if (notify.getType() == SubscribeBodyType.UNSUB.getCode()) {
						for (int i = 0; i < items.size(); i++) {
							if (items.get(i).getCode().equals(notify.getVo().getCode())) {
								items.remove(i);
								break;
							}
						}
					} else {
						items.clear();
					}
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							tableView.refresh();
						}
					});
				} else {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							Alert alert = new Alert(Alert.AlertType.WARNING, event.getMessage());
							alert.initOwner(primaryStage);
							alert.showAndWait();
						}
					});
				}

			}
		});

		notifyHanlder.addListener(NotifyType.CONNECT, new NotifyListener() {

			@Override
			public void onEvent(NotifyEvent event) {
				if (event.getData() != null) {
					ConnectionMessage notify = (ConnectionMessage) event.getData();
					int status = 0;
					if (notify.getState().equals(RTCIceConnectionState.NEW)
							|| notify.getState().equals(RTCIceConnectionState.CHECKING)) {
						status = 2;
					} else if (notify.getState().equals(RTCIceConnectionState.CONNECTED)
							|| notify.getState().equals(RTCIceConnectionState.COMPLETED)) {
						status = 1;
					} else {
						status = 0;
					}
					for (SubcribeVo vo : items) {
						if (vo.getCode().equals(notify.getCode())) {
							vo.setStatus(status);
							break;
						}
					}

				}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						tableView.refresh();
					}
				});
			}

		});
		notifyHanlder.addListener(NotifyType.WEBRTC, new NotifyListener() {

			@Override
			public void onEvent(NotifyEvent event) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						tableView.refresh();
					}
				});
			}

		});

	}

	private TableView<FileInfo> constructSubTable(SubcribeVo vo) {

		ObservableList<FileInfo> items = FXCollections.observableArrayList();
		if (vo.getFiles() != null && !vo.getFiles().isEmpty()) {
			items.addAll(vo.getFiles());
		}
		TableView<FileInfo> subTable = new TableView<FileInfo>();

		TableColumn<FileInfo, String> fileNameCol = new TableColumn<FileInfo, String>("文件名");
		fileNameCol.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("name"));
		fileNameCol.setPrefWidth(250);
		subTable.getColumns().add(fileNameCol);

		TableColumn<FileInfo, String> fileSizeCol = new TableColumn<FileInfo, String>("大小");
		fileSizeCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<FileInfo, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<FileInfo, String> param) {
						FileInfo file = param.getValue();
						return new SimpleStringProperty(FileInfoUtil.formatSize(file.getSize()));
					}
				});
		fileSizeCol.setPrefWidth(100);
		subTable.getColumns().add(fileSizeCol);

		TableColumn<FileInfo, String> fileFormatCol = new TableColumn<FileInfo, String>("类型");
		fileFormatCol.setCellValueFactory(new PropertyValueFactory<FileInfo, String>("format"));
		fileFormatCol.setPrefWidth(60);
		subTable.getColumns().add(fileFormatCol);

		TableColumn<FileInfo, String> fileStatusCol = new TableColumn<FileInfo, String>("状态");
		fileStatusCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<FileInfo, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<FileInfo, String> param) {
						FileInfo vo = param.getValue();
						String status = "";
						if (vo.getStatus() == 1) {
							status = "下载中";
						} else if (vo.getStatus() == 0) {
							status = "可下载";
						} else if (vo.getStatus() == 2) {
							status = "已下载";
						}

						return new SimpleStringProperty(status);
					}
				});
		fileStatusCol.setPrefWidth(80);
		subTable.getColumns().add(fileStatusCol);

		TableColumn<FileInfo, Integer> fileActonCol = new TableColumn<FileInfo, Integer>("操作");
		fileActonCol.setCellValueFactory(new PropertyValueFactory<FileInfo, Integer>("status"));

		fileActonCol.setCellFactory(new Callback<TableColumn<FileInfo, Integer>, TableCell<FileInfo, Integer>>() {
			@Override
			public TableCell<FileInfo, Integer> call(TableColumn<FileInfo, Integer> buttonTableColumn) {

				return new FileCell();
			}
		});
		fileActonCol.setPrefWidth(150);
		subTable.getColumns().add(fileActonCol);
		subTable.setItems(items);
		subTable.setPrefHeight(50 + (vo.getFiles().size() * 30));
		subTable.setStyle("-fx-border-color: #42bff4;");
		return subTable;
	}

	public void initShareView() {

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("选择文件");
		fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));

		ObservableList<ShareFileVo> items = FXCollections.observableArrayList();
		List<ShareFile> shareFlies = shareFileService.findAllShareFile();
		if (shareFlies != null && !shareFlies.isEmpty()) {
			for (ShareFile s : shareFlies) {
				items.add(ShareFileVo.createShareFileVo(s));
			}
		}
		nameCol.setCellValueFactory(new PropertyValueFactory<ShareFileVo, String>("name"));
		pathCol.setCellValueFactory(new PropertyValueFactory<ShareFileVo, String>("path"));
		sizeCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<ShareFileVo, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<ShareFileVo, String> param) {
						ShareFileVo file = param.getValue();
						return new SimpleStringProperty(FileInfoUtil.formatSize(file.getSize()));
					}
				});
		checkCol.setCellValueFactory(param -> param.getValue().getChecked());
		checkCol.setCellFactory(CheckBoxTableCell.forTableColumn(checkCol));
		shareView.setItems(items);
		shareView.setEditable(true);
		fileButton.setOnAction(event -> {
			boolean refresh = false;
			List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
			if (files != null) {
				for (File file : files) {
					ShareFile share = FileInfoUtil.transfer(file);
					ShareFile exist = shareFileService.findShareFileByKey(share.getKey());
					if (exist == null) {
						shareFileService.saveShareFile(share);
						items.add(ShareFileVo.createShareFileVo(share));
						refresh = true;
					}
				}
			}
			if (refresh) {
				shareView.refresh();
				listener.sendFileMessage();
			}
		});
		deleteButton.setOnAction(event -> {
			List<ShareFileVo> checked = new ArrayList<>();
			for (ShareFileVo vo : shareView.getItems()) {
				if (vo.getChecked().get() == true) {
					checked.add(vo);
					shareFileService.deleteShareFile(vo.getId());
				}
			}
			if (!checked.isEmpty()) {
				items.removeAll(checked);
				shareView.refresh();
				listener.sendFileMessage();
			} else {
				Alert alert = new Alert(Alert.AlertType.WARNING, "请选择要删除的分享");
				alert.initOwner(primaryStage);
				alert.showAndWait();
			}
		});

	}

	public void initShareDirView() {

		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("选择分享目录");

		ObservableList<ShareDirVo> items = FXCollections.observableArrayList();
		List<ShareDir> shareFlies = shareDirService.findAllShareDir();
		if (shareFlies != null && !shareFlies.isEmpty()) {
			for (ShareDir s : shareFlies) {
				items.add(ShareDirVo.createShareDirVo(s));
			}
		}
		nameCol2.setCellValueFactory(new PropertyValueFactory<ShareDirVo, String>("name"));
		pathCol2.setCellValueFactory(new PropertyValueFactory<ShareDirVo, String>("path"));

		checkCol2.setCellValueFactory(param -> param.getValue().getChecked());
		checkCol2.setCellFactory(CheckBoxTableCell.forTableColumn(checkCol2));
		shareDirView.setItems(items);
		shareDirView.setEditable(true);
		dirButton.setOnAction(event -> {
			boolean refresh = false;
			File file = chooser.showDialog(primaryStage);
			if (file != null) {
				ShareDir share = FileInfoUtil.transferDir(file);
				ShareDir exist = shareDirService.findShareDirByKey(share.getKey());
				if (exist == null) {
					shareDirService.saveShareDir(share);
					items.add(ShareDirVo.createShareDirVo(share));
					refresh = true;
				}
			}
			if (refresh) {
				shareDirView.refresh();
			}
		});
		dirDelButton.setOnAction(event -> {
			List<ShareDirVo> checked = new ArrayList<>();
			for (ShareDirVo vo : shareDirView.getItems()) {
				if (vo.getChecked().get() == true) {
					checked.add(vo);
					shareDirService.deleteShareDir(vo.getId());
				}
			}
			if (!checked.isEmpty()) {
				items.removeAll(checked);
				shareDirView.refresh();
			} else {
				Alert alert = new Alert(Alert.AlertType.WARNING, "请选择要删除的分享");
				alert.initOwner(primaryStage);
				alert.showAndWait();
			}
		});

	}

	public void initDownloadView() {
		ObservableList<DownloadVo> items = FXCollections.observableArrayList();

		List<DownloadFile> files = downloadFileService.findAllDownloadFile();
		if (files != null && !files.isEmpty()) {
			for (DownloadFile file : files) {
				if (file.getStatus() == null || file.getStatus() == 0 || file.getStatus() == 1) {
					file.setStatus(3);
					downloadFileService.updateDownloadFileStatus(file.getKey(), file.getStatus());
				}
				items.add(DownloadVo.createVo(file));
			}
		}

		fDeviceCol.setCellValueFactory(new PropertyValueFactory<DownloadVo, String>("device"));
		fNameCol.setCellValueFactory(new PropertyValueFactory<DownloadVo, String>("name"));
		fPathCol.setCellValueFactory(new PropertyValueFactory<DownloadVo, String>("path"));
		fSizeCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<DownloadVo, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<DownloadVo, String> param) {
						DownloadVo file = param.getValue();
						return new SimpleStringProperty(FileInfoUtil.formatSize(file.getSize()));
					}
				});
		fStatusCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<DownloadVo, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<DownloadVo, String> param) {
						DownloadVo file = param.getValue();
						String status = "";
						if (file.getStatus() == 0) {
							status = "排队中";
						} else if (file.getStatus() == 1) {
							status = "下载中";
						} else if (file.getStatus() == 2) {
							status = "已完成";
						} else if (file.getStatus() == 3) {
							status = "已暂停";
						}
						if (file.getChunks() > 0 && file.getChunks() < file.getTotal()) {
							BigDecimal pr = new BigDecimal(file.getChunks() * 100.0 / file.getTotal());
							status += " " + pr.setScale(2, BigDecimal.ROUND_DOWN).toString() + "%";
						}
						return new SimpleStringProperty(status);
					}
				});

		fActionCol.setCellValueFactory(new PropertyValueFactory<DownloadVo, Integer>("status"));

		fActionCol.setCellFactory(new Callback<TableColumn<DownloadVo, Integer>, TableCell<DownloadVo, Integer>>() {
			@Override
			public TableCell<DownloadVo, Integer> call(TableColumn<DownloadVo, Integer> buttonTableColumn) {

				return new DownloadCell();
			}
		});
		downloadView.setItems(items);

	}

	public void initUploadView() {
		ObservableList<UploadVo> items = FXCollections.observableArrayList();

		List<UploadFile> files = uploadFileService.findAllUploadFile();
		if (files != null && !files.isEmpty()) {
			for (UploadFile file : files) {
				if (file.getStatus() == null || file.getStatus() == 0 || file.getStatus() == 1) {
					file.setStatus(3);
					uploadFileService.updateUploadFileStatus(file.getDevice(), file.getKey(), file.getStatus());
				}
				items.add(UploadVo.createVo(file));
			}
		}

		uDeviceCol.setCellValueFactory(new PropertyValueFactory<UploadVo, String>("device"));
		uNameCol.setCellValueFactory(new PropertyValueFactory<UploadVo, String>("name"));
		uPathCol.setCellValueFactory(new PropertyValueFactory<UploadVo, String>("path"));
		uSizeCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<UploadVo, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<UploadVo, String> param) {
						UploadVo file = param.getValue();
						return new SimpleStringProperty(FileInfoUtil.formatSize(file.getSize()));
					}
				});
		uStatusCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<UploadVo, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<UploadVo, String> param) {
						UploadVo file = param.getValue();
						String status = "";
						if (file.getStatus() == 0) {
							status = "排队中";
						} else if (file.getStatus() == 1) {
							status = "上传中";
						} else if (file.getStatus() == 2) {
							status = "已完成";
						} else if (file.getStatus() == 3) {
							status = "已暂停";
						}
						if (file.getStatus() == -1) {
							status = "待确认";
						}
						if (file.getChunks() > 0 && file.getChunks() < file.getTotal()) {
							BigDecimal pr = new BigDecimal(file.getChunks() * 100.0 / file.getTotal());
							status += " " + pr.setScale(2, BigDecimal.ROUND_DOWN).toString() + "%";
						}
						return new SimpleStringProperty(status);
					}
				});

		uActionCol.setCellValueFactory(new PropertyValueFactory<UploadVo, Integer>("status"));

		uActionCol.setCellFactory(new Callback<TableColumn<UploadVo, Integer>, TableCell<UploadVo, Integer>>() {
			@Override
			public TableCell<UploadVo, Integer> call(TableColumn<UploadVo, Integer> buttonTableColumn) {

				return new UploadCell();
			}
		});
		uploadView.setItems(items);

	}

	private class ButtonCell extends TableCell<SubcribeVo, Integer> {

		final Button connectButton = new Button("连接");
		final Button sendButton = new Button("发送");
		final Button dirButton = new Button("目录");
		final Button authButton = new Button("认证");
		final Button closeButton = new Button("断开");
		final Button deleteButton = new Button("移除");
		final PasswordField password = new PasswordField();
		final HBox paddedButton = new HBox();

		/**
		 * AddPersonCell constructor
		 * 
		 * @param stage the stage in which the table is placed.
		 * @param table the table to which a new person can be added.
		 */
		ButtonCell() {

			paddedButton.setPadding(new Insets(3));
			paddedButton.getChildren().add(connectButton);
			password.setPromptText("请输入验证密码");
			password.setPrefWidth(120);
			connectButton.setPrefWidth(50);
			sendButton.setPrefWidth(50);
			closeButton.setPrefWidth(50);
			authButton.setPrefWidth(50);
			dirButton.setPrefWidth(50);
			deleteButton.setPrefWidth(50);
			connectButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					SubcribeVo vo = tableView.getItems().get(getIndex());
					listener.initWebRTC(websocketClient.getCurrentSocket(), vo.getCode());
					WebRTCVo rtc = new WebRTCVo();
					rtc.setCode(UserContext.currentUser.getCode());
					if (StringUtils.isNotBlank(vo.getPassword())) {
						rtc.setType(WebRTCType.AUTH.getValue());
						rtc.setPwd(vo.getPassword());
					} else {
						rtc.setType(WebRTCType.APPLY.getValue());
					}
					websocketClient.sendClientMessage(ClientMessageUtil
							.buildWebRTCMessage(WebRTCBodyType.APPLY.getCode(), vo.getUserId(), vo.getChannel(), rtc));
				}
			});
			dirButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					SubcribeVo vo = tableView.getItems().get(getIndex());

					listener.sendDirMessage(ChannelDirType.REQ, vo.getDir());
					shareController.bindData(vo.getDir());
					DesktopApp.showView(ShareView.class, Modality.WINDOW_MODAL);
				}
			});
			sendButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					SubcribeVo vo = tableView.getItems().get(getIndex());
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("选择文件");
					fileChooser.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
					File file = fileChooser.showOpenDialog(primaryStage);
					if (file != null) {
						listener.sendFileRequest(vo.getCode(), file);
					}

				}
			});
			authButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					if (StringUtils.isNotBlank(password.getText())) {
						SubcribeVo vo = tableView.getItems().get(getIndex());
						vo.setPassword(password.getText());
						WebRTCVo rtc = new WebRTCVo();
						rtc.setCode(UserContext.currentUser.getCode());
						rtc.setType(WebRTCType.AUTH.getValue());
						rtc.setPwd(password.getText());
						websocketClient.sendClientMessage(ClientMessageUtil.buildWebRTCMessage(
								WebRTCBodyType.APPLY.getCode(), vo.getUserId(), vo.getChannel(), rtc));
					} else {
						Alert alert = new Alert(Alert.AlertType.WARNING, "请输入验证密码");
						alert.initOwner(primaryStage);
						alert.showAndWait();
					}
				}
			});
			closeButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					SubcribeVo vo = tableView.getItems().get(getIndex());
					UserContext.closeConnection(vo.getCode());
					WebRTCVo rtc = new WebRTCVo();
					rtc.setCode(UserContext.currentUser.getCode());
					rtc.setType(WebRTCType.CLOSE.getValue());
					websocketClient.sendClientMessage(ClientMessageUtil
							.buildWebRTCMessage(WebRTCBodyType.CLOSE.getCode(), vo.getUserId(), vo.getChannel(), rtc));
				}
			});
			deleteButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					SubcribeVo vo = tableView.getItems().get(getIndex());
					UserContext.closeConnection(vo.getCode());
					if (!vo.getLocal()) {
						websocketClient.sendClientMessage(
								ClientMessageUtil.buildSubscribMessage(SubscribeBodyType.UNSUB.getCode(), vo)); 
					} 
					tableView.getItems().remove(getIndex());
					UserContext.deviceMap.remove(vo.getCode());
					
				}
			});
		}

		/** places an add button in the row only if the row is not empty. */
		@Override
		protected void updateItem(Integer item, boolean empty) {
			if (item != null) {
				paddedButton.getChildren().clear();
				if (item == 0) {
					paddedButton.getChildren().add(connectButton); 
				} else if (item == 1) {
					paddedButton.getChildren().add(dirButton);
					paddedButton.getChildren().add(sendButton);
					paddedButton.getChildren().add(closeButton);

				} else if (item == 3) {
					paddedButton.getChildren().add(password);
					paddedButton.getChildren().add(authButton);
				}else {
					paddedButton.getChildren().add(deleteButton);
				}
			}
			super.updateItem(item, empty);
			if (!empty) {
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				setGraphic(paddedButton);
			}
		}
	}

	private class FileCell extends TableCell<FileInfo, Integer> {

		final Button fileButton = new Button("下载");
		final HBox paddedButton = new HBox();

		/**
		 * AddPersonCell constructor
		 * 
		 * @param stage the stage in which the table is placed.
		 * @param table the table to which a new person can be added.
		 */
		FileCell() {

			paddedButton.setPadding(new Insets(3));
			paddedButton.getChildren().add(fileButton);
			fileButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {

					FileInfo vo = getTableView().getItems().get(getIndex());
					vo.setStatus(1);
					listener.dowanloadFile(vo.getDevice(), vo);
					getTableView().refresh();
				}
			});

		}

		/** places an add button in the row only if the row is not empty. */
		@Override
		protected void updateItem(Integer item, boolean empty) {
			if (item != null) {
				if (item == 0) {
					fileButton.setVisible(true);
				} else {
					fileButton.setVisible(false);
				}
			}
			super.updateItem(item, empty);
			if (!empty) {
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				setGraphic(paddedButton);
			}
		}
	}

	private class DownloadCell extends TableCell<DownloadVo, Integer> {

		final Button stopButton = new Button("暂停");
		final Button resumeButton = new Button("继续");
		final Button removeButton = new Button("删除");
		final HBox paddedButton = new HBox();

		/**
		 * AddPersonCell constructor
		 * 
		 * @param stage the stage in which the table is placed.
		 * @param table the table to which a new person can be added.
		 */
		DownloadCell() {

			paddedButton.setPadding(new Insets(3));
			paddedButton.getChildren().add(stopButton);
			paddedButton.getChildren().add(resumeButton);
			paddedButton.getChildren().add(removeButton);
			stopButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					DownloadVo vo = getTableView().getItems().get(getIndex());
					vo.setStatus(2);
					listener.sendStopDownloadChannel(vo.toFileInfo());
					getTableView().refresh();
				}
			});
			removeButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					DownloadVo vo = getTableView().getItems().get(getIndex());
					downloadFileService.deleteDownloadFile(vo.getId());
					getTableView().getItems().remove(getIndex());
					getTableView().refresh();
				}
			});
			resumeButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					DownloadVo vo = getTableView().getItems().get(getIndex());
					vo.setStatus(0);
					listener.sendDownloadChannel(vo.toFileInfo());
					getTableView().refresh();
				}
			});
		}

		/** places an add button in the row only if the row is not empty. */
		@Override
		protected void updateItem(Integer item, boolean empty) {
			if (item != null) {
				if (item == 0 || item == 1) {
					stopButton.setVisible(true);
					resumeButton.setVisible(false);
					removeButton.setVisible(false);
				} else if (item == 2) {
					stopButton.setVisible(false);
					resumeButton.setVisible(false);
					removeButton.setVisible(true);
				} else {
					stopButton.setVisible(false);
					resumeButton.setVisible(true);
					removeButton.setVisible(true);
				}
			}
			super.updateItem(item, empty);
			if (!empty) {
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				setGraphic(paddedButton);
			}
		}
	}

	private class UploadCell extends TableCell<UploadVo, Integer> {

		final Button stopButton = new Button("暂停");
		final Button removeButton = new Button("删除");
		final HBox paddedButton = new HBox();

		/**
		 * AddPersonCell constructor
		 * 
		 * @param stage the stage in which the table is placed.
		 * @param table the table to which a new person can be added.
		 */
		UploadCell() {

			paddedButton.setPadding(new Insets(3));
			paddedButton.getChildren().add(stopButton);
			paddedButton.getChildren().add(removeButton);
			stopButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					UploadVo vo = uploadView.getItems().get(getIndex());
					vo.setStatus(2);

					uploadView.refresh();
				}
			});
			removeButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					UploadVo vo = uploadView.getItems().get(getIndex());
					listener.removeUploadFile(vo.getDevice(), vo.getKey());
					uploadView.getItems().remove(getIndex());
					uploadView.refresh();
				}
			});

		}

		/** places an add button in the row only if the row is not empty. */
		@Override
		protected void updateItem(Integer item, boolean empty) {
			if (item != null) {
				if (item == 0 || item == 1) {
					stopButton.setVisible(true);
					removeButton.setVisible(false);
				} else if (item == 2) {
					stopButton.setVisible(false);
					removeButton.setVisible(true);
				} else {
					stopButton.setVisible(false);
					removeButton.setVisible(true);
				}
			}
			super.updateItem(item, empty);
			if (!empty) {
				setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
				setGraphic(paddedButton);
			}
		}
	}

}

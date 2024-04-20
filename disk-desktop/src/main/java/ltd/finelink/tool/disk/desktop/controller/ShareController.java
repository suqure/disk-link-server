package ltd.finelink.tool.disk.desktop.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;

import de.felixroske.jfxsupport.FXMLController;
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
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import lombok.extern.slf4j.Slf4j;
import ltd.finelink.tool.disk.client.entity.DownloadFile;
import ltd.finelink.tool.disk.client.enums.ChannelDirType;
import ltd.finelink.tool.disk.client.listener.DiskServerListener;
import ltd.finelink.tool.disk.client.service.DownloadFileService;
import ltd.finelink.tool.disk.client.utils.FileInfoUtil;
import ltd.finelink.tool.disk.client.vo.DirInfo;

@Slf4j
@FXMLController
public class ShareController implements Initializable {

	private DirInfo current;

	private ObservableList<DirInfo> items = FXCollections.observableArrayList();

	private List<DirInfo> breadCrumb = new ArrayList<>();

	@Autowired
	private DiskServerListener listener;
	@Autowired
	private DownloadFileService downloadFileService;

	@FXML
	public HBox menuBox;

	@FXML
	public TableView<DirInfo> dirView;

	@FXML
	public TableColumn<DirInfo, String> typeCol;

	@FXML
	public TableColumn<DirInfo, String> nameCol;

	@FXML
	public TableColumn<DirInfo, String> sizeCol;

	@FXML
	public TableColumn<DirInfo, Integer> actionCol;

	private boolean init;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		initData();

		dirView.setItems(items);

		nameCol.setCellValueFactory(new PropertyValueFactory<DirInfo, String>("name"));

		sizeCol.setCellValueFactory(new PropertyValueFactory<DirInfo, String>("device"));

		sizeCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<DirInfo, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<DirInfo, String> param) {
						DirInfo file = param.getValue();
						String result = "";
						if (file.getType() == 1) {
							result = FileInfoUtil.formatSize(file.getSize());
						}
						return new SimpleStringProperty(result);
					}
				});

		typeCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<DirInfo, String>, ObservableValue<String>>() {

					@Override
					public ObservableValue<String> call(CellDataFeatures<DirInfo, String> param) {
						DirInfo vo = param.getValue();
						String status = "";
						if (vo.getType() == 1) {
							status = "文件";
						} else {
							status = "文件夹";
						}

						return new SimpleStringProperty(status);
					}
				});

		actionCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<DirInfo, Integer>, ObservableValue<Integer>>() {

					@Override
					public ObservableValue<Integer> call(CellDataFeatures<DirInfo, Integer> param) {
						Integer type = param.getValue().getType();
						return new SimpleIntegerProperty(type).asObject();
					}

				});
		actionCol.setCellFactory(new Callback<TableColumn<DirInfo, Integer>, TableCell<DirInfo, Integer>>() {
			@Override
			public TableCell<DirInfo, Integer> call(TableColumn<DirInfo, Integer> buttonTableColumn) {

				return new ButtonCell();
			}
		});
		init = true;
	}

	private void initData() {
		if (current != null) {
			if (current.getChildren() != null && !current.getChildren().isEmpty()) {
				items.addAll(current.getChildren());
			}
			breadCrumb.add(current);
			Hyperlink text = new Hyperlink(current.getName());
			text.setId(current.getId());
			text.setOnMouseClicked(event -> {
				current = breadCrumb.get(0);
				listener.sendDirMessage(ChannelDirType.REQ, current);
				if (breadCrumb.size() > 1) {
					menuBox.getChildren().remove(1, breadCrumb.size());
					breadCrumb.clear();
					breadCrumb.add(current);
					items.clear();
					items.addAll(current.getChildren());
				}
			});
			if (menuBox != null) {
				menuBox.getChildren().add(text);
			}

		}
	}

	public void bindData(DirInfo vo) {
		current = vo;
		items.clear();
		if (menuBox != null) {
			menuBox.getChildren().clear();
		}
		breadCrumb.clear();
		initData();
	}

	public void refresh() {
		if (init) {
			items.clear();
			if (current.getChildren() != null && !current.getChildren().isEmpty()) {
				items.addAll(current.getChildren());
			}

			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					dirView.refresh();
				}

			});
		}
	}

	private class ButtonCell extends TableCell<DirInfo, Integer> {

		final Button openButton = new Button("打开");
		final Button downloadButton = new Button("下载");

		final HBox paddedButton = new HBox();

		/**
		 * AddPersonCell constructor
		 * 
		 * @param stage the stage in which the table is placed.
		 * @param table the table to which a new person can be added.
		 */
		ButtonCell() {

			paddedButton.setPadding(new Insets(3));
			paddedButton.getChildren().add(openButton);
			openButton.setPrefWidth(50);
			downloadButton.setPrefWidth(50);

			openButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					DirInfo dir = dirView.getItems().get(getIndex());
					current = dir;
					listener.sendDirMessage(ChannelDirType.REQ, current);
					items.clear();
					if (current.getChildren() != null && !current.getChildren().isEmpty()) {
						items.addAll(current.getChildren());
					}
					Hyperlink text = new Hyperlink(">" + dir.getName());
					text.setId(dir.getId());
					text.setOnMouseClicked(event -> {
						current = dir;
						listener.sendDirMessage(ChannelDirType.REQ, current);
						items.clear();
						items.addAll(current.getChildren());
						int index = 0;
						for (int i = 0; i < breadCrumb.size(); i++) {
							if (dir.getId().equals(breadCrumb.get(i).getId())) {
								index = i;
								break;
							}
						}
						if (index < breadCrumb.size() - 1) {
							menuBox.getChildren().remove(index + 1, breadCrumb.size());
							breadCrumb.subList(index + 1, breadCrumb.size()).clear();
						}

					});
					breadCrumb.add(dir);
					menuBox.getChildren().add(text);
					dirView.refresh();
				}
			});
			downloadButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					DirInfo dir = dirView.getItems().get(getIndex());
					DownloadFile download = downloadFileService.findFileByKey(dir.getId());
					if (download != null) {
						Alert alert = new Alert(Alert.AlertType.WARNING, "文件已经添加进下载任务");
						alert.initOwner(downloadButton.getScene().getWindow());
						alert.showAndWait();
					} else {
						listener.sendDirMessage(ChannelDirType.FILE, dir);
					}
				}
			});

		}

		/** places an add button in the row only if the row is not empty. */
		@Override
		protected void updateItem(Integer item, boolean empty) {
			if (item != null) {
				paddedButton.getChildren().clear();
				if (item == 0) {
					paddedButton.getChildren().add(openButton);

				} else if (item == 1) {
					paddedButton.getChildren().add(downloadButton);

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

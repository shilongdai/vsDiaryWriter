package net.viperfish.journal2;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.InputEvent;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalI18NBundle;
import net.viperfish.journal2.transaction.JournalServices;

public final class JournalController implements Initializable {

	@FXML
	private ListView<Journal> journalList;

	@FXML
	private TextField searchText;

	@FXML
	private TextField keywordText;

	@FXML
	private HTMLEditor journalEditor;

	@FXML
	private Button saveBtn;

	private String currentText;

	private boolean itemChanged;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		itemChanged = false;
		journalList.setCellFactory(new Callback<ListView<Journal>, ListCell<Journal>>() {

			@Override
			public ListCell<Journal> call(ListView<Journal> param) {
				return new ListCell<Journal>() {

					@Override
					protected void updateItem(Journal item, boolean empty) {
						super.updateItem(item, empty);
						if (item != null) {
							setText(item.getTimestamp().toString());
						} else {
							setGraphic(null);
							setText("");
						}
					}

				};
			}
		});

		journalList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Journal>() {

			@Override
			public void changed(ObservableValue<? extends Journal> observable, Journal oldValue, Journal newValue) {
				if (newValue != null) {
					itemChanged = true;
					keywordText.setText(newValue.getSubject());
					journalEditor.setHtmlText(newValue.getContent());
					currentText = newValue.getContent();
				} else {
					keywordText.setText("");
					journalEditor.setHtmlText("");
				}
			}
		});

		journalEditor.addEventHandler(InputEvent.ANY, new EventHandler<InputEvent>() {

			@Override
			public void handle(InputEvent arg0) {
				if (!journalEditor.getHtmlText().equals(currentText)
						&& journalList.getSelectionModel().getSelectedItem() != null) {
					if (!itemChanged) {
						saveBtn.setDisable(false);
					}
					itemChanged = false;
				}
			}

		});

		keywordText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (journalList.getSelectionModel().getSelectedItem() != null) {
					if (!itemChanged) {
						saveBtn.setDisable(false);
					}
					itemChanged = false;
				}
			}

		});

		searchText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				refreshList(0);
			}
		});
		refreshList(0);
		saveBtn.setDisable(true);
	}

	@FXML
	public void newJournal(ActionEvent e) {
		Service<Journal> addEmpty = JournalServices.newSaveJournalService(new Journal());
		addEmpty.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				refreshList(0);
			}
		});
		addEmpty.start();
	}

	@FXML
	public void saveJournal(ActionEvent e) {
		Journal selected = journalList.getSelectionModel().getSelectedItem();
		selected.setSubject(keywordText.getText());
		selected.setContent(journalEditor.getHtmlText());
		Service<Journal> save = JournalServices.newSaveJournalService(selected);
		save.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				event.getSource().getException().printStackTrace();
			}
		});
		save.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				refreshList(journalList.getSelectionModel().getSelectedIndex());
			}
		});
		save.start();
		saveBtn.setDisable(true);
	}

	@FXML
	public void deleteJournal(ActionEvent e) {
		final Journal selected = journalList.getSelectionModel().getSelectedItem();
		long id = selected.getId();

		Service<Journal> delete = JournalServices.newRemoveJournalService(id);
		delete.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				refreshList(0);
			}
		});
		delete.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				event.getSource().getException().printStackTrace();
			}
		});
		delete.start();
	}

	@FXML
	public void changePassword(ActionEvent e) throws IOException {
		Stage changePasswordWindow = new Stage();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/changePassword.fxml"),
				JournalI18NBundle.getBundle());
		fxmlLoader.setController(new ChangePasswordController());
		Parent newPassword = fxmlLoader.load();
		Scene scene = new Scene(newPassword, 400, 190);
		changePasswordWindow.initStyle(StageStyle.UNDECORATED);
		changePasswordWindow.setScene(scene);
		changePasswordWindow.showAndWait();
	}

	@FXML
	public void openConfigWindow(ActionEvent e) throws IOException {
		Stage changePasswordWindow = new Stage();
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Config.fxml"),
				JournalI18NBundle.getBundle());
		fxmlLoader.setController(new ConfigController());
		Parent newPassword = fxmlLoader.load();
		Scene scene = new Scene(newPassword, 511, 340);
		changePasswordWindow.initStyle(StageStyle.DECORATED);
		changePasswordWindow.setScene(scene);
		changePasswordWindow.showAndWait();
	}

	private void refreshList(final int select) {
		if (searchText.getText().trim().isEmpty()) {
			Service<Collection<Journal>> getAll = JournalServices.newGetAllService();
			getAll.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent event) {
					journalList.getItems().clear();
					@SuppressWarnings("unchecked")
					Collection<Journal> result = (Collection<Journal>) event.getSource().getValue();
					ObservableList<Journal> toView = FXCollections.observableArrayList(result);
					currentText = journalEditor.getHtmlText();
					journalList.setItems(toView);
					journalList.getSelectionModel().select(select);
					saveBtn.setDisable(true);
				}
			});
			getAll.setOnFailed(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent event) {
					event.getSource().getException().printStackTrace();
					Alert error = new Alert(AlertType.ERROR, "Cannot get all entries");
					error.showAndWait();
				}
			});
			getAll.start();
		} else {
			Service<Collection<Journal>> serv = JournalServices.newSearchDateRangeService(searchText.getText(),
					new Date(Long.MIN_VALUE), new Date());
			serv.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					journalList.getItems().clear();
					@SuppressWarnings("unchecked")
					Collection<Journal> result = (Collection<Journal>) event.getSource().getValue();
					ObservableList<Journal> toView = FXCollections.observableArrayList(result);
					currentText = new String();
					journalList.setItems(toView);
					journalList.getSelectionModel().select(select);
					saveBtn.setDisable(true);
				}
			});
			serv.setOnFailed(new EventHandler<WorkerStateEvent>() {

				@Override
				public void handle(WorkerStateEvent event) {
					event.getSource().getException().printStackTrace();
					Alert error = new Alert(AlertType.ERROR, "Cannot get all entries");
					error.showAndWait();
				}
			});
			serv.start();
		}
	}

}

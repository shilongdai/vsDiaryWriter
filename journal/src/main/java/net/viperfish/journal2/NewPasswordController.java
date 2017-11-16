/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.journal2;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import net.viperfish.journal2.transaction.JournalServices;

/**
 *
 * @author shilongdai
 */
public class NewPasswordController implements Initializable {

	@FXML
	private PasswordField firstTimeText;

	@FXML
	private PasswordField secondTimeText;

	@FXML
	private Button loginButton;

	@FXML
	private Label warningText;

	@FXML
	private ProgressIndicator progress;

	public NewPasswordController() {
	}

	@FXML
	public void setPassword(ActionEvent e) {
		progress.setVisible(true);
		final Service<Void> setPassword = JournalServices.newSetPasswordService(firstTimeText.getText());
		System.out.printf("state: %s\n", setPassword.getState().toString());
		setPassword.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				System.out.println("Success!");
				progress.setVisible(false);
				((Stage) loginButton.getScene().getWindow()).close();
			}
		});
		setPassword.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				event.getSource().getException().printStackTrace();
			}
		});
		setPassword.start();
	}

	@FXML
	public void cancelSetPassword(ActionEvent e) {
		Platform.exit();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		firstTimeText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				warningText.setVisible(false);
				loginButton.setDisable(false);
				if (!isMatch()) {
					warningText.setVisible(true);
					loginButton.setDisable(true);
				}
			}

		});
		secondTimeText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				warningText.setVisible(false);
				loginButton.setDisable(false);
				if (!isMatch()) {
					warningText.setVisible(true);
					loginButton.setDisable(true);
				}
			}

		});
	}

	private boolean isMatch() {
		return firstTimeText.getText().equals(secondTimeText.getText());
	}

}

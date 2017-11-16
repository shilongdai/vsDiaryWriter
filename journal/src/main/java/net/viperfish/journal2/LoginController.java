package net.viperfish.journal2;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
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

public final class LoginController implements Initializable {

	@FXML
	private Button loginButton;

	@FXML
	private Label warningText;

	@FXML
	private ProgressIndicator progress;

	@FXML
	private PasswordField passwordPrompt;

	@FXML
	public void login(ActionEvent e) {
		Service<Boolean> login = JournalServices.newLoginService(passwordPrompt.getText());
		progress.setVisible(true);
		login.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				boolean success = (boolean) event.getSource().getValue();
				if (success) {
					((Stage) loginButton.getScene().getWindow()).close();
				} else {
					warningText.setVisible(true);
					progress.setVisible(false);
				}
			}
		});
		login.start();
	}

	@FXML
	public void quitApp(ActionEvent e) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				Platform.exit();

			}
		});
		((Stage) loginButton.getScene().getWindow()).close();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

}

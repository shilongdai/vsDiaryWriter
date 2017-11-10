/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.journal2;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import net.viperfish.journal2.core.AuthenticationManager;

/**
 *
 * @author shilongdai
 */
public class NewPasswordController implements Initializable {

    private AuthenticationManager manager;

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

    public NewPasswordController(AuthenticationManager manager) {
        this.manager = manager;
    }

    @FXML
    public void setPassword(ActionEvent e) {
        progress.setVisible(true);
        try {
            manager.setPassword(firstTimeText.getText());
        } catch (IOException ex) {
            Logger.getLogger(NewPasswordController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    public void cancelSetPassword(ActionEvent e) {
        System.exit(0);
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

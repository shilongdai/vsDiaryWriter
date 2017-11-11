package net.viperfish.journal2;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.viperfish.journal2.core.JournalConfiguration;
import net.viperfish.journal2.core.JournalI18NBundle;
import net.viperfish.journal2.transaction.JournalServices;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Bootstrap extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {
            JournalConfiguration.load("config");
            JournalServices.init();
            if (!JournalServices.isSetup()) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/createPassword.fxml"), JournalI18NBundle.getBundle());
                fxmlLoader.setController(new NewPasswordController());
                Parent newPassword = fxmlLoader.load();
                Scene scene = new Scene(newPassword, 400, 190);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setScene(scene);
                stage.show();
            }

        } catch (IOException | ConfigurationException e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                JournalConfiguration.save();
                JournalServices.close();
            } catch (ConfigurationException | IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }

}

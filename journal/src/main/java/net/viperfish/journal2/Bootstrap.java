package net.viperfish.journal2;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.configuration2.ex.ConfigurationException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.viperfish.journal2.core.FileUtils;
import net.viperfish.journal2.core.JournalConfiguration;
import net.viperfish.journal2.core.JournalI18NBundle;
import net.viperfish.journal2.transaction.JournalServices;

public class Bootstrap extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		Path workingDir = FileUtils.getWorkingDirectoryUnderHome();
		workingDir.toFile().mkdirs();
		FileUtils.copyFilesRecusively(new File("vsDiary"), workingDir.getParent().toFile());
		try {
			JournalConfiguration.load(Paths.get(workingDir.toString(), "config").toString());
			JournalServices.init();
			if (!JournalServices.isSetup()) {
				Stage setPassWindow = new Stage();
				FXMLLoader fxmlLoader = new FXMLLoader(
						getClass().getClassLoader().getResource("fxml/createPassword.fxml"),
						JournalI18NBundle.getBundle());
				fxmlLoader.setController(new NewPasswordController());
				Parent newPassword = fxmlLoader.load();
				Scene scene = new Scene(newPassword, 400, 190);
				setPassWindow.initStyle(StageStyle.UNDECORATED);
				setPassWindow.setScene(scene);
				setPassWindow.showAndWait();
			}
			Stage loginWindow = new Stage();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/login.fxml"),
					JournalI18NBundle.getBundle());
			fxmlLoader.setController(new LoginController());
			Parent login = fxmlLoader.load();
			Scene scene = new Scene(login, 400, 130);
			loginWindow.initStyle(StageStyle.UNDECORATED);
			loginWindow.setScene(scene);
			loginWindow.showAndWait();

			Stage mainStage = new Stage();
			fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/journal.fxml"),
					JournalI18NBundle.getBundle());
			fxmlLoader.setController(new JournalController());
			Parent journal = fxmlLoader.load();
			scene = new Scene(journal, 880, 585);
			mainStage.setScene(scene);
			mainStage.showAndWait();
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

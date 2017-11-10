package net.viperfish.journal2;

import java.io.IOException;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.viperfish.journal2.auth.OpenBSDBCryptAuthManager;
import net.viperfish.journal2.core.AuthenticationManager;
import net.viperfish.journal2.core.JournalConfiguration;
import net.viperfish.journal2.core.JournalDatabase;
import net.viperfish.journal2.core.JournalEncryptorChain;
import net.viperfish.journal2.core.JournalI18NBundle;
import net.viperfish.journal2.core.JournalService;
import net.viperfish.journal2.crypt.AEADProccessor;
import net.viperfish.journal2.crypt.CompressionProccessor;
import net.viperfish.journal2.crypt.HMACProcessor;
import net.viperfish.journal2.crypt.StreamCipherProcessor;
import net.viperfish.journal2.db.H2JournalDB;
import net.viperfish.journal2.transaction.TransactionalJournalService;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class Bootstrap extends Application {

    private static JournalEncryptorChain journalEncryptorChain() throws IOException {
        JournalEncryptorChain enc = new JournalEncryptorChain(Paths.get("kdfSalt"));
        enc.addProccessor(new AEADProccessor());
        enc.addProccessor(new CompressionProccessor());
        enc.addProccessor(new HMACProcessor());
        enc.addProccessor(new StreamCipherProcessor());
        return enc;
    }

    private static AuthenticationManager authManager(JournalEncryptorChain enc) throws IOException {
        OpenBSDBCryptAuthManager auth = new OpenBSDBCryptAuthManager(Paths.get("passwd"));
        if (auth.isSetup()) {
            auth.load();
        }
        auth.addObserver(enc);
        return auth;
    }

    private static JournalDatabase createDatabase() {
        JournalDatabase db = new H2JournalDB("./data");
        return db;
    }

    private static JournalService createService(JournalEncryptorChain chain, AuthenticationManager manager)
            throws IOException {
        return new TransactionalJournalService(createDatabase(), chain, manager);
    }

    @Override
    public void start(Stage stage) throws Exception {
        JournalEncryptorChain enc;
        AuthenticationManager manager;
        try {
            enc = journalEncryptorChain();
            manager = authManager(enc);
            try (JournalService service = createService(enc, manager)) {
                JournalConfiguration.load("config");
                if (!manager.isSetup()) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/createPassword.fxml"), JournalI18NBundle.getBundle());
                    fxmlLoader.setController(new NewPasswordController(manager));
                    Parent newPassword = fxmlLoader.load();
                    Scene scene = new Scene(newPassword, 400, 190);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setScene(scene);
                    stage.show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return;
            } finally {
                try {
                    JournalConfiguration.save();
                } catch (ConfigurationException e) {
                    e.printStackTrace();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

}

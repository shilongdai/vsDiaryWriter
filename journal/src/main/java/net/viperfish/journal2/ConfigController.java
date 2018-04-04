package net.viperfish.journal2;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import net.viperfish.framework.compression.Compressors;
import net.viperfish.journal2.core.JournalConfiguration;
import net.viperfish.journal2.crypt.BlockCiphers;
import net.viperfish.journal2.crypt.Digesters;
import net.viperfish.journal2.crypt.StreamCipherEncryptors;

public class ConfigController implements Initializable {

	@FXML
	private ComboBox<String> aeadEncAlg;

	@FXML
	private ComboBox<String> aeadMode;

	@FXML
	private ComboBox<String> compressionAlg;

	@FXML
	private ComboBox<String> streamCipherAlg;

	@FXML
	private ComboBox<String> hmacAlg;

	@FXML
	private Spinner<Integer> hmacKeySize;

	public ConfigController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// aead
		aeadMode.setItems(FXCollections.observableList(new LinkedList<>(BlockCiphers.getAEADModes())));
		aeadMode.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				aeadEncAlg.setItems(null);
				if (aeadMode.getValue().equals("EAX")) {
					aeadEncAlg.setItems(
							FXCollections.observableList(new LinkedList<>(BlockCiphers.getSupportedBlockCipher())));
				} else {
					aeadEncAlg
							.setItems(FXCollections.observableList(new LinkedList<>(BlockCiphers.get128Algorithms())));
				}
				aeadEncAlg.getSelectionModel()
						.select(JournalConfiguration.getString(ConfigMapping.CONFIG_AEAD_ENCRYPTION_ALGORITHM, "AES"));
			}
		});
		aeadMode.getSelectionModel()
				.select(JournalConfiguration.getString(ConfigMapping.CONFIG_AEAD_ENCRYPTION_MODE, "GCM"));
		aeadMode.getOnAction().handle(null);
		// compression
		compressionAlg.setItems(FXCollections.observableArrayList(Compressors.getCompressors()));
		compressionAlg.getSelectionModel()
				.select(JournalConfiguration.getString(ConfigMapping.CONFIG_COMPRESSION, "XZ"));

		// hmac
		hmacAlg.setItems(FXCollections.observableList(new LinkedList<>(Digesters.getSupportedDigest())));
		hmacAlg.getSelectionModel()
				.select(JournalConfiguration.getString(ConfigMapping.CONFIG_HMAC_ALGORITHM, "SHA256"));
		hmacKeySize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 128));
		hmacKeySize.getValueFactory().setValue(JournalConfiguration.getInteger(ConfigMapping.CONFIG_HMAC_SIZE, 32));

		// stream cipher
		streamCipherAlg.setItems(FXCollections.observableArrayList(StreamCipherEncryptors.INSTANCE.getSupported()));
		streamCipherAlg.getSelectionModel()
				.select(JournalConfiguration.getString(ConfigMapping.CONFIG_STREAMCIPHER_ALGORITHM, "XSalsa20"));
	}

	@FXML
	public void saveConfig(ActionEvent e) {
		int aeadKeyLength = BlockCiphers.getKeySize(aeadEncAlg.getValue());
		int streamKeyLength = StreamCipherEncryptors.INSTANCE.getKeySize(streamCipherAlg.getValue());

		JournalConfiguration.setProperty(ConfigMapping.CONFIG_AEAD_ENCRYPTION_ALGORITHM, aeadEncAlg.getValue());
		JournalConfiguration.setProperty(ConfigMapping.CONFIG_AEAD_ENCRYPTION_MODE, aeadMode.getValue());
		JournalConfiguration.setProperty(ConfigMapping.CONFIG_AEAD_ENCRYPTION_KEYLENGTH, aeadKeyLength);

		JournalConfiguration.setProperty(ConfigMapping.CONFIG_COMPRESSION, compressionAlg.getValue());

		JournalConfiguration.setProperty(ConfigMapping.CONFIG_HMAC_ALGORITHM, hmacAlg.getValue());
		JournalConfiguration.setProperty(ConfigMapping.CONFIG_HMAC_SIZE, hmacKeySize.getValue());

		JournalConfiguration.setProperty(ConfigMapping.CONFIG_STREAMCIPHER_ALGORITHM, streamCipherAlg.getValue());
		JournalConfiguration.setProperty(ConfigMapping.CONFIG_STREAMCIPHER_KEYLENGTH, streamKeyLength);

		quit(null);
	}

	public void quit(ActionEvent e) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				((Stage) aeadEncAlg.getScene().getWindow()).close();
			}
		});
	}

}

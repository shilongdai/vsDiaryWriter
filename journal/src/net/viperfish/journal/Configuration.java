package net.viperfish.journal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import net.viperfish.journal.fileDatabase.GZippedDataSourceFactory;
import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.UserInterface;
import net.viperfish.journal.index.JournalIndexerFactory;
import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.IndexerFactory;
import net.viperfish.journal.secure.SecureFactoryWrapper;
import net.viperfish.journal.ui.CommandLineUserInterface;
import net.viperfish.journal.ui.SingleThreadedOperationExecutor;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import test.java.StubDataSourceFactory;

public class Configuration {
	private static DataSourceFactory df;
	private static IndexerFactory indexerFactory;
	private static File dataDir;
	private static Properties properties;
	private static UserInterface ui;
	private static OperationExecutor worker;
	private static File configFile;
	private static boolean firstRun;
	private static final boolean unitTest = false;
	static {
		Security.addProvider(new BouncyCastleProvider());
		initFileStructure();

	}

	private Configuration() {
	}

	public static void defaultConfig() {
		getProperty().setProperty("DataSourceFactory",
				GZippedDataSourceFactory.class.getCanonicalName());
		getProperty().setProperty("IndexerFactory",
				JournalIndexerFactory.class.getCanonicalName());
		getProperty().setProperty("UseSecureWrapper", "true");
		getProperty().setProperty("EncryptionMethod", "AES/CFB/PKCS5PADDING");
		getProperty().setProperty("MacMethod", "HMAC-SHA512");
	}

	private static void cleanUp() {
		getDataSourceFactory().cleanUp();
		getIndexerFactory().cleanUp();
		getWorker().terminate();
	}

	private static InputStream getConfigIn() {
		DataInputStream in;
		try {
			if (firstRun) {
				configFile.createNewFile();
			}
			in = new DataInputStream(new BufferedInputStream(
					new FileInputStream(new File("config.xml"))));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return in;
	}

	private static void initFileStructure() {
		configFile = new File("config.xml");
		dataDir = new File(System.getProperty("user.home") + "/.vJournal");
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}
		if (!configFile.exists()) {

			firstRun = true;
		}
	}

	private static OutputStream getConfigOut() {
		DataOutputStream out;
		try {
			if (firstRun) {
				configFile.createNewFile();
			}
			out = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(new File("config.xml"))));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return out;
	}

	public static OperationExecutor getWorker() {
		if (worker == null) {
			worker = new SingleThreadedOperationExecutor();
		}
		return worker;
	}

	public static DataSourceFactory getDataSourceFactory() {
		if (df == null) {
			if (unitTest) {
				df = new StubDataSourceFactory();
			} else {
				try {
					Class<?> selected = Class.forName(getProperty()
							.getProperty("DataSourceFactory"));
					DataSourceFactory tmp = (DataSourceFactory) selected
							.newInstance();
					if (getProperty().getProperty("UseSecureWrapper").equals(
							"true")) {
						df = new SecureFactoryWrapper(tmp);
					}
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return df;
	}

	public static IndexerFactory getIndexerFactory() {
		if (indexerFactory == null) {
			try {
				indexerFactory = (IndexerFactory) Class.forName(
						getProperty().getProperty("IndexerFactory"))
						.newInstance();
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return indexerFactory;
	}

	public static Properties getProperty() {
		if (properties == null) {
			properties = new Properties();
		}
		return properties;
	}

	public static void persistProperty() throws IOException {
		getProperty().remove("user.password");
		getProperty().storeToXML(getConfigOut(),
				"the configuration for vJournal");
	}

	public static void loadProperty() throws InvalidPropertiesFormatException,
			IOException {
		if (firstRun) {
			defaultConfig();
			ui.setup();
			return;
		}
		getProperty().loadFromXML(getConfigIn());
	}

	public static void main(String[] arg) {
		ui = new CommandLineUserInterface();
		try {
			loadProperty();
		} catch (IOException e1) {
			System.err.println(e1);
			System.exit(1);
		}
		ui.run();
		cleanUp();
		try {
			persistProperty();
		} catch (IOException e) {
			System.err
					.println("critical error incountered while saving configuration, quitting");
		}
	}

}

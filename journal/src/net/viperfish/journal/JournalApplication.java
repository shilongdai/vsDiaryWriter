package net.viperfish.journal;

import java.io.File;
import java.io.IOException;
import java.security.Security;

import net.viperfish.journal.auth.AuthenticationManager;
import net.viperfish.journal.auth.AuthenticationManagerFactory;
import net.viperfish.journal.authentications.HashAuthFactory;
import net.viperfish.journal.cmd.CommandLineUserInterface;
import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.OperationFactory;
import net.viperfish.journal.framework.UserInterface;
import net.viperfish.journal.gui.GraphicalUserInterface;
import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.journal.persistent.IndexerFactory;
import net.viperfish.journal.secure.SecureEntryDatabaseWrapper;
import net.viperfish.journal.secure.SecureFactoryWrapper;
import net.viperfish.journal.ui.StandardOperationFactory;
import net.viperfish.journal.ui.ThreadPoolOperationExecutor;
import net.viperfish.utils.config.Configuration;
import net.viperfish.utils.index.Indexer;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import test.java.StubDataSourceFactory;

/**
 * the Main class of the application, contains all the components
 * 
 * @author sdai
 *
 */
public class JournalApplication {
	private static DataSourceFactory df;
	private static IndexerFactory indexerFactory;
	private static UserInterface ui;
	private static OperationExecutor worker;
	private static OperationFactory opsFactory;
	private static File configFile;
	private static boolean firstRun;
	private static File dataDir;
	private static AuthenticationManagerFactory authFactory;
	private static final boolean unitTest = false;
	private static String password;
	private static SystemConfig sysConf;

	static {
		Security.addProvider(new BouncyCastleProvider());
		initFileStructure();
		sysConf = new SystemConfig();
		Configuration.put(SecureEntryDatabaseWrapper.config().getUnitName(),
				SecureEntryDatabaseWrapper.config());
		Configuration.put(sysConf.getUnitName(), sysConf);
	}

	public JournalApplication() {
	}

	private static void cleanUp() {
		getDataSourceFactory().cleanUp();
		getIndexerFactory().cleanUp();
		getWorker().terminate();
	}

	private static void initFileStructure() {
		configFile = new File("config.xml");
		if (!configFile.exists()) {
		}
		dataDir = new File("data");
		if (!dataDir.exists()) {
			firstRun = true;
			dataDir.mkdir();
		}
	}

	/**
	 * get the factory for creating an appropriate OperationExecuter
	 * 
	 * @return the operation executer
	 * @see OperationExecutor
	 */
	public static OperationExecutor getWorker() {
		if (worker == null) {
			worker = new ThreadPoolOperationExecutor();
		}
		return worker;
	}

	/**
	 * get the factory that returns a instance of a EntryDatabase
	 * 
	 * @return the factory
	 * @see EntryDatabase
	 */
	public static DataSourceFactory getDataSourceFactory() {
		if (df == null) {
			if (unitTest) {
				df = new StubDataSourceFactory();
			} else {
				try {
					Class<?> selected = Class.forName(sysConf
							.getProperty("DataSourceFactory"));
					DataSourceFactory tmp = (DataSourceFactory) selected
							.newInstance();
					tmp.setDataDirectory(dataDir);
					df = new SecureFactoryWrapper(tmp, password);
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return df;
	}

	/**
	 * get a factory that returns a instance of a Indexer<Journal>
	 * 
	 * @see Indexer
	 * @return the factory
	 */
	public static IndexerFactory getIndexerFactory() {
		if (indexerFactory == null) {
			try {
				indexerFactory = (IndexerFactory) Class.forName(
						sysConf.getProperty("IndexerFactory")).newInstance();
				indexerFactory.setDataDir(dataDir);
			} catch (InstantiationException | IllegalAccessException
					| ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return indexerFactory;
	}

	/**
	 * get a factory that returns a instance of a AuthenticationManager
	 * 
	 * @return the authentication manager
	 * @see AuthenticationManager
	 */
	public static AuthenticationManagerFactory getAuthFactory() {
		if (authFactory == null) {
			authFactory = new HashAuthFactory();
			authFactory.setDataDir(dataDir);
		}
		return authFactory;
	}

	/**
	 * gets a factory that returns a Operation
	 * 
	 * @return the factory
	 * @see Operation
	 */
	public static OperationFactory getOperationFactory() {
		if (opsFactory == null) {
			opsFactory = new StandardOperationFactory();
		}
		return opsFactory;
	}

	/**
	 * get the user's password in plaintext, only call it after the use has
	 * entered the correct password via UserInterface.promptPassword
	 * 
	 * @see UserInterface#promptPassword() promptPassword
	 * @return the password
	 */
	public static String getPassword() {
		return password;
	}

	/**
	 * set the current password, should be called by the promptPassword method
	 * of the UserInterface implementer after verifying the password
	 * 
	 * @param password
	 *            the correct password
	 */
	public static void setPassword(String password) {
		JournalApplication.password = password;
		if (getDataSourceFactory().getClass().isInstance(
				SecureFactoryWrapper.class)) {
			SecureEntryDatabaseWrapper tmp = (SecureEntryDatabaseWrapper) df
					.createDatabaseObject();
			tmp.setPassword(getPassword());
		}

	}

	public static void main(String[] args) {
		// TODO Finish Custom Arguments For Options
		boolean consoleMode = System.console() != null;
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("noconsole")) {
				consoleMode = false;
			}
		}
		if (consoleMode) {
			ui = new CommandLineUserInterface();
		} else {
			ui = new GraphicalUserInterface();
		}
		try {
			Configuration.loadAll();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.err.println(e1);
			System.exit(1);
		}
		ui.setAuthManager(getAuthFactory().getAuthenticator());
		if (firstRun) {
			ui.setup();
		}
		password = ui.promptPassword();
		ui.run();
		// clean up after user choose to exit
		cleanUp();
		try {
			Configuration.persistAll();
		} catch (IOException e) {
			System.err
					.println("critical error incountered while saving configuration, quitting");
		}

	}

}

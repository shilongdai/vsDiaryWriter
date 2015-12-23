package net.viperfish.journal;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.viperfish.journal.auth.AuthenticationManager;
import net.viperfish.journal.auth.AuthenticationManagerFactory;
import net.viperfish.journal.authentications.HashAuthFactory;
import net.viperfish.journal.cmd.CommandLineUserInterface;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.OperationFactory;
import net.viperfish.journal.framework.UserInterface;
import net.viperfish.journal.persistent.DataSourceFactory;
import net.viperfish.journal.persistent.EntryDatabase;
import net.viperfish.journal.persistent.IndexerFactory;
import net.viperfish.journal.secure.SecureEntryDatabaseWrapper;
import net.viperfish.journal.secure.SecureFactoryWrapper;
import net.viperfish.journal.ui.StandardOperationFactory;
import net.viperfish.journal.ui.ThreadPoolOperationExecutor;
import net.viperfish.utils.config.ComponentConfig;
import net.viperfish.utils.config.ComponentConfigObserver;
import net.viperfish.utils.config.Configuration;
import net.viperfish.utils.file.CommonFunctions;
import net.viperfish.utils.index.Indexer;
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
	private static boolean firstRun;
	private static File dataDir;
	private static AuthenticationManagerFactory authFactory;
	private static boolean unitTest = false;
	private static String password;
	private static SystemConfig sysConf;

	private static class ConfigurationObserver implements ComponentConfigObserver {

		@Override
		public void sendNotify(ComponentConfig c) {
			List<Journal> l = new LinkedList<>();
			l = df.createDatabaseObject().getAll();
			reset();
			indexerFactory = getIndexerFactory();
			df = getDataSourceFactory();
			indexerFactory.createIndexer().clear();
			df.createDatabaseObject().clear();
			for (Journal j : l) {
				df.createDatabaseObject().addEntry(j);
				indexerFactory.createIndexer().add(j);
			}
		}

	}

	static {
		initFileStructure();
		initConfigUnits();
	}

	public JournalApplication() {
	}

	public static void reset() {
		df = null;
		indexerFactory = null;
		worker = null;
		opsFactory = null;
		authFactory = null;
	}

	private static void initConfigUnits() {
		sysConf = new SystemConfig();
		Configuration.setConfigDirPath("config");
		Configuration.put(SecureEntryDatabaseWrapper.config().getUnitName(), SecureEntryDatabaseWrapper.config());
		Configuration.put(sysConf.getUnitName(), sysConf);
		sysConf.addObserver(new ConfigurationObserver());
	}

	private static void deleteAll() {
		CommonFunctions.delete(dataDir);
		Configuration.clear();
	}

	public static void cleanUp() {
		getDataSourceFactory().cleanUp();
		getIndexerFactory().cleanUp();
		getWorker().terminate();
	}

	private static void initFileStructure() {
		if (!unitTest) {
			dataDir = new File("data");
		} else {
			dataDir = new File("test");
		}
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
					Class<?> selected = Class.forName(sysConf.getProperty("DataSourceFactory"));
					DataSourceFactory tmp = (DataSourceFactory) selected.newInstance();
					df = new SecureFactoryWrapper(tmp, password);
					df.setDataDirectory(dataDir);
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
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
				indexerFactory = (IndexerFactory) Class.forName(sysConf.getProperty("IndexerFactory")).newInstance();
				indexerFactory.setDataDir(dataDir);
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
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
		if (getDataSourceFactory().getClass().isInstance(SecureFactoryWrapper.class)) {
			SecureEntryDatabaseWrapper tmp = (SecureEntryDatabaseWrapper) df.createDatabaseObject();
			tmp.setPassword(getPassword());
		}

	}

	/**
	 * get the system configuration unit
	 * 
	 * @return the system config unit
	 */
	public static SystemConfig getSysConf() {
		if (sysConf == null) {
			sysConf = new SystemConfig();
		}
		return sysConf;
	}

	/**
	 * set the status of unit testing, if unit test, the datasourcefactory would
	 * be one that creates stubEntryDatabase in memory. The current dataDir is
	 * cleared, components reset, and file structure re initialized
	 * 
	 * @param isEnable
	 *            the state of unit test
	 */
	public static void setUnitTest(boolean isEnable) {
		unitTest = isEnable;
		deleteAll();
		initFileStructure();
		reset();
	}

	public static void main(String[] args) {
		File lockFile = new File(".setUpLock");
		if (lockFile.exists()) {
			firstRun = true;
			deleteAll();
			initFileStructure();
			initConfigUnits();
		} else {
			try {
				if (firstRun) {
					lockFile.createNewFile();
				}
			} catch (IOException e) {
				System.err.println("failed to create lock file: exiting");
				return;
			}
		}
		boolean consoleMode = System.console() != null;
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("noconsole")) {
				consoleMode = false;
			}
		}
		if (consoleMode) {
			ui = new CommandLineUserInterface();
		} else {
			ui = new net.viperfish.swtGui.GraphicalUserInterface();
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
			lockFile.delete();
		}
		password = ui.promptPassword();
		ui.run();
		try {
			Configuration.persistAll();
		} catch (IOException e) {
			System.err.println("critical error incountered while saving configuration, quitting");
		}

	}

}

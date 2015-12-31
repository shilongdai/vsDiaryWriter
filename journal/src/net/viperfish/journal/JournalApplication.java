package net.viperfish.journal;

import java.io.File;
import java.io.IOException;

import net.viperfish.journal.auth.ViperfishAuthProvider;
import net.viperfish.journal.cmd.CommandLineUserInterface;
import net.viperfish.journal.dbProvider.ViperfishEntryDatabaseProvider;
import net.viperfish.journal.framework.ComponentProvider;
import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.OperationFactory;
import net.viperfish.journal.framework.UserInterface;
import net.viperfish.journal.index.ViperfishIndexerProvider;
import net.viperfish.journal.secure.SecureEntryDatabaseWrapper;
import net.viperfish.journal.secure.ViperfishEncryptionProvider;
import net.viperfish.journal.ui.StandardOperationFactory;
import net.viperfish.journal.ui.ThreadPoolOperationExecutor;
import net.viperfish.utils.config.Configuration;
import net.viperfish.utils.file.CommonFunctions;

/**
 * the Main class of the application, contains all the components
 * 
 * @author sdai
 * 
 */
public class JournalApplication {
	private static UserInterface ui;
	private static OperationExecutor worker;
	private static OperationFactory opsFactory;
	private static boolean firstRun;
	private static File dataDir;
	private static boolean unitTest = false;
	private static String password;
	private static SystemConfig sysConf;

	static {
		initFileStructure();
		initConfigUnits();
		initProviders();
	}

	public JournalApplication() {
	}

	private static void initProviders() {
		ComponentProvider.registerAuthProvider(new ViperfishAuthProvider());
		ComponentProvider.registerEntryDatabaseProvider(new ViperfishEntryDatabaseProvider());
		ComponentProvider.registerIndexerProvider(new ViperfishIndexerProvider());
		ComponentProvider.registerTransformerProvider(new ViperfishEncryptionProvider());
		ComponentProvider.setDefaultAuthProvider("viperfish");
		ComponentProvider.setDefaultDatabaseProvider("viperfish");
		ComponentProvider.setDefaultIndexerProvider("viperfish");
		ComponentProvider.setDefaultTransformerProvider("viperfish");
	}

	private static void initConfigUnits() {
		sysConf = new SystemConfig();
		Configuration.setConfigDirPath("config");
		Configuration.put(SecureEntryDatabaseWrapper.config().getUnitName(), SecureEntryDatabaseWrapper.config());
		Configuration.put(sysConf.getUnitName(), sysConf);
	}

	private static void deleteAll() {
		CommonFunctions.delete(dataDir);
		Configuration.clear();
	}

	public static void cleanUp() {
		ComponentProvider.dispose();
		System.err.println("Providers disposed");
		getWorker().terminate();
		System.err.println("worker terminated");
	}

	private static void initFileStructure() {
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
			ui = new net.viperfish.journal.swtGui.GraphicalUserInterface();
		}
		try {
			Configuration.loadAll();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.err.println(e1);
			System.exit(1);
		}
		if (firstRun) {
			ui.setup();
			lockFile.delete();
		}
		ui.setAuthManager(ComponentProvider.getAuthManager());
		password = ui.promptPassword();
		ui.run();
		try {
			Configuration.persistAll();
		} catch (IOException e) {
			System.err.println("critical error incountered while saving configuration, quitting");
		}

	}

}

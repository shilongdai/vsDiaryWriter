package net.viperfish.journal;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;

import net.viperfish.journal.auth.ViperfishAuthProvider;
import net.viperfish.journal.dbProvider.ViperfishEntryDatabaseProvider;
import net.viperfish.journal.framework.ComponentProvider;
import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.OperationFactory;
import net.viperfish.journal.framework.UserInterface;
import net.viperfish.journal.index.ViperfishIndexerProvider;
import net.viperfish.journal.secure.ViperfishEncryptionProvider;
import net.viperfish.journal.swtGui.GraphicalUserInterface;
import net.viperfish.journal.swtGui.conf.BlockCipherMacConfigPage;
import net.viperfish.journal.swtGui.conf.SystemConfigPage;
import net.viperfish.journal.ui.StandardOperationFactory;
import net.viperfish.journal.ui.ThreadPoolOperationExecutor;
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
	private static SystemConfig sysConf;
	private static FileConfiguration configuration;

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

	}

	private static void initConfigUnits() {
		File config = new File("config.properties");
		configuration = new PropertiesConfiguration();
		configuration.setFile(config);
	}

	private static void initBuiltInDefaults() {
		ComponentProvider.setDefaultAuthProvider(configuration.getString(ConfigMapping.AUTH_PROVIDER));
		ComponentProvider.setDefaultDatabaseProvider(configuration.getString(ConfigMapping.DB_PROVIDER));
		ComponentProvider.setDefaultIndexerProvider(configuration.getString(ConfigMapping.INDEX_PROVIDER));
		ComponentProvider.setDefaultTransformerProvider(configuration.getString(ConfigMapping.TRANSFORMER_PROVIDER));

	}

	private static void deleteAll() {
		CommonFunctions.delete(dataDir);
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

	public static FileConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * set the status of unit testing, if unit test, The current dataDir is
	 * cleared, components reset, and file structure re initialized
	 * 
	 * @param isEnable
	 *            the state of unit test
	 */
	public static void setUnitTest(boolean isEnable) {
		deleteAll();
		initFileStructure();
		initBuiltInDefaults();
	}

	private static void defaultProviders() {
		JournalApplication.getConfiguration().setProperty(ConfigMapping.AUTH_PROVIDER, "viperfish");
		JournalApplication.getConfiguration().setProperty(ConfigMapping.DB_PROVIDER, "viperfish");
		JournalApplication.getConfiguration().setProperty(ConfigMapping.INDEX_PROVIDER, "viperfish");
		JournalApplication.getConfiguration().setProperty(ConfigMapping.TRANSFORMER_PROVIDER, "viperfish");
		configuration.addProperty(ConfigMapping.CONFIG_PAGES, new String[] { SystemConfigPage.class.getCanonicalName(),
				BlockCipherMacConfigPage.class.getCanonicalName() });
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
		ui = new GraphicalUserInterface();
		if (firstRun) {
			defaultProviders();
			initBuiltInDefaults();
			ui.setup();
			lockFile.delete();
			try {
				configuration.save();
			} catch (ConfigurationException e) {
				System.err.println("could not save configuration, terminating");
				System.exit(1);
			}
		}
		try {
			configuration.load();
		} catch (ConfigurationException e) {
			System.err.println("failed to load configuration, exiting");
			System.exit(1);
		}
		initBuiltInDefaults();
		ui.setAuthManager(ComponentProvider
				.getAuthManager(JournalApplication.getConfiguration().getString(ConfigMapping.AUTH_COMPONENT)));
		ui.promptPassword();
		ui.run();
		System.exit(0);
	}

}

package net.viperfish.journal;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;

import net.viperfish.journal.authProvider.ViperfishAuthProvider;
import net.viperfish.journal.dbProvider.ViperfishEntryDatabaseProvider;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.indexProvider.ViperfishIndexerProvider;
import net.viperfish.journal.secureProvider.ViperfishEncryptionProvider;
import net.viperfish.journal.swtGui.GraphicalUserInterface;
import net.viperfish.journal.swtGui.conf.BlockCipherMacConfigPage;
import net.viperfish.journal.swtGui.conf.SystemConfigPage;
import net.viperfish.journal.ui.OperationExecutor;
import net.viperfish.journal.ui.OperationFactory;
import net.viperfish.journal.ui.StandardOperationFactory;
import net.viperfish.journal.ui.ThreadPoolOperationExecutor;
import net.viperfish.journal.ui.UserInterface;
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
	private static SystemConfig sysConf;
	private static File moduleDir;
	private static File authModules;
	private static File dbModules;
	private static File indexModules;
	private static File transModule;

	static {
		initModules();
		initConfigUnits();
		initProviders();

	}

	public JournalApplication() {
	}

	private static void initModules() {
		moduleDir = new File("modules");
		authModules = new File("modules/auth");
		dbModules = new File("modules/db");
		indexModules = new File("modules/index");
		transModule = new File("modules/trans");
		CommonFunctions.initDir(moduleDir);
		CommonFunctions.initDir(authModules);
		CommonFunctions.initDir(dbModules);
		CommonFunctions.initDir(indexModules);
		CommonFunctions.initDir(transModule);
		ComponentProvider.setLoader(new JarBasedModuleLoader());
		ComponentProvider.loadAuthProvider(authModules);
		ComponentProvider.loadDatabaseProvider(dbModules);
		ComponentProvider.loadIndexer(indexModules);
		ComponentProvider.loadTransformerProvider(transModule);
	}

	private static void initProviders() {
		ComponentProvider.registerAuthProvider(new ViperfishAuthProvider());
		ComponentProvider.registerEntryDatabaseProvider(new ViperfishEntryDatabaseProvider());
		ComponentProvider.registerIndexerProvider(new ViperfishIndexerProvider());
		ComponentProvider.registerTransformerProvider(new ViperfishEncryptionProvider());

	}

	private static void initConfigUnits() {
		File config = new File(Configuration.confFile);
		if (!config.exists()) {
			firstRun = true;
		}
	}

	private static void initBuiltInDefaults() {
		ComponentProvider.setDefaultAuthProvider(Configuration.getString(ConfigMapping.AUTH_PROVIDER));
		ComponentProvider.setDefaultDatabaseProvider(Configuration.getString(ConfigMapping.DB_PROVIDER));
		ComponentProvider.setDefaultIndexerProvider(Configuration.getString(ConfigMapping.INDEX_PROVIDER));
		ComponentProvider.setDefaultTransformerProvider(Configuration.getString(ConfigMapping.TRANSFORMER_PROVIDER));

	}

	public static void cleanUp() {
		ComponentProvider.dispose();
		System.err.println("Providers disposed");
		getWorker().terminate();
		System.err.println("worker terminated");
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

	/**
	 * set the status of unit testing, if unit test, The current dataDir is
	 * cleared, components reset, and file structure re initialized
	 * 
	 * @param isEnable
	 *            the state of unit test
	 */
	public static void setUnitTest(boolean isEnable) {
		initBuiltInDefaults();
	}

	private static void defaultProviders() {
		if (!Configuration.containsKey(ConfigMapping.AUTH_PROVIDER)) {
			Configuration.setProperty(ConfigMapping.AUTH_PROVIDER, "viperfish");
		}
		if (!Configuration.containsKey(ConfigMapping.DB_PROVIDER)) {
			Configuration.setProperty(ConfigMapping.DB_PROVIDER, "viperfish");
		}
		if (!Configuration.containsKey(ConfigMapping.INDEX_PROVIDER)) {
			Configuration.setProperty(ConfigMapping.INDEX_PROVIDER, "viperfish");
		}
		if (!Configuration.containsKey(ConfigMapping.TRANSFORMER_PROVIDER)) {
			Configuration.setProperty(ConfigMapping.TRANSFORMER_PROVIDER, "viperfish");
		}
		Configuration.addProperty(ConfigMapping.CONFIG_PAGES, new String[] { SystemConfigPage.class.getCanonicalName(),
				BlockCipherMacConfigPage.class.getCanonicalName() });
	}

	public static void main(String[] args) {
		File lockFile = new File(".setUpLock");
		if (lockFile.exists()) {
			firstRun = true;
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
		try {
			Configuration.load();
		} catch (ConfigurationException e) {
			System.err.println("failed to load configuration, exiting");
			System.exit(1);
		}
		if (firstRun) {
			defaultProviders();
			initBuiltInDefaults();
			ui.setup();
			lockFile.delete();
			try {
				Configuration.save();
			} catch (ConfigurationException e) {
				System.err.println("could not save configuration, terminating");
				System.exit(1);
			}
		}
		initBuiltInDefaults();
		ui.setAuthManager(ComponentProvider.getAuthManager(Configuration.getString(ConfigMapping.AUTH_COMPONENT)));
		ui.promptPassword();
		ui.run();
	}

}

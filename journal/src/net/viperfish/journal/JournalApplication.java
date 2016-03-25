package net.viperfish.journal;

import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.jface.preference.PreferenceNode;

import net.viperfish.journal.archieveDB.ViperfishArchiveDBProvider;
import net.viperfish.journal.authProvider.ViperfishAuthProvider;
import net.viperfish.journal.dbProvider.ViperfishEntryDatabaseProvider;
import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.provider.AuthManagers;
import net.viperfish.journal.framework.provider.EntryDatabases;
import net.viperfish.journal.framework.provider.Indexers;
import net.viperfish.journal.framework.provider.JournalTransformers;
import net.viperfish.journal.framework.provider.PreferenceGUIManager;
import net.viperfish.journal.framework.provider.Provider;
import net.viperfish.journal.indexProvider.ViperfishIndexerProvider;
import net.viperfish.journal.operation.OperationExecutors;
import net.viperfish.journal.secureProvider.ViperfishEncryptionProvider;
import net.viperfish.journal.swtGui.GraphicalUserInterface;
import net.viperfish.journal.ui.TerminationControlFlowException;
import net.viperfish.journal.ui.UserInterface;
import net.viperfish.utils.index.Indexer;

/**
 * the Main class of the application, contains all the components
 * 
 * @author sdai
 * 
 */
public class JournalApplication {
	private static UserInterface ui;
	// private static File moduleDir;
	// private static File authModules;
	// private static File dbModules;
	// private static File indexModules;
	// private static File transModule;

	public JournalApplication() {
	}

	public static void initModules() {
		// Saved for later reference

		// moduleDir = new File("modules");
		// authModules = new File("modules/auth");
		// dbModules = new File("modules/db");
		// indexModules = new File("modules/index");
		// transModule = new File("modules/trans");
		// CommonFunctions.initDir(moduleDir);
		// CommonFunctions.initDir(authModules);
		// CommonFunctions.initDir(dbModules);
		// CommonFunctions.initDir(indexModules);
		// CommonFunctions.initDir(transModule);
		// ComponentProvider.setLoader(new JarBasedModuleLoader());
		// ComponentProvider.loadAuthProvider(authModules);
		// ComponentProvider.loadDatabaseProvider(dbModules);
		// ComponentProvider.loadIndexer(indexModules);
		// ComponentProvider.loadTransformerProvider(transModule);

		PreferenceNode system = new PreferenceNode("system", "System", null,
				SystemPreferencePage.class.getCanonicalName());
		PreferenceGUIManager.addToRoot(system);

		// register the providers
		AuthManagers.INSTANCE.registerAuthProvider(new ViperfishAuthProvider());
		EntryDatabases.INSTANCE.registerEntryDatabaseProvider(new ViperfishEntryDatabaseProvider());
		EntryDatabases.INSTANCE.registerEntryDatabaseProvider(new ViperfishArchiveDBProvider());
		Indexers.INSTANCE.registerIndexerProvider(new ViperfishIndexerProvider());
		JournalTransformers.INSTANCE.registerTransformerProvider(new ViperfishEncryptionProvider());
	}

	/**
	 * This cleans up all the resources, disposes all providers, and terminate
	 * the worker
	 */
	public static void cleanUp() {
		EntryDatabases.INSTANCE.dispose();
		AuthManagers.INSTANCE.dispose();
		Indexers.INSTANCE.dispose();
		JournalTransformers.INSTANCE.dispose();
		System.err.println("Providers disposed");
		OperationExecutors.dispose();
		System.err.println("worker terminated");
	}

	public static void revert() {
		for (Entry<String, Provider<EntryDatabase>> i : EntryDatabases.INSTANCE.getDatabaseProviders().entrySet()) {
			i.getValue().delete();
		}

		for (Entry<String, Provider<Indexer<Journal>>> i : Indexers.INSTANCE.getIndexerProviders().entrySet()) {
			i.getValue().delete();
		}

		for (Entry<String, Provider<JournalTransformer>> i : JournalTransformers.INSTANCE.getSecureProviders()
				.entrySet()) {
			i.getValue().delete();
		}

		for (Entry<String, Provider<AuthenticationManager>> i : AuthManagers.INSTANCE.getAuthProviders().entrySet()) {
			i.getValue().delete();
		}
		Configuration.delete();
	}

	/**
	 * set the default providers to viperfish, the built-in provider
	 */
	public static void defaultProviders() {
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
		setDefaultProviders();
	}

	/**
	 * synchronize the configuration of defualt providers with the actual
	 * provider managers
	 */
	private static void setDefaultProviders() {
		AuthManagers.INSTANCE.setDefaultAuthProvider(Configuration.getString(ConfigMapping.AUTH_PROVIDER));
		EntryDatabases.INSTANCE.setDefaultDatabaseProvider(Configuration.getString(ConfigMapping.DB_PROVIDER));
		Indexers.INSTANCE.setDefaultIndexerProvider(Configuration.getString(ConfigMapping.INDEX_PROVIDER));
		JournalTransformers.INSTANCE
				.setDefaultTransformerProvider(Configuration.getString(ConfigMapping.TRANSFORMER_PROVIDER));
	}

	private static void defaultPreferences() {
		for (Entry<String, Provider<EntryDatabase>> i : EntryDatabases.INSTANCE.getDatabaseProviders().entrySet()) {
			i.getValue().initDefaults();
		}
		for (Entry<String, Provider<Indexer<Journal>>> i : Indexers.INSTANCE.getIndexerProviders().entrySet()) {
			i.getValue().initDefaults();
		}
		for (Entry<String, Provider<AuthenticationManager>> i : AuthManagers.INSTANCE.getAuthProviders().entrySet()) {
			i.getValue().initDefaults();
		}
		for (Entry<String, Provider<JournalTransformer>> i : JournalTransformers.INSTANCE.getSecureProviders()
				.entrySet()) {
			i.getValue().initDefaults();
		}
	}

	public static void main(String[] args) {
		try {

			// load configuration from storage
			try {
				Configuration.load();
			} catch (ConfigurationException e) {
				System.err.println("failed to load configuration, exiting");
				return;
			}

			// register the providers
			try {
				initModules();
			} catch (Throwable e) {
				System.err.println("error:" + e);
				e.printStackTrace();
				return;
			}

			// create the graphical user interface manager
			ui = new GraphicalUserInterface();

			// set the default providers
			defaultProviders();

			// run the setup if the application is first started
			if (Configuration.getString(ConfigMapping.SET_UP) == null) {
				try {
					defaultPreferences();
					ui.setup();
					ui.setFirstPassword();
					Configuration.setProperty(ConfigMapping.SET_UP, false);
					Configuration.save();
				} catch (ConfigurationException e) {
					revert();
					System.err.println("could not save configuration, terminating");
					return;
				} catch (TerminationControlFlowException e) {
					revert();
					System.err.println("exiting");
					return;
				}
			}

			// login
			try {
				ui.promptPassword();
			} catch (TerminationControlFlowException e) {
				System.err.println("exiting");
				return;
			}

			// start the main portion of the application
			ui.run();
		} finally {
			cleanUp();
		}
	}

}

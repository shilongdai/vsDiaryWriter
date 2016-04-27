package net.viperfish.journal;

import java.io.File;
import java.util.Map.Entry;

import org.apache.commons.configuration.ConfigurationException;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;

import net.viperfish.framework.file.CommonFunctions;
import net.viperfish.framework.index.Indexer;
import net.viperfish.journal.authProvider.ViperfishAuthProvider;
import net.viperfish.journal.dbProvider.ViperfishEntryDatabaseProvider;
import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.ModuleLoader;
import net.viperfish.journal.framework.OperationExecutors;
import net.viperfish.journal.framework.provider.AuthManagers;
import net.viperfish.journal.framework.provider.ConfigurationGUISetup;
import net.viperfish.journal.framework.provider.EntryDatabases;
import net.viperfish.journal.framework.provider.Indexers;
import net.viperfish.journal.framework.provider.JournalTransformers;
import net.viperfish.journal.framework.provider.PreferenceGUIManager;
import net.viperfish.journal.framework.provider.Provider;
import net.viperfish.journal.indexProvider.ViperfishIndexerProvider;
import net.viperfish.journal.secureProvider.ViperfishEncryptionProvider;
import net.viperfish.journal.swtGui.GraphicalUserInterface;
import net.viperfish.journal.ui.ExitStatus;
import net.viperfish.journal.ui.UserInterface;

/**
 * the Main class of the application, contains all the components.
 * 
 * <p>
 * This class is designed to run on single thread only
 * </p>
 * 
 * @author sdai
 * 
 */
final public class JournalApplication {
	private static UserInterface ui;
	private static File modules;
	private static ModuleLoader m;

	public JournalApplication() {
	}

	/**
	 * load all providers.
	 * 
	 * This method loads all provider available.
	 */
	public static void initModules() {

		// prepare to load modules

		modules = new File("modules");
		CommonFunctions.initDir(modules);
		m = new JarBasedModuleLoader();

		// put system configuration first

		ConfigurationGUISetup setup = new ConfigurationGUISetup() {

			@Override
			public void proccess(PreferenceManager mger) {
				PreferenceNode system = new PreferenceNode("system", "System", null,
						SystemPreferencePage.class.getCanonicalName());
				mger.addToRoot(system);
			}
		};

		PreferenceGUIManager.add(setup);

		// register the providers
		AuthManagers.INSTANCE.registerAuthProvider(new ViperfishAuthProvider());
		EntryDatabases.INSTANCE.registerEntryDatabaseProvider(new ViperfishEntryDatabaseProvider());
		Indexers.INSTANCE.registerIndexerProvider(new ViperfishIndexerProvider());
		JournalTransformers.INSTANCE.registerTransformerProvider(new ViperfishEncryptionProvider());

		// load third party
		m.loadModules(modules);
	}

	/**
	 * clean up opened resources
	 * 
	 * This method terminates the worker, and disposes all providers.
	 * 
	 */
	public static void cleanUp() {
		OperationExecutors.dispose();
		System.err.println("worker terminated");
		EntryDatabases.INSTANCE.dispose();
		AuthManagers.INSTANCE.dispose();
		Indexers.INSTANCE.dispose();
		JournalTransformers.INSTANCE.dispose();
		System.err.println("Providers disposed");
	}

	/**
	 * deletes all resources
	 * 
	 * This method removes all resources used by all providers. It also remove
	 * all configurations
	 */
	public static void revert() {
		for (Entry<String, Provider<? extends EntryDatabase>> i : EntryDatabases.INSTANCE.getDatabaseProviders()
				.entrySet()) {
			i.getValue().delete();
		}

		for (Entry<String, Provider<? extends Indexer<Journal>>> i : Indexers.INSTANCE.getIndexerProviders()
				.entrySet()) {
			i.getValue().delete();
		}

		for (Entry<String, Provider<? extends JournalTransformer>> i : JournalTransformers.INSTANCE.getSecureProviders()
				.entrySet()) {
			i.getValue().delete();
		}

		for (Entry<String, Provider<? extends AuthenticationManager>> i : AuthManagers.INSTANCE.getAuthProviders()
				.entrySet()) {
			i.getValue().delete();
		}
		Configuration.delete();
	}

	/**
	 * set default providers
	 * 
	 * This method sets default providers to <i>viperfish</i>, the built in
	 * provider, if no default providers are specified. Otherwise, it uses the
	 * ones configured.
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
	 * set built-in provider in configuration
	 * 
	 * This method sets default providers to <i>viperfish</i> in the
	 * configuration
	 */
	private static void setDefaultProviders() {
		AuthManagers.INSTANCE.setDefaultAuthProvider(Configuration.getString(ConfigMapping.AUTH_PROVIDER));
		EntryDatabases.INSTANCE.setDefaultDatabaseProvider(Configuration.getString(ConfigMapping.DB_PROVIDER));
		Indexers.INSTANCE.setDefaultIndexerProvider(Configuration.getString(ConfigMapping.INDEX_PROVIDER));
		JournalTransformers.INSTANCE
				.setDefaultTransformerProvider(Configuration.getString(ConfigMapping.TRANSFORMER_PROVIDER));
	}

	/**
	 * set all configuration to default
	 * 
	 * This method sets all configuration of all loaded providers to its default
	 * configuration
	 */
	private static void defaultPreferences() {
		for (Entry<String, Provider<? extends EntryDatabase>> i : EntryDatabases.INSTANCE.getDatabaseProviders()
				.entrySet()) {
			i.getValue().initDefaults();
		}
		for (Entry<String, Provider<? extends Indexer<Journal>>> i : Indexers.INSTANCE.getIndexerProviders()
				.entrySet()) {
			i.getValue().initDefaults();
		}
		for (Entry<String, Provider<? extends AuthenticationManager>> i : AuthManagers.INSTANCE.getAuthProviders()
				.entrySet()) {
			i.getValue().initDefaults();
		}
		for (Entry<String, Provider<? extends JournalTransformer>> i : JournalTransformers.INSTANCE.getSecureProviders()
				.entrySet()) {
			i.getValue().initDefaults();
		}
		Configuration.setProperty(ConfigMapping.AUTH_COMPONENT, "BCrypt");
		Configuration.setProperty(ConfigMapping.DB_COMPONENT, "H2Database");
		Configuration.setProperty(ConfigMapping.INDEXER_COMPONENT, "LuceneIndexer");
		Configuration.setProperty(ConfigMapping.TRANSFORMER_COMPONENT, "BlockCipherMAC");
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
					ExitStatus e;
					defaultPreferences();
					e = ui.setFirstPassword();
					if (e == ExitStatus.CANCEL) {
						revert();
						return;
					}
					Configuration.setProperty(ConfigMapping.SET_UP, false);
					Configuration.save();
				} catch (ConfigurationException e) {
					revert();
					System.err.println("could not save configuration, terminating");
					return;
				}
			}
			ExitStatus e = ui.promptPassword();
			if (e == ExitStatus.CANCEL) {
				return;
			}
			// start the main portion of the application
			ui.run();
		} finally {
			cleanUp();
		}
	}

}

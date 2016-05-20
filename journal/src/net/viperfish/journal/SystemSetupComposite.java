package net.viperfish.journal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.viperfish.framework.index.Indexer;
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
import net.viperfish.journal.framework.provider.Provider;

final class SystemSetupComposite extends Composite {

	private Combo dataStorageSelector;
	private Combo indexerSelector;
	private Combo authSelector;
	private Combo transformerSelector;
	private Combo langSelector;
	private Combo regionSelector;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public SystemSetupComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(4, false));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		Label dataLabel = new Label(this, SWT.NONE);
		dataLabel.setText("Data Storage");
		new Label(this, SWT.NONE);

		dataStorageSelector = new Combo(this, SWT.READ_ONLY);
		dataStorageSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		new Label(this, SWT.NONE);

		Label indexerLabel = new Label(this, SWT.NONE);
		indexerLabel.setText("Indexer");
		new Label(this, SWT.NONE);

		indexerSelector = new Combo(this, SWT.READ_ONLY);
		indexerSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);

		Label authManagerLabel = new Label(this, SWT.NONE);
		authManagerLabel.setText("Authentication");
		new Label(this, SWT.NONE);

		authSelector = new Combo(this, SWT.READ_ONLY);
		authSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);

		Label transformerLabel = new Label(this, SWT.NONE);
		transformerLabel.setText("Encryption");
		new Label(this, SWT.NONE);

		transformerSelector = new Combo(this, SWT.READ_ONLY);
		transformerSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);

		Label langLabel = new Label(this, SWT.NONE);
		langLabel.setText("Language");
		new Label(this, SWT.NONE);

		langSelector = new Combo(this, SWT.NONE);
		langSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);

		Label regionLabel = new Label(this, SWT.NONE);
		regionLabel.setText("Region");
		new Label(this, SWT.NONE);

		regionSelector = new Combo(this, SWT.NONE);
		regionSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fillIndexerSelector();
		fillInTransformer();
		fillInAuth();
		fillDataStorageSelector();
		fillInLocale();

		fillInConfigValues();

	}

	private void fillDataStorageSelector() {
		Set<String> buf = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Entry<String, Provider<? extends EntryDatabase>> i : EntryDatabases.INSTANCE.getDatabaseProviders()
				.entrySet()) {
			buf.addAll(Arrays.asList(i.getValue().getSupported()));
		}
		for (String i : buf) {
			dataStorageSelector.add(i);
		}
		return;
	}

	private void fillIndexerSelector() {
		Set<String> buf = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Entry<String, Provider<? extends Indexer<Journal>>> i : Indexers.INSTANCE.getIndexerProviders()
				.entrySet()) {
			buf.addAll(Arrays.asList(i.getValue().getSupported()));
		}
		for (String i : buf) {
			indexerSelector.add(i);
		}
	}

	private void fillInAuth() {
		Set<String> buf = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Entry<String, Provider<? extends AuthenticationManager>> i : AuthManagers.INSTANCE.getAuthProviders()
				.entrySet()) {
			buf.addAll(Arrays.asList(i.getValue().getSupported()));
		}
		for (String i : buf) {
			authSelector.add(i);
		}
	}

	private void fillInLocale() {
		langSelector.add("en");

		regionSelector.add("US");
	}

	private void fillInTransformer() {
		Set<String> buf = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Entry<String, Provider<? extends JournalTransformer>> i : JournalTransformers.INSTANCE.getSecureProviders()
				.entrySet()) {
			buf.addAll(Arrays.asList(i.getValue().getSupported()));
		}
		for (String i : buf) {
			transformerSelector.add(i);
		}
	}

	private void fillInConfigValues() {
		String dbComponent = Configuration.getString(ConfigMapping.DB_COMPONENT);
		if (dbComponent != null) {
			dataStorageSelector.setText(dbComponent);
		} else {
			dataStorageSelector.setText("H2Database");
		}
		String indexerComponent = Configuration.getString(ConfigMapping.INDEXER_COMPONENT);
		if (indexerComponent != null) {
			indexerSelector.setText(indexerComponent);
		} else {
			indexerSelector.setText("LuceneIndexer");
		}
		String authComponent = Configuration.getString(ConfigMapping.AUTH_COMPONENT);
		if (authComponent != null) {
			authSelector.setText(authComponent);
		} else {
			authSelector.setText("Hash");
		}
		String encryptionComponent = Configuration.getString(ConfigMapping.TRANSFORMER_COMPONENT);
		if (encryptionComponent != null) {
			transformerSelector.setText(encryptionComponent);
		} else {
			transformerSelector.setText("BlockCipherMAC");
		}

		String lang = Configuration.getString(ConfigMapping.LANG);
		if (lang != null) {
			langSelector.setText(lang);
		} else {
			langSelector.setText("en");
		}

		String region = Configuration.getString(ConfigMapping.REGION);
		if (region != null) {
			regionSelector.setText(region);
		} else {
			regionSelector.setText("US");
		}
	}

	public Map<String, String> save() {
		Map<String, String> configuration = new HashMap<>();
		configuration.put(ConfigMapping.DB_COMPONENT, dataStorageSelector.getText());
		configuration.put(ConfigMapping.INDEXER_COMPONENT, indexerSelector.getText());
		configuration.put(ConfigMapping.AUTH_COMPONENT, authSelector.getText());
		configuration.put(ConfigMapping.TRANSFORMER_COMPONENT, transformerSelector.getText());
		configuration.put(ConfigMapping.LANG, langSelector.getText());
		configuration.put(ConfigMapping.REGION, regionSelector.getText());
		return configuration;
	}

	public void defaultAll() {
		dataStorageSelector.setText("H2Database");
		indexerSelector.setText("LuceneIndexer");
		authSelector.setText("Hash");
		transformerSelector.setText("BlockCipherMAC");
		regionSelector.setText("US");
		langSelector.setText("en");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}

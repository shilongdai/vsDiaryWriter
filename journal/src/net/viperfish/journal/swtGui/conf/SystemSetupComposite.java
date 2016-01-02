package net.viperfish.journal.swtGui.conf;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.viperfish.journal.ConfigMapping;
import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.provider.ComponentProvider;
import net.viperfish.journal.provider.Provider;
import net.viperfish.utils.index.Indexer;

public class SystemSetupComposite extends Composite {

	private Combo dataStorageSelector;
	private Combo indexerSelector;
	private Combo authSelector;
	private Combo transformerSelector;

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
		fillIndexerSelector();
		fillInTransformer();
		fillInAuth();
		fillDataStorageSelector();

		dataStorageSelector.select(0);
		indexerSelector.select(0);
		authSelector.select(0);
		transformerSelector.select(0);

	}

	private void fillDataStorageSelector() {
		Set<String> buf = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Entry<String, Provider<EntryDatabase>> i : ComponentProvider.getDatabaseProviders().entrySet()) {
			buf.addAll(Arrays.asList(i.getValue().getSupported()));
		}
		for (String i : buf) {
			dataStorageSelector.add(i);
		}
		return;
	}

	private void fillIndexerSelector() {
		Set<String> buf = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Entry<String, Provider<Indexer<Journal>>> i : ComponentProvider.getIndexerProviders().entrySet()) {
			buf.addAll(Arrays.asList(i.getValue().getSupported()));
		}
		for (String i : buf) {
			indexerSelector.add(i);
		}
	}

	private void fillInAuth() {
		Set<String> buf = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Entry<String, Provider<AuthenticationManager>> i : ComponentProvider.getAuthProviders().entrySet()) {
			buf.addAll(Arrays.asList(i.getValue().getSupported()));
		}
		for (String i : buf) {
			authSelector.add(i);
		}
	}

	private void fillInTransformer() {
		Set<String> buf = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		for (Entry<String, Provider<JournalTransformer>> i : ComponentProvider.getSecureProviders().entrySet()) {
			buf.addAll(Arrays.asList(i.getValue().getSupported()));
		}
		for (String i : buf) {
			transformerSelector.add(i);
		}
	}

	public void save() {
		JournalApplication.getConfiguration().setProperty(ConfigMapping.DB_COMPONENT, dataStorageSelector.getText());
		JournalApplication.getConfiguration().setProperty(ConfigMapping.INDEXER_COMPONENT, indexerSelector.getText());
		JournalApplication.getConfiguration().setProperty(ConfigMapping.AUTH_COMPONENT, authSelector.getText());
		JournalApplication.getConfiguration().setProperty(ConfigMapping.TRANSFORMER_COMPONENT,
				transformerSelector.getText());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}

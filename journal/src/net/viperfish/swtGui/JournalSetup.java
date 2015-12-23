package net.viperfish.swtGui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import net.viperfish.utils.config.ComponentConfig;
import net.viperfish.utils.config.Configuration;

public class JournalSetup {

	private ComponentConfig system;
	private ComponentConfig security;

	public JournalSetup() {
		system = Configuration.get("system");
		security = Configuration.get("secureEntryWrapper");
	}

	/**
	 * Open the window.
	 * 
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		Display display = Display.getDefault();
		final Shell shlSetup = new Shell();
		shlSetup.setSize(450, 300);
		shlSetup.setText("Setup");
		shlSetup.setLayout(new GridLayout(1, false));

		TabFolder tabFolder = new TabFolder(shlSetup, SWT.NONE);
		GridData gd_tabFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_tabFolder.widthHint = 410;
		gd_tabFolder.heightHint = 174;
		tabFolder.setLayoutData(gd_tabFolder);

		TabItem tbtmSystem = new TabItem(tabFolder, SWT.NONE);
		tbtmSystem.setText("System");

		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmSystem.setControl(composite);
		composite.setLayout(new GridLayout(2, false));

		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setText("Data Storage");

		final Combo combo = new Combo(composite, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for (String i : system.getOptions("DataSourceFactory")) {
			combo.add(i);
		}

		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setText("Indexer");

		final Combo combo_1 = new Combo(composite, SWT.NONE);
		combo_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for (String i : system.getOptions("IndexerFactory")) {
			combo_1.add(i);
		}

		TabItem tbtmNewItem = new TabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Security");

		Composite security = new Composite(tabFolder, SWT.None);
		tbtmNewItem.setControl(security);
		security.setLayout(new GridLayout(2, false));

		Label lblNewLabel_2 = new Label(security, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("Encryption Algorithm");

		final Combo combo_2 = new Combo(security, SWT.NONE);
		combo_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		for (String i : this.security.getOptions("EncryptionMethod")) {
			combo_2.add(i);
		}

		Label lblNewLabel_3 = new Label(security, SWT.NONE);
		lblNewLabel_3.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_3.setText("Encryption Mode");

		final Combo combo_3 = new Combo(security, SWT.NONE);
		combo_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for (String i : this.security.getOptions("EncryptionMode")) {
			combo_3.add(i);
		}

		Label lblNewLabel_4 = new Label(security, SWT.NONE);
		lblNewLabel_4.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_4.setText("Encryption Padding");

		final Combo combo_4 = new Combo(security, SWT.NONE);
		combo_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for (String i : this.security.getOptions("EncryptionPadding")) {
			combo_4.add(i);
		}

		Label lblNewLabel_5 = new Label(security, SWT.NONE);
		lblNewLabel_5.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_5.setText("MAC Type");

		final Combo combo_5 = new Combo(security, SWT.NONE);
		combo_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for (String i : this.security.getOptions("MacMethod")) {
			combo_5.add(i);
		}
		this.security.setProperty("MacMethod", combo_5.getText());
		Label lblNewLabel_6 = new Label(security, SWT.NONE);
		lblNewLabel_6.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_6.setText("MAC Algorithm");

		final Combo combo_6 = new Combo(security, SWT.NONE);
		combo_6.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		initMacAlgorithms(combo_6);

		combo_5.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				JournalSetup.this.security.setProperty("MacMethod", combo_5.getText());
				initMacAlgorithms(combo_6);
			}
		});

		Button btnNewButton = new Button(shlSetup, SWT.NONE);
		btnNewButton.setText("Done");
		btnNewButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				system.setProperty("DataSourceFactory", combo.getText());
				system.setProperty("IndexerFactory", combo_1.getText());
				JournalSetup.this.security.setProperty("EncryptionMethod", combo_2.getText());
				JournalSetup.this.security.setProperty("EncryptionMode", combo_3.getText());
				JournalSetup.this.security.setProperty("EncryptionPadding", combo_4.getText());
				JournalSetup.this.security.setProperty("MacMethod", combo_5.getText());
				JournalSetup.this.security.setProperty("MacAlgorithm", combo_6.getText());
				shlSetup.dispose();
			}

		});
		shlSetup.open();
		shlSetup.layout();
		while (!shlSetup.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void initMacAlgorithms(Combo c) {
		c.setItems(new String[0]);
		for (String i : security.getOptions("MacAlgorithm")) {
			c.add(i);
		}
	}
}

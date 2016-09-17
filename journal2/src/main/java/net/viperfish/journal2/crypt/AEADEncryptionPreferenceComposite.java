package net.viperfish.journal2.crypt;

import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.springframework.context.MessageSource;

public class AEADEncryptionPreferenceComposite extends Composite {

	private Configuration config;
	private Combo algSelector;
	private Combo modeSelector;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public AEADEncryptionPreferenceComposite(Composite parent, int style, Configuration config, MessageSource i18n) {
		super(parent, style);
		this.config = config;
		setLayout(new GridLayout(3, false));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		Label encryptionAlgLabel = new Label(this, SWT.NONE);
		encryptionAlgLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		encryptionAlgLabel.setText(i18n.getMessage("config.encryption.lblEncryptionAlg", null, Locale.getDefault()));

		algSelector = new Combo(this, SWT.READ_ONLY);
		algSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);

		Label encryptionModeLabel = new Label(this, SWT.NONE);
		encryptionModeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		encryptionModeLabel.setText(i18n.getMessage("config.encryption.mode", null, Locale.getDefault()));

		modeSelector = new Combo(this, SWT.READ_ONLY);
		modeSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		for (String i : BlockCiphers.getAEADModes()) {
			modeSelector.add(i);
		}

		modeSelector.setText(config.containsKey(AEADProccessor.CONFIG_ENCRYPTION_MODE)
				? config.getString(AEADProccessor.CONFIG_ENCRYPTION_MODE) : "GCM");
		new Label(this, SWT.NONE);

		modeSelector.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				fillInAlgorithms();
			}
		});

		fillInAlgorithms();

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void done() {
		config.setProperty(AEADProccessor.CONFIG_ENCRYPTION_ALGORITHM, algSelector.getText());
		config.setProperty(AEADProccessor.CONFIG_ENCRYPTION_MODE, modeSelector.getText());
		config.setProperty(AEADProccessor.CONFIG_ENCRYPTION_KEYLENGTH, BlockCiphers.getKeySize(algSelector.getText()));
	}

	public void setDefault() {
		modeSelector.setText("GCM");
		algSelector.setText("MARS");
	}

	private void fillInAlgorithms() {
		algSelector.removeAll();
		if (modeSelector.getText().equals("EAX")) {
			for (String i : BlockCiphers.getSupportedBlockCipher()) {
				algSelector.add(i);
			}
		} else {
			for (String i : BlockCiphers.get128Algorithms()) {
				algSelector.add(i);
			}
		}
		algSelector.setText(config.containsKey(AEADProccessor.CONFIG_ENCRYPTION_ALGORITHM)
				? config.getString(AEADProccessor.CONFIG_ENCRYPTION_ALGORITHM) : "MARS");

	}

}

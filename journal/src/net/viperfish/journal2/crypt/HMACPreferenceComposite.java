package net.viperfish.journal2.crypt;

import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.springframework.context.MessageSource;

public class HMACPreferenceComposite extends Composite {

	private Combo hashCombo;
	private Spinner keySizeSpinner;
	private Configuration config;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public HMACPreferenceComposite(Composite parent, int style, Configuration config, MessageSource i18n) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		this.config = config;
		Label hashAlgorithmLabel = new Label(this, SWT.NONE);
		hashAlgorithmLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		hashAlgorithmLabel.setText(i18n.getMessage("config.hmac.hash", null, Locale.getDefault()));

		hashCombo = new Combo(this, SWT.READ_ONLY);
		hashCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);

		Label keySizeLabel = new Label(this, SWT.NONE);
		keySizeLabel.setText(i18n.getMessage("config.hmac.keySize", null, Locale.getDefault()));

		keySizeSpinner = new Spinner(this, SWT.BORDER);
		keySizeSpinner.setMaximum(128);
		keySizeSpinner.setToolTipText(i18n.getMessage("config.hmac.keySize.toolTip", null, Locale.getDefault()));

		for (String i : Digesters.getSupportedDigest()) {
			hashCombo.add(i);
		}

		if (config.containsKey(HMACProcessor.MAC_ALGORITHM)) {
			hashCombo.setText(config.getString(HMACProcessor.MAC_ALGORITHM));
		} else {
			hashCombo.setText("SHA256");
			config.setProperty(HMACProcessor.MAC_ALGORITHM, "SHA256");
		}

		if (config.containsKey(HMACProcessor.MAC_SIZE)) {
			keySizeSpinner.setSelection(config.getInt(HMACProcessor.MAC_SIZE));
		} else {
			keySizeSpinner.setSelection(16);
			config.setProperty(HMACProcessor.MAC_SIZE, 16);
		}
	}

	public void setDefault() {
		hashCombo.setText("SHA256");
		keySizeSpinner.setSelection(16);
	}

	public void save() {
		config.setProperty(HMACProcessor.MAC_ALGORITHM, hashCombo.getText());
		config.setProperty(HMACProcessor.MAC_SIZE, keySizeSpinner.getSelection());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}

package net.viperfish.journal2.crypt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import net.viperfish.journal2.core.JournalConfiguration;
import net.viperfish.journal2.core.JournalI18NBundle;

public class HMACPreferenceComposite extends Composite {

	private Combo hashCombo;
	private Spinner keySizeSpinner;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public HMACPreferenceComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		Label hashAlgorithmLabel = new Label(this, SWT.NONE);
		hashAlgorithmLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		hashAlgorithmLabel.setText(JournalI18NBundle.getString("config.hmac.hash"));

		hashCombo = new Combo(this, SWT.READ_ONLY);
		hashCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);

		Label keySizeLabel = new Label(this, SWT.NONE);
		keySizeLabel.setText(JournalI18NBundle.getString("config.hmac.keySize"));

		keySizeSpinner = new Spinner(this, SWT.BORDER);
		keySizeSpinner.setMaximum(128);
		keySizeSpinner.setToolTipText(JournalI18NBundle.getString("config.hmac.keySize.toolTip"));

		for (String i : Digesters.getSupportedDigest()) {
			hashCombo.add(i);
		}

		if (JournalConfiguration.containsKey(HMACProcessor.MAC_ALGORITHM)) {
			hashCombo.setText(JournalConfiguration.getString(HMACProcessor.MAC_ALGORITHM));
		} else {
			hashCombo.setText("SHA256");
			JournalConfiguration.setProperty(HMACProcessor.MAC_ALGORITHM, "SHA256");
		}

		if (JournalConfiguration.containsKey(HMACProcessor.MAC_SIZE)) {
			keySizeSpinner.setSelection(JournalConfiguration.getInt(HMACProcessor.MAC_SIZE));
		} else {
			keySizeSpinner.setSelection(16);
			JournalConfiguration.setProperty(HMACProcessor.MAC_SIZE, 16);
		}
	}

	public void setDefault() {
		hashCombo.setText("SHA256");
		keySizeSpinner.setSelection(16);
	}

	public void save() {
		JournalConfiguration.setProperty(HMACProcessor.MAC_ALGORITHM, hashCombo.getText());
		JournalConfiguration.setProperty(HMACProcessor.MAC_SIZE, keySizeSpinner.getSelection());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}

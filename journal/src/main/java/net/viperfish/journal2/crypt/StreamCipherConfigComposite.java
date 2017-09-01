package net.viperfish.journal2.crypt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.viperfish.journal2.core.JournalConfiguration;
import net.viperfish.journal2.core.JournalI18NBundle;

public class StreamCipherConfigComposite extends Composite {

	private Combo algorithmCombo;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public StreamCipherConfigComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		Label streamCipherLabel = new Label(this, SWT.NONE);
		streamCipherLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		streamCipherLabel.setText(JournalI18NBundle.getString("config.streamCipher.alg"));

		algorithmCombo = new Combo(this, SWT.READ_ONLY);
		algorithmCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		for (String i : StreamCipherEncryptors.INSTANCE.getSupported()) {
			algorithmCombo.add(i);
		}
		algorithmCombo.setText(JournalConfiguration.containsKey(StreamCipherProcessor.CIPHER_ALGORITHM)
				? JournalConfiguration.getString(StreamCipherProcessor.CIPHER_ALGORITHM) : "XSalsa20");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setDefault() {
		algorithmCombo.setText("XSalsa20");
	}

	public void save() {
		JournalConfiguration.setProperty(StreamCipherProcessor.CIPHER_ALGORITHM, algorithmCombo.getText());
		JournalConfiguration.setProperty(StreamCipherProcessor.CIPHER_KEYLENGTH,
				StreamCipherEncryptors.INSTANCE.getKeySize(algorithmCombo.getText()));
	}

}

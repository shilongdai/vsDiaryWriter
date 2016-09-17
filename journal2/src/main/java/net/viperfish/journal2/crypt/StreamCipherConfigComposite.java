package net.viperfish.journal2.crypt;

import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.springframework.context.MessageSource;

public class StreamCipherConfigComposite extends Composite {

	private Configuration config;
	private MessageSource i18n;
	private Combo algorithmCombo;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public StreamCipherConfigComposite(Composite parent, int style, Configuration config, MessageSource i18n) {
		super(parent, style);
		this.config = config;
		this.i18n = i18n;
		setLayout(new GridLayout(2, false));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		Label streamCipherLabel = new Label(this, SWT.NONE);
		streamCipherLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		streamCipherLabel.setText(i18n.getMessage("config.streamCipher.alg", null, Locale.getDefault()));

		algorithmCombo = new Combo(this, SWT.READ_ONLY);
		algorithmCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		for (String i : StreamCipherEncryptors.INSTANCE.getSupported()) {
			algorithmCombo.add(i);
		}
		algorithmCombo.setText(config.getString(StreamCipherProcessor.CIPHER_ALGORITHM));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setDefault() {
		algorithmCombo.setText("XSalsa20");
	}

	public void save() {
		config.setProperty(StreamCipherProcessor.CIPHER_ALGORITHM, algorithmCombo.getText());
		config.setProperty(StreamCipherProcessor.CIPHER_KEYLENGTH,
				StreamCipherEncryptors.INSTANCE.getKeySize(algorithmCombo.getText()));
	}

}

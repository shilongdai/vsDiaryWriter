package net.viperfish.journal.secureProvider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.secureAlgs.Digesters;
import net.viperfish.journal.streamCipher.StreamCipherEncryptors;

final class StreamCipherConfigComposite extends Composite {

	private Combo cipherSelector;
	private Combo hkdfCombo;

	private void fillInCiphers() {
		for (String cipher : StreamCipherEncryptors.INSTANCE.getSupported()) {
			cipherSelector.add(cipher);
		}

		for (String hash : Digesters.getSupportedDigest()) {
			hkdfCombo.add(hash);
		}
		defaultAll();
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public StreamCipherConfigComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));

		Label cipherLabel = new Label(this, SWT.NONE);
		cipherLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		cipherLabel.setText("Stream Cipher");

		cipherSelector = new Combo(this, SWT.READ_ONLY);
		cipherSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);

		Label hkdfSelector = new Label(this, SWT.NONE);
		hkdfSelector.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		hkdfSelector.setText("HKDF Algorithm");

		hkdfCombo = new Combo(this, SWT.READ_ONLY);
		hkdfCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);

		fillInCiphers();
	}

	public Map<String, String> save() {
		Map<String, String> result = new HashMap<>();
		result.put(StreamCipherTransformer.ALG_NAME, cipherSelector.getText());
		result.put(StreamCipherTransformer.HKDF_ALG, hkdfCombo.getText());
		return result;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void defaultAll() {
		String config = Configuration.getString(StreamCipherTransformer.ALG_NAME);
		if (config == null) {
			cipherSelector.setText("ChaCha");
		} else {
			cipherSelector.setText(config);
		}

		String hkdf = Configuration.getString(StreamCipherTransformer.HKDF_ALG);
		if (hkdf == null) {
			hkdfCombo.setText("SHA512");
		} else {
			hkdfCombo.setText(hkdf);
		}

	}

}

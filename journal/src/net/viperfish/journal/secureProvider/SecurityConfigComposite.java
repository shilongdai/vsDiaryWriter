package net.viperfish.journal.secureProvider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.secureAlgs.BlockCiphers;
import net.viperfish.journal.secureAlgs.Digesters;
import net.viperfish.utils.compression.Compressors;

final class SecurityConfigComposite extends Composite {

	private Combo encAlgSelector;
	private Combo encModeSelector;
	private Combo encPadSelector;
	private Combo macTypeSelector;
	private Combo macAlgSelector;
	private Label kdfLabel;
	private Combo kdfCombo;
	private Label compressionLabel;
	private Combo compressionSelector;
	private Label errorLabel;

	private class ValidateModifyListener implements ModifyListener {

		@Override
		public void modifyText(ModifyEvent arg0) {
			validate();

		}

	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public SecurityConfigComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label encryptionAlgLabel = new Label(this, SWT.NONE);
		encryptionAlgLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		encryptionAlgLabel.setText("Encryption Algorithm");

		encAlgSelector = new Combo(this, SWT.READ_ONLY);
		encAlgSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		encAlgSelector.addModifyListener(new ValidateModifyListener());

		Label encModeLabel = new Label(this, SWT.NONE);
		encModeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		encModeLabel.setText("Encryption Mode");

		encModeSelector = new Combo(this, SWT.READ_ONLY);
		encModeSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		encModeSelector.addModifyListener(new ValidateModifyListener());

		Label encPadLabel = new Label(this, SWT.NONE);
		encPadLabel.setText("Encryption Padding");

		encPadSelector = new Combo(this, SWT.READ_ONLY);
		encPadSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		encPadSelector.addModifyListener(new ValidateModifyListener());

		Label macTypeLabel = new Label(this, SWT.NONE);
		macTypeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		macTypeLabel.setText("Mac Type");

		macTypeSelector = new Combo(this, SWT.READ_ONLY);
		macTypeSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		macTypeSelector.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				fillInMacAlg();
			}
		});

		Label macAlgLabel = new Label(this, SWT.NONE);
		macAlgLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		macAlgLabel.setText("Mac Algorithm");

		macAlgSelector = new Combo(this, SWT.READ_ONLY);
		macAlgSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		macAlgSelector.addModifyListener(new ValidateModifyListener());

		kdfLabel = new Label(this, SWT.NONE);
		kdfLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		kdfLabel.setText("KDF Hash");

		kdfCombo = new Combo(this, SWT.READ_ONLY);
		kdfCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		kdfCombo.addModifyListener(new ValidateModifyListener());

		compressionLabel = new Label(this, SWT.NONE);
		compressionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		compressionLabel.setText("Compression");

		compressionSelector = new Combo(this, SWT.READ_ONLY);
		compressionSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		errorLabel = new Label(this, SWT.NONE);
		errorLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		errorLabel.setText("");
		fillIn();

	}

	public Map<String, String> save() {
		Map<String, String> result = new HashMap<>();
		result.put(BlockCipherMacTransformer.ENCRYPTION_ALG_NAME, encAlgSelector.getText());
		result.put(BlockCipherMacTransformer.ENCRYPTION_MODE, encModeSelector.getText());
		result.put(BlockCipherMacTransformer.ENCRYPTION_PADDING, encPadSelector.getText());
		result.put(BlockCipherMacTransformer.MAC_TYPE, macTypeSelector.getText());
		result.put(BlockCipherMacTransformer.MAC_ALGORITHM, macAlgSelector.getText());
		result.put(BlockCipherMacTransformer.KDF_HASH, kdfCombo.getText());
		result.put(BlockCipherMacTransformer.COMPRESSION, compressionSelector.getText());
		return result;

	}

	private void fillIn() {
		for (String i : BlockCiphers.getSupportedBlockCipher()) {
			encAlgSelector.add(i);
		}
		for (String i : BlockCiphers.getSupportedBlockCipherMode()) {
			encModeSelector.add(i);
		}
		for (String i : BlockCiphers.getSupportedBlockCipherPadding()) {
			encPadSelector.add(i);
		}
		for (String i : Digesters.getSupportedDigest()) {
			kdfCombo.add(i);
		}
		for (String i : Compressors.getCompressors()) {
			compressionSelector.add(i);
		}

		macTypeSelector.add("CMAC");
		macTypeSelector.add("GMAC");
		macTypeSelector.add("CBCMAC");
		macTypeSelector.add("CFBMAC");
		macTypeSelector.add("HMAC");

		String macType = Configuration.getString(BlockCipherMacTransformer.MAC_TYPE);
		if (macType != null) {
			macTypeSelector.setText(macType);
		} else {
			macTypeSelector.setText("HMAC");
		}

		String encryptionPad = Configuration.getString(BlockCipherMacTransformer.ENCRYPTION_PADDING);
		if (encryptionPad != null) {
			encPadSelector.setText(encryptionPad);
		} else {
			encPadSelector.setText("PKCS7Padding");
		}

		String encryptionMode = Configuration.getString(BlockCipherMacTransformer.ENCRYPTION_MODE);
		if (encryptionMode != null) {
			encModeSelector.setText(encryptionMode);
		} else {
			encModeSelector.setText("CFB");
		}

		String encryptionAlgorithm = Configuration.getString(BlockCipherMacTransformer.ENCRYPTION_ALG_NAME);
		if (encryptionAlgorithm != null) {
			encAlgSelector.setText(encryptionAlgorithm);
		} else {
			encAlgSelector.setText("AES");
		}

		String kdfAlg = Configuration.getString(BlockCipherMacTransformer.KDF_HASH);
		if (kdfAlg != null) {
			kdfCombo.setText(kdfAlg);
		} else {
			kdfCombo.setText("SHA256");
		}

		String compressionType = Configuration.getString(BlockCipherMacTransformer.COMPRESSION);
		if (compressionType != null) {
			compressionSelector.setText(compressionType);
		} else {
			compressionSelector.setText("GZ");
		}
		fillInMacAlg();
	}

	private void fillInMacAlg() {
		macAlgSelector.setItems(new String[0]);
		if (macTypeSelector.getText().equals("CMAC") || macTypeSelector.getText().equals("CBCMAC")
				|| macTypeSelector.getText().equals("CFBMAC")) {
			for (String i : BlockCiphers.getSupportedBlockCipher()) {
				macAlgSelector.add(i);
			}
			macAlgSelector.setText("Twofish");
		} else if (macTypeSelector.getText().equals("GMAC")) {
			for (String i : BlockCiphers.getGmacAlgorithms()) {
				macAlgSelector.add(i);
			}
			macAlgSelector.setText("Twofish");
		} else {
			for (String i : Digesters.getSupportedDigest()) {
				macAlgSelector.add(i);

			}
			macAlgSelector.setText("SHA256");
		}
		String macAlorithm = Configuration.getString(BlockCipherMacTransformer.MAC_ALGORITHM);
		if (macAlorithm != null) {
			macAlgSelector.setText(macAlorithm);
		}
		validate();
	}

	public boolean validate() {
		if (encAlgSelector.getText().length() == 0) {
			errorLabel.setText("You must select an encryption algorithm");
			return false;
		}
		if (encModeSelector.getText().length() == 0) {
			errorLabel.setText("You must select an encryption mode");
			return false;
		}
		if (encPadSelector.getText().length() == 0) {
			errorLabel.setText("You must select an encryption padding");
			return false;
		}
		if (macTypeSelector.getText().length() == 0) {
			errorLabel.setText("You must select an MAC type");
			return false;
		}
		if (macAlgSelector.getText().length() == 0) {
			errorLabel.setText("You must select an MAC algorithm");
			return false;
		}
		if (kdfCombo.getText().length() == 0) {
			errorLabel.setText("You must select an KDF algorithm");
			return false;
		}
		errorLabel.setText("");
		return true;
	}

	public void defaultAll() {
		macTypeSelector.setText("HMAC");
		encPadSelector.setText("PKCS7Padding");
		encModeSelector.setText("CFB");
		encAlgSelector.setText("AES");
		kdfCombo.setText("SHA256");
		compressionSelector.setText("GZ");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}

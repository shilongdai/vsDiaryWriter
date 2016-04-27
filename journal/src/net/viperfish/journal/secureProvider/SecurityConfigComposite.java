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

import net.viperfish.framework.compression.Compressors;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.secureAlgs.BlockCiphers;
import net.viperfish.journal.secureAlgs.Digesters;

final class SecurityConfigComposite extends Composite {
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
		result.put(CompressMacTransformer.MAC_TYPE, macTypeSelector.getText());
		result.put(CompressMacTransformer.MAC_ALGORITHM, macAlgSelector.getText());
		result.put(CompressMacTransformer.KDF_HASH, kdfCombo.getText());
		result.put(CompressMacTransformer.COMPRESSION, compressionSelector.getText());
		return result;

	}

	private void fillIn() {
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
		kdfCombo.setText("SHA256");
		compressionSelector.setText("GZ");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}

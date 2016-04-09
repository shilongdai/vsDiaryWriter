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

final class BlockCipherConfigComposite extends Composite {
	private Combo encAlgSelector;
	private Combo encModeSelector;
	private Combo encPadSelector;
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
	public BlockCipherConfigComposite(Composite parent, int style) {
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
		errorLabel.setText("");
		return true;
	}

	public void defaultAll() {
		encPadSelector.setText("PKCS7Padding");
		encModeSelector.setText("CFB");
		encAlgSelector.setText("AES");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}

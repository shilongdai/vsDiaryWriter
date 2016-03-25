package net.viperfish.journal.authProvider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.secureAlgs.BlockCiphers;
import net.viperfish.journal.secureAlgs.Digesters;

class UnixAuthComposite extends Composite {

	private Combo encryptionCombo;

	private Combo kdfCombo;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public UnixAuthComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		Label encryptionLabel = new Label(this, SWT.NONE);
		encryptionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		encryptionLabel.setText("Encryption Algorithm");

		encryptionCombo = new Combo(this, SWT.READ_ONLY);
		encryptionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);

		Label kdfLabel = new Label(this, SWT.READ_ONLY);
		kdfLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		kdfLabel.setText("KDF Algorithm");

		kdfCombo = new Combo(this, SWT.READ_ONLY);
		kdfCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		Label classicLabel = new Label(this, SWT.NONE);
		classicLabel.setText("* The classic algorithms");

		initSelections();

	}

	private void initSelections() {
		for (String i : BlockCiphers.getSupportedBlockCipher()) {
			encryptionCombo.add(i);
		}

		for (String i : Digesters.getSupportedDigest()) {
			kdfCombo.add(i);
		}

		String encryptionAlg = Configuration.getString(UnixLikeAuthManager.ENCRYPTION_ALG);
		if (encryptionAlg != null) {
			encryptionCombo.setText(encryptionAlg);
		} else {
			encryptionCombo.setText("DESede");
		}

		String kdfAlgorithm = Configuration.getString(UnixLikeAuthManager.KDF_HASH);
		if (kdfAlgorithm != null) {
			kdfCombo.setText(kdfAlgorithm);
		} else {
			kdfCombo.setText("GOST3411");
		}
	}

	public Map<String, String> save() {
		Map<String, String> result = new HashMap<>();
		result.put(UnixLikeAuthManager.ENCRYPTION_ALG, encryptionCombo.getText());
		result.put(UnixLikeAuthManager.KDF_HASH, kdfCombo.getText());
		return result;
	}

	public void defaultAll() {
		encryptionCombo.setText("DESede");
		kdfCombo.setText("GOST3411");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}

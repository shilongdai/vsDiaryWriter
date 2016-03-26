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
import net.viperfish.journal.secureAlgs.Digesters;

final class HashAuthConfigComposite extends Composite {

	private Combo hashSelector;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public HashAuthConfigComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));

		Label hashAlgLabel = new Label(this, SWT.NONE);
		hashAlgLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		hashAlgLabel.setText("Hash Algorithm");

		hashSelector = new Combo(this, SWT.READ_ONLY);
		hashSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		for (String i : Digesters.getSupportedDigest()) {
			hashSelector.add(i);
		}

		String hashAlg = Configuration.getString(HashAuthManager.HASH_ALG);
		if (hashAlg != null) {
			hashSelector.setText(hashAlg);
		} else {
			defaultAll();
		}
	}

	public Map<String, String> save() {
		Map<String, String> result = new HashMap<>();
		result.put(HashAuthManager.HASH_ALG, hashSelector.getText());
		return result;
	}

	public void defaultAll() {
		hashSelector.setText("SHA256");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}

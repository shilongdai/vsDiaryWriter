package net.viperfish.journal.swtGui.conf;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.secureProvider.AlgorithmSpec;
import net.viperfish.journal.secureProvider.BlockCipherMacTransformer;

public class SecurityConfigComposite extends Composite {

	private Combo encAlgSelector;
	private Combo encModeSelector;
	private Combo encPadSelector;
	private Combo macTypeSelector;
	private Combo macAlgSelector;

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

		Label encModeLabel = new Label(this, SWT.NONE);
		encModeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		encModeLabel.setText("Encryption Mode");

		encModeSelector = new Combo(this, SWT.READ_ONLY);
		encModeSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label encPadLabel = new Label(this, SWT.NONE);
		encPadLabel.setText("Encryption Padding");

		encPadSelector = new Combo(this, SWT.READ_ONLY);
		encPadSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label macTypeLabel = new Label(this, SWT.NONE);
		macTypeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		macTypeLabel.setText("Mac Type");

		macTypeSelector = new Combo(this, SWT.READ_ONLY);
		macTypeSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		macTypeSelector.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				fillInMacAlg();
			}

		});

		Label macAlgLabel = new Label(this, SWT.NONE);
		macAlgLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		macAlgLabel.setText("Mac Algorithm");

		macAlgSelector = new Combo(this, SWT.READ_ONLY);
		macAlgSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		fillIn();

	}

	public void save() {
		JournalApplication.getConfiguration().setProperty(BlockCipherMacTransformer.ENCRYPTION_ALG_NAME,
				encAlgSelector.getText());
		JournalApplication.getConfiguration().setProperty(BlockCipherMacTransformer.ENCRYPTION_MODE,
				encModeSelector.getText());
		JournalApplication.getConfiguration().setProperty(BlockCipherMacTransformer.ENCRYPTION_PADDING,
				encPadSelector.getText());
		JournalApplication.getConfiguration().setProperty(BlockCipherMacTransformer.MAC_TYPE,
				macTypeSelector.getText());
		JournalApplication.getConfiguration().setProperty(BlockCipherMacTransformer.MAC_ALGORITHM,
				macAlgSelector.getText());

	}

	private void fillIn() {
		for (String i : AlgorithmSpec.getSupportedBlockCipher()) {
			encAlgSelector.add(i);
		}
		for (String i : AlgorithmSpec.getSupportedBlockCipherMode()) {
			encModeSelector.add(i);
		}
		for (String i : AlgorithmSpec.getSupportedBlockCipherPadding()) {
			encPadSelector.add(i);
		}
		macTypeSelector.add("CMAC");
		macTypeSelector.add("GMAC");
		macTypeSelector.add("CBCMAC");
		macTypeSelector.add("CFBMAC");
		macTypeSelector.add("HMAC");
		macTypeSelector.select(0);
		encPadSelector.select(0);
		encModeSelector.select(0);
		encAlgSelector.select(0);
		fillInMacAlg();
	}

	private void fillInMacAlg() {
		macAlgSelector.setItems(new String[0]);
		if (macTypeSelector.getText().equals("CMAC") || macTypeSelector.getText().equals("CBCMAC")
				|| macTypeSelector.getText().equals("CFBMAC")) {
			for (String i : AlgorithmSpec.getSupportedBlockCipher()) {
				macAlgSelector.add(i);
			}
		} else if (macTypeSelector.getText().equals("GMAC")) {
			for (String i : AlgorithmSpec.getGmacAlgorithms()) {
				macAlgSelector.add(i);
			}
		} else {
			for (String i : AlgorithmSpec.getSupportedDigest()) {
				macAlgSelector.add(i);
			}
		}
		macAlgSelector.select(0);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}

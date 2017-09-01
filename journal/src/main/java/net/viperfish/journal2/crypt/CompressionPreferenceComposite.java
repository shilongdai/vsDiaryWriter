package net.viperfish.journal2.crypt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.viperfish.framework.compression.Compressors;
import net.viperfish.journal2.core.JournalConfiguration;
import net.viperfish.journal2.core.JournalI18NBundle;

public class CompressionPreferenceComposite extends Composite {

	private Combo compressionSelector;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CompressionPreferenceComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		Label compressionLabel = new Label(this, SWT.NONE);
		compressionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		compressionLabel.setText(JournalI18NBundle.getString("config.compression.alg"));

		compressionSelector = new Combo(this, SWT.READ_ONLY);
		compressionSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for (String i : Compressors.getCompressors()) {
			compressionSelector.add(i);
		}
		compressionSelector.setText(JournalConfiguration.containsKey(CompressionProccessor.CONFIG_COMPRESSION)
				? JournalConfiguration.getString(CompressionProccessor.CONFIG_COMPRESSION) : "XZ");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setDefault() {
		compressionSelector.setText("XZ");
	}

	public void save() {
		JournalConfiguration.setProperty(CompressionProccessor.CONFIG_COMPRESSION, compressionSelector.getText());
	}
}

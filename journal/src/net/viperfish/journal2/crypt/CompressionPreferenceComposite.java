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

import net.viperfish.framework.compression.Compressors;

public class CompressionPreferenceComposite extends Composite {

	private Combo compressionSelector;
	private Configuration config;
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CompressionPreferenceComposite(Composite parent, int style, Configuration config, MessageSource i18n) {
		super(parent, style);
		this.config = config;
		setLayout(new GridLayout(3, false));
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		Label compressionLabel = new Label(this, SWT.NONE);
		compressionLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		compressionLabel.setText(i18n.getMessage("config.compression.alg", null, Locale.getDefault()));

		compressionSelector = new Combo(this, SWT.READ_ONLY);
		compressionSelector.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		for (String i : Compressors.getCompressors()) {
			compressionSelector.add(i);
		}
		compressionSelector.setText(config.containsKey(CompressionProccessor.CONFIG_COMPRESSION) ? config.getString(CompressionProccessor.CONFIG_COMPRESSION) : "XZ");
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void setDefault() {
		compressionSelector.setText("XZ");
	}

	public void save() {
		config.setProperty(CompressionProccessor.CONFIG_COMPRESSION, compressionSelector.getText());
	}
}

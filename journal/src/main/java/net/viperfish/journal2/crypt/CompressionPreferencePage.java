package net.viperfish.journal2.crypt;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import net.viperfish.journal2.core.CopiablePreferencePage;
import net.viperfish.journal2.core.JournalI18NBundle;

class CompressionPreferencePage extends CopiablePreferencePage {

	private CompressionPreferenceComposite com;

	public CompressionPreferencePage() {
		super();
		this.setTitle(JournalI18NBundle.getString("config.compression"));
		this.setDescription(JournalI18NBundle.getString("config.compression.desc"));
	}

	public CompressionPreferencePage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public CompressionPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	@Override
	public PreferencePage copy() {
		CompressionPreferencePage page = new CompressionPreferencePage();
		return page;
	}

	@Override
	protected Control createContents(Composite arg0) {
		com = new CompressionPreferenceComposite(arg0, NONE);
		return com;
	}

	@Override
	protected void performDefaults() {
		if (com != null)
			com.setDefault();
	}

	@Override
	public boolean performOk() {
		if (com != null)
			com.save();
		return true;
	}

}

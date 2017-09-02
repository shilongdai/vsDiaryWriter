package net.viperfish.journal2.crypt;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import net.viperfish.journal2.core.CopiablePreferencePage;
import net.viperfish.journal2.core.JournalI18NBundle;

public class StreamCipherPreferencePage extends CopiablePreferencePage {

	private StreamCipherConfigComposite com;

	public StreamCipherPreferencePage() {
		this.setTitle(JournalI18NBundle.getString("config.streamCipher"));
		this.setDescription(JournalI18NBundle.getString("config.streamCipher.desc"));
	}

	@Override
	public PreferencePage copy() {
		StreamCipherPreferencePage page = new StreamCipherPreferencePage();
		return page;
	}

	@Override
	protected Control createContents(Composite arg0) {
		com = new StreamCipherConfigComposite(arg0, NONE);
		return com;
	}

	@Override
	protected void performDefaults() {
		if (com != null)
			com.setDefault();
	}

	@Override
	public boolean performOk() {
		if (com != null) {
			com.save();
		}
		return true;
	}

}

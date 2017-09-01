package net.viperfish.journal2.crypt;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import net.viperfish.journal2.core.CopiablePreferencePage;
import net.viperfish.journal2.core.JournalI18NBundle;

class AEADPreferencePage extends CopiablePreferencePage {

	private AEADEncryptionPreferenceComposite com;

	public AEADPreferencePage() {
		super();
		this.setTitle(JournalI18NBundle.getString("config.encryption"));
		this.setDescription(JournalI18NBundle.getString("config.encryption.desc"));

	}

	@Override
	protected Control createContents(Composite arg0) {
		com = new AEADEncryptionPreferenceComposite(arg0, NONE);
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
			com.done();
		return true;
	}

	@Override
	public PreferencePage copy() {
		AEADPreferencePage copy = new AEADPreferencePage();
		return copy;
	}

}

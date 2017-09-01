package net.viperfish.journal2.crypt;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import net.viperfish.journal2.core.CopiablePreferencePage;
import net.viperfish.journal2.core.JournalI18NBundle;

class HMACPreferencePage extends CopiablePreferencePage {

	private HMACPreferenceComposite com;

	public HMACPreferencePage() {
		this.setTitle(JournalI18NBundle.getString("config.hmac"));
		this.setDescription("config.hmac.desc");
	}

	@Override
	public PreferencePage copy() {
		HMACPreferencePage page = new HMACPreferencePage();
		return page;
	}

	@Override
	protected Control createContents(Composite arg0) {
		com = new HMACPreferenceComposite(arg0, NONE);
		return com;
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		this.com.setDefault();
	}

	@Override
	public boolean performOk() {
		if (com != null)
			com.save();
		return true;
	}

}

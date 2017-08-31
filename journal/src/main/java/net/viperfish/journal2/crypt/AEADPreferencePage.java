package net.viperfish.journal2.crypt;

import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import net.viperfish.journal2.core.CopiablePreferencePage;

class AEADPreferencePage extends CopiablePreferencePage {

	private AEADEncryptionPreferenceComposite com;
	private Configuration config;
	private MessageSource i18n;

	private Locale loc = Locale.getDefault();

	public Configuration getConfig() {
		return config;
	}

	@Autowired
	public void setConfig(Configuration config) {
		this.config = config;
	}

	public MessageSource getI18n() {
		return i18n;
	}

	@Autowired
	public void setI18n(MessageSource i18n) {
		this.i18n = i18n;
		this.setTitle(i18n.getMessage("config.encryption", null, loc));
		this.setDescription(i18n.getMessage("config.encryption.desc", null, loc));
	}

	public AEADPreferencePage() {
		super();

	}

	@Override
	protected Control createContents(Composite arg0) {
		com = new AEADEncryptionPreferenceComposite(arg0, NONE, config, i18n);
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
		copy.setConfig(config);
		copy.setI18n(i18n);
		copy.loc = this.loc;
		return copy;
	}

}

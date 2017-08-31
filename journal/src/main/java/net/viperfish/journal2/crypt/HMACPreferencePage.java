package net.viperfish.journal2.crypt;

import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import net.viperfish.journal2.core.CopiablePreferencePage;

class HMACPreferencePage extends CopiablePreferencePage {

	private MessageSource i18n;

	private Configuration config;

	private HMACPreferenceComposite com;

	public HMACPreferencePage() {
	}

	@Autowired
	public void setI18n(MessageSource i18n) {
		this.i18n = i18n;
		this.setTitle(i18n.getMessage("config.hmac", null, Locale.getDefault()));
		this.setDescription(i18n.getMessage("config.hmac.desc", null, Locale.getDefault()));
	}

	@Autowired
	public void setConfig(Configuration config) {
		this.config = config;
	}

	@Override
	public PreferencePage copy() {
		HMACPreferencePage page = new HMACPreferencePage();
		page.setI18n(this.i18n);
		page.setConfig(this.config);
		return page;
	}

	@Override
	protected Control createContents(Composite arg0) {
		com = new HMACPreferenceComposite(arg0, NONE, config, i18n);
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

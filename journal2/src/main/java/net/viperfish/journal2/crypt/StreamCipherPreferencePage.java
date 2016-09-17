package net.viperfish.journal2.crypt;

import java.util.Locale;

import org.apache.commons.configuration.Configuration;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import net.viperfish.journal2.core.CopiablePreferencePage;

@Component
class StreamCipherPreferencePage extends CopiablePreferencePage {

	private MessageSource i18n;
	private Configuration config;
	private StreamCipherConfigComposite com;

	@Autowired
	public void setI18n(MessageSource i18n) {
		this.i18n = i18n;
		this.setTitle(i18n.getMessage("config.streamCipher", null, Locale.getDefault()));
		this.setDescription(i18n.getMessage("config.streamCipher.desc", null, Locale.getDefault()));
	}

	@Autowired
	public void setConfig(Configuration config) {
		this.config = config;
	}

	public StreamCipherPreferencePage() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public PreferencePage copy() {
		StreamCipherPreferencePage page = new StreamCipherPreferencePage();
		page.setI18n(i18n);
		page.setConfig(config);
		return page;
	}

	@Override
	protected Control createContents(Composite arg0) {
		com = new StreamCipherConfigComposite(arg0, NONE, config, i18n);
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

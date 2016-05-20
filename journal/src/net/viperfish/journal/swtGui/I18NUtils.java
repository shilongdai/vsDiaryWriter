package net.viperfish.journal.swtGui;

import java.util.Locale;
import java.util.ResourceBundle;

import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;

enum I18NUtils {
	INSTANCE;
	public ResourceBundle getBundle() {
		String lang = Configuration.getString(ConfigMapping.LANG);
		String region = Configuration.getString(ConfigMapping.REGION);
		if (lang == null) {
			lang = "en";
		}
		if (region == null) {
			region = "US";
		}
		Locale currentLoc = new Locale(lang, region);
		return ResourceBundle.getBundle("LabelBundle", currentLoc);
	}
}

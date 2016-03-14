package net.viperfish.journal.swtGui.conf;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.ConfigPage;
import net.viperfish.journal.framework.Configuration;

public class SystemConfigPage implements ConfigPage {

	private SystemSetupComposite com;
	private Composite p;

	public SystemConfigPage() {
	}

	@Override
	public String getName() {
		return "System";
	}

	@Override
	public Composite getDisplay() {
		if (com == null) {
			com = new SystemSetupComposite(p, SWT.None);
		}
		return com;
	}

	@Override
	public Map<String, String> done() {
		return com.save();
	}

	@Override
	public void setParent(Composite p) {
		this.p = p;

	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public void saveDefault() {
		Configuration.setProperty(ConfigMapping.AUTH_COMPONENT, "Hash");
		Configuration.setProperty(ConfigMapping.DB_COMPONENT, "H2Database");
		Configuration.setProperty(ConfigMapping.INDEXER_COMPONENT, "LuceneIndexer");
		Configuration.setProperty(ConfigMapping.TRANSFORMER_COMPONENT, "BlockCipherMAC");

	}

}

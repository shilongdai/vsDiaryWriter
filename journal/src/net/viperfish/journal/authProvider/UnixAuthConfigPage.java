package net.viperfish.journal.authProvider;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import net.viperfish.journal.framework.ConfigPage;
import net.viperfish.journal.framework.Configuration;

public class UnixAuthConfigPage implements ConfigPage {

	private UnixAuthComposite com;
	private Composite parent;

	@Override
	public String getName() {
		return "UnixStyle Authentication";
	}

	@Override
	public Composite getDisplay() {
		if (com == null) {
			com = new UnixAuthComposite(parent, SWT.NONE);
		}
		return com;
	}

	@Override
	public Map<String, String> done() {
		return com.save();
	}

	@Override
	public void setParent(Composite p) {
		this.parent = p;
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public void saveDefault() {
		Configuration.setProperty(UnixLikeAuthManager.ENCRYPTION_ALG, "DESede");
		Configuration.setProperty(UnixLikeAuthManager.KDF_HASH, "SHA256");

	}

}

package net.viperfish.journal.authProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import net.viperfish.journal.swtGui.conf.ConfigPage;

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
	public void done() {
		com.save();
	}

	@Override
	public void setParent(Composite p) {
		this.parent = p;
	}

}

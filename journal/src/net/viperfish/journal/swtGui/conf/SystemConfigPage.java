package net.viperfish.journal.swtGui.conf;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

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
	public void done() {
		com.save();
	}

	@Override
	public void setParent(Composite p) {
		this.p = p;

	}

}

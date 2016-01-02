package net.viperfish.journal.swtGui.conf;

import org.eclipse.swt.widgets.Composite;

public interface ConfigPage {
	public String getName();

	public Composite getDisplay();

	public void done();

	public void setParent(Composite p);
}

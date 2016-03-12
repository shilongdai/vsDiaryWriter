package net.viperfish.journal.swtGui.conf;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;

public interface ConfigPage {
	public String getName();

	public Composite getDisplay();

	public Map<String, String> done();

	public boolean validate();

	public void setParent(Composite p);
}

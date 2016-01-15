package net.viperfish.journal.secureProvider;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import net.viperfish.journal.swtGui.conf.ConfigPage;

public class BlockCipherMacConfigPage implements ConfigPage {

	private SecurityConfigComposite com;
	private Composite p;

	public BlockCipherMacConfigPage() {
	}

	@Override
	public String getName() {
		return "Block Cipher MAC";
	}

	@Override
	public Composite getDisplay() {
		if (com == null) {
			com = new SecurityConfigComposite(p, SWT.NONE);
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

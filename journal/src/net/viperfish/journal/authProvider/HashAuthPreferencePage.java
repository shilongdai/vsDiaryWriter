package net.viperfish.journal.authProvider;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import net.viperfish.journal.framework.OperationExecutor;
import net.viperfish.journal.framework.OperationFactory;
import net.viperfish.journal.operation.OperationExecutors;
import net.viperfish.journal.operation.OperationFactories;

public final class HashAuthPreferencePage extends PreferencePage {

	private HashAuthConfigComposite com;

	public HashAuthPreferencePage() {
		super("Hash Authentication");
		this.setDescription("Configuring for authentication using hashing algorithms");
	}

	@Override
	protected Control createContents(Composite arg0) {
		com = new HashAuthConfigComposite(arg0, SWT.NONE);

		return com;
	}

	@Override
	protected void performDefaults() {
		com.defaultAll();
	}

	@Override
	public boolean performOk() {
		OperationFactory ops = OperationFactories.getOperationFactory();
		OperationExecutor exe = OperationExecutors.getExecutor();
		exe.submit(ops.getChangeConfigOperaion(com.save()));
		return this.isValid();
	}

}

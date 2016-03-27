package net.viperfish.journal.authProvider;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import net.viperfish.journal.framework.OperationExecutors;
import net.viperfish.journal.operation.OperationFactories;
import net.viperfish.journal.operation.StoreConfigurationBufferOperation;

public final class UnixLikeAuthPreferencePage extends PreferencePage {

	private UnixAuthComposite com;

	public UnixLikeAuthPreferencePage() {
		super("Unix Style Authentication");
		this.setDescription(
				"Configures the setting for Unix Like Authentication: Using an encryption algorithm to generate a hash");
	}

	@Override
	protected Control createContents(Composite arg0) {
		com = new UnixAuthComposite(arg0, SWT.NONE);
		return com;
	}

	@Override
	public boolean performOk() {
		OperationExecutors.getExecutor().submit(new StoreConfigurationBufferOperation(com.save()));
		return true;
	}

	@Override
	protected void performDefaults() {
		com.defaultAll();
	}

	@Override
	protected void performApply() {
		OperationExecutors.getExecutor()
				.submit(OperationFactories.getOperationFactory().getChangeConfigOperaion(com.save()));
	}

}

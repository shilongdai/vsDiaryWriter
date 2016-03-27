package net.viperfish.journal;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import net.viperfish.journal.framework.OperationExecutors;
import net.viperfish.journal.operation.OperationFactories;
import net.viperfish.journal.operation.StoreConfigurationBufferOperation;

public final class SystemPreferencePage extends PreferencePage {

	private SystemSetupComposite com;

	public SystemPreferencePage() {
		super("System");
		this.setDescription("Configures the components that the application use to proccess entries");
	}

	@Override
	protected Control createContents(Composite arg0) {
		com = new SystemSetupComposite(arg0, SWT.NONE);
		return com;
	}

	@Override
	protected void performDefaults() {
		com.defaultAll();
	}

	@Override
	public boolean performOk() {
		OperationExecutors.getExecutor().submit(new StoreConfigurationBufferOperation(com.save()));
		return this.isValid();
	}

	@Override
	protected void performApply() {
		OperationExecutors.getExecutor()
				.submit(OperationFactories.getOperationFactory().getChangeConfigOperaion(com.save()));
		;
	}

}

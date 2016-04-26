package net.viperfish.journal.secureProvider;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import net.viperfish.journal.framework.OperationExecutors;
import net.viperfish.journal.framework.StoreConfigurationBufferOperation;
import net.viperfish.journal.operation.OperationFactories;

public final class BlockCipherPreferencePage extends PreferencePage {

	private BlockCipherConfigComposite com;

	public BlockCipherPreferencePage() {
		super("Block Cipher");
		this.setDescription("Configures the settings for encryption with blockciphers");
	}

	@Override
	protected Control createContents(Composite arg0) {
		com = new BlockCipherConfigComposite(arg0, SWT.NONE);
		return com;
	}

	@Override
	protected void performDefaults() {
		com.defaultAll();
	}

	@Override
	public boolean performOk() {
		boolean isValid = com.validate();
		if (isValid) {
			OperationExecutors.getExecutor().submit(new StoreConfigurationBufferOperation(com.save()));
		}
		return isValid;
	}

	@Override
	protected void performApply() {
		OperationExecutors.getExecutor()
				.submit(OperationFactories.getOperationFactory().getChangeConfigOperaion(com.save()));
	}

}

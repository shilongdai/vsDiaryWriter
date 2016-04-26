package net.viperfish.journal.secureProvider;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import net.viperfish.journal.framework.OperationExecutors;
import net.viperfish.journal.framework.StoreConfigurationBufferOperation;
import net.viperfish.journal.operation.OperationFactories;

public class StreamCipherPreferencePage extends PreferencePage {

	private StreamCipherConfigComposite com;

	public StreamCipherPreferencePage() {
		super("Stream Cipher");
		this.setDescription("Configures the settings for encryption with streamciphers");
	}

	@Override
	protected Control createContents(Composite arg0) {
		com = new StreamCipherConfigComposite(arg0, SWT.NONE);
		return com;
	}

	@Override
	protected void performDefaults() {
		com.defaultAll();
	}

	@Override
	public boolean performOk() {
		OperationExecutors.getExecutor().submit(new StoreConfigurationBufferOperation(com.save()));
		return true;
	}

	@Override
	protected void performApply() {
		OperationExecutors.getExecutor()
				.submit(OperationFactories.getOperationFactory().getChangeConfigOperaion(com.save()));
	}

}

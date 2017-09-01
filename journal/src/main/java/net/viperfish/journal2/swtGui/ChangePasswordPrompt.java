package net.viperfish.journal2.swtGui;

import net.viperfish.journal2.core.JournalService;
import net.viperfish.journal2.error.FailToStoreCredentialException;

public class ChangePasswordPrompt extends UpdatePasswordPrompt {

	private JournalService service;

	public ChangePasswordPrompt(JournalService service) {
		super();
		this.service = service;
	}

	@Override
	protected void doUpdate(String password) throws FailToStoreCredentialException {
		this.service.reCrypt(password);

	}

}

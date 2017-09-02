package net.viperfish.journal2.swtGui;

import java.io.IOException;

import net.viperfish.journal2.core.JournalService;

public class ChangePasswordPrompt extends UpdatePasswordPrompt {

	private JournalService service;

	public ChangePasswordPrompt(JournalService service) {
		super();
		this.service = service;
	}

	@Override
	protected void doUpdate(String password) throws IOException {
		this.service.reCrypt(password);

	}

}

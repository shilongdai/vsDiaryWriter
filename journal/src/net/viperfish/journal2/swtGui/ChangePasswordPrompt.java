package net.viperfish.journal2.swtGui;

import java.util.Locale;

import org.springframework.context.MessageSource;

import net.viperfish.journal2.core.JournalService;
import net.viperfish.journal2.error.FailToStoreCredentialException;

public class ChangePasswordPrompt extends UpdatePasswordPrompt {

	private JournalService service;

	public ChangePasswordPrompt(MessageSource messageSource, Locale loc, JournalService service) {
		super(messageSource, loc);
		this.service = service;
	}

	@Override
	protected void doUpdate(String password) throws FailToStoreCredentialException {
		this.service.reCrypt(password);

	}

}

package net.viperfish.journal2.swtGui;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import net.viperfish.journal2.core.AuthenticationManager;
import net.viperfish.journal2.error.FailToStoreCredentialException;

@Component
public class SetPasswordPrompt extends UpdatePasswordPrompt {

	@Autowired
	public SetPasswordPrompt(MessageSource messageSource) {
		super(messageSource, Locale.getDefault());
		// TODO Auto-generated constructor stub
	}

	@Autowired
	private AuthenticationManager manager;

	@Override
	protected void doUpdate(String password) throws FailToStoreCredentialException {
		manager.setPassword(password);
	}

}

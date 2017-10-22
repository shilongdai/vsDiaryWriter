package net.viperfish.journal2.swtGui;

import java.io.IOException;
import net.viperfish.journal2.core.AuthenticationManager;
import net.viperfish.journal2.error.FailToStoreCredentialException;

public class SetPasswordPrompt extends UpdatePasswordPrompt {

    public SetPasswordPrompt(AuthenticationManager manager) {
        this.manager = manager;
    }

    private AuthenticationManager manager;

    @Override
    protected void doUpdate(String password) throws FailToStoreCredentialException, IOException {
        manager.setPassword(password);
    }

}

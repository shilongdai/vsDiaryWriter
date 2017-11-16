package net.viperfish.journal2.transaction;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.viperfish.journal2.core.AuthenticationManager;

final class LoginService extends Service<Boolean> {

	private AuthenticationManager auth;
	private String password;

	public LoginService(AuthenticationManager auth, String password) {
		this.auth = auth;
		this.password = password;
	}

	@Override
	protected Task<Boolean> createTask() {
		return new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				synchronized (Lock.class) {
					return auth.verify(password);
				}
			}

		};
	}

}

package test.java;

import java.io.File;

import net.viperfish.journal.authProvider.HashAuthManager;
import net.viperfish.journal.framework.AuthenticationManager;

public class HashAuthTest extends AuthTest {

	@Override
	protected AuthenticationManager getAuth(File dataDir) {
		return new HashAuthManager(dataDir);
	}

}

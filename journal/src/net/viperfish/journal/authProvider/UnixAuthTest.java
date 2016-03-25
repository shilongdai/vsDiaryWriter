package net.viperfish.journal.authProvider;

import java.io.File;

import net.viperfish.journal.framework.AuthTest;
import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.Configuration;

public class UnixAuthTest extends AuthTest {

	static {
		Configuration.setProperty(UnixLikeAuthManager.ENCRYPTION_ALG, "DES");
		Configuration.setProperty(UnixLikeAuthManager.KDF_HASH, "MD5");
	}

	@Override
	protected AuthenticationManager getAuth(File dataDir) {
		return new UnixLikeAuthManager(dataDir);
	}

}

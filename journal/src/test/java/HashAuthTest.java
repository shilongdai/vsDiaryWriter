package test.java;

import java.io.File;

import net.viperfish.journal.authProvider.HashAuthManager;
import net.viperfish.journal.framework.AuthenticationManager;

import org.junit.Assert;
import org.junit.Test;

public class HashAuthTest {

	private AuthenticationManager auth;
	private File dataDir;

	public HashAuthTest() {
		dataDir = new File("data");
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}
		auth = new HashAuthManager(dataDir);
	}

	@Test
	public void testVerify() {
		auth.clear();
		auth.setPassword("password");
		auth.reload();
		Assert.assertEquals(true, auth.verify("password"));
		auth.clear();
	}
}

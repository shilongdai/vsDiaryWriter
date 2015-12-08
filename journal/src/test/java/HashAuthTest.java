package test.java;

import java.io.File;

import net.viperfish.journal.auth.AuthenticationManager;
import net.viperfish.journal.authentications.HashAuthManager;
import net.viperfish.journal.secureAlgs.Digester;
import net.viperfish.journal.secureAlgs.JCEDigester;

import org.junit.Assert;
import org.junit.Test;

public class HashAuthTest {

	private AuthenticationManager auth;
	private File dataDir;
	private Digester dig;

	public HashAuthTest() {
		dataDir = new File("data");
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}
		auth = new HashAuthManager(dataDir);
		dig = new JCEDigester();
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

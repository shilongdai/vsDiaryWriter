package test.java;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import net.viperfish.journal.authProvider.HashAuthManager;
import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.utils.file.CommonFunctions;

public class HashAuthTest {

	private AuthenticationManager auth;
	private static File dataDir;

	static {
		dataDir = new File("data");
		CommonFunctions.initDir(dataDir);
	}

	public HashAuthTest() {
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

	@AfterClass
	public static void cleanUpFiles() {
		CommonFunctions.delete(dataDir);
	}
}

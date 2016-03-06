package test.java;

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.utils.file.CommonFunctions;

public abstract class AuthTest {

	private AuthenticationManager auth;
	private static File dataDir;

	@BeforeClass
	public static void init() {
		dataDir = new File("data");
		CommonFunctions.initDir(dataDir);
	}

	public AuthTest() {
		auth = getAuth(dataDir);
	}

	protected abstract AuthenticationManager getAuth(File dataDir);

	@Test
	public void testVerify() {
		auth.clear();
		auth.setPassword("password");
		auth = getAuth(dataDir);
		auth.reload();
		Assert.assertEquals(true, auth.verify("password"));
		auth.clear();
	}

	@AfterClass
	public static void cleanUpFiles() {
		CommonFunctions.delete(dataDir);
	}
}

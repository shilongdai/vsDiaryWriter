package test.java;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.secure.SecureEntryDatabaseWrapper;

public class SecurityWrapperTest {
	private SecureEntryDatabaseWrapper wrapper;

	public SecurityWrapperTest() {
		File testDir = new File("test");
		if (!testDir.exists()) {
			testDir.mkdir();
		}
		SecureEntryDatabaseWrapper.config().fillInDefault();
		try {
			wrapper = new SecureEntryDatabaseWrapper(new File(testDir.getCanonicalPath() + "/salt"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		wrapper.setPassword("password");

	}

	@Test
	public void testEncrypt() {
		Journal j = new Journal();
		j.setSubject("test get");
		j.setContent("test get content");
		Journal result = wrapper.encryptJournal(j);
		result = wrapper.decryptJournal(result);
		String plainContent = result.getContent();
		Assert.assertEquals("test get content", plainContent);
		Assert.assertEquals("test get", result.getSubject());
	}

}

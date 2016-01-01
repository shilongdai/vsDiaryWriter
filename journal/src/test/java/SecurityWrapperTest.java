package test.java;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.secure.BlockCipherMacTransformer;

public class SecurityWrapperTest {
	private BlockCipherMacTransformer wrapper;

	private void setupConfig() {
		JournalApplication.getConfiguration().setProperty(BlockCipherMacTransformer.ENCRYPTION_ALG_NAME, "AES");
		JournalApplication.getConfiguration().setProperty(BlockCipherMacTransformer.ENCRYPTION_MODE, "CFB");
		JournalApplication.getConfiguration().setProperty(BlockCipherMacTransformer.ENCRYPTION_PADDING, "PKCS7PADDING");
		JournalApplication.getConfiguration().setProperty(BlockCipherMacTransformer.MAC_ALGORITHM, "MD5");
		JournalApplication.getConfiguration().setProperty(BlockCipherMacTransformer.MAC_TYPE, "HMAC");
	}

	public SecurityWrapperTest() {
		setupConfig();
		File testDir = new File("test");
		if (!testDir.exists()) {
			testDir.mkdir();
		}
		try {
			wrapper = new BlockCipherMacTransformer(new File(testDir.getCanonicalPath() + "/salt"));
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

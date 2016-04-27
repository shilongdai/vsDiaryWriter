package net.viperfish.journal.secureProvider;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import net.viperfish.framework.file.CommonFunctions;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.errors.CipherException;
import net.viperfish.journal.framework.errors.CompromisedDataException;
import net.viperfish.journal.framework.errors.FailToSyncCipherDataException;
import net.viperfish.journal.secureAlgs.BlockCiphers;
import net.viperfish.journal.secureAlgs.Digesters;

public abstract class CompressMacTest {
	private static File testDir;

	static {
		testDir = new File("test");
	}

	public CompressMacTest() {

	}

	protected abstract void cascadeTest();

	protected abstract JournalTransformer createTransformer(File salt);

	protected abstract void initConfig();

	protected void testWrapper() {
		JournalTransformer wrapper;
		try {
			CommonFunctions.initDir(testDir);
			wrapper = createTransformer(new File(testDir.getCanonicalPath() + "/salt"));
		} catch (IOException e) {
			System.err.println("cannot load salt");
			throw new RuntimeException(e);
		}
		try {
			wrapper.setPassword("password");
		} catch (FailToSyncCipherDataException e1) {
			throw new RuntimeException(e1);
		}
		Journal j = new Journal();
		j.setSubject("test get");
		j.setContent("test get content");

		try {
			Journal result = wrapper.encryptJournal(j);
			result = wrapper.decryptJournal(result);
			String plainContent = result.getContent();
			Assert.assertEquals("test get content", plainContent);
			Assert.assertEquals("test get", result.getSubject());
		} catch (CipherException | CompromisedDataException e) {
			throw new RuntimeException(e);
		}

	}

	@Test
	public void testCipher() {
		Configuration.setProperty(CompressMacTransformer.COMPRESSION, "None");
		Configuration.setProperty(BlockCipherMacTransformer.KDF_HASH, "MD5");
		initConfig();

		Configuration.setProperty(CompressMacTransformer.MAC_TYPE, "CBCMAC");
		for (String cbcmac : BlockCiphers.getSupportedBlockCipher()) {
			Configuration.setProperty(CompressMacTransformer.MAC_ALGORITHM, cbcmac);
			testWrapper();
		}
		Configuration.setProperty(CompressMacTransformer.MAC_TYPE, "CMAC");
		for (String cmac : BlockCiphers.getSupportedBlockCipher()) {
			Configuration.setProperty(CompressMacTransformer.MAC_ALGORITHM, cmac);
			testWrapper();
		}
		Configuration.setProperty(CompressMacTransformer.MAC_TYPE, "CFBMAC");
		for (String cfbmac : BlockCiphers.getSupportedBlockCipher()) {
			Configuration.setProperty(CompressMacTransformer.MAC_ALGORITHM, cfbmac);
			testWrapper();
		}
		Configuration.setProperty(CompressMacTransformer.MAC_TYPE, "GMAC");
		for (String gmac : BlockCiphers.getGmacAlgorithms()) {
			Configuration.setProperty(CompressMacTransformer.MAC_ALGORITHM, gmac);
			testWrapper();
		}
		Configuration.setProperty(CompressMacTransformer.MAC_TYPE, "HMAC");
		for (String hmac : Digesters.getSupportedDigest()) {
			Configuration.setProperty(CompressMacTransformer.MAC_ALGORITHM, hmac);
			testWrapper();
		}
		cascadeTest();

	}

	@AfterClass
	public static void cleanUpDirs() {
		CommonFunctions.delete(testDir);
	}
}

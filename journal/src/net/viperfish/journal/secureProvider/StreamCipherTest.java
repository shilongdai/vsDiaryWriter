package net.viperfish.journal.secureProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.streamCipher.StreamCipherEncryptors;

public class StreamCipherTest extends CompressMacTest {

	@Override
	protected void cascadeTest() {
		for (String streamCipher : StreamCipherEncryptors.INSTANCE.getSupported()) {
			Configuration.setProperty(StreamCipherTransformer.ALG_NAME, streamCipher);
			testWrapper();
		}
	}

	@Override
	protected JournalTransformer createTransformer(File salt) {
		try {
			if (!salt.exists()) {
				Files.createFile(salt.toPath());
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new StreamCipherTransformer(salt);
	}

	@Override
	protected void initConfig() {
		Configuration.setProperty(StreamCipherTransformer.ALG_NAME, "HC128");
	}

}

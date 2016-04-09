package net.viperfish.journal.secureProvider;

import java.io.File;
import java.io.IOException;

import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.secureAlgs.BlockCiphers;
import net.viperfish.journal.secureAlgs.Digesters;
import net.viperfish.utils.file.CommonFunctions;

public final class BlockCipherTransformerTest extends CompressMacTest {
	private static File testDir;

	static {
		testDir = new File("test");
		CommonFunctions.initDir(testDir);
	}

	public BlockCipherTransformerTest() {

	}

	@Override
	protected void cascadeTest() {
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_MODE, "CBC");
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_PADDING, "PKCS7PADDING");
		for (String kdf : Digesters.getSupportedDigest()) {
			Configuration.setProperty(CompressMacTransformer.KDF_HASH, kdf);
			for (String encAlg : BlockCiphers.getSupportedBlockCipher()) {
				try {
					Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_ALG_NAME, encAlg);
					StringBuilder sb = new StringBuilder();
					sb.append("Testing:").append(" KDF:").append(kdf).append(" Encryption:").append(encAlg);
					System.out.println(sb);
					testWrapper();
				} catch (RuntimeException e) {
					StringBuilder sb = new StringBuilder();
					sb.append("Exception:").append(e.getMessage()).append(" KDF:").append(kdf).append(" Encryption:")
							.append(encAlg);
					System.err.println(sb.toString());
				}
			}
		}
	}

	@Override
	protected JournalTransformer createTransformer(File salt) {
		try {
			BlockCipherMacTransformer wrapper = new BlockCipherMacTransformer(
					new File(testDir.getCanonicalPath() + "/salt"));
			return wrapper;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void initConfig() {
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_ALG_NAME, "AES");
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_MODE, "CFB");
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_PADDING, "PKCS7PADDING");

	}

}

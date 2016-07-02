package net.viperfish.journal.secureProvider;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;

import net.viperfish.framework.file.CommonFunctions;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.provider.ConfigurationGUISetup;
import net.viperfish.journal.framework.provider.PreferenceGUIManager;
import net.viperfish.journal.framework.provider.TransformerProvider;

public final class ViperfishEncryptionProvider implements TransformerProvider {

	private File secureDir;
	private BlockCipherMacTransformer bm;
	private StreamCipherTransformer sc;

	public ViperfishEncryptionProvider() {
		if (Configuration.containsKey(ConfigMapping.PORTABLE) && Configuration.getBoolean(ConfigMapping.PORTABLE)) {
			secureDir = new File("secure");
		} else {
			File homeDir = new File(System.getProperty("user.home"));
			File vDiaryDir = new File(homeDir, ".vsDiary");
			CommonFunctions.initDir(vDiaryDir);
			secureDir = new File(vDiaryDir, "secure");
		}
		CommonFunctions.initDir(secureDir);
	}

	@Override
	public JournalTransformer newInstance() {
		try {
			return new BlockCipherMacTransformer(new File(secureDir.getCanonicalPath() + "/salt"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public JournalTransformer getInstance() {
		if (bm == null) {
			bm = new BlockCipherMacTransformer(new File(secureDir.getAbsolutePath() + "/salt"));
		}
		return bm;
	}

	@Override
	public JournalTransformer newInstance(String instance) {
		if (instance.equals("BlockCipherMAC")) {
			return newInstance();
		}
		if (instance.equals("StreamCipher")) {
			return new StreamCipherTransformer(new File(secureDir, "salt"));
		}
		return null;
	}

	@Override
	public JournalTransformer getInstance(String instance) {
		if (instance.equals("BlockCipherMAC")) {
			return getInstance();
		}
		if (instance.equals("StreamCipher")) {
			if (sc == null) {
				sc = new StreamCipherTransformer(new File(secureDir, "salt"));
				sc.loadSalt();
			}
			return sc;
		}
		return null;
	}

	@Override
	public String[] getSupported() {
		return new String[] { "BlockCipherMAC", "StreamCipher" };
	}

	@Override
	public String getName() {
		return "viperfish";
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefaultInstance(String instance) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDefaultInstance() {
		return "BlockCipherMAC";
	}

	@Override
	public void delete() {
		CommonFunctions.delete(secureDir);

	}

	@Override
	public void refresh() {
		bm = null;
		sc = null;

	}

	@Override
	public void initDefaults() {
		Configuration.setProperty(BlockCipherMacTransformer.COMPRESSION, "GZ");
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_ALG_NAME, "AES");
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_MODE, "CFB");
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_PADDING, "PKCS7Padding");
		Configuration.setProperty(BlockCipherMacTransformer.KDF_HASH, "SHA256");
		Configuration.setProperty(BlockCipherMacTransformer.MAC_ALGORITHM, "SHA256");
		Configuration.setProperty(BlockCipherMacTransformer.MAC_TYPE, "HMAC");
		Configuration.setProperty(StreamCipherTransformer.ALG_NAME, "ChaCha");

	}

	@Override
	public void registerConfig() {
		ConfigurationGUISetup setup = new ConfigurationGUISetup() {

			@Override
			public void proccess(PreferenceManager mger) {
				PreferenceNode encryption = new PreferenceNode("compressMac", "Encryption", null,
						CompressMacPreference.class.getCanonicalName());
				PreferenceNode blockCipher = new PreferenceNode("blockcipher", "Block Cipher", null,
						BlockCipherPreferencePage.class.getCanonicalName());
				PreferenceNode streamCipher = new PreferenceNode("streamcipher", "Stream Cipher", null,
						StreamCipherPreferencePage.class.getCanonicalName());
				encryption.add(blockCipher);
				encryption.add(streamCipher);
				mger.addToRoot(encryption);
			}
		};

		PreferenceGUIManager.add(setup);
	}

}

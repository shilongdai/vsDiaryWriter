package net.viperfish.journal.secureProvider;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.preference.PreferenceNode;

import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.provider.PreferenceGUIManager;
import net.viperfish.journal.framework.provider.Provider;
import net.viperfish.utils.file.CommonFunctions;

public class ViperfishEncryptionProvider implements Provider<JournalTransformer> {

	private File secureDir;
	private BlockCipherMacTransformer buffer;

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
			buffer = new BlockCipherMacTransformer(new File(secureDir.getCanonicalPath() + "/salt"));
			return buffer;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public JournalTransformer getInstance() {
		if (buffer == null) {
			newInstance();
		}
		return buffer;
	}

	@Override
	public JournalTransformer newInstance(String instance) {
		if (instance.equals("BlockCipherMAC")) {
			return newInstance();
		}
		return null;
	}

	@Override
	public JournalTransformer getInstance(String instance) {
		if (instance.equals("BlockCipherMAC")) {
			return getInstance();
		}
		return null;
	}

	@Override
	public String[] getSupported() {
		return new String[] { "BlockCipherMAC" };
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
		buffer = null;

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

	}

	@Override
	public void registerConfig() {
		PreferenceNode encryption = new PreferenceNode("blockCipherMac", "Encryption", null,
				BlockCipherMacPreference.class.getCanonicalName());
		PreferenceGUIManager.addToRoot(encryption);
	}

}

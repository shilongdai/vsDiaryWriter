package net.viperfish.journal.secure;

import java.io.File;
import java.io.IOException;

import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.Provider;

public class ViperfishEncryptionProvider implements Provider<JournalTransformer> {

	private File secureDir;
	private BlockCipherMacTransformer buffer;

	public ViperfishEncryptionProvider() {
		secureDir = new File("secure");
		if (!secureDir.exists()) {
			secureDir.mkdir();
		}
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

}

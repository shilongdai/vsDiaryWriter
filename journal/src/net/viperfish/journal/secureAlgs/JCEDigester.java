package net.viperfish.journal.secureAlgs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class JCEDigester {

	private MessageDigest hash;
	private String mode;

	private MessageDigest getDigester() throws NoSuchAlgorithmException {
		if (hash == null) {
			hash = MessageDigest.getInstance(mode);
		}
		return hash;
	}

	public JCEDigester() {
		mode = new String("SHA-512");
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public byte[] digest(byte[] text) {
		try {
			return getDigester().digest(text);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}

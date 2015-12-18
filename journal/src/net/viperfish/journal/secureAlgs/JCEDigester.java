package net.viperfish.journal.secureAlgs;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class JCEDigester implements Digester {

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

	/* (non-Javadoc)
	 * @see net.viperfish.journal.secureAlgs.Digester#getMode()
	 */
	@Override
	public String getMode() {
		return mode;
	}

	/* (non-Javadoc)
	 * @see net.viperfish.journal.secureAlgs.Digester#setMode(java.lang.String)
	 */
	@Override
	public void setMode(String mode) {
		this.mode = mode;
	}

	/* (non-Javadoc)
	 * @see net.viperfish.journal.secureAlgs.Digester#digest(byte[])
	 */
	@Override
	public byte[] digest(byte[] text) {
		try {
			return getDigester().digest(text);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}

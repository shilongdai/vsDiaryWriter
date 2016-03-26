package net.viperfish.journal.secureAlgs;

import org.bouncycastle.crypto.Digest;

/**
 * a digester based on the Bouncy Castle's crypto API
 * 
 * @author sdai
 *
 */
public final class BCDigester implements Digester {

	private String mode;

	public BCDigester() {
		mode = new String("SHA512");
	}

	@Override
	public String getMode() {
		return this.mode;
	}

	@Override
	public void setMode(String mode) {
		this.mode = mode;

	}

	@Override
	public byte[] digest(byte[] text) {
		Digest dig = Digesters.getDigester(mode);
		byte[] data = new byte[dig.getDigestSize()];
		for (byte i : text) {
			dig.update(i);
		}
		dig.doFinal(data, 0);
		return data;
	}

}

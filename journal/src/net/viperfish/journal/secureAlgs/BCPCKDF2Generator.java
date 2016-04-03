package net.viperfish.journal.secureAlgs;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * a PCKDF2 KDF function based on Bouncy Castle's crypto api
 * 
 * @author sdai
 *
 */
public final class BCPCKDF2Generator implements PBKDF2KeyGenerator {

	private String digest;
	private byte[] salt;
	private int iteration;
	private Digest digester;

	@Override
	public void setDigest(String digest) {
		this.digest = digest;
		this.digester = Digesters.getDigester(digest);
	}

	@Override
	public String getDigest() {
		return digest;
	}

	@Override
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	@Override
	public int getIteration() {
		return this.iteration;
	}

	@Override
	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	@Override
	public byte[] getSalt() {
		return this.salt;
	}

	@Override
	public byte[] generate(String password, int length) {
		PBEParametersGenerator gen = new PKCS12ParametersGenerator(digester);
		gen.init(PBEParametersGenerator.PKCS12PasswordToBytes(password.toCharArray()), salt, iteration);
		KeyParameter param = (KeyParameter) gen.generateDerivedParameters(length);
		return param.getKey();
	}

	@Override
	public int getDigestSize() {
		return this.digester.getDigestSize();
	}
}

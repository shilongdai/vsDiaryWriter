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
public class BCPCKDF2Generator implements PBKDF2KeyGenerator {

	private String digest;
	private byte[] salt;
	private int iteration;

	@Override
	public void setDigest(String digest) {
		this.digest = digest;

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
		Digest d = Digesters.getDigester(digest);
		PBEParametersGenerator gen = new PKCS12ParametersGenerator(d);
		gen.init(PBEParametersGenerator.PKCS12PasswordToBytes(password.toCharArray()), salt, iteration);
		KeyParameter param = (KeyParameter) gen.generateDerivedParameters(length);
		return param.getKey();
	}
}

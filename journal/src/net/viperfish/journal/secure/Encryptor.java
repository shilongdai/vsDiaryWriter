package net.viperfish.journal.secure;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import net.viperfish.journal.secureAlgs.AlgorithmSpec;

public abstract class Encryptor {

	private String mode;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public abstract byte[] getKey();

	public abstract void setKey(byte[] key);

	public abstract byte[] getIv();

	public abstract void setIv(byte[] iv);

	public abstract byte[] encrypt(byte[] text) throws InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException;

	public abstract byte[] decrypt(byte[] cipher) throws InvalidKeyException,
			InvalidAlgorithmParameterException, IllegalBlockSizeException,
			BadPaddingException;

	public int getKeySize() {
		return AlgorithmSpec.getKeySize(mode.split("/")[0]);
	}

}
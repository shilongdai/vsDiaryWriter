package net.viperfish.journal.secureAlgs;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class JCEMacDigester {
	private String mode;
	private Mac mac;
	private byte[] key;
	private byte[] iv;

	public Mac getMac() {
		if (mac == null) {
			try {
				mac = Mac.getInstance(mode);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}
		return mac;
	}

	private Key genKey() {
		Key secret = new SecretKeySpec(key, mode);
		return secret;
	}

	private AlgorithmParameterSpec genIV() {
		AlgorithmParameterSpec iv = new IvParameterSpec(this.iv);
		return iv;
	}

	public JCEMacDigester() {
		mode = "HMAC-SHA512";
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

	public byte[] getIv() {
		return iv;
	}

	public void setIv(byte[] iv) {
		this.iv = iv;
	}

	public byte[] calculateMac(byte[] data) {
		try {
			if (!mode.matches("(\\w+)?(HMAC)|(Hmac)|(hmac)(\\w+)?")) {
				getMac().init(genKey());
			} else {
				getMac().init(genKey(), genIV());
			}

		} catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		}
		return getMac().doFinal(data);
	}

}

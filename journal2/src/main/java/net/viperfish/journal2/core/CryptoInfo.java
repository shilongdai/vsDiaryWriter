package net.viperfish.journal2.core;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class CryptoInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5696541382533899082L;
	private byte[] key;
	private byte[] nounce;
	private String algorithm;
	private String mode;

	public CryptoInfo() {
	}

	@Basic
	@Column(name = "CryptoInfo_Key")
	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

	@Basic
	@Column(name = "CryptoInfo_Nounce")
	public byte[] getNounce() {
		return nounce;
	}

	public void setNounce(byte[] nounce) {
		this.nounce = nounce;
	}

	@Basic
	@Column(name = "CryptoInfo_Algorithm")
	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	@Basic
	@Column(name = "CryptoInfo_Mode")
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((algorithm == null) ? 0 : algorithm.hashCode());
		result = prime * result + Arrays.hashCode(key);
		result = prime * result + ((mode == null) ? 0 : mode.hashCode());
		result = prime * result + Arrays.hashCode(nounce);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CryptoInfo other = (CryptoInfo) obj;
		if (algorithm == null) {
			if (other.algorithm != null)
				return false;
		} else if (!algorithm.equals(other.algorithm))
			return false;
		if (!Arrays.equals(key, other.key))
			return false;
		if (mode == null) {
			if (other.mode != null)
				return false;
		} else if (!mode.equals(other.mode))
			return false;
		if (!Arrays.equals(nounce, other.nounce))
			return false;
		return true;
	}

}

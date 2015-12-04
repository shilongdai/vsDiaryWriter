package net.viperfish.journal.secureAlgs;

import org.bouncycastle.crypto.Mac;

public abstract class BCMacDigester implements MacDigester {

	private String mode;
	private byte[] key;
	private byte[] iv;

	protected abstract Mac getMac(String mode);

	protected abstract int getKeySize();

	protected abstract void initMac(byte[] key, byte[] iv);

	@Override
	public String getMode() {
		return mode;
	}

	@Override
	public void setMode(String mode) {
		getMac(mode);
		this.mode = mode;

	}

	@Override
	public byte[] getKey() {
		return key;
	}

	@Override
	public void setKey(byte[] key) {
		this.key = key;
	}

	@Override
	public byte[] getIv() {
		return iv;
	}

	@Override
	public void setIv(byte[] iv) {
		this.iv = iv;
	}

	@Override
	public byte[] calculateMac(byte[] data) {
		getMac(mode).reset();
		initMac(key, iv);
		for (byte i : data) {
			getMac(mode).update(i);
		}
		byte[] buffer = new byte[4096];
		int length = getMac(mode).doFinal(buffer, 0);
		byte[] result = new byte[length];
		System.arraycopy(buffer, 0, result, 0, length);
		return result;
	}

	@Override
	public int getIvLength() {
		return getMac(mode).getMacSize();
	}

	@Override
	public int getKeyLength() {
		return getKeySize();
	}

}

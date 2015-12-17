package net.viperfish.journal.secure;

public interface MacDigester {

	public abstract String getMode();

	public abstract void setMode(String mode);

	public abstract byte[] getKey();

	public abstract void setKey(byte[] key);

	public abstract byte[] getIv();

	public abstract void setIv(byte[] iv);

	public abstract byte[] calculateMac(byte[] data);

	public abstract int getIvLength();

	public abstract int getKeyLength();

}
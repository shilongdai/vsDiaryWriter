package net.viperfish.journal.secureAlgs;

public interface PBKDF2KeyGenerator {

	public void setDigest(String digest);

	public String getDigest();

	public void setIteration(int iteration);

	public int getIteration();

	public void setSalt(byte[] salt);

	public byte[] getSalt();

	public byte[] generate(String password, int length);

}

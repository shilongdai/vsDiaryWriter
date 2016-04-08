package net.viperfish.journal.streamCipher;

public interface StreamCipherEncryptor {

	public byte[] encrypt(byte[] data, byte[] key, byte[] iv);

	public byte[] decrypt(byte[] data, byte[] key, byte[] iv);

}

package net.viperfish.journal.streamCipher;

import org.bouncycastle.crypto.CipherParameters;

interface KeyConverter {
	public CipherParameters convertToParam(byte[] key, byte[] iv);
}

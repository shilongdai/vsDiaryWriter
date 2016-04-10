package net.viperfish.journal.streamCipher;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;

final class KeyParamCoverter implements KeyConverter {

	@Override
	public CipherParameters convertToParam(byte[] key, byte[] iv) {
		return new KeyParameter(key);
	}

}

package net.viperfish.journal.streamCipher;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class IVKeyParameterConverter implements KeyConverter {

	@Override
	public CipherParameters convertToParam(byte[] key, byte[] iv) {
		return new ParametersWithIV(new KeyParameter(key), iv);
	}

}

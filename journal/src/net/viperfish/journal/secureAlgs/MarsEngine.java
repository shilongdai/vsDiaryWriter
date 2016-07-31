package net.viperfish.journal.secureAlgs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;

public final class MarsEngine implements BlockCipher {

	private boolean forEncryption;
	private byte[] key;

	public MarsEngine() {

	}

	@Override
	public String getAlgorithmName() {
		return "MARS";
	}

	@Override
	public int getBlockSize() {
		return 16;
	}

	@Override
	public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
		this.forEncryption = forEncryption;
		KeyParameter param = (KeyParameter) params;
		byte[] tmp = param.getKey();
		if (tmp.length > 50 || tmp.length < 16 || tmp.length % 2 != 0) {
			throw new IllegalArgumentException();
		}
		this.key = tmp;
	}

	@Override
	public int processBlock(byte[] in, int inOff, byte[] out, int outOff)
			throws DataLengthException, IllegalStateException {
		if (outOff + 16 > out.length) {
			throw new DataLengthException();
		}
		if (key == null) {
			throw new IllegalStateException();
		}
		byte[] toMod = new byte[16];
		System.arraycopy(in, inOff, toMod, 0, 16);
		byte[] moded;
		if (forEncryption) {
			moded = Mars.encrypt(toMod, key);
		} else {
			moded = Mars.decrypt(toMod, key);
		}
		System.arraycopy(moded, 0, out, outOff, 16);
		return 16;
	}

	@Override
	public void reset() {
		this.key = null;

	}

}

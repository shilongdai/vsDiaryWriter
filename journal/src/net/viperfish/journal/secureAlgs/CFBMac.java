package net.viperfish.journal.secureAlgs;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

final class CFBMac extends BCMacDigester {

	private Mac mac;
	private String currentMode;

	CFBMac() {
		currentMode = "AES";
	}

	@Override
	protected Mac getMac(String mode) {
		if (!currentMode.equals(mode) || mac == null) {
			BlockCipher engine = BlockCiphers.getBlockCipherEngine(mode);
			mac = new CFBBlockCipherMac(engine);
			currentMode = mode;
		}
		return mac;
	}

	@Override
	protected int getKeySize() {
		return BlockCiphers.getKeySize(currentMode);
	}

	@Override
	protected void initMac(byte[] key, byte[] iv) {
		getMac(currentMode).init(new ParametersWithIV(new KeyParameter(key), iv));

	}

	@Override
	public int getIvLength() {
		return BlockCiphers.getBlockCipherEngine(currentMode).getBlockSize();
	}

}

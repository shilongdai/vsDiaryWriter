package net.viperfish.journal.secureAlgs;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;

final class HMac extends BCMacDigester {

	private Mac mac;
	private String currentMode;

	HMac() {
		currentMode = "SHA512";
	}

	@Override
	protected Mac getMac(String mode) {
		if (!currentMode.equals(mode) || mac == null) {
			Digest dig = Digesters.getDigester(mode);
			mac = new org.bouncycastle.crypto.macs.HMac(dig);
			currentMode = mode;
		}
		return mac;
	}

	@Override
	protected int getKeySize() {
		return 512;
	}

	@Override
	protected void initMac(byte[] key, byte[] iv) {
		getMac(currentMode).init(new KeyParameter(key));

	}

	@Override
	public int getIvLength() {
		// TODO Auto-generated method stub
		return 0;
	}

}

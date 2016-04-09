package net.viperfish.journal.secureProvider;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.codec.binary.Base64;

import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.secureAlgs.BCBlockCipherEncryptor;
import net.viperfish.journal.secureAlgs.BlockCipherEncryptor;
import net.viperfish.journal.secureAlgs.MacDigester;
import net.viperfish.journal.secureAlgs.Macs;

/**
 * a journal transformer that uses a compression, a block cipher, and a mac
 * algorithm to cipher an entry
 * 
 * @author sdai
 *
 */
final class BlockCipherMacTransformer extends CompressMacTransformer {

	BlockCipherMacTransformer(File salt) {
		super(salt);

		enc = new BCBlockCipherEncryptor();

		// combines information in the configuration into a mode string
		String mode = new String();
		mode += Configuration.getString(ENCRYPTION_ALG_NAME);
		mode += "/";
		mode += Configuration.getString(ENCRYPTION_MODE);
		mode += "/";
		mode += Configuration.getString(ENCRYPTION_PADDING);
		enc.setMode(mode);

		expander = Macs.getMac("HMAC");
		expander.setMode("SHA512");
	}

	public static final String ENCRYPTION_ALG_NAME = "viperfish.secure.encrytion.algorithm";
	public static final String ENCRYPTION_MODE = "viperfish.secure.encryption.mode";
	public static final String ENCRYPTION_PADDING = "viperfish.secure.encryption.padding";

	private byte[] key;
	private BlockCipherEncryptor enc;
	private MacDigester expander;

	/**
	 * generates a sub key from a master key
	 * 
	 * This method generates a sub key from a master key with schema based on
	 * the HKDF standard.
	 * 
	 * @param masterKey
	 *            the master key
	 * @param previous
	 *            the previous generated key
	 * @param info
	 *            additional informations
	 * @param octet
	 *            an additional byte to append, starting on 0x01
	 * @param length
	 *            the length of key to generate in byte
	 * @return the generated sub key
	 */
	private byte[] expandMasterKey(byte[] masterKey, byte[] previous, byte[] info, int octet, int length) {
		ByteBuffer converter = ByteBuffer.allocate(4);
		converter.putInt(octet);
		byte[] octetData = converter.array();

		byte[] result = new byte[length];
		int currentLength = 0;
		expander.setKey(masterKey);
		byte[] data = new byte[previous.length + info.length + octetData.length];
		System.arraycopy(previous, 0, data, 0, previous.length);
		System.arraycopy(info, 0, data, previous.length, info.length);
		System.arraycopy(octetData, 0, data, 0, octetData.length);
		while (currentLength != length) {
			byte[] temp = expander.calculateMac(data);
			int willAdd = (length - currentLength) > temp.length ? temp.length : length - currentLength;
			System.arraycopy(temp, 0, result, currentLength, willAdd);
			currentLength += willAdd;
		}
		return result;
	}

	/**
	 * derive the key from the password
	 */
	@Override
	public void setPassword(String string) {
		this.key = generateKey(string);
		byte[] encKey = expandMasterKey(key, new byte[0], "Encryption Key".getBytes(StandardCharsets.UTF_16), 0x01,
				enc.getKeySize() / 8);
		byte[] macKey = expandMasterKey(key, encKey, "Mac Key".getBytes(StandardCharsets.UTF_16), 0x02,
				getMacKeySize() / 8);
		enc.setKey(encKey);
		initMac(macKey);
	}

	@Override
	protected String encryptData(byte[] bytes) {
		try {
			byte[] cipher = enc.encrypt(bytes);
			byte[] iv = enc.getIv();
			String ivString = Base64.encodeBase64String(iv);
			String cipherString = Base64.encodeBase64String(cipher);
			cipherString = ivString + "&" + cipherString;
			return cipherString;
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected byte[] decryptData(String data) {
		try {
			String[] seg = data.split("&");
			if (seg.length != 2) {
				throw new IllegalArgumentException(
						"Data contains unexpected segment number. Expected 2, got:" + seg.length);
			}
			byte[] iv = Base64.decodeBase64(seg[0]);
			byte[] cipher = Base64.decodeBase64(seg[1]);

			enc.setIv(iv);
			return enc.decrypt(cipher);
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

}

package net.viperfish.journal.secureProvider;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.commons.codec.binary.Base64;

import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.secureAlgs.BCBlockCipherEncryptor;
import net.viperfish.journal.secureAlgs.BCDigester;
import net.viperfish.journal.secureAlgs.BCPCKDF2Generator;
import net.viperfish.journal.secureAlgs.Digester;
import net.viperfish.journal.secureAlgs.Encryptor;
import net.viperfish.journal.secureAlgs.MacDigester;
import net.viperfish.journal.secureAlgs.Macs;
import net.viperfish.journal.secureAlgs.PBKDF2KeyGenerator;
import net.viperfish.utils.compression.Compressor;
import net.viperfish.utils.compression.Compressors;
import net.viperfish.utils.compression.FailToInitCompressionException;
import net.viperfish.utils.compression.NullCompressor;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;

/**
 * a journal transformer that uses a compression, a block cipher, and a mac
 * algorithm to cipher an entry
 * 
 * @author sdai
 *
 */
public class BlockCipherMacTransformer implements JournalTransformer {

	public static final String ENCRYPTION_ALG_NAME = "viperfish.secure.encrytion.algorithm";
	public static final String ENCRYPTION_MODE = "viperfish.secure.encryption.mode";
	public static final String ENCRYPTION_PADDING = "viperfish.secure.encryption.padding";
	public static final String MAC_TYPE = "viperfish.secure.mac.type";
	public static final String MAC_ALGORITHM = "viperfish.secure.mac.algorithm";
	public static final String KDF_HASH = "viperfish.secure.kdf.algorithm";

	private final File saltStore;
	private byte[] key;
	private byte[] macIV;
	private byte[] saltForKDF;
	private SecureRandom rand;
	private Encryptor enc;
	private Digester dig;
	private MacDigester mac;
	private PBKDF2KeyGenerator keyGenerator;
	private Compressor compress;

	/**
	 * encrypt raw data into format, compressing it first
	 * 
	 * @param bytes
	 *            the data to encrypt
	 * @return the encrypted compressed data in the format of iv$cipher in
	 *         Base64
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private String encryptData(byte[] bytes) throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		byte[] compressed = compress.compress(bytes);
		byte[] cipher = enc.encrypt(compressed);
		byte[] iv = enc.getIv();
		String ivString = Base64.encodeBase64String(iv);
		String cipherString = Base64.encodeBase64String(cipher);
		cipherString = ivString + "$" + cipherString;
		return cipherString;
	}

	/**
	 * calculat a mac of the data and encode it into Base64
	 * 
	 * @param bytes
	 *            the data
	 * @return the mac encoded in Base64
	 */
	private String macData(byte[] bytes) {
		byte[] macValue = mac.calculateMac(bytes);
		String macString = Base64.encodeBase64String(macValue);
		return macString;
	}

	/**
	 * transform the field of a journal into the format of IV$Cipher$Mac
	 * 
	 * @param data
	 *            the field of journal to cipher
	 * @return the result of the ciphering
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private String encrypt_format(String data) throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		byte[] bytes = data.getBytes(StandardCharsets.UTF_16);
		// encrypt
		String cipherString;
		cipherString = encryptData(bytes);

		// generate mac
		String macString = macData(cipherString.getBytes(StandardCharsets.UTF_16));
		cipherString += "$";

		cipherString += macString;
		return cipherString;
	}

	/**
	 * decrypt a field in an entry in the format of IV$Cipher$Mac, that the data
	 * is compressed first
	 * 
	 * @param data
	 *            the field in an entry
	 * @return the plain text
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws CompromisedDataException
	 *             the stored Mac does not match the calculated Mac
	 */
	private String decrypt_format(String data) throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException, CompromisedDataException {
		String[] parts = data.split("\\$");
		String ivData = parts[0];
		String cData = parts[1];
		String macString = parts[2];

		// verify checksum
		byte[] rawMac = Base64.decodeBase64(macString);

		byte[] expectedMac = mac.calculateMac((ivData + "$" + cData).getBytes(StandardCharsets.UTF_16));
		if (!Arrays.equals(expectedMac, rawMac)) {
			throw new CompromisedDataException();
		}

		byte[] rIv = Base64.decodeBase64(ivData);
		enc.setIv(rIv);

		byte[] data64 = Base64.decodeBase64(cData);
		byte[] compressed = enc.decrypt(data64);
		byte[] plain = compress.deflate(compressed);
		String plainText = new String(plain, StandardCharsets.UTF_16);

		return plainText;
	}

	private void newSalt() {
		rand.nextBytes(saltForKDF);
		writeSalt();
	}

	private void writeSalt() {
		IOFile saltFile = new IOFile(saltStore, new TextIOStreamHandler());
		saltFile.write(saltForKDF);
	}

	private void loadSalt() {
		if (!saltStore.exists()) {
			newSalt();
			return;
		} else {
			IOFile saltFile = new IOFile(saltStore, new TextIOStreamHandler());
			saltForKDF = saltFile.read();
		}
	}

	@Override
	public Journal encryptJournal(Journal j) {
		try {
			String encrytSubject = encrypt_format(j.getSubject());
			String encryptContent = encrypt_format(j.getContent());
			Journal result = new Journal();
			result.setSubject(encrytSubject);
			result.setContent(encryptContent);
			result.setDate(j.getDate());
			result.setId(j.getId());
			return result;
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Journal decryptJournal(Journal j) {
		try {
			String decSubject = decrypt_format(j.getSubject());
			String decContent = decrypt_format(j.getContent());
			Journal result = new Journal();
			result.setSubject(decSubject);
			result.setContent(decContent);
			result.setDate(j.getDate());
			result.setId(j.getId());
			return result;
		} catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
				| BadPaddingException | CompromisedDataException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * initialize all the algorithm used to transform an entry
	 */
	private void initAlgorithms() {
		// get the objects for blockcipher, mac, and digest
		enc = new BCBlockCipherEncryptor();
		dig = new BCDigester();
		// get the type of mac
		String macMethod = Configuration.getString(MAC_TYPE);
		mac = Macs.getMac(macMethod);
		// set the algorithm of mac
		mac.setMode(Configuration.getString(MAC_ALGORITHM));

		// combines information in the configuration into a mode string
		String mode = new String();
		mode += Configuration.getString(ENCRYPTION_ALG_NAME);
		mode += "/";
		mode += Configuration.getString(ENCRYPTION_MODE);
		mode += "/";
		mode += Configuration.getString(ENCRYPTION_PADDING);
		enc.setMode(mode);

		dig.setMode("SHA512");

		// try to get a compressor, no compression if compressor not found
		try {
			compress = Compressors.getCompressor("gz");
		} catch (FailToInitCompressionException e) {
			System.err.println("failed to find gz compression, using null compression");
			compress = new NullCompressor();
		}
	}

	/**
	 * initialize the Key Generation schema
	 */
	private void initKDF() {
		rand = new SecureRandom();
		saltForKDF = new byte[10];
		loadSalt();
		keyGenerator = new BCPCKDF2Generator();
		keyGenerator.setDigest(Configuration.getString(KDF_HASH));
		keyGenerator.setIteration(3000);
		keyGenerator.setSalt(saltForKDF);
	}

	public BlockCipherMacTransformer(File salt) {
		this.saltStore = salt;
		initAlgorithms();
		initKDF();
	}

	/**
	 * derive the key from the password
	 */
	@Override
	public void setPassword(String string) {
		this.key = keyGenerator.generate(string, enc.getKeySize());
		// the mac key is derived from the result of the KDF function via a
		// digest
		mac.setKey(this.keyGenerator.generate(Base64.encodeBase64String(dig.digest(this.key)), mac.getKeyLength()));
		enc.setKey(key);
		macIV = new byte[mac.getIvLength()];
		// set mac IV to 0 based on experts
		Arrays.fill(macIV, (byte) 0);
		mac.setIv(macIV);
	}

}

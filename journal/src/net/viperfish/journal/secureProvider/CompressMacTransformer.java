package net.viperfish.journal.secureProvider;

import java.io.File;
import java.io.IOException;
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
import net.viperfish.journal.framework.errors.CipherException;
import net.viperfish.journal.framework.errors.CompromisedDataException;
import net.viperfish.journal.framework.errors.FailToSyncCipherDataException;
import net.viperfish.journal.secureAlgs.BCPCKDF2Generator;
import net.viperfish.journal.secureAlgs.MacDigester;
import net.viperfish.journal.secureAlgs.Macs;
import net.viperfish.journal.secureAlgs.PBKDF2KeyGenerator;
import net.viperfish.utils.compression.Compressor;
import net.viperfish.utils.compression.Compressors;
import net.viperfish.utils.compression.FailToInitCompressionException;
import net.viperfish.utils.compression.NullCompressor;
import net.viperfish.utils.file.IOFile;
import net.viperfish.utils.file.TextIOStreamHandler;

abstract class CompressMacTransformer implements JournalTransformer {

	public static final String MAC_TYPE = "viperfish.secure.mac.type";
	public static final String MAC_ALGORITHM = "viperfish.secure.mac.algorithm";
	public static final String KDF_HASH = "viperfish.secure.kdf.algorithm";
	public static final String COMPRESSION = "viperfish.secure.compression.algorithm";

	private final File saltStore;
	private byte[] macIV;
	private byte[] saltForKDF;
	private SecureRandom rand;
	private MacDigester expander;
	private MacDigester mac;
	private PBKDF2KeyGenerator keyGenerator;
	private Compressor compress;

	/**
	 * encrypt raw data into format
	 * 
	 * @param bytes
	 *            the data to encrypt
	 * @return the encrypted data in the format of iv$cipher in Base64
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	protected abstract String encryptData(byte[] bytes);

	protected abstract byte[] decryptData(String cData);

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
		bytes = compress.compress(bytes);
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
		String cData = parts[0];
		String macString = parts[1];

		// verify checksum
		byte[] rawMac = Base64.decodeBase64(macString);

		byte[] expectedMac = mac.calculateMac(cData.getBytes(StandardCharsets.UTF_16));
		if (!Arrays.equals(expectedMac, rawMac)) {
			throw new CompromisedDataException(
					"Compromised: expected = " + Arrays.toString(expectedMac) + " got = " + Arrays.toString(rawMac));
		}

		byte[] compressed = decryptData(cData);
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
		try {
			saltFile.write(saltForKDF);
		} catch (IOException e) {
			FailToSyncCipherDataException f = new FailToSyncCipherDataException(
					"Cannot write salt to file:" + e.getMessage());
			f.initCause(e);
			throw new RuntimeException(f);
		}
	}

	private void loadSalt() {
		if (!saltStore.exists()) {
			newSalt();
			return;
		} else {
			IOFile saltFile = new IOFile(saltStore, new TextIOStreamHandler());
			try {
				saltForKDF = saltFile.read();
			} catch (IOException e) {
				FailToSyncCipherDataException f = new FailToSyncCipherDataException(
						"Cannot load salt from file:" + e.getMessage());
				f.initCause(e);
				throw new RuntimeException(f);
			}
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
			CipherException c = new CipherException("Cannot encrypt entry:" + e.getMessage());
			c.initCause(e);
			throw new RuntimeException(c);
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
			CipherException c = new CipherException("cannot decrypt entry:" + e.getMessage());
			c.initCause(e);
			throw new RuntimeException(c);
		}
	}

	/**
	 * initialize all the algorithm used to transform an entry
	 */
	private void initAlgorithms() {
		// get the objects for mac, and digest
		expander = Macs.getMac("HMAC");
		expander.setMode("SHA512");
		// get the type of mac
		String macMethod = Configuration.getString(MAC_TYPE);
		mac = Macs.getMac(macMethod);
		// set the algorithm of mac
		mac.setMode(Configuration.getString(MAC_ALGORITHM));

		// try to get a compressor, no compression if compressor not found
		try {
			compress = Compressors.getCompressor(Configuration.getString(COMPRESSION));
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
		keyGenerator.setIteration(64000);
		keyGenerator.setSalt(saltForKDF);
	}

	CompressMacTransformer(File salt) {
		this.saltStore = salt;
		initAlgorithms();
		initKDF();
	}

	protected byte[] generateKey(String password) {
		return keyGenerator.generate(password, keyGenerator.getDigestSize());
	}

	protected int getMacKeySize() {
		return mac.getKeyLength();
	}

	protected void initMac(byte[] key) {
		mac.setKey(key);
		macIV = new byte[mac.getIvLength()];
		// set mac IV to 0 based on experts
		Arrays.fill(macIV, (byte) 0);
		mac.setIv(macIV);
	}

}

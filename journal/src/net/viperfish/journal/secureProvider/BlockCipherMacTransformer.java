package net.viperfish.journal.secureProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import net.viperfish.journal.secureAlgs.BCBlockCipherEncryptor;
import net.viperfish.journal.secureAlgs.BCDigester;
import net.viperfish.journal.secureAlgs.BCPCKDF2Generator;
import net.viperfish.journal.secureAlgs.CBCMac;
import net.viperfish.journal.secureAlgs.CFBMac;
import net.viperfish.journal.secureAlgs.CMac;
import net.viperfish.journal.secureAlgs.GMac;
import net.viperfish.journal.secureAlgs.HMac;
import net.viperfish.journal.secureAlgs.PBKDF2KeyGenerator;
import net.viperfish.utils.compression.Compressor;
import net.viperfish.utils.compression.Compressors;
import net.viperfish.utils.compression.FailToInitCompressionException;
import net.viperfish.utils.compression.NullCompressor;
import net.viperfish.utils.config.ComponentConfig;

public class BlockCipherMacTransformer implements JournalTransformer {

	public static final String ENCRYPTION_ALG_NAME = "viperfish.secure.encrytion.algorithm";
	public static final String ENCRYPTION_MODE = "viperfish.secure.encryption.mode";
	public static final String ENCRYPTION_PADDING = "viperfish.secure.encryption.padding";
	public static final String MAC_TYPE = "viperfish.secure.mac.type";
	public static final String MAC_ALGORITHM = "viperfish.secure.mac.algorithm";

	private static SecureEntryWrapperConfig config;

	public static ComponentConfig config() {
		if (config == null) {
			config = new SecureEntryWrapperConfig();
		}
		return config;
	}

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

	private String encryptData(byte[] bytes) throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		byte[] cipher = enc.encrypt(bytes);
		byte[] iv = enc.getIv();
		String ivString = Base64.encodeBase64String(iv);
		String cipherString = Base64.encodeBase64String(cipher);
		cipherString = ivString + "$" + cipherString;
		return cipherString;
	}

	private String macData(byte[] bytes) {
		byte[] macValue = mac.calculateMac(bytes);
		String macString = Base64.encodeBase64String(macValue);
		return macString;
	}

	private String encrypt_format(String data) throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		byte[] bytes = data.getBytes(StandardCharsets.UTF_16);
		// encrypt
		String cipherString;
		cipherString = encryptData(bytes);
		cipherString += "$";

		// generate mac
		String macString = macData(bytes);

		cipherString += macString;
		return cipherString;
	}

	private String decrypt_format(String data) throws InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException, CompromisedDataException {
		String[] parts = data.split("\\$");
		String ivData = parts[0];
		String cData = parts[1];
		String macString = parts[2];

		byte[] rIv = Base64.decodeBase64(ivData);
		enc.setIv(rIv);

		byte[] data64 = Base64.decodeBase64(cData);
		byte[] plain = enc.decrypt(data64);
		String plainText = new String(plain, StandardCharsets.UTF_16);

		// verify checksum
		byte[] rawMac = Base64.decodeBase64(macString);

		byte[] expectedMac = mac.calculateMac(plain);
		if (!Arrays.equals(expectedMac, rawMac)) {
			throw new CompromisedDataException();
		}
		return plainText;
	}

	private void writeSalt() {
		if (!saltStore.exists()) {
			try {
				saltStore.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(saltStore)));
			out.write(saltForKDF);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	private void loadSalt() {
		DataInputStream in = null;
		if (!saltStore.exists()) {
			try {
				saltStore.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			rand.nextBytes(saltForKDF);
			writeSalt();
			return;
		} else {
			try {
				in = new DataInputStream(new BufferedInputStream(new FileInputStream(saltStore)));
				for (int i = 0; i < 10; ++i) {
					saltForKDF[i] = in.readByte();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
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
			e.fillInStackTrace();
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
			e.fillInStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void initAlgorithms() {
		enc = new BCBlockCipherEncryptor();
		dig = new BCDigester();
		String macMethod = Configuration.getString(MAC_TYPE);
		if (macMethod.equalsIgnoreCase("CBCMAC")) {
			mac = new CBCMac();
		} else if (macMethod.equalsIgnoreCase("CMAC")) {
			mac = new CMac();
		} else if (macMethod.equalsIgnoreCase("CFBMAC")) {
			mac = new CFBMac();
		} else if (macMethod.equalsIgnoreCase("GMAC")) {
			mac = new GMac();
		} else if (macMethod.equalsIgnoreCase("HMAC")) {
			mac = new HMac();
		}
		mac.setMode(Configuration.getString(MAC_ALGORITHM));
		String mode = new String();
		mode += Configuration.getString(ENCRYPTION_ALG_NAME);
		mode += "/";
		mode += Configuration.getString(ENCRYPTION_MODE);
		mode += "/";
		mode += Configuration.getString(ENCRYPTION_PADDING);
		enc.setMode(mode);
		dig.setMode("SHA512");

		try {
			compress = Compressors.getCompressor("gz");
		} catch (FailToInitCompressionException e) {
			System.err.println("failed to find gz compression, using null compression");
			compress = new NullCompressor();
		}
	}

	private void initKDF() {
		rand = new SecureRandom();
		saltForKDF = new byte[10];
		loadSalt();
		keyGenerator = new BCPCKDF2Generator();
		keyGenerator.setDigest("SHA256");
		keyGenerator.setIteration(3000);
		keyGenerator.setSalt(saltForKDF);
	}

	public BlockCipherMacTransformer(File salt) {
		this.saltStore = salt;
		initAlgorithms();
		initKDF();
	}

	@Override
	public void setPassword(String string) {
		this.key = keyGenerator.generate(string, enc.getKeySize());
		mac.setKey(this.keyGenerator.generate(Base64.encodeBase64String(dig.digest(this.key)), mac.getKeyLength()));
		enc.setKey(key);
		macIV = new byte[mac.getIvLength()];
		Arrays.fill(macIV, (byte) 0);
		mac.setIv(macIV);
	}

}

package net.viperfish.journal2.crypt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA3Digest;

import net.viperfish.journal2.core.CryptoInfo;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalConfiguration;
import net.viperfish.journal2.core.Observable;
import net.viperfish.journal2.core.Observer;
import net.viperfish.journal2.core.Processor;
import net.viperfish.journal2.error.CipherException;
import net.viperfish.journal2.error.CompromisedDataException;

public class JournalEncryptorChain extends Observable<byte[]> implements Observer<String> {

	public static final String CONFIG_KEY_ENCRYPTION_ALGORITHM = "crypt.keyEncryption.algorithm";

	public static final String CONFIG_KEY_ENCRYPTION_KEYLENGTH = "crypt.keyEncryption.keyLength";

	public static final String CONFIG_ENABLED_PROCESSORS = "crypt.processors";

	public JournalEncryptorChain(Path salt) {
		rand = new SecureRandom();
		processors = new HashMap<>();
		this.saltFile = salt;
		this.salt = new byte[0];
		lastEngine = new String();
	}

	private SecureRandom rand;
	private Map<String, Processor> processors;
	private byte[] masterkey;
	private Path saltFile;
	private byte[] salt;
	private BlockCipher engine;
	private String lastEngine;

	private BlockCipher initEngine(String cipher) {
		if (cipher.equalsIgnoreCase(lastEngine)) {
			return engine;
		} else {
			engine = BlockCiphers.getBlockCipherEngine(cipher);
			lastEngine = cipher;
			return engine;
		}
	}

	private void loadSalt() throws IOException {
		if (saltFile.toFile().exists()) {
			this.salt = Files.readAllBytes(saltFile);
		}
		if (salt.length != 10) {
			newSalt();
		}
	}

	private void newSalt() throws IOException {
		salt = new byte[10];
		rand.nextBytes(salt);
		Files.write(saltFile, salt, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
	}

	private Journal proccess(Journal j) {
		byte[] subject = j.getSubject().getBytes(StandardCharsets.UTF_8);
		byte[] content = j.getContent().getBytes(StandardCharsets.UTF_8);
		String[] enabledString = JournalConfiguration.getStringArray(CONFIG_ENABLED_PROCESSORS);
		SortedMap<Long, String> enabledProcessors = new TreeMap<>();
		long loadOrder = 0;
		for (String i : enabledString) {
			enabledProcessors.put(loadOrder++, i);
		}
		j.setProcessedBy(enabledProcessors);
		for (Entry<Long, String> e : enabledProcessors.entrySet()) {
			Processor p = this.processors.get(e.getValue());
			p.generator().generate(j.getInfoMapping());
			Map<String, byte[]> col = new HashMap<>();
			col.put("subject", subject);
			col.put("content", content);
			col = p.doProccess(col, j.getInfoMapping());
			subject = col.get("subject");
			content = col.get("content");
		}
		String stringSubject = Base64.encodeBase64String(subject);
		String stringContent = Base64.encodeBase64String(content);
		j.setSubject(stringSubject);
		j.setContent(stringContent);

		return j;
	}

	private CryptoInfo cryptInfo(CryptoInfo info) {
		engine = initEngine(JournalConfiguration.getString(CONFIG_KEY_ENCRYPTION_ALGORITHM));
		byte[] key = info.getKey();
		try {
			if (key != null) {
				key = CryptUtils.INSTANCE.ecbCrypt(key, masterkey, engine);
			}
			info.setKey(key);
		} catch (DataLengthException | IllegalStateException | InvalidCipherTextException e) {
			throw new CipherException(e);
		}
		return info;
	}

	private CryptoInfo decryptInfo(CryptoInfo info)
			throws DataLengthException, IllegalStateException, InvalidCipherTextException {
		engine = initEngine(JournalConfiguration.getString(CONFIG_KEY_ENCRYPTION_ALGORITHM));
		byte[] key = info.getKey();

		if (key != null) {
			key = CryptUtils.INSTANCE.ecbDecrypt(key, masterkey, engine);
		}
		info.setKey(key);
		return info;

	}

	private Journal undoProccess(Journal j) {
		byte[] subject = Base64.decodeBase64(j.getSubject());
		byte[] content = Base64.decodeBase64(j.getContent());
		Map<Long, String> reversed = new TreeMap<>(Collections.reverseOrder());
		reversed.putAll(j.getProcessedBy());
		for (Entry<Long, String> entry : reversed.entrySet()) {
			Processor p = processors.get(entry.getValue());
			try {
				Map<String, byte[]> data = new HashMap<>();
				data.put("subject", subject);
				data.put("content", content);
				data = p.undoProccess(data, j.getInfoMapping());
				subject = data.get("subject");
				content = data.get("content");
			} catch (CipherException | CompromisedDataException e) {
				break;
			}
		}

		String subjectString = new String(subject, StandardCharsets.UTF_8);
		String contentString = new String(content, StandardCharsets.UTF_8);

		j.setSubject(subjectString);
		j.setContent(contentString);
		return j;
	}

	public Journal encryptJournal(Journal j) {
		j = proccess(j);

		Map<String, CryptoInfo> infos = j.getInfoMapping();
		// encrypt with master key
		for (Entry<String, CryptoInfo> e : infos.entrySet()) {
			cryptInfo(e.getValue());
		}
		j.setInfoMapping(infos);
		return j;
	}

	public Journal decryptJournal(Journal j) {
		Map<String, CryptoInfo> infos = j.getInfoMapping();
		for (Entry<String, CryptoInfo> e : infos.entrySet()) {

			try {
				decryptInfo(e.getValue());
			} catch (DataLengthException | IllegalStateException | InvalidCipherTextException e1) {
				return j;
			}

		}
		j.setInfoMapping(infos);

		j = undoProccess(j);
		return j;
	}

	public void setPassword(String password) {
		try {
			loadSalt();
		} catch (IOException e) {
			throw new CipherException(e);
		}
		this.masterkey = CryptUtils.INSTANCE.kdfKey(password, salt,
				JournalConfiguration.containsKey(CONFIG_KEY_ENCRYPTION_KEYLENGTH)
						? JournalConfiguration.getInt(CONFIG_KEY_ENCRYPTION_KEYLENGTH)
						: BlockCiphers.getKeySize(JournalConfiguration.getString(CONFIG_KEY_ENCRYPTION_ALGORITHM)),
				new SHA3Digest(256));
		this.notifyObservers(masterkey);
	}

	public void addProccessor(Processor p) {
		this.processors.put(p.getId(), p);
	}

	public void clear() throws IOException {
		this.processors.clear();
		Files.deleteIfExists(saltFile);
	}

	@Override
	public void beNotified(String data) {
		this.setPassword(data);
	}

}

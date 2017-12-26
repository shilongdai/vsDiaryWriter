package net.viperfish.journal2.core;

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
import net.viperfish.journal2.crypt.BlockCiphers;
import net.viperfish.journal2.crypt.CryptUtils;
import net.viperfish.journal2.error.CipherException;
import net.viperfish.journal2.error.CompromisedDataException;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.digests.SHA3Digest;

public class JournalEncryptorChain extends Observable<byte[]> implements Observer<String> {

    public static final String CONFIG_KEY_ENCRYPTION_ALGORITHM = "crypt.keyEncryption.algorithm";

    public static final String CONFIG_KEY_ENCRYPTION_KEYLENGTH = "crypt.keyEncryption.keyLength";

    public static final String CONFIG_ENABLED_PROCESSORS = "crypt.processors";

    public JournalEncryptorChain(Path salt) {
        rand = new SecureRandom();
        processors = new HashMap<>();
        this.saltFile = salt;
        this.salt = new byte[0];
        engine = BlockCiphers
                .getBlockCipherEngine(JournalConfiguration.getString(CONFIG_KEY_ENCRYPTION_ALGORITHM, "AES"));
    }

    public Journal encryptJournal(Journal j) {
        try {
            j = proccess(j);

            Map<String, CryptoInfo> infos = j.getInfoMapping();
            // encrypt with master key
            for (Entry<String, CryptoInfo> e : infos.entrySet()) {
                cryptInfo(e.getValue());
            }
            j.setInfoMapping(infos);
            return j;
        } catch (DataLengthException | IllegalStateException | InvalidCipherTextException ex) {
            throw new IllegalArgumentException("invalid journal", ex);
        }
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

    public String encryptSubject(String subject) {
        StringBuilder sb = new StringBuilder();
        try {
            for (String w : subject.split(" ")) {
                byte[] cryptedWord = CryptUtils.INSTANCE.ecbCrypt(w.getBytes(StandardCharsets.UTF_8), subjectKey,
                        engine);
                sb.append(Base64.encodeBase64URLSafeString(cryptedWord)).append(" ");
            }
            return sb.toString().trim();
        } catch (DataLengthException | IllegalStateException | InvalidCipherTextException e) {
            engine.reset();
            throw new CipherException(e);
        }
    }

    public String decryptSubject(String cryptSubject) {
        String[] words = cryptSubject.split(" ");
        StringBuilder sb = new StringBuilder();
        try {
            for (String i : words) {
                byte[] cryptWord = Base64.decodeBase64(i);
                byte[] plainWord = CryptUtils.INSTANCE.ecbDecrypt(cryptWord, subjectKey, engine);
                sb.append(new String(plainWord, StandardCharsets.UTF_8)).append(" ");
            }
            return sb.toString().trim();
        } catch (DataLengthException | IllegalStateException | InvalidCipherTextException e) {
            engine.reset();
            throw new CipherException(e);
        }
    }

    public void setPassword(String password) {
        try {
            loadSalt();
        } catch (IOException e) {
            throw new CipherException(e);
        }
        this.masterkey = CryptUtils.INSTANCE.kdfKey(password, salt,
                JournalConfiguration.getInt(CONFIG_KEY_ENCRYPTION_KEYLENGTH,
                        BlockCiphers
                                .getKeySize(JournalConfiguration.getString(CONFIG_KEY_ENCRYPTION_ALGORITHM, "AES"))),
                new SHA3Digest(256));
        this.subjectKey = CryptUtils.INSTANCE.kdfKey(Base64.encodeBase64String(masterkey), salt,
                JournalConfiguration.getInt(CONFIG_KEY_ENCRYPTION_KEYLENGTH,
                        BlockCiphers
                                .getKeySize(JournalConfiguration.getString(CONFIG_KEY_ENCRYPTION_ALGORITHM, "AES"))),
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

    private SecureRandom rand;
    private Map<String, Processor> processors;
    private byte[] masterkey;
    private byte[] subjectKey;
    private Path saltFile;
    private byte[] salt;
    private BlockCipher engine;

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

    private SortedMap<Long, String> enabledToMap(String[] enabledString) {
        SortedMap<Long, String> result = new TreeMap<>();
        long loadOrder = 0;
        for (String i : enabledString) {
            result.put(loadOrder++, i);
        }
        return result;
    }

    private Journal proccess(Journal j) throws DataLengthException, IllegalStateException, InvalidCipherTextException {
        byte[] subject = j.getSubject().getBytes(StandardCharsets.UTF_8);
        byte[] content = j.getContent().getBytes(StandardCharsets.UTF_8);
        String[] enabledString = JournalConfiguration.getStringArray(CONFIG_ENABLED_PROCESSORS);
        SortedMap<Long, String> enabledProcessors = enabledToMap(enabledString);
        j.setProcessedBy(enabledProcessors);
        for (Entry<Long, String> e : enabledProcessors.entrySet()) {
            Processor p = this.processors.get(e.getValue());
            CryptoInfo info = j.getInfoMapping().get(p.getId());
            if (info == null) {
                info = new CryptoInfo();
                p.generator().generate(info);
                j.getInfoMapping().put(p.getId(), info);
            }
            Map<String, byte[]> col = new HashMap<>();
            col.put("subject", subject);
            col.put("content", content);
            col = p.doProccess(col, info);
            content = col.get("content");
        }
        String stringSubject = new String(subject, StandardCharsets.UTF_8);
        String stringContent = Base64.encodeBase64String(content);
        j.setSubject(stringSubject);
        j.setContent(stringContent);
        return j;
    }

    private CryptoInfo cryptInfo(CryptoInfo info) {
        byte[] key = info.getKey();
        try {
            if (key != null && key.length > 0) {
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
        byte[] key = info.getKey();
        if (key != null && key.length > 0) {
            key = CryptUtils.INSTANCE.ecbDecrypt(key, masterkey, engine);
        }
        info.setKey(key);
        return info;

    }

    private Journal undoProccess(Journal j) {
        byte[] subject = j.getSubject().getBytes(StandardCharsets.UTF_8);
        byte[] content = Base64.decodeBase64(j.getContent());
        Map<Long, String> reversed = new TreeMap<>(Collections.reverseOrder());
        reversed.putAll(j.getProcessedBy());
        for (Entry<Long, String> entry : reversed.entrySet()) {
            Processor p = processors.get(entry.getValue());
            try {
                Map<String, byte[]> data = new HashMap<>();
                data.put("subject", subject);
                data.put("content", content);
                data = p.undoProccess(data, j.getInfoMapping().get(p.getId()));
                content = data.get("content");
            } catch (CipherException e) {
                throw new CompromisedDataException(e, j.getId());
            }
        }

        String subjectString = new String(subject, StandardCharsets.UTF_8);
        String contentString = new String(content, StandardCharsets.UTF_8);

        j.setSubject(subjectString);
        j.setContent(contentString);
        return j;
    }

}

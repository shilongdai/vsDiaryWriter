/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.viperfish.journal2.crypt;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import net.viperfish.journal2.core.Observer;
import net.viperfish.journal2.error.CipherException;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.configuration.Configuration;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.springframework.util.Base64Utils;

/**
 *
 * @author sdai
 */
public class TextIndexFieldEncryptor implements Observer<byte[]> {

    public static String INDEX_ENCRYPTION_ALGORITHM = "crypt.index.alg";
    public static String INDEX_ENCRYPTION_KEY = "crypt.index.key";
    public static String INDEX_ENCRYPTION_KEYSIZE = "crypt.index.keysize";

    private BlockCipher cipher;
    private Configuration config;
    private byte[] masterKey;
    private SecureRandom rand;
    private byte[] key;
    private Base32 encoder;

    public TextIndexFieldEncryptor(Configuration config) {
        rand = new SecureRandom();
        this.config = config;
        encoder = new Base32();
    }

    public void setMasterKey(byte[] masterKey) {
        this.masterKey = masterKey.clone();
    }

    private byte[] initKey() throws NoSuchAlgorithmException, DataLengthException, IllegalStateException, InvalidCipherTextException {
        String alg = config.getString(INDEX_ENCRYPTION_ALGORITHM, "AES");
        cipher = BlockCiphers.getBlockCipherEngine(alg);
        if (cipher == null) {
            throw new NoSuchAlgorithmException(alg);
        }
        String encodedEncryptedKey = config.getString(INDEX_ENCRYPTION_KEY);
        if (encodedEncryptedKey != null) {
            byte[] encryptedKey = Base64Utils.decodeFromUrlSafeString(config.getString(INDEX_ENCRYPTION_KEY));
            return CryptUtils.INSTANCE.ecbDecrypt(encryptedKey, masterKey, cipher);
        } else {
            int keySize = config.getInt(INDEX_ENCRYPTION_KEYSIZE, BlockCiphers.getKeySize(alg));
            byte[] newKey = new byte[keySize / 8];
            rand.nextBytes(newKey);
            config.setProperty(INDEX_ENCRYPTION_KEY, Base64Utils.encodeToUrlSafeString(CryptUtils.INSTANCE.ecbCrypt(newKey, masterKey, cipher)));
            return newKey;
        }

    }

    public String cryptStringWords(String plaintext) {
        StringBuilder sb = new StringBuilder();
        try {
            key = initKey();
            for (String w : plaintext.split(" ")) {
                byte[] cryptedWord = CryptUtils.INSTANCE.ecbCrypt(w.getBytes(StandardCharsets.UTF_8), key, cipher);
                sb.append(encoder.encodeToString(cryptedWord)).append(" ");
            }
            System.err.println("ToIndex:" + sb.toString());
            return sb.toString().trim();
        } catch (DataLengthException | IllegalStateException | InvalidCipherTextException e) {
            throw new CipherException(e);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void beNotified(byte[] data) {
        this.masterKey = data;
    }

}

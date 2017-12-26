package net.viperfish.journal2.crypt;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import net.viperfish.journal2.core.CryptoInfo;
import net.viperfish.journal2.core.CryptoInfoGenerator;
import net.viperfish.journal2.core.JournalConfiguration;
import net.viperfish.journal2.core.Processor;
import net.viperfish.journal2.error.CipherException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;

public class AEADProccessor implements Processor {

    public static final String CONFIG_ENCRYPTION_ALGORITHM = "crypt.encryption.algorithm";
    public static final String CONFIG_ENCRYPTION_KEYLENGTH = "crypt.encryption.keyLength";
    public static final String CONFIG_ENCRYPTION_MODE = "crypt.encryption.mode";

    public AEADProccessor() {
    }

    private AEADBlockCipher initCipher(CryptoInfo info) {
        return BlockCiphers.useAEADMode(BlockCiphers.getBlockCipherEngine(info.getAlgorithm()), info.getMode());
    }

    @Override
    public Map<String, byte[]> doProccess(Map<String, byte[]> data, CryptoInfo c)
            throws CipherException {
        AEADBlockCipher cipher = initCipher(c);
        int aeadSize;
        if (c.getMode().equalsIgnoreCase("GCM") || c.getMode().equalsIgnoreCase("OCB")) {
            aeadSize = 128;
        } else {
            aeadSize = 16;
        }
        cipher.init(true, new AEADParameters(new KeyParameter(c.getKey()), aeadSize, c.getNounce()));
        Map<String, byte[]> result = new HashMap<>();
        byte[] content = data.get("content");
        try {
            result.put("content", CryptUtils.INSTANCE.transformData(cipher, content, data.get("subject")));
        } catch (DataLengthException | IllegalStateException | InvalidCipherTextException ex) {
            throw new CipherException(ex);
        }
        return result;

    }

    @Override
    public Map<String, byte[]> undoProccess(Map<String, byte[]> data, CryptoInfo c)
            throws CipherException {
        AEADBlockCipher cipher = initCipher(c);
        int aeadSize;
        if (c.getMode().equalsIgnoreCase("GCM") || c.getMode().equalsIgnoreCase("OCB")) {
            aeadSize = 128;
        } else {
            aeadSize = 16;
        }
        cipher.init(false, new AEADParameters(new KeyParameter(c.getKey()), aeadSize, c.getNounce()));
        Map<String, byte[]> result = new HashMap<>();
        byte[] contentBytes = data.get("content");
        try {
            result.put("content", CryptUtils.INSTANCE.transformData(cipher, contentBytes, data.get("subject")));
        } catch (DataLengthException | IllegalStateException | InvalidCipherTextException e1) {
            throw new CipherException(e1);
        }

        return result;
    }

    @Override
    public String getId() {
        return "aeadEncryption";
    }

    @Override
    public CryptoInfoGenerator generator() {
        return new CryptoInfoGenerator() {

            @Override
            public void generate(CryptoInfo info) {
                String algorithm;
                String mode;
                algorithm = JournalConfiguration.getString(CONFIG_ENCRYPTION_ALGORITHM, "AES");
                mode = JournalConfiguration.getString(CONFIG_ENCRYPTION_MODE, "GCM");

                SecureRandom rand = new SecureRandom();
                info.setAlgorithm(algorithm);
                info.setMode(mode);

                byte[] key = new byte[JournalConfiguration.getInt(CONFIG_ENCRYPTION_KEYLENGTH,
                        BlockCiphers.getKeySize(algorithm)) / 8];
                rand.nextBytes(key);
                byte[] iv = CryptUtils.INSTANCE.generateNonce(BlockCiphers.getNounceSize(mode));

                info.setKey(key);
                info.setNounce(iv);
            }
        };
    }

}

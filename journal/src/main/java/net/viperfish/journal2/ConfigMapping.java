package net.viperfish.journal2;

import net.viperfish.journal2.core.JournalEncryptorChain;
import net.viperfish.journal2.crypt.AEADProccessor;
import net.viperfish.journal2.crypt.CompressionProccessor;
import net.viperfish.journal2.crypt.HMACProcessor;
import net.viperfish.journal2.crypt.StreamCipherProcessor;

final class ConfigMapping {
	// encryption chain
	public static final String CONFIG_KEY_ENCRYPTION_ALGORITHM = JournalEncryptorChain.CONFIG_KEY_ENCRYPTION_ALGORITHM;

	public static final String CONFIG_KEY_ENCRYPTION_KEYLENGTH = JournalEncryptorChain.CONFIG_KEY_ENCRYPTION_KEYLENGTH;

	public static final String CONFIG_ENABLED_PROCESSORS = JournalEncryptorChain.CONFIG_ENABLED_PROCESSORS;

	// aead

	public static final String CONFIG_AEAD_ENCRYPTION_ALGORITHM = AEADProccessor.CONFIG_ENCRYPTION_ALGORITHM;

	public static final String CONFIG_AEAD_ENCRYPTION_KEYLENGTH = AEADProccessor.CONFIG_ENCRYPTION_KEYLENGTH;

	public static final String CONFIG_AEAD_ENCRYPTION_MODE = AEADProccessor.CONFIG_ENCRYPTION_MODE;

	// compression

	public static final String CONFIG_COMPRESSION = CompressionProccessor.CONFIG_COMPRESSION;

	// hmac
	public static final String CONFIG_HMAC_ALGORITHM = HMACProcessor.MAC_ALGORITHM;

	public static final String CONFIG_HMAC_SIZE = HMACProcessor.MAC_SIZE;

	// stream cipher
	public static final String CONFIG_STREAMCIPHER_KEYLENGTH = StreamCipherProcessor.CIPHER_KEYLENGTH;
	public static final String CONFIG_STREAMCIPHER_ALGORITHM = StreamCipherProcessor.CIPHER_ALGORITHM;

}

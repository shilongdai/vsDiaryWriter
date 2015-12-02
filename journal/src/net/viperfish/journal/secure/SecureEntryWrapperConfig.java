package net.viperfish.journal.secure;

import java.io.ByteArrayOutputStream;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.viperfish.journal.secureAlgs.BCBlockCipherEncryptor;
import net.viperfish.utils.config.ComponentConfig;

public class SecureEntryWrapperConfig extends ComponentConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5227765591860888711L;

	public SecureEntryWrapperConfig() {
		super("secureEntryWrapper");
	}

	@Override
	public Set<String> requiredConfig() {
		return new HashSet<>();
	}

	@Override
	public Set<String> optionalConfig() {
		HashSet<String> result = new HashSet<>();
		result.add("EncryptionMethod");
		result.add("MacMethod");
		return result;
	}

	@Override
	public void fillInDefault() {
		this.setProperty("MacMethod", "HMAC-SHA512");
		this.setProperty("EncryptionMethod", "AES/OFB/PKCS7PADDING");
	}

	@Override
	public Map<String, String> getHelp() {
		Map<String, String> result = new HashMap<String, String>();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		Formatter fmt = new Formatter(buffer);
		fmt.format("Available Algorithms:%n");
		Set<String> supported = new BCBlockCipherEncryptor().getSupported();
		for (String i : supported) {
			fmt.format("%s%n", i);
		}
		fmt.format("supported mode:CBC, CFB, OFB, CTR%n");
		fmt.format("supported padding:ISO10126d2PADDING, ISO7816d4PADDING, PKCS7PADDING, PKCS5PADDING, TBCPADDING, X923PADDING, ZEROBYTEPADDING%n");
		fmt.close();
		String helpEncrypt = buffer.toString();
		result.put("EncryptionMethod", helpEncrypt);
		return result;
	}
}

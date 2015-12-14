package net.viperfish.journal.secure;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.viperfish.journal.secureAlgs.AlgorithmSpec;
import net.viperfish.utils.config.ComponentConfig;

public class SecureEntryWrapperConfig extends ComponentConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5227765591860888711L;

	private Set<String> getEncryptionMethods() {
		return AlgorithmSpec.getSupportedBlockCipher();
	}

	private Set<String> getEncryptionModes() {
		return AlgorithmSpec.getSupportedBlockCipherMode();
	}

	private Set<String> getEncryptionPadding() {
		return AlgorithmSpec.getSupportedBlockCipherPadding();
	}

	private Set<String> getMacOptions() {
		return getAvailableMac();
	}

	private Set<String> getMacAlgOptions() {
		String macMethod = getProperty("MacMethod");
		if (macMethod.equalsIgnoreCase("CMAC")
				|| macMethod.equalsIgnoreCase("CBCMAC")
				|| macMethod.equalsIgnoreCase("CFBMAC")) {
			return AlgorithmSpec.getSupportedBlockCipher();
		} else if (macMethod.equalsIgnoreCase("GMAC")) {
			return AlgorithmSpec.getGmacAlgorithms();
		} else if (macMethod.equalsIgnoreCase("HMAC")) {
			return AlgorithmSpec.getSupportedDigest();
		}
		return new TreeSet<>();
	}

	public SecureEntryWrapperConfig() {
		super("secureEntryWrapper");
	}

	private Set<String> getAvailableMac() {
		Set<String> result = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		result.add("CMAC");
		result.add("GMAC");
		result.add("CBCMAC");
		result.add("CFBMAC");
		result.add("HMAC");
		return result;
	}

	@Override
	public List<String> requiredConfig() {
		List<String> result = new LinkedList<>();
		result.add("EncryptionMethod");
		return result;
	}

	@Override
	public List<String> optionalConfig() {
		List<String> result = new LinkedList<>();
		result.add("MacMethod");
		result.add("MacAlgorithm");
		result.add("EncryptionMode");
		result.add("EncryptionPadding");
		return result;
	}

	@Override
	public void fillInDefault() {
		this.setProperty("MacMethod", "HMAC");
		this.setProperty("EncryptionMethod", "AES/CFB/PKCS7PADDING");
		this.setProperty("EncryptionMode", "CFB");
		this.setProperty("EncryptionPadding", "PKCS7PADDING");
		this.setProperty("MacAlgorithm", "SHA512");
	}

	@Override
	public Set<String> getOptions(String key) {
		if (key.equals("EncryptionMethod")) {
			return getEncryptionMethods();
		} else if (key.equals("MacMethod")) {
			return getMacOptions();
		} else if (key.equals("MacAlgorithm")) {
			return getMacAlgOptions();
		} else if (key.equals("EncryptionMode")) {
			return getEncryptionModes();
		} else if (key.equals("EncryptionPadding")) {
			return getEncryptionPadding();
		} else {
			return new TreeSet<>();
		}
	}

}

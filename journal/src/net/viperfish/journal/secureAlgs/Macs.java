package net.viperfish.journal.secureAlgs;

import java.util.Set;
import java.util.TreeSet;

/**
 * utils for macs
 * 
 * @author sdai
 *
 */
public class Macs {

	private Macs() {
		// TODO Auto-generated constructor stub
	}

	public static Set<String> getSupportedMacType() {
		Set<String> result = new TreeSet<>();
		result.add("CBCMAC");
		result.add("CMAC");
		result.add("CFBMAC");
		result.add("GMAC");
		result.add("HMAC");
		return result;
	}

	public static MacDigester getMac(String macMethod) {
		if (macMethod.equalsIgnoreCase("CBCMAC")) {
			return new CBCMac();
		} else if (macMethod.equalsIgnoreCase("CMAC")) {
			return new CMac();
		} else if (macMethod.equalsIgnoreCase("CFBMAC")) {
			return new CFBMac();
		} else if (macMethod.equalsIgnoreCase("GMAC")) {
			return new GMac();
		} else if (macMethod.equalsIgnoreCase("HMAC")) {
			return new HMac();
		} else {
			return null;
		}
	}

}

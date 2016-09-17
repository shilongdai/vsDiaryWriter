package net.viperfish.journal2.core;

import java.util.Map;

import org.apache.commons.configuration.Configuration;

public interface CryptoInfoGenerator {
	public void generate(Map<String, CryptoInfo> target, Configuration config);
}

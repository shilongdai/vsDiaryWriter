package net.viperfish.utils.config;

import java.util.Properties;

public interface ComponentConfigObserver {
	public void sendNotify(Properties config);
}

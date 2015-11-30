package net.viperfish.journal.framework;

import java.util.Properties;

public abstract class UserInterface extends Subject {
	public abstract void run();

	public abstract void setup();

	public abstract Properties getConfig();

	public abstract void setConfig(Properties p);

}

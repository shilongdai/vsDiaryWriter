package net.viperfish.journal.framework;

public interface Observer<P> {
	public void beNotified(P data);
}

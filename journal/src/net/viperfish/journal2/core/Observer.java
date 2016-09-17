package net.viperfish.journal2.core;

/**
 * the Observer in the observer pattern
 * 
 * @author sdai
 *
 * @param <P>
 *            the type of data to be notified
 */
public interface Observer<P> {
	/**
	 * handles subject push of data
	 * 
	 * @param data
	 *            the data pushed
	 */
	public void beNotified(P data);
}

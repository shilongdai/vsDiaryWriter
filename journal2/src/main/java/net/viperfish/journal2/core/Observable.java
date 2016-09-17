package net.viperfish.journal2.core;

import java.util.LinkedList;
import java.util.List;

/**
 * The subject in the observer pattern
 * 
 * @author sdai
 *
 * @param <P>
 *            the data type to push to observers
 */
public abstract class Observable<P> {
	private List<Observer<P>> observers;

	public Observable() {
		observers = new LinkedList<>();
	}

	/**
	 * adds an observer to this subject
	 * 
	 * @param o
	 *            the observer to add
	 */
	public void addObserver(Observer<P> o) {
		if (o == null) {
			throw new NullPointerException("Observer cannot be null");
		}
		observers.add(o);
	}

	/**
	 * push data to all observers
	 * 
	 * @param data
	 *            the data to push
	 */
	protected void notifyObservers(P data) {
		for (Observer<P> i : observers) {
			i.beNotified(data);
		}
	}
}

package net.viperfish.journal.framework;

import java.util.LinkedList;
import java.util.List;

public abstract class Observable<P> {
	private List<Observer<P>> observers;

	public Observable() {
		observers = new LinkedList<>();
	}

	public void addObserver(Observer<P> o) {
		observers.add(o);
	}

	protected void notifyObservers(P data) {
		for (Observer<P> i : observers) {
			i.beNotified(data);
		}
	}
}

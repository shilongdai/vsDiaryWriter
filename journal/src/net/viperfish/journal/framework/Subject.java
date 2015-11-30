package net.viperfish.journal.framework;

import java.util.LinkedList;
import java.util.List;

public abstract class Subject {

	private List<Observer> observers;

	public Subject() {
		observers = new LinkedList<Observer>();
	}

	public void addObserver(Observer o) {
		observers.add(o);
	}

	protected void notifyObservers() {
		for (Observer i : observers) {
			i.notifyObserver();
		}
	}

}

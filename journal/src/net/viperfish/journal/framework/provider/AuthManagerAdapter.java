package net.viperfish.journal.framework.provider;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.Observable;
import net.viperfish.journal.framework.Observer;
import net.viperfish.journal.framework.errors.CannotClearPasswordException;
import net.viperfish.journal.framework.errors.FailToLoadCredentialException;
import net.viperfish.journal.framework.errors.FailToStoreCredentialException;

/**
 * An adapter to a regular AuthenticationManager that pushes data to subscriber
 * observers when a password is set
 * 
 * @author sdai
 *
 */
final class AuthManagerAdapter extends Observable<String> implements AuthenticationManager {
	private AuthenticationManager mger;

	public AuthenticationManager getMger() {
		return mger;
	}

	public void setMger(AuthenticationManager mger) {
		this.mger = mger;
	}

	@Override
	public void clear() throws CannotClearPasswordException {
		mger.clear();
	}

	@Override
	public void setPassword(String pass) throws FailToStoreCredentialException {
		mger.setPassword(pass);
		this.notifyObservers(pass);
	}

	@Override
	public void load() throws FailToLoadCredentialException {
		mger.load();
	}

	@Override
	public String getPassword() {
		return mger.getPassword();
	}

	@Override
	public boolean verify(String string) {
		return mger.verify(string);
	}

	@Override
	public void addObserver(Observer<String> o) {
		super.addObserver(o);
		String password = getPassword();
		if (password != null) {
			o.beNotified(password);
		}
	}

	public void pushPassword() {
		this.notifyObservers(mger.getPassword());
	}

}

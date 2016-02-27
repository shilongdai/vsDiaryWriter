package net.viperfish.journal.framework;

public class AuthManagerAdapter extends Observable<String> implements AuthenticationManager {
	private AuthenticationManager mger;

	public AuthenticationManager getMger() {
		return mger;
	}

	public void setMger(AuthenticationManager mger) {
		this.mger = mger;
	}

	@Override
	public void clear() {
		mger.clear();
	}

	@Override
	public void setPassword(String pass) {
		mger.setPassword(pass);
		this.notifyObservers(pass);
	}

	@Override
	public void reload() {
		mger.reload();
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
	public boolean isPasswordSet() {
		return mger.isPasswordSet();
	}

	public void pushPassword() {
		this.notifyObservers(mger.getPassword());
	}

}

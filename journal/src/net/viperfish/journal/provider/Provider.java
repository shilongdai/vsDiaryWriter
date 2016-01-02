package net.viperfish.journal.provider;

public interface Provider<T> {
	public void setDefaultInstance(String instance);

	public String getDefaultInstance();

	public T newInstance();

	public T getInstance();

	public T newInstance(String instance);

	public T getInstance(String instance);

	public String[] getSupported();

	public String getName();

	public void dispose();

}

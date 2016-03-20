package net.viperfish.journal.framework.provider;

/**
 * a provider that provides a type of service
 * 
 * @author sdai
 *
 * @param <T>
 *            the type of service
 */
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

	public void delete();

	public void refresh();

	public void initDefaults();

	public void registerConfig();

}

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
	/**
	 * set the default type of the instance to create/get if
	 * {@link Provider#newInstance()} or {@link Provider#getInstance()} is
	 * called
	 * 
	 * @param instance
	 *            the type of instance
	 */
	public void setDefaultInstance(String instance);

	/**
	 * get the type of default instance
	 * 
	 * @return the type of default instance
	 */
	public String getDefaultInstance();

	/**
	 * create a new instance with the type of default instance
	 * 
	 * @return the new instance
	 */
	public T newInstance();

	/**
	 * get a instance with the type of default instance
	 * 
	 * @return the instance
	 */
	public T getInstance();

	/**
	 * create a new instance of the specified type
	 * 
	 * @param instance
	 *            the new instance type
	 * @return the created instance
	 */
	public T newInstance(String instance);

	/**
	 * get a instance of the specified type
	 * 
	 * @param instance
	 *            the instance type
	 * @return the instance
	 */
	public T getInstance(String instance);

	/**
	 * get supported types provided by this provider
	 * 
	 * @return supported types
	 */
	public String[] getSupported();

	/**
	 * get the name of this provider
	 * 
	 * @return the name of the provider
	 */
	public String getName();

	/**
	 * clean up resources used by this provider
	 */
	public void dispose();

	/**
	 * delete all the resources used by this provider
	 */
	public void delete();

	/**
	 * refresh this provider. This should be sensitive to configuration change
	 */
	public void refresh();

	/**
	 * set default configurations
	 */
	public void initDefaults();

	/**
	 * register the configuration unit to the application so that user can
	 * configure it in the GUI
	 */
	public void registerConfig();

}

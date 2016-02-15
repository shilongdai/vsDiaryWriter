package net.viperfish.journal.framework;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public enum AuthManagers {
	INSTANCE;
	private Map<String, Provider<AuthenticationManager>> authProviders;
	private String defaultAuthProvider;

	private AuthManagers() {
		authProviders = new HashMap<>();
		defaultAuthProvider = "viperfish";
	}

	/**
	 * register an authentication provider
	 * 
	 * @param p
	 *            the provider to register
	 */
	public void registerAuthProvider(Provider<AuthenticationManager> p) {
		authProviders.put(p.getName(), p);
	}

	/**
	 * get all the providers
	 * 
	 * @return all of the providers
	 */
	public Map<String, Provider<AuthenticationManager>> getAuthProviders() {
		return authProviders;
	}

	/**
	 * get the name of the default authentication provider
	 * 
	 * @return
	 */
	public String getDefaultAuthProvider() {
		return defaultAuthProvider;
	}

	/**
	 * set the name of the default authentication provider
	 * 
	 * @param defaultAuthProvider
	 */
	public void setDefaultAuthProvider(String defaultAuthProvider) {
		this.defaultAuthProvider = defaultAuthProvider;
	}

	/**
	 * create a new Authentication Manager that is specified in the
	 * configuration
	 * 
	 * @return a new Authentication Manager
	 */
	public AuthenticationManager newAuthManager() {
		return newAuthManager(Configuration.getString(ConfigMapping.AUTH_COMPONENT));
	}

	/**
	 * create a new Authentication Manager based on the name parameter
	 * 
	 * @param instance
	 *            the type of the Authentication Manager
	 * @return the new instance
	 */
	public AuthenticationManager newAuthManager(String instance) {
		AuthenticationManager am = authProviders.get(defaultAuthProvider).newInstance();
		if (am != null) {
			return am;
		}
		for (Entry<String, Provider<AuthenticationManager>> i : authProviders.entrySet()) {
			AuthenticationManager result = i.getValue().newInstance(instance);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * create a new Authentication Manager from a specified provider with a
	 * specified name
	 * 
	 * @param provider
	 *            the chosen provider
	 * @param instance
	 *            the type of Authentication Manager
	 * @return the new Authentication Manager
	 */
	public AuthenticationManager newAuthManager(String provider, String instance) {
		return authProviders.get(provider).newInstance(instance);
	}

	/**
	 * get an Authentication Manager specified in the configuration
	 * 
	 * @return the authentication manager
	 */
	public AuthenticationManager getAuthManager() {
		return getAuthManager(Configuration.getString(ConfigMapping.AUTH_COMPONENT));
	}

	/**
	 * get an authentication manager of the specified type
	 * 
	 * @param instance
	 *            the type of authentication manager
	 * @return the authentication manager
	 */
	public AuthenticationManager getAuthManager(String instance) {
		AuthenticationManager am = authProviders.get(defaultAuthProvider).getInstance(instance);
		if (am != null) {
			return am;
		}
		for (Entry<String, Provider<AuthenticationManager>> i : authProviders.entrySet()) {
			AuthenticationManager result = i.getValue().getInstance(instance);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * get an authentication manager from a provider, of a specific type
	 * 
	 * @param provider
	 *            the provider
	 * @param instance
	 *            the type of authentication manager
	 * @return the authentication manager
	 */
	public AuthenticationManager getAuthManager(String provider, String instance) {
		return authProviders.get(provider).getInstance(instance);
	}

	/**
	 * clean up
	 */
	public void dispose() {
		for (Entry<String, Provider<AuthenticationManager>> i : authProviders.entrySet()) {
			i.getValue().dispose();
			System.err.println("disposed " + i.getKey());
		}
		authProviders.clear();
		System.err.println("disposed auth providers");
	}
}

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

	public void registerAuthProvider(Provider<AuthenticationManager> p) {
		authProviders.put(p.getName(), p);
	}

	public Map<String, Provider<AuthenticationManager>> getAuthProviders() {
		return authProviders;
	}

	public String getDefaultAuthProvider() {
		return defaultAuthProvider;
	}

	public void setDefaultAuthProvider(String defaultAuthProvider) {
		this.defaultAuthProvider = defaultAuthProvider;
	}

	public AuthenticationManager newAuthManager() {
		return newAuthManager(Configuration.getString(ConfigMapping.AUTH_COMPONENT));
	}

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

	public AuthenticationManager newAuthManager(String provider, String instance) {
		return authProviders.get(provider).newInstance(instance);
	}

	public AuthenticationManager getAuthManager() {
		return getAuthManager(Configuration.getString(ConfigMapping.AUTH_COMPONENT));
	}

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

	public AuthenticationManager getAuthManager(String provider, String instance) {
		return authProviders.get(provider).getInstance(instance);
	}

	public void dispose() {
		for (Entry<String, Provider<AuthenticationManager>> i : authProviders.entrySet()) {
			i.getValue().dispose();
			System.err.println("disposed " + i.getKey());
		}
		authProviders.clear();
		System.err.println("disposed auth providers");
	}
}

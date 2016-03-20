package net.viperfish.journal.authProvider;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.preference.PreferenceNode;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.provider.PreferenceGUIManager;
import net.viperfish.journal.framework.provider.Provider;
import net.viperfish.utils.file.CommonFunctions;

public class ViperfishAuthProvider implements Provider<AuthenticationManager> {

	private File dataDir;
	private Map<String, Class<? extends AuthenticationManager>> authters;
	private Map<String, AuthenticationManager> cache;
	private String defaultInstance;

	public ViperfishAuthProvider() {
		if (Configuration.containsKey(ConfigMapping.PORTABLE) && Configuration.getBoolean(ConfigMapping.PORTABLE)) {
			dataDir = new File("secure");
		} else {
			File homeDir = new File(System.getProperty("user.home"));
			File vDiaryDir = new File(homeDir, ".vsDiary");
			CommonFunctions.initDir(vDiaryDir);
			dataDir = new File(vDiaryDir, "secure");
		}
		CommonFunctions.initDir(dataDir);

		authters = new HashMap<>();
		cache = new HashMap<>();
		initServices();
	}

	private void initServices() {
		authters.put("Hash", HashAuthManager.class);
		authters.put("Unix-Style", UnixLikeAuthManager.class);
	}

	@Override
	public AuthenticationManager newInstance() {
		return newInstance(getDefaultInstance());
	}

	@Override
	public AuthenticationManager getInstance() {
		return getInstance(getDefaultInstance());
	}

	@Override
	public AuthenticationManager newInstance(String instance) {
		Class<? extends AuthenticationManager> c = authters.get(instance);
		if (c == null) {
			return null;
		}
		try {
			Constructor<? extends AuthenticationManager> ctor = c.getConstructor(File.class);
			AuthenticationManager result = ctor.newInstance(dataDir);
			result.reload();
			return result;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public AuthenticationManager getInstance(String instance) {
		Class<? extends AuthenticationManager> c = authters.get(instance);
		if (c == null) {
			return null;
		}
		AuthenticationManager cached = cache.get(instance);
		if (cached != null) {
			return cached;
		}
		try {
			Constructor<? extends AuthenticationManager> ctor = c.getConstructor(File.class);
			AuthenticationManager result = ctor.newInstance(dataDir);
			result.reload();
			cache.put(instance, result);
			return result;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String[] getSupported() {
		List<String> result = new LinkedList<>();
		for (Entry<String, Class<? extends AuthenticationManager>> i : authters.entrySet()) {
			result.add(i.getKey());
		}
		return result.toArray(new String[0]);
	}

	@Override
	public String getName() {
		return "viperfish";
	}

	@Override
	public void dispose() {

	}

	@Override
	public void setDefaultInstance(String instance) {
		this.defaultInstance = instance;
	}

	@Override
	public String getDefaultInstance() {
		return defaultInstance;
	}

	@Override
	public void delete() {
		CommonFunctions.delete(dataDir);

	}

	@Override
	public void refresh() {
		cache.clear();

	}

	@Override
	public void initDefaults() {
		Configuration.setProperty(HashAuthManager.HASH_ALG, "SHA256");
		Configuration.setProperty(UnixLikeAuthManager.ENCRYPTION_ALG, "DESede");
		Configuration.setProperty(UnixLikeAuthManager.KDF_HASH, "SHA256");

	}

	@Override
	public void registerConfig() {
		PreferenceNode hash = new PreferenceNode("hashAuth", "Hash Authentication", null,
				HashAuthPreferencePage.class.getCanonicalName());
		PreferenceNode unix = new PreferenceNode("unixLikeAuth", "Unix Like Authentication", null,
				UnixLikeAuthPreferencePage.class.getCanonicalName());
		PreferenceGUIManager.addToRoot(hash);
		PreferenceGUIManager.addToRoot(unix);
	}

}

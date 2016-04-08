package net.viperfish.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public final class TypeCache {

	private Map<Class<?>, Object> cache;

	public TypeCache() {
		cache = new HashMap<>();
	}

	public <T> T getObject(Class<T> c, Object... params) throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object buffer = cache.get(c);
		if (buffer == null) {
			Class<?>[] paramTypes = new Class<?>[params.length];
			for (int i = 0; i < params.length; ++i) {
				paramTypes[i] = params[i].getClass();
			}
			Constructor<T> ctor = c.getConstructor(paramTypes);
			T result = ctor.newInstance(params);
			cache.put(c, result);
			return result;
		} else {
			T result = c.cast(buffer);
			return result;
		}
	}

	public <T> T getObject(Class<T> c) {
		Object buffer = cache.get(c);
		if (buffer == null) {
			return null;
		} else {
			return c.cast(buffer);
		}
	}

	public <T> void putObject(T obj) {
		cache.put(obj.getClass(), obj);
	}

	public void clear() {
		cache.clear();
	}

}

package net.viperfish.journal2.core;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationDecoder;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.event.EventType;
import org.apache.commons.configuration2.ex.ConfigurationException;

public final class JournalConfiguration {
	private static AbstractConfiguration configuration;
	private static FileBasedConfigurationBuilder<PropertiesConfiguration> configBuilder;

	static {
		configuration = new PropertiesConfiguration();
	}

	static void load(String fileName) throws ConfigurationException {
		configBuilder = new Configurations().propertiesBuilder(fileName);
		configuration = configBuilder.getConfiguration();
	}

	static void save() throws ConfigurationException {
		configBuilder.save();
	}

	static public boolean isEmpty() {
		return configuration.isEmpty();
	}

	static public int size() {
		return configuration.size();
	}

	static public Configuration subset(String prefix) {
		return configuration.subset(prefix);
	}

	static public boolean containsKey(String key) {
		return configuration.containsKey(key);
	}

	static public Object getProperty(String key) {
		return configuration.getProperty(key);
	}

	static public void addProperty(String key, Object value) {
		configuration.addProperty(key, value);
	}

	static public Iterator<String> getKeys(String prefix) {
		return configuration.getKeys(prefix);
	}

	static public void clearProperty(String key) {
		configuration.clearProperty(key);
	}

	static public void clear() {
		configuration.clear();
	}

	static public Iterator<String> getKeys() {
		return configuration.getKeys();
	}

	static public Properties getProperties(String key) {
		return configuration.getProperties(key);
	}

	static public boolean getBoolean(String key) {
		return configuration.getBoolean(key);
	}

	static public boolean getBoolean(String key, boolean defaultValue) {
		return configuration.getBoolean(key, defaultValue);
	}

	static public Boolean getBoolean(String key, Boolean defaultValue) {
		return configuration.getBoolean(key, defaultValue);
	}

	static public byte getByte(String key) {
		return configuration.getByte(key);
	}

	static public byte getByte(String key, byte defaultValue) {
		return configuration.getByte(key, defaultValue);
	}

	static public Byte getByte(String key, Byte defaultValue) {
		return configuration.getByte(key, defaultValue);
	}

	static public double getDouble(String key) {
		return configuration.getDouble(key);
	}

	static public double getDouble(String key, double defaultValue) {
		return configuration.getDouble(key, defaultValue);
	}

	static public Double getDouble(String key, Double defaultValue) {
		return configuration.getDouble(key, defaultValue);
	}

	static public float getFloat(String key) {
		return configuration.getFloat(key);
	}

	static public float getFloat(String key, float defaultValue) {
		return configuration.getFloat(key, defaultValue);
	}

	static public Float getFloat(String key, Float defaultValue) {
		return configuration.getFloat(key, defaultValue);
	}

	static public int getInt(String key) {
		return configuration.getInt(key);
	}

	static public int getInt(String key, int defaultValue) {
		return configuration.getInt(key, defaultValue);
	}

	static public Integer getInteger(String key, Integer defaultValue) {
		return configuration.getInteger(key, defaultValue);
	}

	static public long getLong(String key) {
		return configuration.getLong(key);
	}

	static public long getLong(String key, long defaultValue) {
		return configuration.getLong(key, defaultValue);
	}

	static public Long getLong(String key, Long defaultValue) {
		return configuration.getLong(key, defaultValue);
	}

	static public short getShort(String key) {
		return configuration.getShort(key);
	}

	static public short getShort(String key, short defaultValue) {
		return configuration.getShort(key, defaultValue);
	}

	static public Short getShort(String key, Short defaultValue) {
		return configuration.getShort(key, defaultValue);
	}

	static public BigDecimal getBigDecimal(String key) {
		return configuration.getBigDecimal(key);
	}

	static public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
		return configuration.getBigDecimal(key, defaultValue);
	}

	static public BigInteger getBigInteger(String key) {
		return configuration.getBigInteger(key);
	}

	static public BigInteger getBigInteger(String key, BigInteger defaultValue) {
		return configuration.getBigInteger(key, defaultValue);
	}

	static public String getString(String key) {
		return configuration.getString(key);
	}

	static public String getString(String key, String defaultValue) {
		return configuration.getString(key, defaultValue);
	}

	static public String getEncodedString(String key, ConfigurationDecoder decoder) {
		return configuration.getEncodedString(key, decoder);
	}

	static public String getEncodedString(String key) {
		return configuration.getEncodedString(key);
	}

	static public String[] getStringArray(String key) {
		return configuration.getStringArray(key);
	}

	static public List<Object> getList(String key) {
		return configuration.getList(key);
	}

	static public List<Object> getList(String key, List<?> defaultValue) {
		return configuration.getList(key, defaultValue);
	}

	static public <T> T get(Class<T> cls, String key) {
		return configuration.get(cls, key);
	}

	static public <T> T get(Class<T> cls, String key, T defaultValue) {
		return configuration.get(cls, key, defaultValue);
	}

	static public Object getArray(Class<?> cls, String key) {
		return configuration.getArray(cls, key);
	}

	static public <T> List<T> getList(Class<T> cls, String key) {
		return configuration.getList(cls, key);
	}

	static public <T> List<T> getList(Class<T> cls, String key, List<T> defaultValue) {
		return configuration.getList(cls, key, defaultValue);
	}

	static public <T> Collection<T> getCollection(Class<T> cls, String key, Collection<T> target) {
		return configuration.getCollection(cls, key, target);
	}

	static public <T> Collection<T> getCollection(Class<T> cls, String key, Collection<T> target,
			Collection<T> defaultValue) {
		return configuration.getCollection(cls, key, target, defaultValue);
	}

	static public ImmutableConfiguration immutableSubset(String prefix) {
		return configuration.immutableSubset(prefix);
	}

	static public <T extends Event> void addEventListener(EventType<T> eventType, EventListener<? super T> listener) {
		configuration.addEventListener(eventType, listener);
	}

	public static final void setProperty(String key, Object value) {
		configuration.setProperty(key, value);
	}

	private JournalConfiguration() {

	}

}

package net.viperfish.journal.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.ReloadingStrategy;

import net.viperfish.utils.file.CommonFunctions;

/**
 * a static wrapper class around the Apache common configuration class
 * 
 * @author sdai
 *
 */
public class Configuration {
	private static FileConfiguration conf;

	public static final String confFile = "config.properties";

	static {
		conf = new PropertiesConfiguration();
		conf.setFileName(confFile);
		try {
			CommonFunctions.initFile(new File(confFile));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Configuration() {

	}

	public static void addProperty(String arg0, Object arg1) {
		conf.addProperty(arg0, arg1);
	}

	public static void clear() {
		conf.clear();
	}

	public static void clearProperty(String arg0) {
		conf.clearProperty(arg0);
	}

	public static boolean containsKey(String arg0) {
		return conf.containsKey(arg0);
	}

	public static String getBasePath() {
		return conf.getBasePath();
	}

	public static BigDecimal getBigDecimal(String arg0, BigDecimal arg1) {
		return conf.getBigDecimal(arg0, arg1);
	}

	public static BigDecimal getBigDecimal(String arg0) {
		return conf.getBigDecimal(arg0);
	}

	public static BigInteger getBigInteger(String arg0, BigInteger arg1) {
		return conf.getBigInteger(arg0, arg1);
	}

	public static BigInteger getBigInteger(String arg0) {
		return conf.getBigInteger(arg0);
	}

	public static boolean getBoolean(String arg0, boolean arg1) {
		return conf.getBoolean(arg0, arg1);
	}

	public static Boolean getBoolean(String arg0, Boolean arg1) {
		return conf.getBoolean(arg0, arg1);
	}

	public static boolean getBoolean(String arg0) {
		return conf.getBoolean(arg0);
	}

	public static byte getByte(String arg0, byte arg1) {
		return conf.getByte(arg0, arg1);
	}

	public static Byte getByte(String arg0, Byte arg1) {
		return conf.getByte(arg0, arg1);
	}

	public static byte getByte(String arg0) {
		return conf.getByte(arg0);
	}

	public static double getDouble(String arg0, double arg1) {
		return conf.getDouble(arg0, arg1);
	}

	public static Double getDouble(String arg0, Double arg1) {
		return conf.getDouble(arg0, arg1);
	}

	public static double getDouble(String arg0) {
		return conf.getDouble(arg0);
	}

	public static String getEncoding() {
		return conf.getEncoding();
	}

	public static File getFile() {
		return conf.getFile();
	}

	public static String getFileName() {
		return conf.getFileName();
	}

	public static float getFloat(String arg0, float arg1) {
		return conf.getFloat(arg0, arg1);
	}

	public static Float getFloat(String arg0, Float arg1) {
		return conf.getFloat(arg0, arg1);
	}

	public static float getFloat(String arg0) {
		return conf.getFloat(arg0);
	}

	public static int getInt(String arg0, int arg1) {
		return conf.getInt(arg0, arg1);
	}

	public static int getInt(String arg0) {
		return conf.getInt(arg0);
	}

	public static Integer getInteger(String arg0, Integer arg1) {
		return conf.getInteger(arg0, arg1);
	}

	public static Iterator<String> getKeys() {
		return conf.getKeys();
	}

	public static Iterator<String> getKeys(String arg0) {
		return conf.getKeys(arg0);
	}

	public static List<Object> getList(String arg0, List<?> arg1) {
		return conf.getList(arg0, arg1);
	}

	public static List<Object> getList(String arg0) {
		return conf.getList(arg0);
	}

	public static long getLong(String arg0, long arg1) {
		return conf.getLong(arg0, arg1);
	}

	public static Long getLong(String arg0, Long arg1) {
		return conf.getLong(arg0, arg1);
	}

	public static long getLong(String arg0) {
		return conf.getLong(arg0);
	}

	public static Properties getProperties(String arg0) {
		return conf.getProperties(arg0);
	}

	public static Object getProperty(String arg0) {
		return conf.getProperty(arg0);
	}

	public static ReloadingStrategy getReloadingStrategy() {
		return conf.getReloadingStrategy();
	}

	public static short getShort(String arg0, short arg1) {
		return conf.getShort(arg0, arg1);
	}

	public static Short getShort(String arg0, Short arg1) {
		return conf.getShort(arg0, arg1);
	}

	public static short getShort(String arg0) {
		return conf.getShort(arg0);
	}

	public static String getString(String arg0, String arg1) {
		return conf.getString(arg0, arg1);
	}

	public static String getString(String arg0) {
		return conf.getString(arg0);
	}

	public static String[] getStringArray(String arg0) {
		return conf.getStringArray(arg0);
	}

	public static URL getURL() {
		return conf.getURL();
	}

	public static boolean isAutoSave() {
		return conf.isAutoSave();
	}

	public static boolean isEmpty() {
		return conf.isEmpty();
	}

	public static void load() throws ConfigurationException {
		conf.load();
	}

	public static void load(File arg0) throws ConfigurationException {
		conf.load(arg0);
	}

	public static void load(InputStream arg0, String arg1) throws ConfigurationException {
		conf.load(arg0, arg1);
	}

	public static void load(InputStream arg0) throws ConfigurationException {
		conf.load(arg0);
	}

	public static void load(Reader arg0) throws ConfigurationException {
		conf.load(arg0);
	}

	public static void load(String arg0) throws ConfigurationException {
		conf.load(arg0);
	}

	public static void load(URL arg0) throws ConfigurationException {
		conf.load(arg0);
	}

	public static void reload() {
		conf.reload();
	}

	public static void save() throws ConfigurationException {
		conf.save();
	}

	public static void save(File arg0) throws ConfigurationException {
		conf.save(arg0);
	}

	public static void save(OutputStream arg0, String arg1) throws ConfigurationException {
		conf.save(arg0, arg1);
	}

	public static void save(OutputStream arg0) throws ConfigurationException {
		conf.save(arg0);
	}

	public static void save(String arg0) throws ConfigurationException {
		conf.save(arg0);
	}

	public static void save(URL arg0) throws ConfigurationException {
		conf.save(arg0);
	}

	public static void save(Writer arg0) throws ConfigurationException {
		conf.save(arg0);
	}

	public static void setAutoSave(boolean arg0) {
		conf.setAutoSave(arg0);
	}

	public static void setBasePath(String arg0) {
		conf.setBasePath(arg0);
	}

	public static void setEncoding(String arg0) {
		conf.setEncoding(arg0);
	}

	public static void setFile(File arg0) {
		conf.setFile(arg0);
	}

	public static void setFileName(String arg0) {
		conf.setFileName(arg0);
	}

	public static void setProperty(String arg0, Object arg1) {
		conf.setProperty(arg0, arg1);
	}

	public static void setReloadingStrategy(ReloadingStrategy arg0) {
		conf.setReloadingStrategy(arg0);
	}

	public static void setURL(URL arg0) {
		conf.setURL(arg0);
	}

	public static org.apache.commons.configuration.Configuration subset(String arg0) {
		return conf.subset(arg0);
	}

}

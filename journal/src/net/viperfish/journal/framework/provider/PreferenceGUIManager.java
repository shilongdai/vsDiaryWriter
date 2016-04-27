package net.viperfish.journal.framework.provider;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jface.preference.PreferenceManager;

/**
 * static adapter for PreferenceManager
 * 
 * @author sdai
 *
 */
public final class PreferenceGUIManager {

	private static List<ConfigurationGUISetup> runners;

	static {
		runners = new LinkedList<>();
	}

	private PreferenceGUIManager() {

	}

	public static boolean add(ConfigurationGUISetup arg0) {
		return runners.add(arg0);
	}

	public static void add(int arg0, ConfigurationGUISetup arg1) {
		runners.add(arg0, arg1);
	}

	public static boolean addAll(Collection<? extends ConfigurationGUISetup> arg0) {
		return runners.addAll(arg0);
	}

	public static boolean addAll(int arg0, Collection<? extends ConfigurationGUISetup> arg1) {
		return runners.addAll(arg0, arg1);
	}

	public static void clear() {
		runners.clear();
	}

	public static boolean contains(Object arg0) {
		return runners.contains(arg0);
	}

	public static boolean containsAll(Collection<?> arg0) {
		return runners.containsAll(arg0);
	}

	public static ConfigurationGUISetup get(int arg0) {
		return runners.get(arg0);
	}

	public static int indexOf(Object arg0) {
		return runners.indexOf(arg0);
	}

	public static boolean isEmpty() {
		return runners.isEmpty();
	}

	public static Iterator<ConfigurationGUISetup> iterator() {
		return runners.iterator();
	}

	public static int lastIndexOf(Object arg0) {
		return runners.lastIndexOf(arg0);
	}

	public static ListIterator<ConfigurationGUISetup> listIterator() {
		return runners.listIterator();
	}

	public static ListIterator<ConfigurationGUISetup> listIterator(int arg0) {
		return runners.listIterator(arg0);
	}

	public static ConfigurationGUISetup remove(int arg0) {
		return runners.remove(arg0);
	}

	public static boolean remove(Object arg0) {
		return runners.remove(arg0);
	}

	public static boolean removeAll(Collection<?> arg0) {
		return runners.removeAll(arg0);
	}

	public static boolean retainAll(Collection<?> arg0) {
		return runners.retainAll(arg0);
	}

	public static ConfigurationGUISetup set(int arg0, ConfigurationGUISetup arg1) {
		return runners.set(arg0, arg1);
	}

	public static int size() {
		return runners.size();
	}

	public static List<ConfigurationGUISetup> subList(int arg0, int arg1) {
		return runners.subList(arg0, arg1);
	}

	public static Object[] toArray() {
		return runners.toArray();
	}

	public static <T> T[] toArray(T[] arg0) {
		return runners.toArray(arg0);
	}

	public static PreferenceManager getMger() {
		PreferenceManager mger = new PreferenceManager();
		for (ConfigurationGUISetup i : runners) {
			i.proccess(mger);
		}
		return mger;
	}

}

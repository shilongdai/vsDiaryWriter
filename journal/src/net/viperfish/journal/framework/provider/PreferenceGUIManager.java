package net.viperfish.journal.framework.provider;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceManager;

public final class PreferenceGUIManager {
	private static PreferenceManager mger;

	static {
		mger = new PreferenceManager();
	}

	private PreferenceGUIManager() {

	}

	public static boolean addTo(String path, IPreferenceNode node) {
		return mger.addTo(path, node);
	}

	public static void addToRoot(IPreferenceNode node) {
		mger.addToRoot(node);
	}

	public static IPreferenceNode find(String path) {
		return mger.find(path);
	}

	public static List<IPreferenceNode> getElements(int order) {
		return mger.getElements(order);
	}

	public static final IPreferenceNode[] getRootSubNodes() {
		return mger.getRootSubNodes();
	}

	public static boolean remove(IPreferenceNode node) {
		return mger.remove(node);
	}

	public static IPreferenceNode remove(String path) {
		return mger.remove(path);
	}

	public static void removeAll() {
		mger.removeAll();
	}

	public static PreferenceManager getMger() {
		return mger;
	}

}

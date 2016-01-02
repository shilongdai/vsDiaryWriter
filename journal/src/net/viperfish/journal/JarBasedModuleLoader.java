package net.viperfish.journal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.provider.ModuleLoader;
import net.viperfish.journal.provider.Provider;
import net.viperfish.utils.index.Indexer;

public class JarBasedModuleLoader implements ModuleLoader {

	private Collection<?> loadIndividual(String path) {
		try (JarFile jarFile = new JarFile(path)) {
			List<Provider<?>> result = new LinkedList<>();
			Enumeration<JarEntry> e = jarFile.entries();
			URL[] urls = { new URL("jar:file:" + path + "!/") };
			URLClassLoader cl = URLClassLoader.newInstance(urls);

			while (e.hasMoreElements()) {
				JarEntry je = e.nextElement();
				if (je.isDirectory() || !je.getName().endsWith(".class")) {
					continue;
				}
				String className = je.getName().substring(0, je.getName().length() - 6);
				className = className.replace('/', '.');
				Class<?> c = cl.loadClass(className);
				if (isUsable(c)) {
					if (c.isInstance(Provider.class)) {
						try {
							result.add((Provider<?>) c.newInstance());
						} catch (InstantiationException | IllegalAccessException e1) {
							continue;
						}
					}
				}
			}
			return result;
		} catch (IOException | ClassNotFoundException e1) {
			return new LinkedList<>();
		}
	}

	public boolean isUsable(Class<?> toTest) {
		if (Modifier.isAbstract(toTest.getModifiers())) {
			return false;
		}
		if (Modifier.isInterface(toTest.getModifiers())) {
			return false;
		}
		if (Modifier.isPrivate(toTest.getModifiers())) {
			return false;
		}
		if (Modifier.isProtected(toTest.getModifiers())) {
			return false;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Provider<EntryDatabase>> loadDatabaseProvider(File baseDir) {
		List<Provider<EntryDatabase>> result = new LinkedList<>();
		if (baseDir.isDirectory()) {
			for (File i : baseDir.listFiles()) {
				try {
					result.addAll((Collection<? extends Provider<EntryDatabase>>) loadIndividual(i.getCanonicalPath()));
				} catch (IOException e) {
					continue;
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Provider<AuthenticationManager>> loadAuthProvider(File baseDir) {
		List<Provider<AuthenticationManager>> result = new LinkedList<>();
		if (baseDir.isDirectory()) {
			for (File i : baseDir.listFiles()) {
				try {
					result.addAll((Collection<? extends Provider<AuthenticationManager>>) loadIndividual(
							i.getCanonicalPath()));
				} catch (IOException e) {
					continue;
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Provider<JournalTransformer>> loadTransformerProvider(File baseDir) {
		List<Provider<JournalTransformer>> result = new LinkedList<>();
		if (baseDir.isDirectory()) {
			for (File i : baseDir.listFiles()) {
				try {
					result.addAll(
							(Collection<? extends Provider<JournalTransformer>>) loadIndividual(i.getCanonicalPath()));
				} catch (IOException e) {
					continue;
				}
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Provider<Indexer<Journal>>> loadIndexer(File baseDir) {
		List<Provider<Indexer<Journal>>> result = new LinkedList<>();
		if (baseDir.isDirectory()) {
			for (File i : baseDir.listFiles()) {
				try {
					result.addAll(
							(Collection<? extends Provider<Indexer<Journal>>>) loadIndividual(i.getCanonicalPath()));
				} catch (IOException e) {
					continue;
				}
			}
		}
		return result;
	}

}

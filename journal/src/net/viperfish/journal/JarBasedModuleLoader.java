package net.viperfish.journal;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import net.viperfish.framework.index.Indexer;
import net.viperfish.journal.framework.AuthenticationManager;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.ModuleLoader;
import net.viperfish.journal.framework.provider.AuthManagers;
import net.viperfish.journal.framework.provider.EntryDatabases;
import net.viperfish.journal.framework.provider.Indexers;
import net.viperfish.journal.framework.provider.JournalTransformers;
import net.viperfish.journal.framework.provider.Provider;

/**
 * a module loader that loads providers from jar files
 * 
 * This class loads provider from jar files in a specified directory. The
 * manifest of the jar file must contain a provider-class and a provider-type
 * attribute. The provider-class attribute should be the full name of the meta
 * provider in the jar file. The provider-type can be
 * <ul>
 * <li><i>database</i>: if the provider provides {@link EntryDatabase}</li>
 * <i>indexer</i>: if the provider provides {@link Indexer}
 * <li><i>transformer</i>: if the provider provides {@link JournalTransformer}
 * <li><i>auth</i>: if the provider provides {@link AuthenticationManager}
 * <li>
 * </ul>
 * If the jar file fails to load or contains invalid information, it will be
 * skipped.
 *
 * 
 * @author sdai
 *
 */
final class JarBasedModuleLoader implements ModuleLoader {

	/**
	 * checks if the class being tested can be created and used.
	 * 
	 * This method tests whether a class is a concrete public class that can be
	 * successfully created and used by this application
	 * 
	 * @param toTest
	 *            the class to test
	 * @return true if usable, false if unsable
	 */
	private boolean isUsable(Class<?> toTest) {
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
	public void loadModules(File baseDir) {
		for (File i : baseDir.listFiles()) {
			try (JarFile jarFile = new JarFile(i)) {
				Manifest manifest = jarFile.getManifest();
				String provider = manifest.getMainAttributes().getValue("provider-class");
				String type = manifest.getMainAttributes().getValue("provider-type");
				if (provider == null || type == null) {
					continue;
				}
				Enumeration<JarEntry> e = jarFile.entries();
				URL[] urls = { new URL("jar:file:" + i.getAbsolutePath() + "!/") };
				URLClassLoader cl = URLClassLoader.newInstance(urls);
				while (e.hasMoreElements()) {
					JarEntry je = e.nextElement();
					if (je.isDirectory() || !je.getName().endsWith(".class")) {
						continue;
					}
					String className = je.getName().substring(0, je.getName().length() - 6);
					className = className.replace('/', '.');
					if (className.equals(provider)) {
						Class<?> c = cl.loadClass(className);
						if (isUsable(c)) {
							if (Provider.class.isAssignableFrom(c)) {
								try {
									Object o = c.newInstance();
									if (type.equals("database")) {
										EntryDatabases.INSTANCE
												.registerEntryDatabaseProvider((Provider<EntryDatabase>) o);
									} else if (type.equals("indexer")) {
										Indexers.INSTANCE.registerIndexerProvider((Provider<Indexer<Journal>>) o);
									} else if (type.equals("transformer")) {
										JournalTransformers.INSTANCE
												.registerTransformerProvider((Provider<JournalTransformer>) o);
									} else if (type.equals("auth")) {
										AuthManagers.INSTANCE.registerAuthProvider((Provider<AuthenticationManager>) o);
									}
									System.err.println("loading:" + c);
								} catch (Throwable e1) {
									e1.printStackTrace();
									continue;
								}
							}
						}
					}
				}
			} catch (IOException | ClassNotFoundException e1) {
				continue;
			}
		}

	}

}

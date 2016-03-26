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
import net.viperfish.utils.index.Indexer;

final class JarBasedModuleLoader implements ModuleLoader {

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
				String type = manifest.getMainAttributes().getValue("provider-type:");
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

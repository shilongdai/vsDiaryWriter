package net.viperfish.journal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.viperfish.journal.provider.ModuleLoader;

public class JarBasedModuleLoader implements ModuleLoader {

	private void loadIndividual(String path) {
		try (JarFile jarFile = new JarFile(path)) {
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
			}

		} catch (IOException | ClassNotFoundException e1) {
			return;
		}
	}

	@Override
	public void load(File baseDir) {
		if (baseDir.isDirectory()) {
			for (File i : baseDir.listFiles()) {
				try {
					loadIndividual(i.getCanonicalPath());
				} catch (IOException e) {
					continue;
				}
			}
		}
	}

}

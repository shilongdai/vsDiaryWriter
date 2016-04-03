package net.viperfish.journal.archieveDB;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.provider.Provider;
import net.viperfish.utils.file.CommonFunctions;

/**
 * The provider providing a type of {@link EntryDatabase} stored in archive
 * format
 * 
 * This class is a {@link Provider} providing implementations of archive
 * {@link EntryDatabase}. The default implementation is ArArchive. All
 * {@link EntryDatabase} provided will store data in directory <i>./data</i> if
 * <i>system.portable = true</i> in the {@link Configuration}. Otherwise, it
 * will store data in <i>{user.home}/.vsDiary/data</i>. This provider will
 * periodically flushes all used {@link EntryDatabase} with a {@link Timer}. It
 * also caches {@link EntryDatabase} when the instance is requested.
 * 
 * This provider does not have any configuration.
 * 
 * <p>
 * This class is NOT thread safe
 * </p>
 * 
 * @author sdai
 *
 */
public final class ViperfishArchiveDBProvider implements Provider<EntryDatabase> {

	private final Map<String, Class<? extends ArchiveEntryDatabase>> dbs;
	private final Map<String, ArchiveEntryDatabase> concretes;
	private String defaultInstance;
	private File archiveFile;
	private final File dataDir;
	private Timer t;
	private boolean isUsed;

	/**
	 * creates an {@link ViperfishArchiveDBProvider}
	 * 
	 * This constructor creates an {@link ViperfishArchiveDBProvider} and
	 * creates the data directory. It will start a background {@link Timer} for
	 * periodically flushing used {@link EntryDatabase}
	 */
	public ViperfishArchiveDBProvider() {
		dbs = new HashMap<>();
		concretes = new HashMap<>();
		defaultInstance = "ArArchive";
		if (Configuration.containsKey(ConfigMapping.PORTABLE) && Configuration.getBoolean(ConfigMapping.PORTABLE)) {
			dataDir = new File("data");
		} else {
			File homeDir = new File(System.getProperty("user.home"));
			File vDiaryDir = new File(homeDir, ".vsDiary");
			CommonFunctions.initDir(vDiaryDir);
			dataDir = new File(vDiaryDir, "data");
		}
		CommonFunctions.initDir(dataDir);
		archiveFile = null;
		addArchives();
		isUsed = false;
		t = new Timer();
	}

	/**
	 * adds available archives
	 */
	private void addArchives() {
		dbs.put("ArArchive", ArArchiveEntryDatabase.class);
		dbs.put("CpioArchive", CpioArchiveEntryDatabase.class);
		dbs.put("TarArchive", TarArchiveEntryDatabase.class);
		dbs.put("ZipArchive", ZipArchiveEntryDatabase.class);
	}

	/**
	 * flush all {@link EntryDatabase} created
	 */
	private void flushAll() {
		for (Entry<String, ArchiveEntryDatabase> i : concretes.entrySet()) {
			i.getValue().flush();
		}

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
	public EntryDatabase newInstance() {
		return newInstance(defaultInstance);
	}

	@Override
	public EntryDatabase getInstance() {
		return getInstance(defaultInstance);
	}

	@Override
	public EntryDatabase newInstance(String instance) {
		try {
			Class<? extends ArchiveEntryDatabase> cl = dbs.get(instance);
			if (cl == null) {
				return null;
			}
			Constructor<? extends ArchiveEntryDatabase> ctor = cl.getConstructor(File.class);
			archiveFile = new File(dataDir, instance);
			ArchiveEntryDatabase tmp = ctor.newInstance(archiveFile);
			tmp.load();
			return tmp;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public EntryDatabase getInstance(String instance) {
		ArchiveEntryDatabase result = concretes.get(instance);
		if (result == null) {
			try {
				Class<? extends ArchiveEntryDatabase> cl = dbs.get(instance);
				if (cl == null) {
					return null;
				}
				Constructor<? extends ArchiveEntryDatabase> ctor = cl.getConstructor(File.class);
				archiveFile = new File(dataDir, instance);
				ArchiveEntryDatabase tmp = ctor.newInstance(archiveFile);
				concretes.put(instance, tmp);
				result = tmp;
				result.load();
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		if (!isUsed) {
			t.schedule(new TimerTask() {

				@Override
				public void run() {
					flushAll();
				}
			}, 0, 60000);
		}
		return result;
	}

	@Override
	public String[] getSupported() {
		List<String> supported = new LinkedList<>();
		for (Entry<String, Class<? extends ArchiveEntryDatabase>> i : dbs.entrySet()) {
			supported.add(i.getKey());
		}
		return supported.toArray(new String[0]);
	}

	@Override
	public String getName() {
		return "ViperfishArchive";
	}

	@Override
	public void dispose() {
		flushAll();
		t.cancel();
	}

	@Override
	public void delete() {
		CommonFunctions.delete(dataDir);
	}

	@Override
	public void refresh() {
		concretes.clear();

	}

	@Override
	public void initDefaults() {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerConfig() {
		// TODO Auto-generated method stub

	}

}

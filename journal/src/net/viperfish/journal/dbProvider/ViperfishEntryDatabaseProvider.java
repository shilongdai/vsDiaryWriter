package net.viperfish.journal.dbProvider;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Provider;

public class ViperfishEntryDatabaseProvider implements Provider<EntryDatabase> {

	private Map<String, DataSourceFactory> mapping;
	private File dataDir;
	private String defaultInstance;

	public ViperfishEntryDatabaseProvider() {
		mapping = new HashMap<>();
		dataDir = new File("data");
		if (!dataDir.exists()) {
			dataDir.mkdir();
		}
		initMapping();
		defaultInstance = "H2Database";
	}

	private void initMapping() {
		mapping.put("TextFile", initFactory(new TextFileEntryDatabaseFactory()));
		mapping.put("GZipFile", initFactory(new GZIPFileEntryDatabaseFactory()));
		mapping.put("H2Database", initFactory(new H2DatasourceFactory()));
	}

	private DataSourceFactory initFactory(DataSourceFactory f) {
		f.setDataDirectory(dataDir);
		return f;
	}

	@Override
	public EntryDatabase newInstance() {
		return mapping.get(defaultInstance).createDatabaseObject();
	}

	@Override
	public EntryDatabase getInstance() {
		return mapping.get(defaultInstance).getDatabaseObject();
	}

	@Override
	public EntryDatabase newInstance(String instance) {
		return mapping.get(instance).createDatabaseObject();
	}

	@Override
	public EntryDatabase getInstance(String instance) {
		return mapping.get(instance).getDatabaseObject();
	}

	@Override
	public String[] getSupported() {
		List<String> result = new LinkedList<>();
		for (Entry<String, DataSourceFactory> i : mapping.entrySet()) {
			result.add(i.getKey());
		}
		return result.toArray(new String[3]);
	}

	@Override
	public String getName() {
		return "viperfish";
	}

	@Override
	public void dispose() {
		for (Entry<String, DataSourceFactory> i : mapping.entrySet()) {
			i.getValue().cleanUp();
		}

	}

	@Override
	public void setDefaultInstance(String instance) {
		defaultInstance = instance;

	}

	@Override
	public String getDefaultInstance() {
		return defaultInstance;
	}

}

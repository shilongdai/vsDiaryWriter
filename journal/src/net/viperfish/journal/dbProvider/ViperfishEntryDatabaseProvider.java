package net.viperfish.journal.dbProvider;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Provider;
import net.viperfish.utils.file.CommonFunctions;

public class ViperfishEntryDatabaseProvider implements Provider<EntryDatabase> {

	private Map<String, DataSourceFactory> mapping;
	private File dataDir;
	private String defaultInstance;

	public ViperfishEntryDatabaseProvider() {
		mapping = new HashMap<>();
		File homeDir = new File(System.getProperty("user.home"));
		File vDiaryDir = new File(homeDir, ".vsDiary");
		CommonFunctions.initDir(vDiaryDir);
		dataDir = new File(vDiaryDir, "data");
		CommonFunctions.initDir(dataDir);
		initMapping();
		defaultInstance = "H2Database";
	}

	private void initMapping() {
		mapping.put("TextFile", initFactory(new TextFileEntryDatabaseFactory()));
		mapping.put("GZipFile", initFactory(new GZIPFileEntryDatabaseFactory()));
		mapping.put("H2Database", initFactory(new H2DatasourceFactory()));
		mapping.put("MemoryHashMap", initFactory(new StubDataSourceFactory()));
		mapping.put("JavaSerialization", initFactory(new JSerializationDataSourceFactory()));
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
		DataSourceFactory f = mapping.get(instance);
		if (f == null) {
			return null;
		}
		return f.createDatabaseObject();
	}

	@Override
	public EntryDatabase getInstance(String instance) {
		DataSourceFactory f = mapping.get(instance);
		if (f == null) {
			return null;
		}
		return f.getDatabaseObject();
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

	@Override
	public void delete() {
		CommonFunctions.delete(dataDir);

	}

}

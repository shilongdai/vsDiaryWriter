package test.java.dbTests;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.utils.file.CommonFunctions;

public abstract class DatabaseTest {

	private static File dataDir;

	protected abstract EntryDatabase getDB(File dataDir);

	public DatabaseTest() {
	}

	@BeforeClass
	public static void createDataDir() {
		dataDir = new File("test");
		CommonFunctions.initDir(dataDir);
	}

	@Test
	public void testAdd() {
		cleanUp();
		Journal j = new Journal();
		j.setSubject("test");
		j.setDate(new Date());
		j.setContent("content");
		Journal result = getDB(dataDir).addEntry(j);
		j.setId(result.getId());
		Assert.assertEquals(true, result.equals(j));
		cleanUp();
	}

	@Test
	public void testDelete() {
		cleanUp();
		Journal j = new Journal();
		j.setContent("test");
		j.setDate(new Date());
		j.setSubject("test");
		Journal result = getDB(dataDir).addEntry(j);
		j.setId(result.getId());
		result = getDB(dataDir).removeEntry(j.getId());
		Assert.assertEquals(null, getDB(dataDir).getEntry(j.getId()));
		cleanUp();
	}

	@Test
	public void testGet() {
		cleanUp();
		Journal j = new Journal();
		j.setContent("test");
		j.setSubject("test");
		j.setDate(new Date());
		Journal result = getDB(dataDir).addEntry(j);
		j.setId(result.getId());
		Assert.assertEquals(true, getDB(dataDir).getEntry(result.getId()).equals(j));
		cleanUp();
	}

	@Test
	public void testEdit() {
		Journal j = new Journal();
		j.setContent("test 1");
		j.setDate(new Date());
		j.setSubject("test");
		Journal result = getDB(dataDir).addEntry(j);
		Long id = result.getId();
		j.setContent("test 2");
		result = getDB(dataDir).updateEntry(id, j);
		Assert.assertEquals("test 2", result.getContent());
		cleanUp();
	}

	@Test
	public void testGetAll() {
		Journal j = new Journal();
		j.setContent("1");
		j.setSubject("1");
		Journal result = getDB(dataDir).addEntry(j);
		j.setId(result.getId());
		Journal i = new Journal();
		i.setContent("2");
		i.setSubject("2");
		result = getDB(dataDir).addEntry(i);
		i.setId(result.getId());
		List<Journal> all = getDB(dataDir).getAll();
		Collections.sort(all);
		Assert.assertEquals(true, (Collections.binarySearch(all, i) >= 0));
		Assert.assertEquals(true, (Collections.binarySearch(all, j) >= 0));
		cleanUp();
	}

	@Test
	public void testClear() {
		getDB(dataDir).clear();
		for (int k = 0; k < 100; ++k) {
			Journal i = new Journal();
			i.setDate(new Date());
			i.setContent(Integer.toString(k));
			i.setSubject(Integer.toString(k));
			getDB(dataDir).addEntry(i);
		}
		List<Journal> result = getDB(dataDir).getAll();
		Assert.assertEquals(100, result.size());
		getDB(dataDir).clear();
		List<Journal> cleared = getDB(dataDir).getAll();
		Assert.assertEquals(0, cleared.size());
		cleanUp();
	}

	public void cleanUp() {
		getDB(dataDir).clear();
	}

	@AfterClass
	public static void cleanUpDir() {
		CommonFunctions.delete(dataDir);
	}
}

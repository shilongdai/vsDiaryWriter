package net.viperfish.journal.framework;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import net.viperfish.utils.file.CommonFunctions;

public abstract class DatabaseTest {

	private static File dataDir;

	protected abstract EntryDatabase getDB(File dataDir);

	private EntryDatabase db;

	public DatabaseTest() {
		db = getDB(dataDir);
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
		Journal result = db.addEntry(j);
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
		Journal result = db.addEntry(j);
		j.setId(result.getId());
		result = db.removeEntry(j.getId());
		Assert.assertEquals(null, db.getEntry(j.getId()));
		cleanUp();
	}

	@Test
	public void testGet() {
		cleanUp();
		Journal j = new Journal();
		j.setContent("test");
		j.setSubject("test");
		j.setDate(new Date());
		Journal result = db.addEntry(j);
		j.setId(result.getId());
		Assert.assertEquals(true, db.getEntry(result.getId()).equals(j));
		cleanUp();
	}

	@Test
	public void testEdit() {
		Journal j = new Journal();
		j.setContent("test 1");
		j.setDate(new Date());
		j.setSubject("test");
		Journal result = db.addEntry(j);
		Long id = result.getId();
		j.setContent("test 2");
		result = db.updateEntry(id, j);
		Assert.assertEquals("test 2", result.getContent());
		cleanUp();
	}

	@Test
	public void testGetAll() {
		Journal j = new Journal();
		j.setContent("1");
		j.setSubject("1");
		Journal result = db.addEntry(j);
		j.setId(result.getId());
		Journal i = new Journal();
		i.setContent("2");
		i.setSubject("2");
		result = db.addEntry(i);
		i.setId(result.getId());
		List<Journal> all = db.getAll();
		Collections.sort(all);
		Assert.assertEquals(true, (Collections.binarySearch(all, i) >= 0));
		Assert.assertEquals(true, (Collections.binarySearch(all, j) >= 0));
		cleanUp();
	}

	@Test
	public void testClear() {
		db.clear();
		for (int k = 0; k < 100; ++k) {
			Journal i = new Journal();
			i.setDate(new Date());
			i.setContent(Integer.toString(k));
			i.setSubject(Integer.toString(k));
			db.addEntry(i);
		}
		List<Journal> result = db.getAll();
		Assert.assertEquals(100, result.size());
		db.clear();
		List<Journal> cleared = db.getAll();
		Assert.assertEquals(0, cleared.size());
		cleanUp();
	}

	public void cleanUp() {
		db.clear();
	}

	@AfterClass
	public static void cleanUpDir() {
		CommonFunctions.delete(dataDir);
	}
}

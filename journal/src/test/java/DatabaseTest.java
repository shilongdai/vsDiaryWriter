package test.java;

import java.util.Date;
import java.util.List;

import net.viperfish.journal.Configuration;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.persistent.EntryDatabase;

import org.junit.Assert;
import org.junit.Test;

public class DatabaseTest {
	private EntryDatabase db;

	public DatabaseTest() {
		Configuration.defaultConfig();
		Configuration.getProperty().setProperty("user.password", "password");
		Configuration.getProperty().setProperty("UseSecureWrapper", "false");
		db = Configuration.getDataSourceFactory().createDatabaseObject();
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
		j.setDate(new Date());
		Journal result = db.addEntry(j);
		j.setId(result.getId());
		Journal i = new Journal();
		i.setContent("2");
		i.setSubject("2");
		i.setDate(new Date());
		result = db.addEntry(i);
		i.setId(result.getId());
		List<Journal> all = db.getAll();
		Assert.assertEquals(true, all.get(0).equals(j));
		Assert.assertEquals(true, all.get(1).equals(i));
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
}

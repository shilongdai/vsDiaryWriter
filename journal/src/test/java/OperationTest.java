package test.java;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;

import net.viperfish.journal.JournalApplication;
import net.viperfish.journal.framework.AuthManagers;
import net.viperfish.journal.framework.ConfigMapping;
import net.viperfish.journal.framework.Configuration;
import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.EntryDatabases;
import net.viperfish.journal.framework.Indexers;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.Operation;
import net.viperfish.journal.framework.OperationWithResult;
import net.viperfish.journal.indexProvider.JournalIndexer;
import net.viperfish.journal.operation.AddEntryOperation;
import net.viperfish.journal.operation.DeleteEntryOperation;
import net.viperfish.journal.operation.EditContentOperation;
import net.viperfish.journal.operation.EditSubjectOperation;
import net.viperfish.journal.operation.GetAllOperation;
import net.viperfish.journal.operation.GetEntryOperation;
import net.viperfish.journal.operation.SearchEntryOperation;
import net.viperfish.journal.secureProvider.BlockCipherMacTransformer;

public class OperationTest {

	private EntryDatabase db;
	private JournalIndexer indexer;
	private final ExecutorService threadpool;

	private void setupConfig() {
		JournalApplication.initModules();
		JournalApplication.defaultProviders();
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_ALG_NAME, "AES");
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_MODE, "CFB");
		Configuration.setProperty(BlockCipherMacTransformer.ENCRYPTION_PADDING, "PKCS7PADDING");
		Configuration.setProperty(BlockCipherMacTransformer.MAC_ALGORITHM, "MD5");
		Configuration.setProperty(BlockCipherMacTransformer.MAC_TYPE, "HMAC");
		Configuration.setProperty(BlockCipherMacTransformer.KDF_HASH, "SHA256");
		Configuration.setProperty(ConfigMapping.AUTH_PROVIDER, "viperfish");
		Configuration.setProperty(ConfigMapping.AUTH_COMPONENT, "HashAuthentication");
		Configuration.setProperty(ConfigMapping.DB_PROVIDER, "viperfish");
		Configuration.setProperty(ConfigMapping.DB_COMPONENT, "TextFile");
		Configuration.setProperty(ConfigMapping.INDEX_PROVIDER, "viperfish");
		Configuration.setProperty(ConfigMapping.INDEXER_COMPONENT, "LuceneIndexer");
		Configuration.setProperty(ConfigMapping.TRANSFORMER_PROVIDER, "viperfish");
		Configuration.setProperty(ConfigMapping.TRANSFORMER_COMPONENT, "BlockCipherMAC");
	}

	public OperationTest() {
		setupConfig();
		initComponents();
		threadpool = Executors.newCachedThreadPool();
	}

	private void executeAsyncOperation(final Operation ops) {
		threadpool.execute(new Runnable() {

			@Override
			public void run() {
				ops.execute();

			}

		});
	}

	private boolean isSorted(List<Journal> result) {
		for (int i = 1; i < result.size(); ++i) {
			if (result.get(i).compareTo(result.get(i - 1)) < 0) {
				return false;
			}
		}
		return true;
	}

	@Test
	public void testAddEntryOperation() {
		cleanUp();
		Journal toAdd = new Journal();
		toAdd.setContent("test");
		toAdd.setDate(new Date());
		toAdd.setSubject("test");
		new AddEntryOperation(toAdd).execute();
		toAdd.setId(0L);
		Journal get = db.getEntry(0L);
		Assert.assertEquals("test", get.getSubject());
		Assert.assertEquals("test", get.getContent());
		Assert.assertEquals(toAdd.getDate().getTime(), get.getDate().getTime());
		Assert.assertEquals(toAdd.getId(), get.getId());
		Assert.assertEquals(true, indexer.contains(0L));
		cleanUp();
	}

	@Test
	public void testDeleteEntryOperation() {
		cleanUp();
		Journal toAdd = new Journal();
		toAdd.setContent("test");
		toAdd.setDate(new Date());
		toAdd.setSubject("test");
		Journal result = db.addEntry(toAdd);
		Long id = result.getId();
		toAdd.setId(id);
		result = db.getEntry(id);
		Assert.assertEquals(true, result.equals(toAdd));
		new DeleteEntryOperation(id).execute();
		result = db.getEntry(id);
		Assert.assertEquals(null, result);
		Assert.assertEquals(false, indexer.contains(id));
		cleanUp();
	}

	@Test
	public void testEditSubjectOperation() {
		cleanUp();
		Journal test = new Journal();
		test.setSubject("unedited");
		test.setDate(new Date());
		test.setContent("mary has a little lamb");
		Journal result = db.addEntry(test);
		Long id = result.getId();
		new EditSubjectOperation(id, "edited").execute();
		result = db.getEntry(id);
		Assert.assertEquals("edited", result.getSubject());
		cleanUp();
	}

	@Test
	public void testEditContentOperation() {
		cleanUp();
		Journal test = new Journal();
		test.setSubject("test");
		test.setDate(new Date());
		test.setContent("unedited");
		Journal result = db.addEntry(test);
		Long id = result.getId();
		new EditContentOperation(id, "edited").execute();
		result = db.getEntry(id);
		Assert.assertEquals("edited", result.getContent());
		cleanUp();
	}

	@Test
	public void testGetEntryOperation() {
		cleanUp();
		Journal test = new Journal();
		test.setContent("test");
		test.setDate(new Date());
		test.setSubject("test");
		Journal result = db.addEntry(test);
		test.setId(result.getId());
		OperationWithResult<Journal> o = new GetEntryOperation(test.getId());
		executeAsyncOperation(o);
		result = o.getResult();
		Assert.assertEquals(true, o.isDone());
		Assert.assertEquals(test.getId(), result.getId());
		Assert.assertEquals(test.getContent(), result.getContent());
		Assert.assertEquals(test.getSubject(), result.getSubject());
		Assert.assertEquals(test.getDate().getTime(), result.getDate().getTime());
		cleanUp();
	}

	@Test
	public void testSearchEntryOperation() {
		cleanUp();
		Journal t1 = new Journal();
		t1.setContent("test1");
		t1.setDate(new Date());
		t1.setSubject("test1");
		Journal t2 = new Journal();
		t2.setSubject("test2");
		t2.setDate(new Date());
		t2.setContent("test2");
		Journal t3 = new Journal();
		t3.setContent("random stuff");
		t3.setSubject("something");
		t3.setDate(new Date());
		db.addEntry(t1);
		db.addEntry(t2);
		db.addEntry(t3);
		indexer.add(t1);
		indexer.add(t2);
		indexer.add(t3);
		SearchEntryOperation ops = new SearchEntryOperation("test");
		executeAsyncOperation(ops);
		Set<Journal> s = ops.getResult();
		Assert.assertEquals(true, s.contains(t1));
		Assert.assertEquals(true, s.contains(t2));
		Assert.assertEquals(false, s.contains(t3));
		cleanUp();
	}

	@Test
	public void testGetAllOperation() {
		cleanUp();
		for (int i = 0; i < 100; ++i) {
			Journal j = new Journal();
			j.setSubject("test " + i);
			j.setContent("test " + i);
			db.addEntry(j);
		}
		GetAllOperation getAll = new GetAllOperation();
		executeAsyncOperation(getAll);
		List<Journal> result = getAll.getResult();
		Assert.assertEquals(true, isSorted(result));
		Assert.assertEquals(100, result.size());
		cleanUp();
	}

	public void cleanUp() {
		db.clear();
		indexer.clear();
	}

	private void initComponents() {
		AuthManagers.INSTANCE.getAuthManager().setPassword("test");
		db = EntryDatabases.INSTANCE.getEntryDatabase();
		indexer = (JournalIndexer) Indexers.INSTANCE.getIndexer();
	}
}

package net.viperfish.journal.framework;

import java.util.LinkedList;
import java.util.List;

/**
 * a wrapper around an EntryDatabase to provide encryption support via the
 * decorator pattern
 * 
 * @author sdai
 *
 */
public class JournalEncryptionWrapper implements EntryDatabase, Observer<String> {

	private JournalTransformer encryptor;
	private EntryDatabase db;

	public JournalEncryptionWrapper() {
		encryptor = JournalTransformers.INSTANCE.getTransformer();
	}

	public EntryDatabase getDb() {
		return db;
	}

	public void setDb(EntryDatabase db) {
		this.db = db;
	}

	@Override
	public Journal addEntry(Journal j) {
		Journal toAdd = encryptor.encryptJournal(j);
		Journal result = db.addEntry(toAdd);
		j.setId(result.getId());
		return j;
	}

	@Override
	public Journal removeEntry(Long id) {
		Journal result = db.removeEntry(id);
		if (result != null) {
			result = encryptor.decryptJournal(result);
		}
		return result;
	}

	@Override
	public Journal getEntry(Long id) {
		Journal cipher = db.getEntry(id);
		if (cipher != null) {
			cipher = encryptor.decryptJournal(cipher);
		}
		return cipher;
	}

	@Override
	public Journal updateEntry(Long id, Journal j) {
		Journal trueUpdate = encryptor.encryptJournal(j);
		Journal result = db.updateEntry(id, trueUpdate);
		j.setId(result.getId());
		return j;
	}

	@Override
	public List<Journal> getAll() {
		List<Journal> result = new LinkedList<>();
		for (Journal i : db.getAll()) {
			result.add(encryptor.decryptJournal(i));
		}
		return result;
	}

	@Override
	public void clear() {
		db.clear();

	}

	@Override
	public void beNotified(String data) {
		this.encryptor.setPassword(data);
	}

}

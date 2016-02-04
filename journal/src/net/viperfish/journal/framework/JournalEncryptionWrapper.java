package net.viperfish.journal.framework;

import java.util.LinkedList;
import java.util.List;

public class JournalEncryptionWrapper implements EntryDatabase {

	private JournalTransformer encryptor;
	private EntryDatabase db;

	public JournalEncryptionWrapper() {
	}

	public JournalTransformer getEncryptor() {
		return encryptor;
	}

	public void setEncryptor(JournalTransformer encryptor) {
		this.encryptor = encryptor;
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
		return db.addEntry(toAdd);
	}

	@Override
	public Journal removeEntry(Long id) {
		return db.removeEntry(id);
	}

	@Override
	public Journal getEntry(Long id) {
		Journal cipher = db.getEntry(id);
		return encryptor.encryptJournal(cipher);
	}

	@Override
	public Journal updateEntry(Long id, Journal j) {
		Journal trueUpdate = encryptor.encryptJournal(j);
		return db.updateEntry(id, trueUpdate);
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

}

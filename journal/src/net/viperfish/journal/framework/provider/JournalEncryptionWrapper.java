package net.viperfish.journal.framework.provider;

import java.util.LinkedList;
import java.util.List;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.framework.JournalTransformer;
import net.viperfish.journal.framework.Observer;
import net.viperfish.journal.framework.errors.CipherException;
import net.viperfish.journal.framework.errors.CompromisedDataException;
import net.viperfish.journal.framework.errors.FailToSyncEntryException;

/**
 * a wrapper around an EntryDatabase to provide encryption support via the
 * decorator pattern
 * 
 * @author sdai
 *
 */
public final class JournalEncryptionWrapper implements EntryDatabase, Observer<String> {

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
	public Journal addEntry(Journal j) throws FailToSyncEntryException {
		Journal toAdd;
		try {
			toAdd = encryptor.encryptJournal(j);
		} catch (CipherException e) {
			throw new RuntimeException(e);
		}
		Journal result = db.addEntry(toAdd);
		j.setId(result.getId());
		return j;
	}

	@Override
	public Journal removeEntry(Long id) throws FailToSyncEntryException {
		Journal result = db.removeEntry(id);
		if (result != null) {
			try {
				result = encryptor.decryptJournal(result);
			} catch (CipherException | CompromisedDataException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	@Override
	public Journal getEntry(Long id) {
		Journal cipher = db.getEntry(id);
		if (cipher != null) {
			try {
				cipher = encryptor.decryptJournal(cipher);
			} catch (CipherException | CompromisedDataException e) {
				throw new RuntimeException(e);
			}
		}
		return cipher;
	}

	@Override
	public Journal updateEntry(Long id, Journal j) throws FailToSyncEntryException {
		Journal trueUpdate;
		try {
			trueUpdate = encryptor.encryptJournal(j);
			Journal result = db.updateEntry(id, trueUpdate);
			j.setId(result.getId());
			return j;
		} catch (CipherException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<Journal> getAll() {
		List<Journal> result = new LinkedList<>();
		for (Journal i : db.getAll()) {
			try {
				result.add(encryptor.decryptJournal(i));
			} catch (CipherException | CompromisedDataException e) {
				throw new RuntimeException(e);
			}
		}
		return result;
	}

	@Override
	public void clear() throws FailToSyncEntryException {
		db.clear();

	}

	@Override
	public void beNotified(String data) {
		this.encryptor.setPassword(data);
	}

}

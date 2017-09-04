package net.viperfish.journal2.transaction;

import java.io.IOException;
import java.util.Date;

import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalDatabase;
import net.viperfish.journal2.core.JournalEncryptorChain;

public final class CryptedJournalDatabase implements JournalDatabase {

	private JournalEncryptorChain chain;
	private JournalDatabase src;

	public CryptedJournalDatabase(JournalEncryptorChain enc, JournalDatabase src) {
		this.chain = enc;
		this.src = src;
	}

	@Override
	public Journal save(Journal data) throws IOException {
		data.setSubject(chain.encryptSubject(data.getSubject()));
		Journal encrypted = chain.encryptJournal(data);
		src.save(encrypted);
		return encrypted;
	}

	@Override
	public void save(Iterable<Journal> data) throws IOException {
		for (Journal j : data) {
			j.setSubject(chain.encryptSubject(j.getSubject()));
			chain.encryptJournal(j);
		}
		src.save(data);
	}

	@Override
	public Iterable<Journal> findAll() throws IOException {
		Iterable<Journal> result = src.findAll();
		for (Journal j : result) {
			chain.decryptJournal(j);
			j.setSubject(chain.decryptSubject(j.getSubject()));
		}
		return result;
	}

	@Override
	public Iterable<Journal> findAll(Iterable<Long> ids) throws IOException {
		Iterable<Journal> result = src.findAll(ids);
		for (Journal j : result) {
			chain.decryptJournal(j);
			j.setSubject(chain.decryptSubject(j.getSubject()));
		}
		return result;
	}

	@Override
	public Journal findOne(Long id) throws IOException {
		Journal j = src.findOne(id);
		chain.decryptJournal(j);
		j.setSubject(chain.decryptSubject(j.getSubject()));
		return j;
	}

	@Override
	public void delete(Long id) throws IOException {
		src.delete(id);
	}

	@Override
	public void close() throws Exception {
		src.close();
	}

	@Override
	public Iterable<Journal> findByTimestampBetween(Date lower, Date upper) throws IOException {
		Iterable<Journal> result = src.findByTimestampBetween(lower, upper);
		for (Journal j : result) {
			System.out.println("Subject:" + j.getSubject());
			chain.decryptJournal(j);
			j.setSubject(chain.decryptSubject(j.getSubject()));
		}
		return result;
	}

	@Override
	public Iterable<Journal> findByIdInAndTimestampBetween(Iterable<Long> id, Date lower, Date upper)
			throws IOException {
		Iterable<Journal> result = src.findByIdInAndTimestampBetween(id, lower, upper);
		for (Journal j : result) {
			chain.decryptJournal(j);
			j.setSubject(chain.decryptSubject(j.getSubject()));
		}
		return result;
	}

	@Override
	public Iterable<Journal> findBySubjectAndTimestampBetween(String[] subjectKeyWord, Date lower, Date upper)
			throws IOException {
		String[] cryptedSearch = new String[subjectKeyWord.length];
		System.out.println("Search String");
		for (int i = 0; i < subjectKeyWord.length; ++i) {
			cryptedSearch[i] = chain.encryptSubject(subjectKeyWord[i]);
			System.out.println(cryptedSearch[i] + " ");
		}
		Iterable<Journal> result = src.findBySubjectAndTimestampBetween(cryptedSearch, lower, upper);
		for (Journal j : result) {
			chain.decryptJournal(j);
			j.setSubject(chain.decryptSubject(j.getSubject()));
		}
		return result;
	}

}

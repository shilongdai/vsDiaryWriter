package net.viperfish.journal2.transaction;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalEncryptor;
import net.viperfish.journal2.core.JournalIndexer;
import net.viperfish.journal2.core.TransactionWithResult;

final class SearchTransaction extends TransactionWithResult<List<Journal>> {

	private JournalIndexer indexer;
	private JournalEncryptor enc;
	private CrudRepository<Journal, Long> db;
	private String keyword;

	SearchTransaction(JournalIndexer indexer, JournalEncryptor enc, CrudRepository<Journal, Long> db, String keyword) {
		super();
		this.indexer = indexer;
		this.enc = enc;
		this.db = db;
		this.keyword = keyword;
	}

	@Override
	public void execute() {
		Iterable<Long> ids = indexer.search(keyword);
		List<Journal> result = new LinkedList<>();
		for (Journal j : db.findAll(ids)) {
			result.add(enc.decryptJournal(j));
		}
		this.setResult(result);
	}

}

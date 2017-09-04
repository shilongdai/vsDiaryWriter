package net.viperfish.journal2.transaction;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalDatabase;
import net.viperfish.journal2.core.JournalIndexer;
import net.viperfish.journal2.core.TransactionWithResult;
import net.viperfish.journal2.crypt.JournalEncryptorChain;
import net.viperfish.journal2.crypt.TextIndexFieldEncryptor;
import net.viperfish.journal2.error.CipherException;
import net.viperfish.journal2.error.CompromisedDataException;

final class SearchDateRangeTransaction extends TransactionWithResult<Collection<Journal>> {

	private JournalDatabase db;
	private JournalIndexer indexer;
	private JournalEncryptorChain enc;
	private TextIndexFieldEncryptor indexCrypt;
	private String keyword;
	private Date lower;
	private Date upper;

	SearchDateRangeTransaction(JournalDatabase db, JournalIndexer indexer, JournalEncryptorChain enc,
			TextIndexFieldEncryptor indexCrypt, String keyword, Date lower, Date upper) {
		super();
		this.db = db;
		this.indexer = indexer;
		this.enc = enc;
		this.keyword = keyword;
		this.lower = lower;
		this.upper = upper;
		this.indexCrypt = indexCrypt;
	}

	@Override
	public void execute() throws CipherException, CompromisedDataException, IOException {
		Iterable<Long> ids = indexer.search(indexCrypt.cryptStringWords(keyword));
		List<Journal> result = new LinkedList<>();

		for (Journal j : db.findByIdInAndTimestampBetween(ids, lower, upper)) {
			result.add(enc.decryptJournal(j));
		}
		this.setResult(result);
	}

}

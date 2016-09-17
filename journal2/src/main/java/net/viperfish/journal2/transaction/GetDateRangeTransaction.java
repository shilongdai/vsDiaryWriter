package net.viperfish.journal2.transaction;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalDatabase;
import net.viperfish.journal2.core.JournalEncryptor;
import net.viperfish.journal2.core.TransactionWithResult;

final class GetDateRangeTransaction extends TransactionWithResult<Collection<Journal>> {
	private Date lower;
	private Date upper;
	private JournalDatabase db;
	private JournalEncryptor enc;

	GetDateRangeTransaction(Date lower, Date upper, JournalDatabase db, JournalEncryptor enc) {
		super();
		this.lower = lower;
		this.upper = upper;
		this.db = db;
		this.enc = enc;
	}

	@Override
	public void execute() {
		List<Journal> result = new LinkedList<>();
		for (Journal j : db.findByTimestampBetween(lower, upper)) {
			result.add(enc.decryptJournal(j));
		}

		this.setResult(result);

	}

}

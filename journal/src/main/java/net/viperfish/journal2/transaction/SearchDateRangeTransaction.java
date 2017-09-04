package net.viperfish.journal2.transaction;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalDatabase;
import net.viperfish.journal2.core.TransactionWithResult;
import net.viperfish.journal2.error.CipherException;
import net.viperfish.journal2.error.CompromisedDataException;

final class SearchDateRangeTransaction extends TransactionWithResult<Collection<Journal>> {

	private JournalDatabase db;
	private String keyword;
	private Date lower;
	private Date upper;

	SearchDateRangeTransaction(JournalDatabase db, String keyword, Date lower, Date upper) {
		super();
		this.db = db;
		this.keyword = keyword;
		this.lower = lower;
		this.upper = upper;
	}

	@Override
	public void execute() throws CipherException, CompromisedDataException, IOException {
		LinkedList<Journal> result = new LinkedList<>();
		String[] words = keyword.split(" ");
		for (Journal j : db.findBySubjectAndTimestampBetween(words, lower, upper)) {
			result.add(j);
		}
		this.setResult(result);
	}

}

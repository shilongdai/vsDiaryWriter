package net.viperfish.journal2.transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalEncryptor;
import net.viperfish.journal2.core.TransactionWithResult;

final class GetAllPagedTransaction extends TransactionWithResult<Page<Journal>> {

	private PagingAndSortingRepository<Journal, ?> db;
	private JournalEncryptor enc;
	private Pageable paging;

	GetAllPagedTransaction(PagingAndSortingRepository<Journal, ?> db, JournalEncryptor enc, Pageable paging) {
		super();
		this.db = db;
		this.enc = enc;
		this.paging = paging;
	}

	@Override
	public void execute() {
		Page<Journal> result = db.findAll(paging);

		for (Journal j : result) {
			enc.decryptJournal(j);
		}

		this.setResult(result);

	}

}

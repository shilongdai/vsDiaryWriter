package net.viperfish.journal2.transaction;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import net.viperfish.journal2.core.AsyncTransactionExecutor;
import net.viperfish.journal2.core.AuthenticationManager;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.JournalDatabase;
import net.viperfish.journal2.core.JournalEncryptorChain;
import net.viperfish.journal2.core.JournalService;
import net.viperfish.journal2.core.TransactionExecutor;
import net.viperfish.journal2.error.CompromisedDataException;

public class TransactionalJournalService implements JournalService {

	private JournalEncryptorChain enc;
	private JournalDatabase db;
	private TransactionalUtils utils;
	private AuthenticationManager auth;
	private TransactionExecutor executor;

	public TransactionalJournalService(JournalDatabase db, JournalEncryptorChain enc, AuthenticationManager manager) {
		executor = new AsyncTransactionExecutor();
		this.db = new CryptedJournalDatabase(enc, db);
		this.auth = manager;
		this.enc = enc;
		this.utils = new TransactionalUtils(executor);
	}

	@Override
	public synchronized Journal get(Long id) throws ExecutionException {
		GetJournalTransaction trans = new GetJournalTransaction(id, db);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized Journal add(Journal t) throws ExecutionException {
		SaveJournalTransaction trans = new SaveJournalTransaction(t, db);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized Journal remove(Long id) throws ExecutionException {
		RemoveJournalTransaction trans = new RemoveJournalTransaction(id, db);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized Journal update(Long id, Journal updated) throws ExecutionException {
		updated.setId(id);
		SaveJournalTransaction trans = new SaveJournalTransaction(updated, db);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized Collection<Journal> getAll() throws ExecutionException {
		GetAllTransaction trans = new GetAllTransaction(db);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized Collection<Journal> getRange(Date lower, Date upper) throws ExecutionException {
		GetDateRangeTransaction trans = new GetDateRangeTransaction(lower, upper, db);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized Collection<Journal> searchWithinRange(Date lower, Date upper, String keyword)
			throws ExecutionException {
		SearchDateRangeTransaction trans = new SearchDateRangeTransaction(db, keyword, lower, upper);
		return utils.transactionToResult(trans);
	}

	@Override
	public synchronized void reCrypt(String password) throws CompromisedDataException, IOException {
		List<Journal> decrypted = new LinkedList<>();
		for (Journal i : db.findAll()) {
			decrypted.add(enc.decryptJournal(i));
		}
		auth.setPassword(password);
		for (Journal j : decrypted) {
			enc.encryptJournal(j);
		}
		db.save(decrypted);
		return;
	}

	@Override
	public synchronized void reCrypt() throws IOException {
		for (Journal i : db.findAll()) {
			db.save(enc.encryptJournal(enc.decryptJournal(i)));
		}

	}

	@Override
	public void close() throws Exception {
		db.close();
		executor.close();
	}

}

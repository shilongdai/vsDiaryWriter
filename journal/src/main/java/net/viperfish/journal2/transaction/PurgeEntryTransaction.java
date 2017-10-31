package net.viperfish.journal2.transaction;

import java.nio.file.Files;
import java.nio.file.Paths;

import net.viperfish.journal2.core.CrudRepository;
import net.viperfish.journal2.core.Journal;
import net.viperfish.journal2.core.TransactionWithResult;

public final class PurgeEntryTransaction extends TransactionWithResult<Journal> {

	private CrudRepository<Journal, Long> repo;
	private long id;

	public PurgeEntryTransaction(long id, CrudRepository<Journal, Long> repo) {
		if (repo instanceof CryptedJournalDatabase) {
			this.repo = ((CryptedJournalDatabase) repo).getSource();
		} else {
			this.repo = repo;
		}
		this.id = id;
	}

	@Override
	public void execute() throws Exception {
		Journal corrupted = repo.findOne(id);
		if (!Paths.get(corrupted.getTimestamp().toString()).toFile().exists()) {
			Files.createFile(Paths.get(corrupted.getTimestamp().toString()));
		}
		StringBuilder purgeContent = new StringBuilder();
		purgeContent.append("subject:").append(corrupted.getSubject()).append("\n\n").append("content:")
				.append(corrupted.getContent()).append("\n\n").append("processed by:")
				.append(corrupted.getProcessedBy()).append("\n\n");
		Files.write(Paths.get(corrupted.getTimestamp().toString()), purgeContent.toString().getBytes());
		repo.delete(id);
		this.setResult(corrupted);
	}

}

package net.viperfish.journal2.transaction;

import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import net.viperfish.journal2.core.CrudRepository;
import net.viperfish.journal2.core.Journal;

final class PurgeEntryService extends Service<Journal> {

	private CrudRepository<Journal, Long> repo;
	private long id;

	PurgeEntryService(long id, CrudRepository<Journal, Long> repo) {
		if (repo instanceof CryptedJournalDatabase) {
			this.repo = ((CryptedJournalDatabase) repo).getSource();
		} else {
			this.repo = repo;
		}
		this.id = id;
	}

	@Override
	protected Task<Journal> createTask() {
		return new Task<Journal>() {
			@Override
			protected Journal call() throws Exception {
				synchronized (Lock.class) {
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
					return corrupted;
				}
			}
		};
	}

}

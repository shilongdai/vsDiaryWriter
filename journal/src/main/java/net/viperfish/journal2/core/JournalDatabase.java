package net.viperfish.journal2.core;

import java.io.IOException;
import java.util.Date;

public interface JournalDatabase extends CrudRepository<Journal, Long> {
	public Iterable<Journal> findByTimestampBetween(Date lower, Date upper) throws IOException;

	public Iterable<Journal> findByIdInAndTimestampBetween(Iterable<Long> id, Date lower, Date upper)
			throws IOException;

	public Iterable<Journal> findBySubjectAndTimestampBetween(String[] subjectKeyWord, Date lower, Date upper)
			throws IOException;

}

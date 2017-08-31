package net.viperfish.journal2.core;

import java.util.Date;

public interface JournalDatabase extends CrudRepository<Journal, Long> {
	public Iterable<Journal> findByTimestampBetween(Date lower, Date upper);

	public Iterable<Journal> findByIdInAndTimestampBetween(Iterable<Long> id, Date lower, Date upper);

}

package net.viperfish.journal2.core;

import java.util.Date;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface JournalDatabase extends CrudRepository<Journal, Long>, PagingAndSortingRepository<Journal, Long> {
	public Iterable<Journal> findByTimestampBetween(Date lower, Date upper);

	public Iterable<Journal> findByIdInAndTimestampBetween(Iterable<Long> id, Date lower, Date upper);

}

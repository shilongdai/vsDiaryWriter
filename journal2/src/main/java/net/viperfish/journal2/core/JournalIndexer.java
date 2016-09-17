package net.viperfish.journal2.core;

public interface JournalIndexer {
	public Iterable<Long> search(String query);

	public void delete(Long id);

	public void add(Journal toAdd);

	public void clear();

	public boolean contains(Long id);

}

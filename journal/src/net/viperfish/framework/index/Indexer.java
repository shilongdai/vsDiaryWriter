package net.viperfish.framework.index;

public interface Indexer<T> {

	public Iterable<Long> search(String query);

	public void delete(Long id);

	public void add(T toAdd);

	public void clear();

	public boolean isMemoryBased();

	public boolean contains(Long id);

}
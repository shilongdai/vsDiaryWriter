package net.viperfish.utils.index;

public interface Indexer<T> {

	Iterable<Long> search(String query);

	void delete(Long id);

	void add(T toAdd);

	void clear();

}
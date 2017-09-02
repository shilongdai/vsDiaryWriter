package net.viperfish.journal2.core;

import java.io.IOException;

public interface CrudRepository<T, ID> extends AutoCloseable {
	public T save(T data) throws IOException;

	public void save(Iterable<T> data) throws IOException;

	public Iterable<T> findAll() throws IOException;

	public Iterable<T> findAll(Iterable<ID> ids) throws IOException;

	public T findOne(ID id) throws IOException;

	public void delete(ID id) throws IOException;
}

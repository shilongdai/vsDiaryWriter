package net.viperfish.journal2.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

public interface CRUDService<T, ID extends Serializable> extends AutoCloseable {
	public T get(ID id) throws ExecutionException;

	public T add(T t) throws ExecutionException;

	public T remove(ID id) throws ExecutionException;

	public T update(ID id, T updated) throws ExecutionException;

	public Collection<T> getAll() throws ExecutionException;
}

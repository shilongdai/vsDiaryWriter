package net.viperfish.journal2.core;

public interface CrudRepository<T, ID> {
	public T save(T data);

	public void save(Iterable<T> data);

	public Iterable<T> findAll();

	public Iterable<T> findAll(Iterable<ID> ids);

	public T findOne(ID id);

	public void delete(ID id);
}

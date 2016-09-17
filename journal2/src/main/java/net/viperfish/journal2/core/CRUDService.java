package net.viperfish.journal2.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CRUDService<T, ID extends Serializable> {
	public @Valid T get(@NotNull ID id) throws ExecutionException;

	public @Valid T add(@Valid @NotNull T t) throws ExecutionException;

	public @Valid T remove(@NotNull ID id) throws ExecutionException;

	public @Valid T update(@NotNull ID id, @Valid @NotNull T updated) throws ExecutionException;

	public @NotNull Page<T> getAll(@NotNull Pageable page) throws ExecutionException;

	public @NotNull Collection<T> getAll() throws ExecutionException;
}

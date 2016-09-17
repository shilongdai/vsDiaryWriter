package net.viperfish.journal2.core;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import net.viperfish.journal2.error.FailToStoreCredentialException;

public interface JournalService extends CRUDService<Journal, Long> {
	public List<Journal> search(String keyword) throws ExecutionException;

	public Collection<Journal> getRange(Date lower, Date upper) throws ExecutionException;

	public Collection<Journal> searchWithinRange(Date lower, Date upper, String keyword) throws ExecutionException;

	public void reCrypt(String password) throws FailToStoreCredentialException;
}

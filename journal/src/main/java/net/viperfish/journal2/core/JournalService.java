package net.viperfish.journal2.core;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import net.viperfish.journal2.error.FailToStoreCredentialException;

public interface JournalService extends CRUDService<Journal, Long> {
	public Collection<Journal> getRange(Date lower, Date upper) throws ExecutionException;

	public Collection<Journal> searchWithinRange(Date lower, Date upper, String keyword) throws ExecutionException;

	public void reCrypt(String password) throws FailToStoreCredentialException, IOException;

	public void reCrypt() throws IOException;

	public void purge(long id) throws ExecutionException;
}

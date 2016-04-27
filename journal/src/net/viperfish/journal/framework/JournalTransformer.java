package net.viperfish.journal.framework;

import net.viperfish.journal.framework.errors.CipherException;
import net.viperfish.journal.framework.errors.CompromisedDataException;
import net.viperfish.journal.framework.errors.FailToSyncCipherDataException;

/**
 * A encryptor that encrypts journals
 * 
 * @author sdai
 *
 */
public interface JournalTransformer {

	/**
	 * encrypts a journal
	 * 
	 * This method encrypts the given journal, and returns the copied encrypted
	 * version. The original version is not modified.
	 * 
	 * @param j
	 *            the journal to encrypt
	 * @return the new encrypted journal
	 * 
	 * @throws CipherException
	 *             if error when encrypting
	 */
	public Journal encryptJournal(Journal j) throws CipherException;

	/**
	 * decrypt a journal
	 * 
	 * This method returns a copy of the original that is decrypted.
	 * 
	 * @param j
	 *            the journal to decrypt
	 * @return the new decrypted journal
	 * 
	 * @throws CipherException
	 *             if error during decryption
	 * 
	 * @throws CompromisedDataException
	 *             if the journal data is compromised
	 */
	public Journal decryptJournal(Journal j) throws CipherException, CompromisedDataException;

	/**
	 * set the password
	 * 
	 * @param pass
	 *            the password
	 * @throws FailToSyncCipherDataException
	 */
	public void setPassword(String pass) throws FailToSyncCipherDataException;

}

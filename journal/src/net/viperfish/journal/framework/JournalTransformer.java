package net.viperfish.journal.framework;

/**
 * a cipher that will secure journals
 * 
 * @author sdai
 *
 */
public interface JournalTransformer {

	/**
	 * encrypt a journal, leaving the original unmodified
	 * 
	 * @param j
	 *            the journal to encrypt
	 * @return the new encrypted journal
	 */
	public Journal encryptJournal(Journal j);

	/**
	 * decrypt a journal, leaving the original unmodified
	 * 
	 * @param j
	 *            the journal to decrypt
	 * @return the new decrypted journal
	 */
	public Journal decryptJournal(Journal j);

	/**
	 * set the password
	 * 
	 * @param pass
	 *            the password
	 */
	public void setPassword(String pass);

}

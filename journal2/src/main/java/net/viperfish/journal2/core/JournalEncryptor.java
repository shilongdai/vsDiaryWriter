package net.viperfish.journal2.core;

import net.viperfish.journal2.error.CipherException;
import net.viperfish.journal2.error.CompromisedDataException;

public interface JournalEncryptor {
	public Journal encryptJournal(Journal j) throws CipherException;

	public Journal decryptJournal(Journal j) throws CipherException, CompromisedDataException;

	public void setPassword(String password) throws CipherException;
}

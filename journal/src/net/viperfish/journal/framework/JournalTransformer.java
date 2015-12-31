package net.viperfish.journal.framework;

public interface JournalTransformer {

	public Journal encryptJournal(Journal j);

	public Journal decryptJournal(Journal j);

	public void setPassword(String pass);

}

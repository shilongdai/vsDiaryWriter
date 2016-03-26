package net.viperfish.journal.dbProvider;

import java.io.File;

import org.h2.Driver;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.dialect.H2Dialect;

/**
 * an Hibernate managed database that uses H2
 * 
 * @author sdai
 *
 */
final class H2EntryDatabase extends HibernateEntryDatabase {

	private static SessionFactory factory;
	private Session s;

	static {
		factory = null;

	}

	H2EntryDatabase(File dataDir) {
		if (factory == null) {
			factory = new HibernateBuilder().confDriver(Driver.class).confDialect(H2Dialect.class)
					.confConnection("jdbc:h2:file:" + dataDir.getAbsolutePath() + "/journalEntries")
					.buildServiceRegistry().build();
		}
		s = factory.openSession();
		createTable();
	}

	/**
	 * create the table of entries if not already exists
	 */
	void createTable() {
		Transaction tr = getSession().beginTransaction();
		getSession()
				.createSQLQuery(
						"create table if not exists JOURNAL(Id BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT, Subject TEXT, Date TIMESTAMP, Content TEXT);")
				.executeUpdate();
		tr.commit();
	}

	@Override
	protected Session getSession() {
		return s;
	}

	/**
	 * close resources
	 */
	public void closeSession() {
		s.close();
	}

	public static void disposeShared() {
		if (factory != null) {
			factory.close();
		}
	}

}

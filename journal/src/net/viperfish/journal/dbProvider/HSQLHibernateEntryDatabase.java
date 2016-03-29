package net.viperfish.journal.dbProvider;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.dialect.HSQLDialect;
import org.hsqldb.jdbc.JDBCDriver;

final class HSQLHibernateEntryDatabase extends HibernateEntryDatabase {

	private static SessionFactory factory = null;
	private Session s;

	HSQLHibernateEntryDatabase(File dataDir) {
		if (factory == null) {
			factory = new HibernateBuilder().confDriver(JDBCDriver.class).confDialect(HSQLDialect.class)
					.confConnection("jdbc:hsqldb:" + new File(dataDir, "hsqlEntryDB").getAbsolutePath())
					.buildServiceRegistry().build();
		}
		s = factory.openSession();
		createTable();
	}

	private void createTable() {
		Transaction tr = getSession().beginTransaction();
		getSession()
				.createSQLQuery(
						"create table if not exists JOURNAL(Id BIGINT NOT NULL PRIMARY KEY IDENTITY, Subject VARCHAR(2000), Date TIMESTAMP, Content CLOB(5242880));")
				.executeUpdate();
		tr.commit();
	}

	@Override
	protected Session getSession() {
		return s;
	}

	public void closeSession() {
		s.close();
	}

	public static void closeFactory() {
		if (factory != null) {
			factory.close();
		}
	}

}

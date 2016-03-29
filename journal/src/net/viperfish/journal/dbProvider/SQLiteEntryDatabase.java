package net.viperfish.journal.dbProvider;

import java.io.File;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.sqlite.JDBC;

final class SQLiteEntryDatabase extends HibernateEntryDatabase {

	private static SessionFactory factory;
	private Session s;

	SQLiteEntryDatabase(File dataDir) {
		if (factory == null) {
			factory = new HibernateBuilder().confDialect(SQLiteDialect.class).confDriver(JDBC.class)
					.confConnection("jdbc:sqlite:" + new File(dataDir, "sqliteEntryDB.db").getAbsolutePath())
					.buildServiceRegistry().build();
		}
		s = factory.openSession();
		createTable();
	}

	private void createTable() {
		Transaction tr = getSession().beginTransaction();
		getSession()
				.createSQLQuery(
						"create table if not exists JOURNAL(Id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, Subject VARCHAR(2000), Date TIMESTAMP, Content CLOB(5242880));")
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

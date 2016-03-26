package net.viperfish.journal.dbProvider;

import java.io.File;
import java.sql.SQLException;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.GenericJDBCException;

final class DerbyEntryDatabase extends HibernateEntryDatabase {

	private static SessionFactory factory;
	private Session s;

	DerbyEntryDatabase(File dataDir) {
		if (factory == null) {
			HibernateBuilder b = new HibernateBuilder().confDriver(EmbeddedDriver.class);
			File databaseFile = new File(dataDir, "derbyEntryDB");
			if (!databaseFile.exists()) {
				b.confConnection("jdbc:derby:" + databaseFile.getAbsolutePath() + ";create=true");
			} else {
				b.confConnection("jdbc:derby:" + databaseFile.getAbsolutePath());
			}
			factory = b.buildServiceRegistry().build();
		}
		s = factory.openSession();
		createTable();
	}

	private void createTable() {
		try {
			Transaction tr = getSession().beginTransaction();
			getSession()
					.createSQLQuery(
							"create table JOURNAL(Id INTEGER NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1), Subject VARCHAR(32672), Date TIMESTAMP, Content VARCHAR(32672))")
					.executeUpdate();
			tr.commit();
		} catch (GenericJDBCException e) {
			try {
				throw e.getCause();
			} catch (SQLException e1) {
				if (e1.getSQLState().equals("X0Y32")) {
					return;
				}
				throw new RuntimeException(e1);
			} catch (Throwable e1) {
				throw new RuntimeException(e1);
			}
		}
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

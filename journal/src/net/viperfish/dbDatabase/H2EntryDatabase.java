package net.viperfish.dbDatabase;

import java.io.File;
import java.io.IOException;

import org.h2.Driver;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;

import net.viperfish.journal.framework.Journal;

public class H2EntryDatabase extends HibernateEntryDatabase {

	private SessionFactory factory;
	private Session s;

	public H2EntryDatabase(File dataDir) {
		Configuration cfg = new Configuration();
		cfg.addAnnotatedClass(Journal.class);
		cfg.setProperty("hibernate.connection.driver_class", Driver.class.getCanonicalName());
		try {
			cfg.setProperty("hibernate.connection.url", "jdbc:h2:" + dataDir.getCanonicalPath() + "/journalEntries");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		cfg.setProperty("hibernate.connection.username", "journalManager");
		cfg.setProperty("hibernate.connection.password", "journalManager");
		cfg.setProperty("hibernate.dialect", H2Dialect.class.getCanonicalName());
		cfg.setProperty("hibernate.hbm2ddl.auto", "update");
		factory = cfg.buildSessionFactory();
		s = factory.openSession();
	}

	@Override
	protected Session getSession() {
		return s;
	}

	public void closeSession() {
		s.close();
		factory.close();
	}

}

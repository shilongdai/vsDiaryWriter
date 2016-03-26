package net.viperfish.journal.dbProvider;

import java.io.File;
import java.io.IOException;

import org.h2.Driver;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.service.ServiceRegistry;

import net.viperfish.journal.framework.Journal;

/**
 * an Hibernate managed database that uses H2
 * 
 * @author sdai
 *
 */
final class H2EntryDatabase extends HibernateEntryDatabase {

	private static SessionFactory factory;
	private Session s;
	private static Configuration cfg;
	private static ServiceRegistry svReg;

	static {
		cfg = null;
		factory = null;
		svReg = null;

	}

	public H2EntryDatabase(File dataDir) {
		if (cfg == null) {
			cfg = new Configuration();
			cfg.addAnnotatedClass(Journal.class);
			cfg.setProperty("hibernate.connection.driver_class", Driver.class.getCanonicalName());
			try {
				cfg.setProperty("hibernate.connection.url",
						"jdbc:h2:file:" + dataDir.getCanonicalPath() + "/journalEntries");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			cfg.setProperty("hibernate.connection.username", "journalManager");
			cfg.setProperty("hibernate.connection.password", "journalManager");
			cfg.setProperty("hibernate.dialect", H2Dialect.class.getCanonicalName());
			cfg.setProperty("hibernate.c3p0.min_size", "1");
			cfg.setProperty("hibernate.c3p0.max_size", "10");
			cfg.setProperty("hibernate.c3p0.timeout", "1800");
			cfg.setProperty("hibernate.c3p0.max_statements", "50");
			svReg = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
			factory = cfg.buildSessionFactory(svReg);
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
		factory.close();
	}

}

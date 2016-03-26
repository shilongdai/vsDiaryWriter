package net.viperfish.journal.dbProvider;

import java.sql.Driver;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.service.ServiceRegistry;

import net.viperfish.journal.framework.Journal;

final class HibernateBuilder {
	private Configuration cfg;
	private ServiceRegistry svReg;

	class HibernateFactoryBuilder {
		public SessionFactory build() {
			return cfg.buildSessionFactory(svReg);
		}
	}

	HibernateBuilder() {
		cfg = new Configuration();
		cfg.addAnnotatedClass(Journal.class);
		cfg.setProperty("hibernate.connection.username", "journalManager");
		cfg.setProperty("hibernate.connection.password", "journalManager");
		cfg.setProperty("hibernate.c3p0.min_size", "1");
		cfg.setProperty("hibernate.c3p0.max_size", "10");
		cfg.setProperty("hibernate.c3p0.timeout", "1800");
		cfg.setProperty("hibernate.c3p0.max_statements", "50");
	}

	public HibernateBuilder confDriver(Class<? extends Driver> d) {
		cfg.setProperty("hibernate.connection.driver_class", d.getCanonicalName());
		return this;
	}

	public HibernateBuilder confDialect(Class<? extends Dialect> d) {
		cfg.setProperty("hibernate.dialect", d.getCanonicalName());
		return this;
	}

	public HibernateBuilder confConnection(String connectionUrl) {
		cfg.setProperty("hibernate.connection.url", connectionUrl);
		return this;
	}

	public HibernateFactoryBuilder buildServiceRegistry() {
		svReg = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
		return new HibernateFactoryBuilder();
	}
}

package net.viperfish.journal.dbProvider;

import java.util.List;
import java.util.logging.Level;

import org.hibernate.Session;
import org.hibernate.Transaction;

import net.viperfish.journal.framework.EntryDatabase;
import net.viperfish.journal.framework.Journal;

/**
 * an EntryDatabase that uses Hibernate ORM for database
 * 
 * @author sdai
 *
 */
abstract class HibernateEntryDatabase implements EntryDatabase {

	/**
	 * get a session for persistent operations, should be implemented to support
	 * different type of hibernate dialect and options
	 * 
	 * @return a usable session
	 */
	protected abstract Session getSession();

	{
		java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.OFF);
		java.util.logging.Logger.getLogger("com.mchange").setLevel(Level.OFF);
	}

	@Override
	public Journal addEntry(Journal j) {
		Transaction tr = this.getSession().beginTransaction();
		try {
			this.getSession().persist(j);
			this.getSession().flush();
			return j;
		} catch (RuntimeException e) {
			tr.rollback();
			throw e;
		} finally {
			tr.commit();
		}
	}

	@Override
	public Journal removeEntry(Long id) {
		Transaction tr = this.getSession().beginTransaction();
		try {
			Journal deleted = getEntry(id);
			this.getSession().delete(getEntry(id));
			return deleted;
		} catch (RuntimeException e) {
			tr.rollback();
			throw e;
		} finally {
			tr.commit();
		}
	}

	@Override
	public Journal getEntry(Long id) {
		Journal result = this.getSession().get(Journal.class, id);
		return result;
	}

	@Override
	public Journal updateEntry(Long id, Journal j) {
		Transaction tr = this.getSession().beginTransaction();
		try {
			j.setId(id);
			this.getSession().merge(j);
			return j;
		} catch (RuntimeException e) {
			tr.rollback();
			throw e;
		} finally {
			tr.commit();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Journal> getAll() {
		List<Journal> result = this.getSession().createQuery("FROM Journal J ORDER BY J.date DESC").list();
		return result;
	}

	@Override
	public void clear() {
		Transaction tr = this.getSession().beginTransaction();
		try {
			this.getSession().createQuery("DELETE FROM Journal").executeUpdate();
		} catch (RuntimeException e) {
			tr.rollback();
			throw e;
		} finally {
			tr.commit();
		}
		return;
	}

}

package net.viperfish.journal.dbDatabase;

import java.util.List;

import org.hibernate.Session;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.persistent.EntryDatabase;

public abstract class HibernateEntryDatabase implements EntryDatabase {

	protected abstract Session getSession();

	@Override
	public Journal addEntry(Journal j) {
		this.getSession().getTransaction().begin();
		this.getSession().persist(j);
		this.getSession().getTransaction().commit();
		this.getSession().flush();
		return j;
	}

	@Override
	public Journal removeEntry(Long id) {
		this.getSession().getTransaction().begin();
		Journal deleted = getEntry(id);
		this.getSession().delete(getEntry(id));
		this.getSession().getTransaction().commit();
		return deleted;
	}

	@Override
	public Journal getEntry(Long id) {
		Journal result = this.getSession().get(Journal.class, id);
		return result;
	}

	@Override
	public Journal updateEntry(Long id, Journal j) {
		this.getSession().getTransaction().begin();
		j.setId(id);
		this.getSession().merge(j);
		this.getSession().getTransaction().commit();
		return j;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Journal> getAll() {
		List<Journal> result = this.getSession().createQuery("FROM Journal J ORDER BY J.date DESC").list();
		return result;
	}

	@Override
	public void clear() {
		this.getSession().getTransaction().begin();
		this.getSession().createQuery("DELETE FROM Journal").executeUpdate();
		this.getSession().getTransaction().commit();
		return;
	}

}

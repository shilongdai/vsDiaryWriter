package net.viperfish.dbDatabase;

import java.util.List;

import org.hibernate.Session;

import net.viperfish.journal.framework.Journal;
import net.viperfish.journal.persistent.EntryDatabase;

public abstract class HibernateEntryDatabase implements EntryDatabase {

	protected abstract Session getSession();

	@Override
	public Journal addEntry(Journal j) {
		this.getSession().persist(j);
		this.getSession().flush();
		return j;
	}

	@Override
	public Journal removeEntry(Long id) {
		Journal deleted = getEntry(id);
		this.getSession().delete(getEntry(id));
		return deleted;
	}

	@Override
	public Journal getEntry(Long id) {
		Journal result = this.getSession().get(Journal.class, id);
		return result;
	}

	@Override
	public Journal updateEntry(Long id, Journal j) {
		j.setId(id);
		this.getSession().merge(j);
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
		this.getSession().createQuery("DELETE FROM Journal").executeUpdate();
		return;
	}

}

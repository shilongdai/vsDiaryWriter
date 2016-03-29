package net.viperfish.journal.framework;

import java.util.Date;

public final class JournalPointer implements Comparable<JournalPointer> {
	private String title;
	private Date date;
	private Long id;

	public JournalPointer(Journal j) {
		this.title = new String(j.getSubject());
		this.date = new Date(j.getDate().getTime());
		this.id = new Long(j.getId());
	}

	public String getTitle() {
		return title;
	}

	public Long getId() {
		return id;
	}

	public Date getDate() {
		return new Date(this.date.getTime());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JournalPointer other = (JournalPointer) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public int compareTo(JournalPointer o) {
		if (o.equals(this)) {
			return 0;
		}
		int dateComp = o.date.compareTo(date);
		if (dateComp == 0) {
			int idComp = o.id.compareTo(id);
			if (idComp == 0) {
				int titleComp = o.title.compareTo(title);
				return titleComp;
			} else {
				return idComp;
			}
		} else {
			return dateComp;
		}
	}

}

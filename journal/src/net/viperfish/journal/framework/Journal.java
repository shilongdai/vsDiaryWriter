package net.viperfish.journal.framework;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * the data model of a journal entry
 * 
 * @author sdai
 *
 */
@Entity
@Table
public class Journal implements Comparable<Journal> {
	private String subject;
	private Date date;
	private String content;
	private Long id;

	public Journal() {
		date = new Date();
		subject = new String();
		content = new String();
	}

	public Journal(Journal src) {
		this.date = src.getDate();
		this.id = src.getId();
		this.content = src.getContent();
		this.subject = src.getSubject();
	}

	@Basic
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Temporal(TemporalType.DATE)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Basic
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		Journal other = (Journal) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
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
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	@Override
	public int compareTo(Journal o) {
		return this.id.compareTo(o.getId());
	}

	@Override
	public String toString() {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String result = new String();
		result += subject;
		result += " On ";
		result += df.format(date);
		return result;
	}

}

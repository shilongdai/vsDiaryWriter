package net.viperfish.journal2.core;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Journal")
public class Journal implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -5731874363027283666L;
	@DatabaseField
	private String subject;
	@DatabaseField
	private String content;
	@DatabaseField(generatedId = true)
	private Long id;
	@DatabaseField
	private Date timestamp;
	@DatabaseField
	private String processors;
	private Map<String, CryptoInfo> infoMapping;

	public Journal() {
		this.subject = "";
		this.content = "";
		processors = "";
		infoMapping = new HashMap<>();
		timestamp = new Date();
	}

	public Journal(Journal src) {
		this.subject = src.subject;
		this.content = src.content;
		this.id = src.id;
		this.infoMapping = src.infoMapping;
		this.timestamp = src.timestamp;
		this.processors = src.processors;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		if (subject == null) {
			subject = "";
		}
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		if (content == null) {
			content = "";
		}
		this.content = content;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getProcessors() {
		return processors;
	}

	public void setProcessors(String processors) {
		this.processors = processors;
	}

	public Map<String, CryptoInfo> getInfoMapping() {
		return infoMapping;
	}

	public void setInfoMapping(Map<String, CryptoInfo> infoMapping) {
		this.infoMapping = infoMapping;
	}

	public SortedMap<Long, String> getProcessedBy() {
		TreeMap<Long, String> result = new TreeMap<>();
		String[] parts = processors.split(";");
		long i = 0;
		for (String iter : parts) {
			result.put(i++, iter);
		}
		return result;
	}

	public void setProcessedBy(SortedMap<Long, String> processedBy) {
		StringBuilder sb = new StringBuilder();
		for (Entry<Long, String> i : processedBy.entrySet()) {
			sb.append(i.getValue()).append(";");
		}
		sb.deleteCharAt(sb.length() - 1);
		processors = sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((infoMapping == null) ? 0 : infoMapping.hashCode());
		result = prime * result + ((processors == null) ? 0 : processors.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Journal other = (Journal) obj;
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!content.equals(other.content)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (infoMapping == null) {
			if (other.infoMapping != null) {
				return false;
			}
		} else if (!infoMapping.equals(other.infoMapping)) {
			return false;
		}
		if (processors == null) {
			if (other.processors != null) {
				return false;
			}
		} else if (!processors.equals(other.processors)) {
			return false;
		}
		if (subject == null) {
			if (other.subject != null) {
				return false;
			}
		} else if (!subject.equals(other.subject)) {
			return false;
		}
		if (timestamp == null) {
			if (other.timestamp != null) {
				return false;
			}
		} else if (!timestamp.equals(other.timestamp)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Journal [subject=" + subject + ", content=" + content + ", id=" + id + ", timestamp=" + timestamp
				+ ", processors=" + processors + ", infoMapping=" + infoMapping + "]";
	}

}

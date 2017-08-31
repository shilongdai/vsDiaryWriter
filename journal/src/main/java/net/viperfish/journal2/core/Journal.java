package net.viperfish.journal2.core;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.persistence.Basic;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table
public class Journal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5731874363027283666L;
	@NotNull
	@Size(max = 200)
	private String subject;
	@NotNull
	@Size(max = 5240856)
	private String content;
	private Long id;
	private Date timestamp;
	private Map<String, CryptoInfo> infoMapping;
	private SortedMap<Long, String> processedBy;

	public Journal() {
		this.subject = "";
		this.content = "";
		infoMapping = new HashMap<>();
		processedBy = new TreeMap<>();
	}

	public Journal(Journal src) {
		this.subject = src.subject;
		this.content = src.content;
		this.id = src.id;
		this.infoMapping = src.infoMapping;
		this.timestamp = src.timestamp;
		this.processedBy = src.processedBy;
	}

	@Basic
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
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

	@Temporal(TemporalType.TIMESTAMP)
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "Journal_CryptoInfo", joinColumns = {
			@JoinColumn(name = "Journal", referencedColumnName = "Id") })
	@MapKeyColumn(name = "Key")
	public Map<String, CryptoInfo> getInfoMapping() {
		return infoMapping;
	}

	public void setInfoMapping(Map<String, CryptoInfo> infoMapping) {
		this.infoMapping = infoMapping;
	}

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "Journal_Processor", joinColumns = {
			@JoinColumn(name = "Journal", referencedColumnName = "Id") })
	@MapKeyColumn(name = "Load_Order")
	@Column(name = "Processor")
	@OrderBy
	public SortedMap<Long, String> getProcessedBy() {
		return processedBy;
	}

	public void setProcessedBy(SortedMap<Long, String> processedBy) {
		this.processedBy = processedBy;
	}

}

package mz.co.crud.model;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "stock_entries")
@NamedQueries({
		@NamedQuery(name = "StockEntry.findAll", query = "SELECT s FROM StockEntry s ORDER BY s.entryDate DESC"),
		@NamedQuery(name = "StockEntry.existsByDate", query = "SELECT COUNT(s) FROM StockEntry s WHERE s.entryDate = :date"),
		@NamedQuery(name = "StockEntry.findByDate", query = "SELECT s FROM StockEntry s WHERE s.entryDate BETWEEN :startDate AND :endDate"),
		@NamedQuery(name = "StockEntry.findByDateWithDetails", query = "SELECT DISTINCT s FROM StockEntry s "
				+ "LEFT JOIN FETCH s.listEntriesDetails " + "WHERE s.entryDate BETWEEN :startDate AND :endDate "
				+ "ORDER BY s.entryDate DESC"),
		@NamedQuery(name = "StockEntry.findByEntryDetailsId", query = "SELECT e FROM StockEntry e LEFT JOIN FETCH e.listEntriesDetails d LEFT JOIN FETCH d.product WHERE e.id = :entryId") })

@NamedNativeQueries({
		@NamedNativeQuery(name = "StockEntry.findProductSummary", query = "SELECT p.name, SUM(std.quantity) as total, se.entry_date "
				+ "FROM sales_management.stock_entries se "
				+ "INNER JOIN sales_management.stock_entry_details std ON std.stock_entry_id = se.id "
				+ "INNER JOIN sales_management.products p ON p.id = std.product_id " + "GROUP BY p.name, se.entry_date "
				+ "ORDER BY se.entry_date DESC, p.name") })

public class StockEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "entry_date", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date entryDate;

	@Column(nullable = false)
	private Integer year;

	@OneToMany(mappedBy = "stockEntry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<StockEntryDetails> listEntriesDetails;

	@PrePersist
	public void prePersist() {
		if (entryDate != null) {
			this.year = entryDate.toInstant().atZone(java.time.ZoneId.systemDefault()).getYear();
		}
	}

	// Getters e Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public List<StockEntryDetails> getListEntriesDetails() {
		return listEntriesDetails;
	}

	public void setListEntriesDetails(List<StockEntryDetails> listEntriesDetails) {
		this.listEntriesDetails = listEntriesDetails;
	}
}
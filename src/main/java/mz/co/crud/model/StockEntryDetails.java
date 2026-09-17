package mz.co.crud.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock_entry_details")
@NamedQueries({
		@NamedQuery(name = "StockEntryDetails.findByEntry", query = "SELECT d FROM StockEntryDetails d WHERE d.stockEntry.id = :entryId"),
		@NamedQuery(name = "StockEntryDetails.findByStockEntryId", query = "SELECT d FROM StockEntryDetails d JOIN FETCH d.product WHERE d.stockEntry.id = :entryId") })
public class StockEntryDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "stock_entry_id")
	private StockEntry stockEntry;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(nullable = false)
	private Integer quantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StockEntry getStockEntry() {
		return stockEntry;
	}

	public void setStockEntry(StockEntry stockEntry) {
		this.stockEntry = stockEntry;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
}

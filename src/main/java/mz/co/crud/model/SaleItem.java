package mz.co.crud.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "sale_items")
@Data
@NamedQueries({ @NamedQuery(name = "SaleItem.findAll", query = "SELECT i FROM SaleItem i ORDER BY i.id DESC"),
		@NamedQuery(name = "SaleItem.findBySale", query = "SELECT i FROM SaleItem i WHERE i.sale.id = :saleId ORDER BY i.id DESC"),
		@NamedQuery(name = "SaleItem.findByProduct", query = "SELECT i FROM SaleItem i WHERE i.product.id = :productId ORDER BY i.id DESC") })
public class SaleItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sale_id", nullable = false)
	private Sale sale;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Column(name = "price", precision = 10, scale = 2, nullable = false)
	private BigDecimal price;

	@Column(name = "discount", precision = 10, scale = 2)
	private BigDecimal discount = BigDecimal.ZERO;

	@Column(name = "subtotal", precision = 12, scale = 2, nullable = false)
	private BigDecimal subtotal = BigDecimal.ZERO;

	// Construtores
	public SaleItem() {
		this.quantity = 1;
		this.price = BigDecimal.ZERO;
		this.subtotal = BigDecimal.ZERO;
	}

	public SaleItem(Product product, Integer quantity, BigDecimal price) {
		this.product = product;
		this.quantity = quantity;
		this.price = price;
		this.subtotal = price.multiply(new BigDecimal(quantity));
	}

 
	public void calculateSubtotal() {
		if (price != null && quantity != null) {
			BigDecimal total = price.multiply(BigDecimal.valueOf(quantity));
			if (discount != null) {
				total = total.subtract(discount);
			}
			this.subtotal = total;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
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

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

}

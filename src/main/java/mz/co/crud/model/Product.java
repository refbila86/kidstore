package mz.co.crud.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
@NamedQueries(
{ @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p ORDER BY p.ID DESC"),
		@NamedQuery(name = "Product.findByCategory", query = "SELECT p FROM Product p WHERE p.category.id = :catId ORDER BY p.name"),
		@NamedQuery(name = "Product.findByName", query = "SELECT p FROM Product p WHERE p.name = :name ORDER BY p.name"),
		@NamedQuery(name = "Product.countProducts", query = "SELECT count (p) FROM Product  p ORDER BY p.name DESC"),
		@NamedQuery(name = "Product.findByManufacturer", query = "SELECT p FROM Product p WHERE LOWER(p.manufacturer) LIKE LOWER(CONCAT('%', :manufacturer, '%'))"),
		@NamedQuery(name = "Product.findBelowMinimumQuantity", query = "SELECT p FROM Product p WHERE p.currentStock <= p.minimumStock ORDER BY p.currentStock"),
		@NamedQuery(name = "Product.findExpiringProducts", query = "SELECT p FROM Product p WHERE p.expirationDate BETWEEN :startDate AND :endDate ORDER BY p.expirationDate"),
		@NamedQuery(name = "Product.findExpiredProducts", query = "SELECT p FROM Product p WHERE p.expirationDate < :currentDate ORDER BY p.expirationDate"),
		@NamedQuery(name = "Product.findBySupplier", query = "SELECT p FROM Product p WHERE p.supplier.id = :supplierId ORDER BY p.name"),
		@NamedQuery(name = "Product.findAllOrderByCreatedAtDesc", query = "SELECT p FROM Product p ORDER BY p.createdAt DESC"),
		@NamedQuery(name = "Product.findExpired", query = "SELECT p FROM Product p WHERE p.expirationDate < CURRENT_DATE"),
		@NamedQuery(name = "Product.findProductsWithNoMoviment", query = "SELECT p FROM Product p LEFT JOIN p.saleItems si GROUP BY p.id, p.name HAVING COALESCE(SUM(si.quantity), 0) <= 1"),
		@NamedQuery(name = "Product.findNearExpiry", query = "SELECT p FROM Product p WHERE p.expirationDate <= :expirationDate ORDER BY p.expirationDate ASC"),
		@NamedQuery(name = "Product.findActiveProducts", query = "SELECT p FROM Product p WHERE p.quantity > 0 AND p.expirationDate > :currentDate ORDER BY p.name"),
		@NamedQuery(name = "Product.findTopSellingProducts", query = "SELECT p.name, SUM(si.quantity) FROM SaleItem si JOIN si.product p GROUP BY p.name HAVING SUM(si.quantity) > 2 ORDER BY SUM(si.quantity) DESC"),
		@NamedQuery(name = "Product.countZeroProducts", query = "SELECT count (p) FROM Product p WHERE p.currentStock <=1 ORDER BY p.name DESC") })

public class Product
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String name;

	@Column(nullable = false, length = 150)
	private String manufacturer;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(nullable = true)
	private Integer quantity;

	@Column(name = "minimum_quantity", nullable = true)
	private Integer minimumQuantity;

	@Column(name = "purchase_price", nullable = false, precision = 10, scale = 2)
	private BigDecimal purchasePrice;

	@Column(name = "expiration_date")
	private LocalDate expirationDate;

	@Column(name = "registration_date", nullable = false)
	private LocalDate registrationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supplier_id")
	private Supplier supplier;

	@Column(name = "sale_price", nullable = false, scale = 2, precision = 10)
	private BigDecimal salePrice;

	@Column(name = "current_stock", nullable = false)
	private Integer currentStock;

	@Column(name = "minimum_stock", nullable = true)
	private Integer minimumStock;

	@ManyToOne
	@JoinColumn(name = "year_id")
	private Year year;

	@ManyToOne
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
	private List<SaleItem> saleItems;

	public Product()
	{
	}

	public boolean isExpired()
	{
		return expirationDate != null && expirationDate.isBefore(LocalDate.now());
	}

	public boolean isBelowMinimumQuantity()
	{
		if (minimumStock == null)
		{
			return false;
		}

		int stock = currentStock != null ? currentStock : 0;

		return stock <= minimumStock;
	}

	public boolean isExpiringSoon(int days)
	{
		if (expirationDate == null)
			return false;
		LocalDate thresholdDate = LocalDate.now().plusDays(days);
		return expirationDate.isBefore(thresholdDate) && !isExpired();
	}

	@Override
	public String toString()
	{
		return "[nome=" + name + ", validade=" + expirationDate + "]";
	}

	public BigDecimal getProfit()
	{
		return salePrice.subtract(purchasePrice);
	}

	public BigDecimal getProfitMargin()
	{
		if (purchasePrice.compareTo(BigDecimal.ZERO) == 0)
		{
			return BigDecimal.ZERO;
		}
		return getProfit().divide(purchasePrice, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getManufacturer()
	{
		return manufacturer;
	}

	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Integer getQuantity()
	{
		return quantity;
	}

	public void setQuantity(Integer quantity)
	{
		this.quantity = quantity;
	}

	public Integer getMinimumQuantity()
	{
		return minimumQuantity;
	}

	public void setMinimumQuantity(Integer minimumQuantity)
	{
		this.minimumQuantity = minimumQuantity;
	}

	public BigDecimal getPurchasePrice()
	{
		return purchasePrice;
	}

	public void setPurchasePrice(BigDecimal purchasePrice)
	{
		this.purchasePrice = purchasePrice;
	}

	public LocalDate getExpirationDate()
	{
		return expirationDate;
	}

	public void setExpirationDate(LocalDate expirationDate)
	{
		this.expirationDate = expirationDate;
	}

	public LocalDate getRegistrationDate()
	{
		return registrationDate;
	}

	public void setRegistrationDate(LocalDate registrationDate)
	{
		this.registrationDate = registrationDate;
	}

	public Supplier getSupplier()
	{
		return supplier;
	}

	public void setSupplier(Supplier supplier)
	{
		this.supplier = supplier;
	}

	public BigDecimal getSalePrice()
	{
		return salePrice;
	}

	public void setSalePrice(BigDecimal salePrice)
	{
		this.salePrice = salePrice;
	}

	public Integer getCurrentStock()
	{
		return currentStock;
	}

	public void setCurrentStock(Integer currentStock)
	{
		this.currentStock = currentStock;
	}

	public Integer getMinimumStock()
	{
		return minimumStock;
	}

	public void setMinimumStock(Integer minimumStock)
	{
		this.minimumStock = minimumStock;
	}

	public Year getYear()
	{
		return year;
	}

	public void setYear(Year year)
	{
		this.year = year;
	}

	public Category getCategory()
	{
		return category;
	}

	public void setCategory(Category category)
	{
		this.category = category;
	}

	public LocalDateTime getCreatedAt()
	{
		return createdAt;
	}

	public List<SaleItem> getSaleItems()
	{
		return saleItems;
	}

	public void setSaleItems(List<SaleItem> saleItems)
	{
		this.saleItems = saleItems;
	}

	public void setCreatedAt(LocalDateTime createdAt)
	{
		this.createdAt = createdAt;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return Objects.equals(id, other.id);
	}

}

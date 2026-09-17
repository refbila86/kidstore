package mz.co.crud.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "sales")
@Data
@NamedQueries(
{ @NamedQuery(name = "Sale.findAll", query = "SELECT s FROM Sale s ORDER BY s.dateSale DESC"),
		@NamedQuery(name = "Sale.findDateByDate", query = "SELECT s FROM Sale s WHERE FUNCTION('DATE', s.dateSale) = :date ORDER BY s.dateSale DESC"),
		@NamedQuery(name = "Sale.findByCustomer", query = "SELECT s FROM Sale s WHERE s.customer.id = :customerId ORDER BY s.dateSale DESC"),

		@NamedQuery(name = "Sale.findByDate", query = "SELECT s FROM Sale s " + "WHERE FUNCTION('DATE', s.dateSale) = :date " + "ORDER BY s.saleCode DESC"),
		@NamedQuery(name = "Sale.countDailySales", query = "SELECT count (s) FROM Sale s WHERE s.dateSale BETWEEN :startDate AND :endDate"),
		@NamedQuery(name = "Sale.sumDailySales", query = "SELECT sum (s.totalSale) FROM Sale s WHERE s.dateSale BETWEEN :startDate AND :endDate"),
		@NamedQuery(name = "Sale.findDistinctDatesNotInCashier", query = "SELECT DISTINCT s.dateSale FROM Sale s "
				+ "WHERE s.dateSale NOT IN (SELECT c.date FROM Cashier c) " + "ORDER BY s.dateSale DESC"),
		@NamedQuery(name = "Sale.findById", query = "SELECT s FROM Sale s WHERE s.id = :id ORDER BY s.id DESC"),
		@NamedQuery(name = "Sale.findByIdsWithItems", query = "SELECT s FROM Sale s LEFT JOIN FETCH s.salesItems WHERE s.id = :id"),
		@NamedQuery(name = "Sale.findByIdWithItems", query = "SELECT DISTINCT s FROM Sale s " + "LEFT JOIN FETCH s.salesItems si "
				+ "LEFT JOIN FETCH si.product " + "WHERE s.id = :id"),
		@NamedQuery(name = "Sale.findDistinctDates", query = "SELECT DISTINCT FUNCTION('DATE', s.dateSale) FROM Sale s ORDER BY s.dateSale DESC"),
		@NamedQuery(name = "Sale.findAllWithItems", query = "SELECT DISTINCT s FROM Sale s LEFT JOIN FETCH s.salesItems si LEFT JOIN FETCH si.product"),
		@NamedQuery(name = "Sale.findByIdWithItemss", query = "SELECT DISTINCT s FROM Sale s LEFT JOIN FETCH s.salesItems si LEFT JOIN FETCH si.product WHERE s.id = :saleId"),
		@NamedQuery(name = "Sale.findByCodeAndYear", query = "SELECT s FROM Sale s WHERE s.saleCode = :saleCode AND s.year.id = :yearId"),
		@NamedQuery(name = "Sale.findMaxCodeByYear", query = "SELECT MAX(s.numericCode) FROM Sale s WHERE s.year = :year") })
public class Sale implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "sale_code", nullable = false, length = 20)
	private String saleCode;

	@Column(name = "numeric_code")
	private Integer numericCode;

	@ManyToOne
	@JoinColumn(name = "year_id")
	private Year year;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Column(name = "date_sale", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dateSale;

	@Column(name = "total_sale", precision = 10, scale = 2, nullable = false)
	private BigDecimal totalSale = BigDecimal.ZERO;

	@OneToMany(mappedBy = "sale", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SaleItem> salesItems = new ArrayList<>();

//	@OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<SaleItem> salesItems = new ArrayList<>();

	public Sale()
	{
		this.totalSale = BigDecimal.ZERO;
	}

	public String getDateSaleFormatted()
	{
		if (dateSale == null)
		{
			return "";
		}
		return new SimpleDateFormat("dd/MM/yyyy").format(dateSale);
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getSaleCode()
	{
		return saleCode;
	}

	public void setSaleCode(String saleCode)
	{
		this.saleCode = saleCode;
	}

	public Integer getNumericCode()
	{
		return numericCode;
	}

	public void setNumericCode(Integer numericCode)
	{
		this.numericCode = numericCode;
	}

	public Year getYear()
	{
		return year;
	}

	public void setYear(Year year)
	{
		this.year = year;
	}

	public Customer getCustomer()
	{
		return customer;
	}

	public void setCustomer(Customer customer)
	{
		this.customer = customer;
	}

	public Date getDateSale()
	{
		return dateSale;
	}

	public void setDateSale(Date currentDate)
	{
		this.dateSale = currentDate;
	}

	public BigDecimal getTotalSale()
	{
		return totalSale;
	}

	public void setTotalSale(BigDecimal totalSale)
	{
		this.totalSale = totalSale;
	}

	public List<SaleItem> getSalesItems()
	{
		return salesItems;
	}

	public void setSalesItems(List<SaleItem> salesItems)
	{
		this.salesItems = salesItems;
	}
}

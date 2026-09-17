package mz.co.crud.model;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "cashier")
@NamedQueries(
{
		// Lista todos
		@NamedQuery(name = "Cashier.findAll", query = "SELECT c FROM Cashier c ORDER BY c.date DESC"),

		// Verifica se caixa já existe para uma data
		@NamedQuery(name = "Cashier.findByDate", query = "SELECT c FROM Cashier c WHERE c.date = :date"),

		@NamedQuery(name = "Cashier.findDistinctDatesNotInCashier", query = "SELECT DISTINCT s.dateSale FROM Sale s WHERE s.dateSale NOT IN (SELECT c.date FROM Cashier c)			             ORDER BY s.dateSale DESC"),

		// Busca caixas abertos
		@NamedQuery(name = "Cashier.findOpen", query = "SELECT c FROM Cashier c WHERE c.status = 'ABERTO' ORDER BY c.date DESC"),

		// Busca caixas fechados
		@NamedQuery(name = "Cashier.findClosed", query = "SELECT c FROM Cashier c WHERE c.status ='FECHADO' ORDER BY c.date DESC"),

		// Buscar apenas datas de vendas que NÃO possuem caixa
		@NamedQuery(name = "Cashier.findMissingDates", query = "SELECT DISTINCT v.dateSale FROM Sale v "
				+ "WHERE v.dateSale NOT IN (SELECT c.date FROM Cashier c) " + "ORDER BY v.dateSale DESC"),

		@NamedQuery(name = "Cashier.countMissingCashierDates", query = "SELECT COUNT(DISTINCT s.dateSale) "
				+ "FROM Sale s " + "WHERE s.dateSale NOT IN (SELECT c.date FROM Cashier c)"),

		@NamedQuery(name = "Cashier.countByDate", query = "SELECT COUNT(c) FROM Cashier c WHERE c.date = :date"),

		// Total do dia (para o fecho) countMissingCashierDates
		@NamedQuery(name = "Cashier.sumSalesOfDay", query = "SELECT SUM(v.totalSale) FROM Sale v WHERE v.dateSale = :date")

})
public class Cashier
{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Dia do caixa
	@Column(name = "date", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date date;

	// Total do dia
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal totalOfDay;

	@Column(name = "close_date", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date closeDate;

	// Estado: ABERTO / FECHADO
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private CashierStatus status;

	// Ano
	@ManyToOne
	@JoinColumn(name = "year_id", nullable = false)
	private Year year;

	// Utilizador que fechou
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@Column(name = "number_of_sales", nullable = false)
	private int numberOfSales;

	// Getters e setters (como anteriormente)

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public BigDecimal getTotalOfDay()
	{
		return totalOfDay;
	}

	public void setTotalOfDay(BigDecimal totalOfDay)
	{
		this.totalOfDay = totalOfDay;
	}

	public Date getCloseDate()
	{
		return closeDate;
	}

	public void setCloseDate(Date closeDate)
	{
		this.closeDate = closeDate;
	}

	public CashierStatus getStatus()
	{
		return status;
	}

	public void setStatus(CashierStatus status)
	{
		this.status = status;
	}

	public Year getYear()
	{
		return year;
	}

	public void setYear(Year year)
	{
		this.year = year;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public int getNumberOfSales()
	{
		return numberOfSales;
	}

	public void setNumberOfSales(int numberOfSales)
	{
		this.numberOfSales = numberOfSales;
	}
}

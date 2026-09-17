package mz.co.crud.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import mz.co.crud.model.Cashier;
import mz.co.crud.model.CashierStatus;
import mz.co.crud.model.Sale;
import mz.co.crud.model.User;
import mz.co.crud.model.Year;
import mz.co.crud.service.CashierService;
import mz.co.crud.service.SaleService;
import mz.co.crud.service.YearService;

@Component ("cashierBean")  
@SessionScope
public class CashierBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Sale> listSalesOfDay;

	private List<LocalDate> uniqueDates;

	@Autowired
	private CashierService cashierService;

	@Autowired
	private DashboardBean dashboardBean;

	@Autowired
	private SaleService saleService;

	@Autowired
	private LoginBean loginBean;

	@Autowired	private YearService yearService;

	private List<Cashier> listCashier;

	private Long countMissingCashier;

	private List<LocalDate> openDates;

	private Cashier cashierSelected;

	private Date selectedDate;

	private List<Sale> listSales;

	private List<Sale> filteredSales;

	private List<Date> distinctSaleNotInCashier;

	private BigDecimal totalGeral;

	private Cashier itemCashier;

	private Year year;

	private Year currentYear;

	@PostConstruct
	public void init() {
		loadOpenDates();
		loadCashiers();
		loadUniqueDates();
		// loadDistinctDatesNotInCashier();
		countMissingCashierDates();
		loadYear();
	}

	public void loadYear() {
		int currentYear = java.time.Year.now().getValue();
		this.setCurrentYear(yearService.findByYear(currentYear));
	}

	public BigDecimal getTotalCash() {
		if (listCashier == null || listCashier.isEmpty()) {
			return BigDecimal.ZERO;
		}

		return listCashier.stream().map(c -> c.getTotalOfDay() != null ? c.getTotalOfDay() : BigDecimal.ZERO)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public int getTotalCashiers() {
		return listCashier != null ? listCashier.size() : 0;
	}

	public int getClosedCashiers() {
		if (listCashier == null)
			return 0;
		return (int) listCashier.stream().filter(c -> c.getStatus() == CashierStatus.FECHADO).count();
	}

	public int getOpenCashiers() {
		if (listCashier == null)
			return 0;
		return (int) listCashier.stream().filter(c -> c.getStatus() == CashierStatus.ABERTO).count();
	}

	// Visualizar caixa
	public void viewCashier(Cashier c) {
		this.cashierSelected = c;
		// Redirecionar ou abrir modal de visualização
	}

	public List<LocalDate> getUniqueDates() {
		if (openDates == null) {
			return List.of();
		}
		return openDates.stream().distinct().toList();
	}

	public void loadSalesByDate() {

		listSalesOfDay = saleService.findByDate(selectedDate);
		calcularTotalGeral();

	}

	public void closeCashieer() {
		try {
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Africa/Maputo"));
			cal.setTime(selectedDate);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			
			itemCashier.setDate(cal.getTime());
			
			itemCashier.setStatus(CashierStatus.FECHADO);
			itemCashier.setCloseDate(new Date());
			itemCashier.setNumberOfSales(getListSalesOfDay().size());
			itemCashier.setTotalOfDay(getTotalGeral());
			itemCashier.setUser(getLoginBean().getLoggedUser());
			Year year = new Year();
			year.setYear(LocalDate.now().getYear());
			itemCashier.setYear(currentYear);
			cashierService.saveOrUpdateCashier(itemCashier);
			loadUniqueDates();
			loadCashiers();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Caixa fechado com sucesso!"));

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
					"Não foi possível fechar o caixa: " + e.getMessage()));
		}
	}

	public Date getCurrentDate() {
		return new Date();
	}

	// Fechar caixa direto da tabela
	public void closeCashier(Cashier c) {
		this.cashierSelected = c;
		closeCashier(); // chama o método existente
	}

	public void loadCashiers() {
		listCashier = cashierService.findAll();
	}

	public void loadOpenDates() {
		openDates = cashierService.findMissingDates();
	}

	public void countMissingCashierDates() {
		setCountMissingCashier(cashierService.countMissingCashierDates());
	}

	public String goToSalesPage() {
		if (selectedDate != null) {
			// Aqui você pode setar o filtro da saleBean
			FacesContext.getCurrentInstance().getExternalContext().getFlash().put("filterDate", selectedDate);

			// return "sales-page.xhtml?faces-redirect=true";
			dashboardBean.setCurrentPage("/pages/sale.xhtml");
		}
		return null;
	}

	// Chamado ao clicar "Fechar Caixa" na confirmação
	public void closeCashier() {
		if (cashierSelected != null) {

			User u = new User();
			u.setId(1L); // Substituir pelo user logado

			Year year = new Year();
			// year.setId(1L); // Exemplo, ajustar conforme seu sistema

			// Se não existir no banco, cria
			if (cashierSelected.getId() == null) {
				cashierSelected = cashierService.create(cashierSelected.getDate(), u, year);
			}

			cashierService.closeCashier(cashierSelected, u);

			// Atualiza listas
			loadCashiers();
			loadOpenDates();

			// Limpa seleção
			cashierSelected = null;
			selectedDate = null;
		}
	}

	public String formatDate(Date date) {
		if (date == null)
			return "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setTimeZone(java.util.TimeZone.getTimeZone("Africa/Maputo"));
		return sdf.format(date);
	}

	public void loadSalesBySelectedDate() {

		setListSalesOfDay(saleService.findByDate(selectedDate));

	}

	public void loadSales(Cashier cashier) {
		this.cashierSelected = cashier;
		if (cashier != null) {
			// this.listSales = cashier.getSales();
			this.setTotalGeral(listSales.stream().map(Sale::getTotalSale).reduce(BigDecimal.ZERO, BigDecimal::add));
		}
	}

	public void viewSale(Sale sale) {
		// Lógica para visualizar detalhes da venda (modal ou redirecionamento)
	}

	public void closeCashRegister() {
		if (cashierSelected != null) {
			// Lógica para fechar o caixa
		}
	}

	public void onFilter() {
		// Pode recalcular totais se necessário
		if (filteredSales != null) {
			totalGeral = filteredSales.stream().map(Sale::getTotalSale).reduce(BigDecimal.ZERO, BigDecimal::add);
		}
	}

	// Getters e Setters
	public List<Sale> getListSales() {
		return listSales;
	}

	public void setListSales(List<Sale> listSales) {
		this.listSales = listSales;
	}

	public List<Sale> getFilteredSales() {
		return filteredSales;
	}

	public void setFilteredSales(List<Sale> filteredSales) {
		this.filteredSales = filteredSales;
	}

//	public BigDecimal getTotalGeral() {
//		if (totalGeral == null) {
//			calcularTotalGeral();
//		}
//		return totalGeral;
//	}
	public BigDecimal getTotalGeral() {
		if (listSalesOfDay == null || listSalesOfDay.isEmpty()) {
			return BigDecimal.ZERO;
		}
		return listSalesOfDay.stream().map(Sale::getTotalSale).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private void calcularTotalGeral() {
		if (listSalesOfDay != null && !listSalesOfDay.isEmpty()) {
			totalGeral = listSalesOfDay.stream().map(Sale::getTotalSale).reduce(BigDecimal.ZERO, BigDecimal::add);
		} else {
			totalGeral = BigDecimal.ZERO;
		}
	}

//	public BigDecimal getTotalGeral() {
//		List<Sale> salesList = (filteredSales != null && !filteredSales.isEmpty()) ? filteredSales : listSalesOfDay;
//
//		if (salesList == null || salesList.isEmpty()) {
//			return BigDecimal.ZERO;
//		}
//
//		return salesList.stream().map(Sale::getTotalSale).reduce(BigDecimal.ZERO, BigDecimal::add);
//	}

	// Getters & Setters

	public void newCashier() {
		this.setItemCashier(new Cashier());
	}

	public List<Cashier> getListCashier() {
		return listCashier;
	}

	public List<LocalDate> getOpenDates() {
		return openDates;
	}

	public Cashier getCashierSelected() {
		return cashierSelected;
	}

	public void setCashierSelected(Cashier cashierSelected) {
		this.cashierSelected = cashierSelected;
	}

	public Date getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(Date selectedDate) {
		this.selectedDate = selectedDate;
	}

	public List<Sale> getListSalesOfDay() {
		return listSalesOfDay;
	}

	public void setListSalesOfDay(List<Sale> listSalesOfDay) {
		this.listSalesOfDay = listSalesOfDay;
	}

	public Long getCountMissingCashier() {
		return countMissingCashier;
	}

	public void setCountMissingCashier(Long countMissingCashier) {
		this.countMissingCashier = countMissingCashier;
	}

	public Cashier getItemCashier() {
		return itemCashier;
	}

	public void setItemCashier(Cashier itemCashier) {
		this.itemCashier = itemCashier;
	}

	public LoginBean getLoginBean() {
		return loginBean;
	}

	public void setLoginBean(LoginBean loginBean) {
		this.loginBean = loginBean;
	}

	public Year getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(Year currentYear) {
		this.currentYear = currentYear;
	}

	public void setTotalGeral(BigDecimal totalGeral) {
		this.totalGeral = totalGeral;
	}

	public Year getYear() {
		return year;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	public void loadDistinctDatesNotInCashier() {
		// Exemplo: buscar datas únicas de vendas do banco de dados
		uniqueDates = saleService.findDistinctDatesNotInCashier(); // ou sua lógica

		// Se ainda for null, inicializa como lista vazia
		if (uniqueDates == null) {
			uniqueDates = new ArrayList<>();
		}
	}

	public void loadUniqueDates() {
		// Exemplo: buscar datas únicas de vendas do banco de dados
		uniqueDates = saleService.findDistinctDatesNotInCashier(); // ou sua lógica

		// Se ainda for null, inicializa como lista vazia
		if (uniqueDates == null) {
			uniqueDates = new ArrayList<>();
		}
	}

	public List<Date> getDistinctSaleNotInCashier() {
		// Verifica se a lista está null ou vazia
		if (uniqueDates == null || uniqueDates.isEmpty()) {
			return new ArrayList<>(); // Retorna lista vazia
		}

		return uniqueDates.stream()
				.map(localDate -> Date.from(localDate.atStartOfDay(ZoneId.of("Africa/Maputo")).toInstant()))
				.collect(Collectors.toList());
	}

	public List<Date> getUniqueDatesAsDate() {
		// Verifica se a lista está null ou vazia
		if (uniqueDates == null || uniqueDates.isEmpty()) {
			return new ArrayList<>(); // Retorna lista vazia
		}

		return uniqueDates.stream()
				.map(localDate -> Date.from(localDate.atStartOfDay(ZoneId.of("Africa/Maputo")).toInstant()))
				.collect(Collectors.toList());
	}

}

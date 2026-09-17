package mz.co.crud.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import mz.co.crud.model.Customer;
import mz.co.crud.model.Product;
import mz.co.crud.model.Sale;
import mz.co.crud.model.SaleItem;
import mz.co.crud.model.Year;
import mz.co.crud.service.ProductService;
import mz.co.crud.service.SaleService;
import mz.co.crud.service.YearService;

@Component("reportSaleBean")
@SessionScope
public class ReportSaleBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Sale currentSale;

	private SaleItem currentItem;

	private Date selectedDate;

	private Customer customer;

	private List<Sale> listSalesOfDay;

	private List<Sale> listSales;

	private List<Customer> listCustomers;

	private List<Product> listProducts;

	private Product selectedProduct;

	private Customer selectedCustomer;

	private Year year;

	private Year currentYear;

	private int quantity;

	private double discount;

	private boolean anonymousCustomer;

	private String newSale;

	private Date currentDate = new Date();

	private BigDecimal totalGeral;

	private boolean editing = false;

	private Long editingSaleId;

	private String saleCode;

	private List<Sale> filteredSales;

	@Autowired
	private DashboardBean dashboardBean;

	@Autowired
	private ProductService productService;

	@Autowired
	private SaleService saleService;

	@Autowired
	private YearService yearService;

	@PostConstruct
	public void init() {

		currentSale = new Sale();

		currentSale.setSalesItems(new ArrayList<>());

		currentItem = new SaleItem();

		getCurrentDate();

		loadSales();

		setCurrentItem(new SaleItem());
	}

	public List<Sale> getFilteredSales() {
		return filteredSales;
	}

	public void setFilteredSales(List<Sale> filteredSales) {
		this.filteredSales = filteredSales;
	}

	public void recalculateTotals() {
		List<Sale> salesList = getCurrentSalesList();

		if (salesList == null || salesList.isEmpty()) {
			this.totalGeral = BigDecimal.ZERO;
			return;
		}

		this.totalGeral = salesList.stream().map(Sale::getTotalSale).filter(Objects::nonNull).reduce(BigDecimal.ZERO,
				BigDecimal::add);
	}

	public BigDecimal getTotalGeral() {
		List<Sale> activeList = getCurrentSalesList();

		if (activeList == null || activeList.isEmpty()) {
			return BigDecimal.ZERO;
		}

		return activeList.stream().map(Sale::getTotalSale).filter(Objects::nonNull).reduce(BigDecimal.ZERO,
				BigDecimal::add);
	}

	 
//	public BigDecimal getTotalGeral() {
//	    List<Sale> activeList = getCurrentSalesList();
//	    if (activeList == null || activeList.isEmpty()) {
//	        return BigDecimal.ZERO;
//	    }
//
//	    return activeList.stream()
//	            .map(Sale::getTotalSale)
//	            .filter(java.util.Objects::nonNull)
//	            .reduce(BigDecimal.ZERO, BigDecimal::add);
//	}

	public List<Sale> getCurrentSalesList() {
		return (filteredSales != null) ? filteredSales : listSales;
	}

	public void closeCashRegister() {
		try {
			// Sua lógica para fechar o caixa aqui
			// Exemplo: salvar fechamento, gerar relatório final, etc.

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Caixa fechado com sucesso!"));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao fechar o caixa: " + e.getMessage()));
		}
	}

	public void loadYear() {

		setCurrentYear(yearService.findByYear(2025));
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public List<Product> completeProduct(String query) {
		return listProducts.stream().filter(p -> p.getName().toLowerCase().contains(query.toLowerCase())).toList();
	}

	// 🔹 Carrega todos os sales
	public void loadSales() {
		try {
			listSales = new ArrayList<>();
			listSales = saleService.findAllWithItems();
		} catch (Exception e) {
			addMessage("Erro ao carregar as vendas: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
		}
	}

	public void loadProducts() {
		listProducts = productService.findAll();
	}

	public void calculateSubtotal() {
		if (currentItem != null && currentItem.getPrice() != null && currentItem.getQuantity() != null) {
			BigDecimal price = currentItem.getPrice();
			BigDecimal quantity = new BigDecimal(currentItem.getQuantity());
			currentItem.setSubtotal(price.multiply(quantity));

			// System.out.println("Subtotal calculado: " + currentItem.getSubtotal());
		}
	}

	public void selectProduct() {
		if (getCurrentItem().getProduct() != null) {
			Product p = getCurrentItem().getProduct();
			getCurrentItem().setPrice(p.getSalePrice());
			getCurrentItem().setQuantity(1);
			calculateSubtotal();
		}
	}

	public void viewSale(Sale sale) {
		try {
			if (sale == null) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Venda não encontrada."));
				return;
			}

			this.editing = true;
			this.editingSaleId = sale.getId();
			this.setCurrentSale(sale);
			this.setSaleCode(sale.getSaleCode());
			this.setSelectedCustomer(sale.getCustomer());
			this.currentDate = sale.getDateSale();
			this.currentSale.setSalesItems(new ArrayList<>(sale.getSalesItems()));
			this.selectedProduct = null;
			this.currentItem = new SaleItem();
			dashboardBean.setCurrentPage("/pages/report-sale-view.xhtml");

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Erro ao carregar venda para edição: " + e.getMessage()));
		}
	}

	public void view(Sale sale) {
		this.setCurrentSale(sale);
	}

	/** Ações de navegação **/
	public String cancel() {
		return "sales.xhtml?faces-redirect=true";
	}

	public String goNext() {
		return "sale-confirm.xhtml?faces-redirect=true";
	}

	public void onFilter() {
		if (filteredSales != null && !filteredSales.isEmpty()) {
			setTotalGeral(filteredSales.stream()
					.map(sale -> sale.getTotalSale() != null ? sale.getTotalSale() : BigDecimal.ZERO)
					.reduce(BigDecimal.ZERO, BigDecimal::add));
		} else {
			// se não houver filtro ou lista estiver vazia, soma o total de todas as vendas
			setTotalGeral(
					listSales.stream().map(sale -> sale.getTotalSale() != null ? sale.getTotalSale() : BigDecimal.ZERO)
							.reduce(BigDecimal.ZERO, BigDecimal::add));
		}
	}

	public String goBack() {
		return "sale-include.xhtml?faces-redirect=true";
	}

	private void addMessage(String msg, FacesMessage.Severity severity) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, msg, null));
	}

	public void resetSale() {
		currentSale = new Sale();
		currentSale.setSalesItems(new ArrayList<>());
		selectedCustomer = null;
		selectedProduct = null;
		currentItem = new SaleItem();
	}

	public List<Sale> getListSalesOfDay() {
		return listSalesOfDay;
	}

	public void setListSalesOfDay(List<Sale> listSalesOfDay) {
		this.listSalesOfDay = listSalesOfDay;
	}

	public List<Customer> getListCustomers() {
		return listCustomers;
	}

	public void setListCustomers(List<Customer> listCustomers) {
		this.listCustomers = listCustomers;
	}

	public List<Product> getListProducts() {
		return listProducts;
	}

	public void setListProducts(List<Product> listProducts) {
		this.listProducts = listProducts;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public Date getSelectedDate() {
		if (selectedDate == null) {
			selectedDate = new Date();
		}
		return selectedDate;
	}

	public void setSelectedDate(Date selectedDate) {
		this.selectedDate = selectedDate;
	}

	public Sale getCurrentSale() {
		return currentSale;
	}

	public void setCurrentSale(Sale currentSale) {
		this.currentSale = currentSale;
	}

	public Product getSelectedProduct() {
		return selectedProduct;
	}

	public void setSelectedProduct(Product selectedProduct) {
		this.selectedProduct = selectedProduct;
	}

	public boolean isAnonymousCustomer() {
		return anonymousCustomer;
	}

	public void setAnonymousCustomer(boolean anonymousCustomer) {
		this.anonymousCustomer = anonymousCustomer;
	}

	public SaleItem getCurrentItem() {
		return currentItem;
	}

	public void setCurrentItem(SaleItem currentItem) {
		this.currentItem = currentItem;
	}

	public Year getYear() {
		return year;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	public String getNewSale() {
		return newSale;
	}

	public void setNewSale(String newSale) {
		this.newSale = newSale;
	}

	public Customer getSelectedCustomer() {
		return selectedCustomer;
	}

	public void setSelectedCustomer(Customer selectedCustomer) {
		this.selectedCustomer = selectedCustomer;
	}

	public Year getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(Year currentYear) {
		this.currentYear = currentYear;
	}

	public List<Sale> getListSales() {
		return listSales;
	}

	public void setListSales(List<Sale> listSales) {
		this.listSales = listSales;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getSaleCode() {
		return saleCode;
	}

	public void setSaleCode(String saleCode) {
		this.saleCode = saleCode;
	}

	public boolean isEditing() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	public Long getEditingSaleId() {
		return editingSaleId;
	}

	public void setEditingSaleId(Long editingSaleId) {
		this.editingSaleId = editingSaleId;
	}

	public void setTotalGeral(BigDecimal totalGeral) {
		this.totalGeral = totalGeral;
	}

}

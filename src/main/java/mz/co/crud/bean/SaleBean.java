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
import java.util.Locale;
import java.util.Objects;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.line.LineChartDataSet;
import org.primefaces.model.charts.line.LineChartModel;
import org.primefaces.model.charts.line.LineChartOptions;
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
import mz.co.crud.service.CustomerService;
import mz.co.crud.service.ProductService;
import mz.co.crud.service.SaleService;
import mz.co.crud.service.YearService;

@Component("saleBean")
@SessionScope
public class SaleBean implements Serializable
{

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

	private String saleCode;

	private Long dailySales;

	private List<Sale> filteredSales;

	private BigDecimal sumDailySales;

	private LineChartModel dailySalesLineModel;

	@Autowired
	private ProductBean productBean;

	String code;

	private boolean editing = false;

	private Long editingSaleId;

	@Autowired
	private DashboardBean dashboardBean;

	@Autowired
	private ProductService productService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private SaleService saleService;

	@Autowired
	private YearService yearService;

	@PostConstruct
	public void init()
	{

		currentSale = new Sale();

		currentSale.setSalesItems(new ArrayList<>());

		currentItem = new SaleItem();

		// createSalesOgiveModel();

		createDailySalesLineModel();

		getCurrentDate();

		loadSales();

		loadSalesByDate();

		loadSumDailySales();

		loadDailySales();

		loadCustomers();

		loadProducts();

		loadYear();

		setCurrentItem(new SaleItem());
	}

	public void loadYear()
	{
		int currentYear = java.time.Year.now().getValue();
		this.setCurrentYear(yearService.findByYear(currentYear));
	}

	public Date getCurrentDate()
	{
		return currentDate;
	}

	public String getSaleCode()
	{

		return this.saleCode = saleService.generateSaleCode(this.getCurrentYear());
	}

	/**
	 * Cria o gráfico de vendas dos últimos 7 dias. Cada ponto representa o valor
	 * total vendido naquele dia.
	 */

	private void createDailySalesLineModel()
	{

		dailySalesLineModel = new LineChartModel();

		ChartData data = new ChartData();
		LineChartDataSet dataSet = new LineChartDataSet();

		List<Object> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		LocalDate today = LocalDate.now();

		for (int i = 6; i >= 0; i--)
		{

			LocalDate date = today.minusDays(i);

			String label = String.format("%02d/%02d", date.getDayOfMonth(), date.getMonthValue());

			labels.add(label);

			BigDecimal total = BigDecimal.ZERO;

			Date searchDate = java.sql.Date.valueOf(date);

			List<Sale> sales = saleService.findByDate(searchDate);

			if (sales != null)
			{

				for (Sale sale : sales)
				{

					if (sale != null && sale.getTotalSale() != null)
					{
						total = total.add(sale.getTotalSale());
					}
				}
			}

			values.add(total.doubleValue());

			System.out.println("GRÁFICO - " + label + " | Vendas: " + (sales != null ? sales.size() : 0) + " | Total: " + total);
		}

		dataSet.setData(values);
		dataSet.setLabel("Vendas (MT)");

		dataSet.setFill(true);
		dataSet.setBorderColor("#2563eb");
		dataSet.setBackgroundColor("rgba(37, 99, 235, 0.15)");
		dataSet.setBorderWidth(3);
		dataSet.setTension(0.3);

		data.addChartDataSet(dataSet);
		data.setLabels(labels);

		dailySalesLineModel.setData(data);

		// IMPORTANTE
		LineChartOptions options = new LineChartOptions();

		CartesianScales scales = new CartesianScales();

		CartesianLinearAxes yAxis = new CartesianLinearAxes();
		yAxis.setBeginAtZero(true);

		scales.addYAxesData(yAxis);

		options.setScales(scales);

		dailySalesLineModel.setOptions(options);
	}

	private void createsDailySalesLineModel()
	{

		dailySalesLineModel = new LineChartModel();

		ChartData data = new ChartData();

		LineChartDataSet dataSet = new LineChartDataSet();

		List<Object> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		LocalDate today = LocalDate.now();

		// Criar os 7 dias
		for (int i = 6; i >= 0; i--)
		{

			LocalDate date = today.minusDays(i);

			labels.add(String.format("%02d/%02d", date.getDayOfMonth(), date.getMonthValue()));

			BigDecimal total = BigDecimal.ZERO;

			List<Sale> sales = saleService.findByDate(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));

			if (sales != null)
			{

				for (Sale sale : sales)
				{

					if (sale.getTotalSale() != null)
					{
						total = total.add(sale.getTotalSale());
					}
				}
			}

			values.add(total.doubleValue());
		}

		dataSet.setData(values);
		dataSet.setLabel("Vendas (MT)");
		dataSet.setFill(true);
		dataSet.setTension(0.4);

		data.addChartDataSet(dataSet);
		data.setLabels(labels);

		dailySalesLineModel.setData(data);
	}

	private void createDailySalesLineModels()
	{

		dailySalesLineModel = new LineChartModel();

		ChartData data = new ChartData();

		LineChartDataSet dataSet = new LineChartDataSet();

		List<Object> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();

		Calendar calendar = Calendar.getInstance();

		// Começar 6 dias atrás
		calendar.setTime(new Date());
		calendar.add(Calendar.DAY_OF_MONTH, -6);

		for (int i = 0; i < 7; i++)
		{

			Date date = calendar.getTime();

			// Buscar vendas daquele dia
			List<Sale> sales = saleService.findByDate(date);

			BigDecimal total = BigDecimal.ZERO;

			if (sales != null && !sales.isEmpty())
			{

				for (Sale sale : sales)
				{

					if (sale != null && sale.getTotalSale() != null)
					{
						total = total.add(sale.getTotalSale());
					}
				}
			}

			// Formato do dia
			SimpleDateFormat labelFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

			labels.add(labelFormat.format(date));

			// Chart.js trabalha bem com Number
			values.add(total.doubleValue());

			// Próximo dia
			calendar.add(Calendar.DAY_OF_MONTH, 1);
		}

		dataSet.setData(values);
		dataSet.setLabel("Vendas (MT)");

		// Área preenchida abaixo da linha
		dataSet.setFill(true);

		// Cor da linha
		dataSet.setBorderColor("#2563eb");

		// Cor do preenchimento
		dataSet.setBackgroundColor("rgba(37, 99, 235, 0.15)");

		// Espessura da linha
		dataSet.setBorderWidth(3);

		// Suavidade da linha
		dataSet.setTension(0.3);

		data.addChartDataSet(dataSet);
		data.setLabels(labels);

		dailySalesLineModel.setData(data);
	}

	public void updateCustomerSelection()
	{
		if (anonymousCustomer)
		{

			int tempId = 1;

			for (Customer itemCustomer : listCustomers)
			{

				if (itemCustomer.getId() == tempId)
				{

					selectedCustomer.setId(1L);
					break;
				}

			}
		}

	}

	public List<Product> completeProduct(String query)
	{
		return listProducts.stream().filter(p -> p.getName().toLowerCase().contains(query.toLowerCase())).toList();
	}

	// 🔹 Carrega todos os sales
	public void loadSales()
	{
		try
		{
			listSales = new ArrayList<>();
			listSales = saleService.findAllWithItems();
		} catch (Exception e)
		{
			addMessage("Erro ao carregar as vendas: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
		}
	}

	public List<Sale> getFilteredSales()
	{
		return filteredSales;
	}

	public void setFilteredSales(List<Sale> filteredSales)
	{
		this.filteredSales = filteredSales;
	}

	public BigDecimal getTotalGeral()
	{
		List<Sale> salesList = (filteredSales != null && !filteredSales.isEmpty()) ? filteredSales : listSalesOfDay;

		if (salesList == null || salesList.isEmpty())
		{
			return BigDecimal.ZERO;
		}

		return salesList.stream().map(Sale::getTotalSale).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public BigDecimal calculateTotal()
	{
		List<Sale> salesList = (filteredSales != null && !filteredSales.isEmpty()) ? filteredSales : listSalesOfDay;

		return salesList.stream().map(Sale::getTotalSale).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public void loadSalesByDate()
	{

		listSalesOfDay = saleService.findByDate(currentDate);

	}

	public List<Sale> retrieveSalesOfDay(String date)
	{
		System.out.println("=== Lista e data ===");
		System.out.println("Data a tual: " + date);
		System.out.println("Lista de vendas do dia " + date);

		// return listSalesOfDay = saleService.findDateByDate(date);
		return listSalesOfDay = saleService.findAll();

	}

	public Long loadDailySales()
	{
		Long countDailySales;
		countDailySales = saleService.countDailySales(currentDate);
		setDailySales(countDailySales);
		return getDailySales();
	}

	public BigDecimal loadSumDailySales()
	{

		return setSumDailySales(saleService.sumDailySales(currentDate));
	}

	public void loadCustomers()
	{
		listCustomers = customerService.findAll();
	}

	public void loadNewSale()
	{

		newSale();

		if (year == null)
		{

			int currentYear = java.time.LocalDate.now().getYear();

			year = yearService.findByYear(currentYear);

			if (year == null)
			{
				year = new Year();
				year.setYear((int) currentYear);
				year.setDescription("Ano " + currentYear);
			}
		}
	}

	public void newSale()
	{
		try
		{
			int currentYear = java.time.LocalDate.now().getYear();

			year = yearService.findOrCreateByYear(currentYear);

			currentSale = new Sale();

			currentSale.setDateSale(new Date());

			currentSale.setTotalSale(BigDecimal.ZERO);

			currentSale.setSalesItems(new ArrayList<>());

			code = saleService.generateSaleCode(year);

			currentSale.setNumericCode(Integer.parseInt(code.split("/")[0]));

			this.currentSale.setSaleCode(code);

			listCustomers = customerService.findAll();

			listProducts = productService.findAll();

			setCurrentItem(new SaleItem());

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Erro ao criar nova venda: " + e.getMessage()));
		}
	}

	public void loadProducts()
	{
		listProducts = productService.findAll();
	}

	public void saveTemporary()
	{
		dashboardBean.setCurrentPage("/pages/sale-confirm.xhtml");
	}

	public void onProductSelect()
	{
		if (selectedProduct == null)
		{
			currentItem = new SaleItem();
			return;
		}

		// Verificar estoque
		Integer stock = selectedProduct.getCurrentStock();

		if (stock == null || stock <= 0)
		{
			addMessage("O produto \"" + selectedProduct.getName() + "\" está sem estoque!", FacesMessage.SEVERITY_WARN);

			selectedProduct = null;
			currentItem = new SaleItem();
			return;
		}

		if (currentItem == null)
		{
			currentItem = new SaleItem();
		}

		currentItem.setProduct(selectedProduct);
		currentItem.setPrice(selectedProduct.getSalePrice());
		currentItem.setQuantity(1);

		calculateSubtotal();
	}

	public void onProductSelects()
	{

		if (selectedProduct != null)
		{
			if (currentItem == null)
			{
				currentItem = new SaleItem();
			}

			currentItem.setProduct(selectedProduct);
			currentItem.setPrice(selectedProduct.getSalePrice());
			currentItem.setQuantity(1);

			calculateSubtotal();

		} else
		{

			currentItem = new SaleItem();
		}
	}

	public void calculateSubtotal()
	{
		if (currentItem != null && currentItem.getPrice() != null && currentItem.getQuantity() != null)
		{
			BigDecimal price = currentItem.getPrice();
			BigDecimal quantity = new BigDecimal(currentItem.getQuantity());
			currentItem.setSubtotal(price.multiply(quantity));

		}
	}

	public void calcularSubtotal()
	{
		if (currentItem == null || currentItem.getProduct() == null)
			return;

		BigDecimal quantity = BigDecimal.valueOf(currentItem.getQuantity() != null ? currentItem.getQuantity() : 0);
		BigDecimal subtotal = currentItem.getPrice().multiply(quantity);

		currentItem.setSubtotal(subtotal);
		updateTotal();
	}

	public void selectProduct()
	{
		if (getCurrentItem() == null || getCurrentItem().getProduct() == null)
		{
			return;
		}

		Product p = getCurrentItem().getProduct();

		Integer stock = p.getCurrentStock();

		if (stock == null || stock <= 0)
		{
			addMessage("O produto \"" + p.getName() + "\" está sem estoque!", FacesMessage.SEVERITY_WARN);

			getCurrentItem().setProduct(null);
			getCurrentItem().setPrice(null);
			getCurrentItem().setQuantity(null);

			return;
		}

		getCurrentItem().setPrice(p.getSalePrice());
		getCurrentItem().setQuantity(1);

		calculateSubtotal();
	}

	// Metodo para adicionar os items
	public void addItem()
	{
		if (currentItem == null)
		{
			addMessage("Erro interno: item não inicializado!", FacesMessage.SEVERITY_ERROR);
			return;
		}

		if (currentItem.getProduct() == null)
		{
			addMessage("Selecione um produto!", FacesMessage.SEVERITY_WARN);
			return;
		}

		Product product = currentItem.getProduct();

		Integer stock = product.getCurrentStock();

		if (stock == null || stock <= 0)
		{
			addMessage("O produto \"" + product.getName() + "\" está sem estoque!", FacesMessage.SEVERITY_WARN);

			selectedProduct = null;
			currentItem = new SaleItem();

			return;
		}

		if (currentItem.getQuantity() == null || currentItem.getQuantity() <= 0)
		{
			addMessage("Informe uma quantidade válida!", FacesMessage.SEVERITY_WARN);
			return;
		}

		if (currentItem.getQuantity() > stock)
		{
			addMessage("Quantidade solicitada (" + currentItem.getQuantity() + ") superior ao estoque disponível (" + stock + ") para o produto \""
					+ product.getName() + "\"!", FacesMessage.SEVERITY_WARN);

			return;
		}

		boolean exists = currentSale.getSalesItems().stream().anyMatch(i -> i.getProduct() != null && i.getProduct().getId().equals(product.getId()));

		if (exists)
		{
			addMessage("O produto \"" + product.getName() + "\" já foi adicionado à venda!", FacesMessage.SEVERITY_WARN);
			return;
		}

		currentItem.setSale(currentSale);

		currentSale.getSalesItems().add(currentItem);

		updateTotal();

		selectedProduct = null;
		currentItem = new SaleItem();

		addMessage("Produto adicionado com sucesso!", FacesMessage.SEVERITY_INFO);
	}

	public void addItems()
	{
		if (currentItem == null)
		{
			addMessage("Erro interno: item não inicializado!", FacesMessage.SEVERITY_ERROR);
			return;
		}

		if (currentItem.getProduct() == null)
		{
			addMessage("Selecione um produto!", FacesMessage.SEVERITY_WARN);
			return;
		}

		if (currentItem.getQuantity() == null || currentItem.getQuantity() <= 0)
		{
			addMessage("Informe a quantidade!", FacesMessage.SEVERITY_WARN);
			return;
		}

		boolean exists = currentSale.getSalesItems().stream().anyMatch(i -> i.getProduct().getId().equals(currentItem.getProduct().getId()));

		if (exists)
		{
			addMessage("Produto já adicionado!", FacesMessage.SEVERITY_WARN);
			return;
		}

		currentItem.setSale(currentSale);
		currentSale.getSalesItems().add(currentItem);

		updateTotal();

		selectedProduct = null;
		currentItem = new SaleItem();

		addMessage("Produto adicionado com sucesso!", FacesMessage.SEVERITY_INFO);
	}

	// Método para remover item da venda
	public void removeItem(SaleItem item)
	{
		try
		{
			if (currentSale == null)
			{
				addMessage("Não existe uma venda em andamento.", FacesMessage.SEVERITY_WARN);
				return;
			}

			if (currentSale.getSalesItems() == null || currentSale.getSalesItems().isEmpty())
			{
				addMessage("O carrinho está vazio.", FacesMessage.SEVERITY_WARN);
				return;
			}

			if (item == null)
			{
				addMessage("Item inválido.", FacesMessage.SEVERITY_ERROR);
				return;
			}

			// Busca comparando primariamente pelo Produto (ID do produto) ou pelo ID do
			// SaleItem
			SaleItem itemToRemove = currentSale.getSalesItems().stream().filter(i ->
			{
				// 1. Compara por ID do SaleItem (se existir no banco)
				if (i.getId() != null && item.getId() != null)
				{
					return i.getId().equals(item.getId());
				}
				// 2. Compara pelo Produto (Garante o funcionamento de itens novos em memória)
				if (i.getProduct() != null && item.getProduct() != null && i.getProduct().getId() != null && item.getProduct().getId() != null)
				{
					return i.getProduct().getId().equals(item.getProduct().getId());
				}
				// 3. Fallback: igualdade de objetos (se tiver equals/hashCode bem definidos)
				return i.equals(item);
			}).findFirst().orElse(null);

			if (itemToRemove == null)
			{
				addMessage("O item não foi encontrado no carrinho.", FacesMessage.SEVERITY_WARN);
				return;
			}

			// Remover da lista
			boolean removed = currentSale.getSalesItems().remove(itemToRemove);

			if (removed)
			{
				// Recalcular o total da venda
				updateTotal();
				addMessage("Produto removido do carrinho com sucesso!", FacesMessage.SEVERITY_INFO);
			} else
			{
				addMessage("Não foi possível remover o produto.", FacesMessage.SEVERITY_ERROR);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Erro ao remover item: " + e.getMessage()));
		}
	}

	// Método para calcular o total da venda
	private void calculateTotals()
	{
		if (currentSale != null && currentSale.getSalesItems() != null)
		{
			BigDecimal total = BigDecimal.ZERO;
			for (SaleItem item : currentSale.getSalesItems())
			{
				total = total.add(item.getSubtotal());
			}
			currentSale.setTotalSale(total);
		}
	}

	private void updateTotal()
	{
		if (currentSale == null || currentSale.getSalesItems() == null)
			return;

		BigDecimal total = currentSale.getSalesItems().stream().map(SaleItem::getSubtotal).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);

		currentSale.setTotalSale(total);
	}

	/** Ações de navegação **/
	public String cancel()
	{
		newSale();
		return "sales.xhtml?faces-redirect=true";
	}

	public String goNext()
	{

		return "sale-confirm.xhtml?faces-redirect=true";
	}

	public String goBack()
	{
		return "sale-include.xhtml?faces-redirect=true";
	}

	public void editSale(Sale sale)
	{
		try
		{
			if (sale == null || sale.getId().equals(null))
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Venda não encontrada."));
				return;
			}

			this.editing = true;
			this.editingSaleId = sale.getId();
			this.setCurrentSale(sale);
			// this.setSaleCode(sale.getSaleCode());
			this.setSelectedCustomer(sale.getCustomer());
			this.currentDate = sale.getDateSale();
			this.currentSale.setSalesItems(new ArrayList<>(sale.getSalesItems()));
			this.selectedProduct = null;
			this.currentItem = new SaleItem();
			dashboardBean.setCurrentPage("/pages/sale-include.xhtml");

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Erro ao carregar venda para edição: " + e.getMessage()));
		}
	}
//	public void viewSpecificSale(Long saleId) {
//	    try {
//	        Sale sale = saleService.findByIdWithItems(saleId);  
//
//	        this.editing = true;
//	        this.editingSaleId = sale.getId();
//	        this.currentSale = sale;
//	        this.saleCode = sale.getSaleCode();
//	        this.selectedCustomer = sale.getCustomer();
//	        this.currentDate = sale.getDateSale();
//	        
//	        
//	        
//	        List<SaleItem> items = this.currentSale.getSalesItems();
//	        items.clear();
//	        items.addAll(sale.getSalesItems());
//	        
//	        this.currentSale.setSalesItems(new ArrayList<>(sale.getSalesItems()));
//
//	        
//	        this.selectedProduct = null;
//	        this.currentItem = new SaleItem();
//	        dashboardBean.setCurrentPage("/pages/sale-view.xhtml");
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        FacesContext.getCurrentInstance().addMessage(null,
//	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
//	                "Erro ao carregar venda: " + e.getMessage()));
//	    }
//	}
//	public void viewSpecificSale(Long saleId) {
//	    try {
//	        Sale sale = saleService.findByIdWithItems(saleId);
//	        
//	        // LOG PARA DEBUG - ADICIONE ISSO
//	        System.out.println("=== DEBUG SALE ===");
//	        System.out.println("Sale ID: " + sale.getId());
//	        System.out.println("Sale Code: " + sale.getSaleCode());
//	        System.out.println("Items não nulos? " + (sale.getSalesItems() != null));
//	        System.out.println("Quantidade de items: " + (sale.getSalesItems() != null ? sale.getSalesItems().size() : 0));
//	        
//	        if (sale.getSalesItems() != null && !sale.getSalesItems().isEmpty()) {
//	            System.out.println("Primeiro item: " + sale.getSalesItems().get(0).getProduct().getName());
//	        }
//	        System.out.println("==================");
//
//	        this.editing = true;
//	        this.editingSaleId = sale.getId();
//	        this.currentSale = sale;
//	        this.saleCode = sale.getSaleCode();
//	        this.selectedCustomer = sale.getCustomer();
//	        this.currentDate = sale.getDateSale();
//
//	        this.selectedProduct = null;
//	        this.currentItem = new SaleItem();
//	        dashboardBean.setCurrentPage("/pages/sale-view.xhtml");
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        FacesContext.getCurrentInstance().addMessage(null,
//	            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
//	                "Erro ao carregar venda: " + e.getMessage()));
//	    }
//	}

	public void viewSpecificSale(Long saleId)
	{
		try
		{
			Sale sale = saleService.findByIdWithItems(saleId);

			if (sale == null)
			{
				addMessage("Venda não encontrada.", FacesMessage.SEVERITY_WARN);
				return;
			}

			// 1. Define o modo apenas como LEITURA (Evita acionar lógicas de edição/novo)
			this.editing = false;
			this.editingSaleId = sale.getId();
			this.currentSale = sale;

			// 2. Garante que o código venha diretamente do objeto do Banco de Dados
			this.saleCode = sale.getSaleCode();
			this.selectedCustomer = sale.getCustomer();
			this.currentDate = sale.getDateSale();

			this.selectedProduct = null;
			this.currentItem = new SaleItem();

			// 3. Redireciona a página via DashboardBean
			dashboardBean.setCurrentPage("/pages/sale-view.xhtml");

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Erro ao carregar venda: " + e.getMessage()));
		}
	}

	public void viewSpecificSales(Long saleId)
	{
		try
		{
			this.currentSale = null;
			this.selectedProduct = null;
			this.currentItem = null;

			Sale sale = saleService.findByIdWithItems(saleId);

			this.editing = true;
			this.editingSaleId = sale.getId();
			this.currentSale = sale;
			this.saleCode = sale.getSaleCode();
			this.selectedCustomer = sale.getCustomer();
			this.currentDate = sale.getDateSale();

			this.selectedProduct = null;
			this.currentItem = new SaleItem();

			dashboardBean.setCurrentPage("/pages/sale-view.xhtml");

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Erro ao carregar venda: " + e.getMessage()));
		}
	}

	public void viewSale(Sale sale)
	{
		try
		{
			if (sale == null)
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Venda não encontrada."));
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
			dashboardBean.setCurrentPage("/pages/sale-view.xhtml");

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Erro ao carregar venda para edição: " + e.getMessage()));
		}
	}

	private void addMessage(String msg, FacesMessage.Severity severity)
	{
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, msg, null));
	}

	// Método atualizado para salvar (criar ou editar)
	// Método atualizado para salvar (criar ou editar)
	public void confirmSale()
	{
		try
		{

			int currentYear = java.time.Year.now().getValue();
			setCurrentYear(yearService.findByYear(currentYear));

			Year year = new Year();
			year.setYear(LocalDate.now().getYear());
			code = saleService.generateSaleCode(getCurrentYear());

			if (currentSale == null || currentSale.getSalesItems() == null || currentSale.getSalesItems().isEmpty())
			{
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Não há itens na venda."));
				return;
			}

			if (editing && editingSaleId != null)
			{
				Sale originalSale = saleService.findById(editingSaleId);
				if (originalSale != null)
				{
					for (SaleItem originalItem : originalSale.getSalesItems())
					{
						Product product = originalItem.getProduct();
						product.setCurrentStock(product.getCurrentStock() + originalItem.getQuantity());
						productService.saveOrUpdateProduct(product);
					}
				}
			}

			// Atualizar o stock dos produtos com os novos valores
			for (SaleItem item : currentSale.getSalesItems())
			{
				Product product = item.getProduct();
				int newStock = product.getCurrentStock() - item.getQuantity();

				if (newStock < 0)
				{
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Stock insuficiente para o produto: " + product.getName()));
					return;
				}

				product.setCurrentStock(newStock);
				productService.saveOrUpdateProduct(product);
			}

			currentSale.setDateSale(currentDate != null ? currentDate : new Date());

			if (selectedCustomer != null)
			{
				currentSale.setCustomer(selectedCustomer);
			}

			if (editing && editingSaleId != null)
			{
				currentSale.setSaleCode(saleCode);
				currentSale.setYear(getCurrentYear());
				currentSale.setDateSale(currentDate);
				currentSale.setCustomer(selectedCustomer);
				currentSale.setNumericCode(Integer.parseInt(code.split("/")[0]));
				currentSale.setId(editingSaleId);
				saleService.saveOrUpdate(currentSale);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Venda atualizada com sucesso! Venda nº: " + currentSale.getSaleCode()));
			} else
			{
				currentSale.setSaleCode(saleCode);
				currentSale.setYear(getCurrentYear());
				currentSale.setDateSale(currentDate);
				currentSale.setCustomer(selectedCustomer);
				currentSale.setNumericCode(Integer.parseInt(code.split("/")[0]));
				saleService.saveOrUpdate(currentSale);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Venda confirmada com sucesso! Venda nº: " + currentSale.getSaleCode()));
			}

			// RECARREGA AS MÉTRICAS DE VENDAS E FATURAMENTO DO DIA
			loadDashboardMetrics();

			createDailySalesLineModel();

			// RECARREGA OS DADOS DE ESTOQUE NO PRODUCTBEAN (SE INJETADO)
			if (productBean != null)
			{
				productBean.init();
			}

			resetSale();

		} catch (Exception e)
		{
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Erro ao confirmar a venda: " + e.getMessage()));
		}
	}

	public void loadDashboardMetrics()
	{
		loadSalesByDate();
		loadDailySales();
		loadSumDailySales();
	}

	public void resetSale()
	{
		currentSale = new Sale();
		currentSale.setSalesItems(new ArrayList<>());
		selectedCustomer = null;
		selectedProduct = null;
		currentItem = new SaleItem();
		saleCode = generateSaleCode(); // Gerar novo código de venda
	}

	// Método para gerar código da venda
	private String generateSaleCode()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return "VND" + sdf.format(new Date());
	}

	public List<Sale> getListSalesOfDay()
	{
		return listSalesOfDay;
	}

	public void setListSalesOfDay(List<Sale> listSalesOfDay)
	{
		this.listSalesOfDay = listSalesOfDay;
	}

	public List<Customer> getListCustomers()
	{
		return listCustomers;
	}

	public void setListCustomers(List<Customer> listCustomers)
	{
		this.listCustomers = listCustomers;
	}

	public List<Product> getListProducts()
	{
		return listProducts;
	}

	public void setListProducts(List<Product> listProducts)
	{
		this.listProducts = listProducts;
	}

	public int getQuantity()
	{
		return quantity;
	}

	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}

	public double getDiscount()
	{
		return discount;
	}

	public void setDiscount(double discount)
	{
		this.discount = discount;
	}

	public Date getSelectedDate()
	{
		if (selectedDate == null)
		{
			selectedDate = new Date();
		}
		return selectedDate;
	}

	public void setSelectedDate(Date selectedDate)
	{
		this.selectedDate = selectedDate;
	}

	public Sale getCurrentSale()
	{
		return currentSale;
	}

	public void setCurrentSale(Sale currentSale)
	{
		this.currentSale = currentSale;
	}

	public Product getSelectedProduct()
	{
		return selectedProduct;
	}

	public void setSelectedProduct(Product selectedProduct)
	{
		this.selectedProduct = selectedProduct;
	}

	public boolean isAnonymousCustomer()
	{
		return anonymousCustomer;
	}

	public void setAnonymousCustomer(boolean anonymousCustomer)
	{
		this.anonymousCustomer = anonymousCustomer;
	}

	public SaleItem getCurrentItem()
	{
		return currentItem;
	}

	public void setCurrentItem(SaleItem currentItem)
	{
		this.currentItem = currentItem;
	}

	public Year getYear()
	{
		return year;
	}

	public void setYear(Year year)
	{
		this.year = year;
	}

	public String getNewSale()
	{
		return newSale;
	}

	public void setNewSale(String newSale)
	{
		this.newSale = newSale;
	}

	public Customer getSelectedCustomer()
	{
		return selectedCustomer;
	}

	public void setSelectedCustomer(Customer selectedCustomer)
	{
		this.selectedCustomer = selectedCustomer;
	}

	public Year getCurrentYear()
	{
		return currentYear;
	}

	public void setCurrentYear(Year currentYear)
	{
		this.currentYear = currentYear;
	}

	public void setSaleCode(String saleCode)
	{
		this.saleCode = saleCode;
	}

	public List<Sale> getListSales()
	{
		if (listSales == null)
		{
			listSales = saleService.findByDate(currentDate); // Carregar do banco
		}
		return listSales;
	}

	public void setListSales(List<Sale> listSales)
	{
		this.listSales = listSales;
	}

	public Customer getCustomer()
	{
		return customer;
	}

	public void setCustomer(Customer customer)
	{
		this.customer = customer;
	}

	public Long getDailySales()
	{
		return dailySales;
	}

	public BigDecimal getSumDailySales()
	{
		return sumDailySales;
	}

	public void setDailySales(Long dailySales)
	{
		this.dailySales = dailySales;
	}

	public BigDecimal setSumDailySales(BigDecimal sumDailySales)
	{
		this.sumDailySales = sumDailySales;
		return sumDailySales;
	}

	public boolean isEditing()
	{
		return editing;
	}

	public void setEditing(boolean editing)
	{
		this.editing = editing;
	}

	public Long getEditingSaleId()
	{
		return editingSaleId;
	}

	public void setEditingSaleId(Long editingSaleId)
	{
		this.editingSaleId = editingSaleId;
	}

	public LineChartModel getDailySalesLineModel()
	{
		return dailySalesLineModel;
	}

}

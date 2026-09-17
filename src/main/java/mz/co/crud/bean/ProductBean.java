package mz.co.crud.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import mz.co.crud.model.Category;
import mz.co.crud.model.Product;
import mz.co.crud.model.Supplier;
import mz.co.crud.service.CategoryService;
import mz.co.crud.service.ProductService;
import mz.co.crud.service.SupplierService;

@Component ("productBean")  
@SessionScope
public class ProductBean implements Serializable
{

	private static final long serialVersionUID = 1L;

	private Product product;

	private List<Product> listProduct;

	private List<Category> listCategory;

	private Long supplierId;

	private Long totalProducts;

	private Long zeroStockProducts;

	private boolean editMode = false;

	private List<Product> filteredProducts;

	private List<Supplier> listSupplier;

	private Long selectedSupplierId;

	// For search/filters
	private String searchTerm;

	private String selectedCategory;

	private String selectedManufacturer;

	private List<Product> lastProducts;

	private List<Product> expiredProducts;

	private LocalDate registrationDate;

	private LocalDate expirationDate;

	private Category category;

	private Supplier supplier;

	@Autowired
	private DashboardBean dashboardBean;

	@Autowired
	private SupplierService supplierService;

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	private List<Product> nearExpiryProducts;

	private BarChartModel stockBarModel;

	private BarChartModel topSalesBarModel;

	private PieChartModel topSalesPieModel;

	private List<Product> productsWithNoMoviment = new ArrayList<>();

	@PostConstruct
	public void init()
	{
		product = new Product();

		loadSuppliers();

		loadCategories();

		loadProducts();

		loadTotalProducts();

		loadZeroStockProducts();

		createTopSalesPieModel();

		createStockBarModel();

		createTopSalesBarModel();

		loadProductsWithNoMoviment();
	}

	private void createTopSalesPieModel()
	{
		topSalesPieModel = new PieChartModel();
		ChartData data = new ChartData();

		PieChartDataSet dataSet = new PieChartDataSet();

		List<Number> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<String> bgColors = new ArrayList<>();

		// O repositório já retorna List<Object[]>, não precisa de stream().limit(5)
		// pois a consulta já limita a 5
		List<Object[]> topSalesData = productService.findTopSellingProducts();

		// Paleta de cores para cada fatia da pizza
		String[] colors = new String[]
		{ "#2563eb", "#3b82f6", "#60a5fa", "#93c5fd", "#bfdbfe" };
		int colorIndex = 0;

		for (Object[] row : topSalesData)
		{
			String productName = (String) row[0];
			Number totalSold = (Number) row[1];

			labels.add(productName);
			values.add(totalSold != null ? totalSold : 0);
			bgColors.add(colors[colorIndex % colors.length]);
			colorIndex++;
		}

		dataSet.setData(values);
		dataSet.setBackgroundColor(bgColors);

		data.addChartDataSet(dataSet);
		data.setLabels(labels);

		topSalesPieModel.setData(data);
	}

	// 4. Getter para a página XHTML
	public PieChartModel getTopSalesPieModel()
	{
		return topSalesPieModel;
	}

	private void createStockBarModel()
	{
		stockBarModel = new BarChartModel();
		ChartData data = new ChartData();

		BarChartDataSet dataSet = new BarChartDataSet();
		dataSet.setLabel("Quantidade em Estoque");

		List<Number> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<String> bgColors = new ArrayList<>();

		// Pega os 5 produtos com maior estoque cadastrado
		List<Product> topStockProducts = productService.findAll().stream().filter(p -> p.getCurrentStock() != null)
				.sorted(Comparator.comparing(Product::getCurrentStock).reversed()).limit(5).collect(Collectors.toList());

		for (Product p : topStockProducts)
		{
			values.add(p.getCurrentStock());
			labels.add(p.getName());
			bgColors.add("#2563eb"); // Cor azul
		}

		dataSet.setData(values);
		dataSet.setBackgroundColor(bgColors);

		data.addChartDataSet(dataSet);
		data.setLabels(labels);

		stockBarModel.setData(data);
	}

	private void createTopSalesBarModel()
	{
		topSalesBarModel = new BarChartModel();
		ChartData data = new ChartData();

		BarChartDataSet dataSet = new BarChartDataSet();
		dataSet.setLabel("Unidades Vendidas");

		List<Number> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<String> bgColors = new ArrayList<>();

		// Exemplo: pegando produtos cadastrados para exibição no gráfico
		List<Product> topProducts = productService.findAll().stream().limit(5).collect(Collectors.toList());

		for (Product p : topProducts)
		{
			values.add(p.getCurrentStock() != null ? p.getCurrentStock() : 0);
			labels.add(p.getName());
			bgColors.add("#1d4ed8");
		}

		dataSet.setData(values);
		dataSet.setBackgroundColor(bgColors);

		data.addChartDataSet(dataSet);
		data.setLabels(labels);

		topSalesBarModel.setData(data);
	}

	// 4. Getters para os modelos (chamados no arquivo XHTML via
	// #{productBean.stockBarModel})
	public BarChartModel getStockBarModel()
	{
		return stockBarModel;
	}

	public BarChartModel getTopSalesBarModel()
	{
		return topSalesBarModel;
	}

	public void loadSuppliers()
	{
		try
		{
			listSupplier = supplierService.findAll();

			System.out.println("Fornecedores encontrados: " + listSupplier.size());

		} catch (Exception e)
		{
			e.printStackTrace();
			listSupplier = new ArrayList<>();
		}
	}

	public void loadProductsWithNoMoviment()
	{
		try
		{
			this.productsWithNoMoviment = productService.findProductsWithNoMoviment();
		} catch (Exception e)
		{
			this.productsWithNoMoviment = new ArrayList<>();
			// adicione o tratamento de mensagem de erro do JSF se necessário
		}
	}

	public void incluirNovo()
	{
		System.out.println("------Produto-------");
		this.product = new Product();
		// Navegar via dashboardBean
		FacesContext context = FacesContext.getCurrentInstance();
		DashboardBean dashboardBean = context.getApplication().evaluateExpressionGet(context, "#{dashboardBean}", DashboardBean.class);
		dashboardBean.setCurrentPage("product-include.xhtml");
	}

	public void saveProduct()
	{
		try
		{
			// Validate
			if (!validateProduct())
			{
				return;
			}

			// Set supplier if selected
			if (selectedSupplierId != null)
			{
				supplierService.findByIds(selectedSupplierId).ifPresent(product::setSupplier);
			}

			// Set registration date if new
			if (product.getId() == null && product.getRegistrationDate() == null)
			{
				product.setRegistrationDate(LocalDate.now());
			}

			productService.saveOrUpdateProduct(product);
			clear();
			loadProducts();

			loadTotalProducts();
			loadZeroStockProducts();

		} catch (Exception e)
		{
		}
	}

	private boolean validateProduct()
	{
		boolean valid = true;

		if (product.getName() == null || product.getName().trim().isEmpty())
		{
			addMessage("Nome do produto é obrigatório", FacesMessage.SEVERITY_WARN);
			valid = false;
		}

		if (product.getCategory() == null || product.getCategory().equals(null))
		{
			addMessage("Categoria é obrigatória", FacesMessage.SEVERITY_WARN);
			valid = false;
		}

		if (product.getManufacturer() == null || product.getManufacturer().trim().isEmpty())
		{
			addMessage("Fabricante é obrigatório", FacesMessage.SEVERITY_WARN);
			valid = false;
		}

		if (product.getPurchasePrice() == null || product.getPurchasePrice().compareTo(BigDecimal.ZERO) < 0)
		{
			addMessage("Preço de compra inválido", FacesMessage.SEVERITY_WARN);

			valid = false;
		}

		if (product.getSalePrice() == null || product.getSalePrice().compareTo(BigDecimal.ZERO) < 0)
		{
			addMessage("Preço de venda inválido!", FacesMessage.SEVERITY_WARN);

			valid = false;
		}

		if (product.getSalePrice() != null && product.getPurchasePrice() != null && product.getSalePrice().compareTo(product.getPurchasePrice()) < 0)
		{
			addMessage("Preço de venda não pode ser menor que o preço de compra!", FacesMessage.SEVERITY_INFO);
			valid = false;
		}

		return valid;
	}

	public List<Product> getLastProducts()
	{
		if (lastProducts == null)
		{
			lastProducts = productService.lastProducts(5); // últimos 5 produtos
		}
		return lastProducts;
	}

	public List<Product> getExpiredProducts()
	{
		if (expiredProducts == null)
		{
			expiredProducts = productService.expiredProducts();
		}
		return expiredProducts;
	}

	public void clear()
	{
		product = new Product();
		product.setQuantity(0);
		product.setMinimumQuantity(0);
		product.setPurchasePrice(BigDecimal.ZERO);
		product.setSalePrice(BigDecimal.ZERO);
		selectedSupplierId = null;
		searchTerm = null;
		selectedCategory = null;
		selectedManufacturer = null;
	}

	public void edit(Product product)
	{
		this.product = product;
		if (product.getSupplier() != null)
		{
			this.selectedSupplierId = product.getSupplier().getId();
		} else
		{
			this.selectedSupplierId = null;
		}
	}

	public void searchByName()
	{
		if (searchTerm != null && !searchTerm.trim().isEmpty())
		{
			listProduct = productService.findByName(searchTerm);
		} else
		{
			loadProducts();
		}
	}

	public void filterByCategory()
	{
		if (selectedCategory != null && !selectedCategory.isEmpty())
		{
			listProduct = productService.findByCategory(selectedCategory);
		} else
		{
			loadProducts();
		}
	}

	public void filterByManufacturer()
	{
		if (selectedManufacturer != null && !selectedManufacturer.isEmpty())
		{
			listProduct = productService.findByManufacturer(selectedManufacturer);
		} else
		{
			loadProducts();
		}
	}

	public void filterBySupplier()
	{
		if (selectedSupplierId != null)
		{
			listProduct = productService.findBySupplier(selectedSupplierId);
		} else
		{
			loadProducts();
		}
	}

	public void loadLowStockProducts()
	{
		try
		{
			listProduct = productService.findBelowMinimumQuantity();
			addInfoMessage("Exibindo produtos com estoque baixo");
		} catch (Exception e)
		{
			addErrorMessage("Erro ao carregar produtos: " + e.getMessage());
		}
	}

	public void loadExpiredProducts()
	{
		try
		{
			listProduct = productService.findExpiredProducts();
			addWarnMessage("Exibindo produtos vencidos");
		} catch (Exception e)
		{
			addErrorMessage("Erro ao carregar produtos: " + e.getMessage());
		}
	}

	public void loadExpiringProducts()
	{
		try
		{
			listProduct = productService.findProductsExpiringSoon(30);
			addWarnMessage("Exibindo produtos que vencem em 30 dias");
		} catch (Exception e)
		{
			addErrorMessage("Erro ao carregar produtos: " + e.getMessage());
		}
	}

	public void loadActiveProducts()
	{
		try
		{
			listProduct = productService.findActiveProducts();
			addInfoMessage("Exibindo produtos ativos");
		} catch (Exception e)
		{
			addErrorMessage("Erro ao carregar produtos: " + e.getMessage());
		}
	}

	// ==================== Stock Management ====================

	public void addStock(Product product, Integer quantity)
	{
		try
		{
			if (quantity == null || quantity <= 0)
			{
				addWarnMessage("Quantidade deve ser maior que zero");
				return;
			}
			productService.addStock(product.getId(), quantity);
			addSuccessMessage("Estoque adicionado com sucesso!");
			loadProducts();
		} catch (Exception e)
		{
			addErrorMessage("Erro ao adicionar estoque: " + e.getMessage());
		}
	}

	public void removeStock(Product product, Integer quantity)
	{
		try
		{
			if (quantity == null || quantity <= 0)
			{
				addWarnMessage("Quantidade deve ser maior que zero");
				return;
			}
			productService.removeStock(product.getId(), quantity);
			addSuccessMessage("Estoque removido com sucesso!");
			loadProducts();
		} catch (IllegalArgumentException e)
		{
			addErrorMessage(e.getMessage());
		} catch (Exception e)
		{
			addErrorMessage("Erro ao remover estoque: " + e.getMessage());
		}
	}

	public void updatePrice(Product product, BigDecimal purchasePrice, BigDecimal salePrice)
	{
		try
		{
			if (purchasePrice == null || salePrice == null)
			{
				addWarnMessage("Preços inválidos");
				return;
			}

			if (salePrice.compareTo(purchasePrice) < 0)
			{
				addWarnMessage("Preço de venda não pode ser menor que o preço de compra");
				return;
			}

			productService.updatePrice(product.getId(), purchasePrice, salePrice);
			addSuccessMessage("Preços atualizados com sucesso!");
			loadProducts();
		} catch (Exception e)
		{
			addErrorMessage("Erro ao atualizar preços: " + e.getMessage());
		}
	}

	// ==================== Business Logic Methods ====================

	public boolean isProductExpired(Product product)
	{
		return product != null && product.isExpired();
	}

	public boolean isProductLowStock(Product product)
	{
		if (product == null)
		{
			return false;
		}

		if (product.getMinimumStock() == null)
		{
			return false;
		}

		return product.isBelowMinimumQuantity();
	}

	public boolean isProductExpiringSoon(Product product)
	{
		return product != null && product.isExpiringSoon(30);
	}

	public BigDecimal calculateProfit(Product product)
	{
		return product != null ? product.getProfit() : BigDecimal.ZERO;
	}

	public BigDecimal calculateProfitMargin(Product product)
	{
		return product != null ? product.getProfitMargin() : BigDecimal.ZERO;
	}

	// ==================== Helper Methods ====================

	private void addSuccessMessage(String message)
	{
		addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", message);
	}

	private void addInfoMessage(String message)
	{
		addMessage(FacesMessage.SEVERITY_INFO, "Informação", message);
	}

	private void addWarnMessage(String message)
	{
		addMessage(FacesMessage.SEVERITY_WARN, "Atenção", message);
	}

	private void addErrorMessage(String message)
	{
		addMessage(FacesMessage.SEVERITY_ERROR, "Erro", message);
	}

	private void addMessage(FacesMessage.Severity severity, String summary, String detail)
	{
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
	}

	public void loadProducts()
	{
		try
		{
			listProduct = productService.findAll();
		} catch (Exception e)
		{
			addMessage("Erro ao carregar produtos: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
		}
	}

	// 🔹 Carrega categorias para o selectOneMenu
	public void loadCategories()
	{
		try
		{
			listCategory = categoryService.listar();
		} catch (Exception e)
		{
			listCategory = new ArrayList<>();
			addMessage("Erro ao carregar categorias: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
		}
	}

	public List<Product> getProductsNearExpiry()
	{
		LocalDate today = LocalDate.now();
		LocalDate thirtyDaysFromNow = today.plusDays(30);

		return productService.findAll().stream().filter(p -> p.getExpirationDate() != null).filter(p -> !p.getExpirationDate().isBefore(today))
				.filter(p -> p.getExpirationDate().isBefore(thirtyDaysFromNow)).sorted(Comparator.comparing(Product::getExpirationDate))
				.collect(Collectors.toList());
	}

	// Lista de produtos vencidos
	public List<Product> getProductsExpired()
	{
		LocalDate today = LocalDate.now();

		return productService.findAll().stream().filter(p -> p.getExpirationDate() != null).filter(p -> p.getExpirationDate().isBefore(today))
				.sorted(Comparator.comparing(Product::getExpirationDate)).collect(Collectors.toList());
	}

	// Lista de produtos vencidos
//	public List<Product> getProductsWithNoMoviment()
//	{
//		return productService.findProductsWithNoMoviment();
//	}

	// Calcular quantos dias o produto está vencido
	public long calculateDaysExpired(LocalDate expirationDate)
	{
		if (expirationDate == null)
		{
			return 0;
		}
		return ChronoUnit.DAYS.between(expirationDate, LocalDate.now());
	}

	// Calcular dias até vencer (para produtos próximos da validade)
	public long calculateDaysUntilExpiry(LocalDate expirationDate)
	{
		if (expirationDate == null)
		{
			return 0;
		}
		return ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
	}

	// Calcular o valor total de perdas por produtos vencidos
	public BigDecimal getTotalLossFromExpiredProducts()
	{
		return getProductsExpired().stream().map(p -> p.getSalePrice().multiply(BigDecimal.valueOf(p.getCurrentStock()))).reduce(BigDecimal.ZERO,
				BigDecimal::add);
	}

	public BigDecimal getTotalLossFromProductsWithNoMovement()
	{
		if (productsWithNoMoviment == null)
		{
			return BigDecimal.ZERO;
		}

		return productsWithNoMoviment.stream().filter(p -> p.getSalePrice() != null && p.getCurrentStock() != null)
				.map(p -> p.getSalePrice().multiply(BigDecimal.valueOf(p.getCurrentStock()))).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public void save()
	{

		product.setCategory(product.getCategory());
		productService.saveOrUpdateProduct(product);
		addMessage("Produto salvo com sucesso!", FacesMessage.SEVERITY_INFO);
		loadProducts();
		reset();
	}

	public Long loadTotalProducts()
	{

		return setTotalProducts(productService.countProducts());
	}

	public Long loadZeroStockProducts()
	{

		return setZeroStockProducts(productService.countZeroProducts());
	}

	public void delete(Product p)
	{
		try
		{
			productService.removeProduct(p.getId());
			addMessage("Produto removido com sucesso!", FacesMessage.SEVERITY_INFO);
			loadProducts();
		} catch (Exception e)
		{
			addMessage("Erro ao excluir produto: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
		}
	}

	public void editProduct(Product p)
	{
		this.product = p;
		this.editMode = true;
		dashboardBean.setCurrentPage("/pages/product-include.xhtml");
	}

	public void viewProduct(Product p)
	{
		this.product = p;
		this.editMode = false;
		dashboardBean.setCurrentPage("/pages/product-view.xhtml");
	}

	public void editar(Product product)
	{
		this.setProduct(product);
	}

	public void view(Product p)
	{
		this.product = p;
		this.editMode = false; // garante que não seja editável
	}

	public void remover(Long id)
	{
		productService.removeProduct(id);
		setListProduct(productService.findAll());
	}

	public void newProduct()
	{
		this.setEditMode(false);
		this.setProduct(new Product());
	}

	public void reset()
	{
		this.product = new Product();
		this.setEditMode(false);
	}

	public String cancel()
	{
		product = new Product();
		return "product.xhtml?faces-redirect=true";
	}

	public void saveTemporarys()
	{
		// supplier = supplierService.findById(getSupplierId());
		getProduct().getSupplier();
		dashboardBean.setCurrentPage("/pages/product-confirm.xhtml");
	}

	public void saveTemporary()
	{

		if (selectedSupplierId != null)
		{

			supplierService.findByIds(selectedSupplierId).ifPresent(supplier -> product.setSupplier(supplier));

		} else
		{
			product.setSupplier(null);
		}

		dashboardBean.setCurrentPage("/pages/product-confirm.xhtml");
	}

	private void addMessage(String msg, FacesMessage.Severity severity)
	{
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, msg, null));
	}

	public boolean isEditMode()
	{
		return editMode;
	}

	public List<Product> getListProduct()
	{
		return listProduct;
	}

	public void setListProduct(List<Product> listProduct)
	{
		this.listProduct = listProduct;
	}

	public List<Category> getListCategory()
	{
		return listCategory;
	}

	public void setListCategory(List<Category> listCategory)
	{
		this.listCategory = listCategory;
	}

	public Long getTotalProducts()
	{
		return totalProducts;
	}

	public Long setTotalProducts(Long totalProducts)
	{
		this.totalProducts = totalProducts;
		return totalProducts;
	}

	public Long getZeroStockProducts()
	{
		return zeroStockProducts;
	}

	public Long setZeroStockProducts(Long zeroStockProducts)
	{
		this.zeroStockProducts = zeroStockProducts;
		return zeroStockProducts;
	}

	public void setEditMode(boolean editMode)
	{
		this.editMode = editMode;
	}

	public Product getProduct()
	{
		return product;
	}

	public void setProduct(Product product)
	{
		this.product = product;
	}

	public List<Product> getFilteredProducts()
	{
		return filteredProducts;
	}

	public void setFilteredProducts(List<Product> filteredProducts)
	{
		this.filteredProducts = filteredProducts;
	}

	public List<Supplier> getListSupplier()
	{
		return listSupplier;
	}

	public void setListSupplier(List<Supplier> listSuppliers)
	{
		this.listSupplier = listSuppliers;
	}

	public Long getSelectedSupplierId()
	{
		return selectedSupplierId;
	}

	public void setSelectedSupplierId(Long selectedSupplierId)
	{
		this.selectedSupplierId = selectedSupplierId;
	}

	public String getSearchTerm()
	{
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm)
	{
		this.searchTerm = searchTerm;
	}

	public String getSelectedCategory()
	{
		return selectedCategory;
	}

	public void setSelectedCategory(String selectedCategory)
	{
		this.selectedCategory = selectedCategory;
	}

	public String getSelectedManufacturer()
	{
		return selectedManufacturer;
	}

	public void setSelectedManufacturer(String selectedManufacturer)
	{
		this.selectedManufacturer = selectedManufacturer;
	}

	public List<Product> getNearExpiryProducts()
	{
		return nearExpiryProducts;
	}

	public Long getSupplierId()
	{
		return supplierId;
	}

	public void setSupplierId(Long supplierId)
	{
		this.supplierId = supplierId;
	}

	public LocalDate getRegistrationDate()
	{
		return registrationDate;
	}

	public void setRegistrationDate(LocalDate registrationDate)
	{
		this.registrationDate = registrationDate;
	}

	public LocalDate getExpirationDate()
	{
		return expirationDate;
	}

	public void setExpirationDate(LocalDate expirationDate)
	{
		this.expirationDate = expirationDate;
	}

	public Category getCategory()
	{
		return category;
	}

	public void setCategory(Category category)
	{
		this.category = category;
	}

	public Supplier getSupplier()
	{
		return supplier;
	}

	public void setSupplier(Supplier supplier)
	{
		this.supplier = supplier;
	}

	public List<Product> getProductsWithNoMoviment()
	{
		return productsWithNoMoviment;
	}

	public void setProductsWithNoMoviment(List<Product> productsWithNoMoviment)
	{
		this.productsWithNoMoviment = productsWithNoMoviment;
	}
}

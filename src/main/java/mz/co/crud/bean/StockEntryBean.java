package mz.co.crud.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import mz.co.crud.model.Product;
import mz.co.crud.model.StockEntry;
import mz.co.crud.model.StockEntryDetails;
import mz.co.crud.model.Year;
import mz.co.crud.service.ProductService;
import mz.co.crud.service.StockEntryService;
import mz.co.crud.service.YearService;

@Component("stockEntryBean")
@SessionScope
public class StockEntryBean implements Serializable {

	public static class ProductSummary implements Serializable {
		
		private static final long serialVersionUID = 1L;

		private String productName;
		private Long total;
		private Date entryDate;

		public ProductSummary(String productName, Long total, Date entryDate) {
			this.productName = productName;
			this.total = total;
			this.entryDate = entryDate;
		}

		public String getProductName() {
			return productName;
		}

		public void setProductName(String productName) {
			this.productName = productName;
		}

		public Long getTotal() {
			return total;
		}

		public void setTotal(Long total) {
			this.total = total;
		}

		public Date getEntryDate() {
			return entryDate;
		}

		public void setEntryDate(Date entryDate) {
			this.entryDate = entryDate;
		}
	}

	/**
	 * @author Refinado Bila
	 */
	private static final long serialVersionUID = 1L;

	private StockEntry itemStockEntry;

	private List<StockEntry> listStockEntry;

	private Date currentDate = new Date();

	private StockEntryDetails itemStockEntryDetails;

	private List<StockEntry> filteredStockEntry;

	private List<StockEntryDetails> listEntriesDetails;

	private Product selectedProduct;

	private Product itemProduct;

	private List<Product> listProducts;

	private boolean editMode = false;

	private boolean editing = false;

	private Year year;

	private int quantity;

	private int previousStock;

	private Year currentYear;

	private Long editingSaleId;
	
	private List<ProductSummary> productSummaryList;

	@Inject
	private YearService yearService;

	@Inject
	private StockEntryService stockEntryService;

	@Inject
	private ProductService productService;

	@Inject
	private DashboardBean dashboardBean;
	
	@Inject
	private LoginBean loginBean;

	@PostConstruct
	public void init() {

		itemStockEntry = new StockEntry();

		itemStockEntry.setListEntriesDetails(new ArrayList<>());

		itemStockEntryDetails = new StockEntryDetails();

		listProducts = productService.findAll();

		this.setListStockEntry(new ArrayList<>());

		loadListEntries();
		
		loadProductSummary(); 
	}

	public void loadYear() {
		int currentYear = java.time.Year.now().getValue();
		this.setCurrentYear(yearService.findByYear(currentYear));
	}

	public void onProductSelect() {

		if (selectedProduct != null) {

			this.setPreviousStock(selectedProduct.getCurrentStock());
			selectedProduct.setCurrentStock(selectedProduct.getCurrentStock());
			selectedProduct.setSalePrice(selectedProduct.getSalePrice());
		}
	}

	public void newStockEntry() {
		try {

			setEditing(false);
			int currentYear = java.time.LocalDate.now().getYear();

			setYear(yearService.findOrCreateByYear(currentYear));

			itemStockEntry = new StockEntry();

			itemStockEntry.setListEntriesDetails(new ArrayList<>());

			listProducts = productService.findAll();

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Erro ao criar nova entrada: " + e.getMessage()));
		}
	}

	public void loadEntryDetails(Long entryId) {
		listEntriesDetails = stockEntryService.findByStockEntryId(entryId);
	}

	public void loadListEntries() {

		listStockEntry = stockEntryService.findAll();
	}

	public void addItem() {
		if (itemStockEntryDetails == null) {
			return;
		}

		if (itemStockEntryDetails.getProduct() == null) {
			return;
		}

		if (itemStockEntryDetails.getQuantity() == null || itemStockEntryDetails.getQuantity() <= 0) {
			return;
		}

		boolean exists = itemStockEntry.getListEntriesDetails().stream()
				.anyMatch(i -> i.getProduct().getId().equals(itemStockEntryDetails.getProduct().getId()));

		if (exists) {
			return;
		}
	}

	public void addProduct() {
		try {
			if (selectedProduct == null) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Selecione um produto"));
				return;
			}

			if (this.quantity == 0 || quantity <= 0) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Informe uma quantidade válida"));
				return;
			}

			if (itemStockEntry.getListEntriesDetails() != null) {
				for (StockEntryDetails existingDetail : itemStockEntry.getListEntriesDetails()) {
					if (existingDetail.getProduct().getId().equals(selectedProduct.getId())) {
						FacesContext.getCurrentInstance().addMessage(null,
								new FacesMessage(FacesMessage.SEVERITY_WARN, "Produto já adicionado",
										"O produto '" + selectedProduct.getName() + "' já está no carrinho."));
						return;
					}
				}
			}

			StockEntryDetails detail = new StockEntryDetails();
			detail.setProduct(selectedProduct);
			detail.setQuantity(quantity);
			this.setPreviousStock(selectedProduct.getCurrentStock());  

			if (itemStockEntry.getListEntriesDetails() == null) {
				itemStockEntry.setListEntriesDetails(new ArrayList<>());
			}

			itemStockEntry.getListEntriesDetails().add(detail);

			selectedProduct = null;
			quantity = 0;

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Produto adicionado ao carrinho"));

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
					"Erro ao adicionar produto: " + e.getMessage()));
		}
	}

	public void saveStockEntries() {
		itemStockEntry.setEntryDate(new Date());
		stockEntryService.save(itemStockEntry);

		itemStockEntry = new StockEntry();
		itemStockEntry.setListEntriesDetails(new ArrayList<>());
	}
 
	public int getTotalQuantity() {
		if (itemStockEntry.getListEntriesDetails() == null) {
			return 0;
		}
		return itemStockEntry.getListEntriesDetails().stream().mapToInt(StockEntryDetails::getQuantity).sum();
	}
  
	public void removeItem(StockEntryDetails item) {
		if (itemStockEntry.getListEntriesDetails() != null) {
			itemStockEntry.getListEntriesDetails().remove(item);

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso",
					"Produto '" + item.getProduct().getName() + "' removido"));
		}
	}

	public void editItem(StockEntryDetails item) {
		try {
			this.selectedProduct = item.getProduct();
			this.quantity = item.getQuantity();

			itemStockEntry.getListEntriesDetails().remove(item);

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Produto pronto para edição.",
							"Você pode alterar a quantidade ou o produto e adicioná-lo novamente."));

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao preparar edição.", e.getMessage()));
		}
	}

	public void removerItem(StockEntryDetails item) {
		try {
			itemStockEntry.getListEntriesDetails().remove(item);

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Item removido com sucesso!", null));

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao remover item.", e.getMessage()));
		}
	}

	public void saveProductsEntry() {
		try {
			if (itemStockEntry.getListEntriesDetails() == null || itemStockEntry.getListEntriesDetails().isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Adicione pelo menos um produto"));
				return;
			}

			itemStockEntry.setEntryDate(new Date());
			stockEntryService.saveEntry(itemStockEntry);

			for (StockEntryDetails detail : itemStockEntry.getListEntriesDetails()) {
				Product product = detail.getProduct();
				product.setCurrentStock(product.getCurrentStock() + detail.getQuantity());
				productService.saveOrUpdateProduct(product);
			}

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso",
					"Entrada de stock registrada com sucesso!"));

			clearForm();
			listStockEntry = stockEntryService.findAll();
			loadProductSummary();  

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar entrada: " + e.getMessage()));
			e.printStackTrace();
		}
	}

	public void loadProductSummary() {
		try {
			List<Object[]> results = stockEntryService.getProductSummary();
			productSummaryList = new ArrayList<>();

			for (Object[] row : results) {
				String productName = (String) row[0];
				Long total = ((Number) row[1]).longValue();
				Date entryDate = (Date) row[2];

				productSummaryList.add(new ProductSummary(productName, total, entryDate));
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro",
					"Erro ao carregar resumo de produtos: " + e.getMessage()));
		}
	}

	public List<ProductSummary> getProductSummaryList() {
		if (productSummaryList == null) {
			loadProductSummary();
		}
		return productSummaryList;
	}

	public void setProductSummaryList(List<ProductSummary> productSummaryList) {
		this.productSummaryList = productSummaryList;
	}

	public Long getGrandTotalQuantity() {
		if (productSummaryList == null || productSummaryList.isEmpty()) {
			return 0L;
		}
		return productSummaryList.stream().mapToLong(ProductSummary::getTotal).sum();
	}

	public void saveProductEntry() {
		try {
			if (itemStockEntry.getListEntriesDetails() == null || itemStockEntry.getListEntriesDetails().isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Adicione pelo menos um produto"));
				return;
			}

			itemStockEntry.setEntryDate(new Date());

			stockEntryService.saveEntry(itemStockEntry);

			for (StockEntryDetails detail : itemStockEntry.getListEntriesDetails()) {
				Product product = detail.getProduct();
				product.setCurrentStock(product.getCurrentStock() + detail.getQuantity());
				productService.saveOrUpdateProduct(product);
			}

			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso",
					"Entrada de stock registrada com sucesso!"));

			clearForm();

			listStockEntry = stockEntryService.findAll();

		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro ao salvar entrada: " + e.getMessage()));
			e.printStackTrace(); // Para debug
		}
	}

	public void clearForm() {
		itemStockEntry = new StockEntry();
		itemStockEntry.setListEntriesDetails(new ArrayList<>());
		selectedProduct = null;
		quantity = 0;
	}

	public String getCurrentUser() {
		// Retorne o usuário logado
		// Exemplo: return
		// SecurityContextHolder.getContext().getAuthentication().getName();
		//return "Administrador";  
		return loginBean.getUsername();
	}

	public void editarStockEntry(StockEntry stockEntry) {
		try {
			if (stockEntry == null) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Entrada não encontrada."));
				return;
			}

			// Carrega detalhes do stock com produtos (JOIN FETCH)
			this.listEntriesDetails = stockEntryService.findByStockEntryId(stockEntry.getId());

			this.editing = true;
			this.editingSaleId = stockEntry.getId();
			this.itemStockEntry = stockEntry;
			this.currentDate = stockEntry.getEntryDate();

			// Copia os detalhes para o itemStockEntry
			this.itemStockEntry.setListEntriesDetails(new ArrayList<>(listEntriesDetails));

			// Limpa seleção para edição de produto
			this.selectedProduct = null;
			this.quantity = 0;

			// Redireciona para a página de inclusão/edição
			dashboardBean.setCurrentPage("/pages/stock-entry-include.xhtml");

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!",
					"Erro ao carregar entrada para edição: " + e.getMessage()));
		}
	}

//	public String editStockEntry(StockEntry entry) {
//	    try {
//	        // Define a entrada que será editada
//	        this.itemStockEntry = entry;
//	        this.editing = true;
//
//	        // Pede ao serviço os detalhes dessa entrada (produtos incluídos)
//	        this.listEntriesDetails = stockEntryService.getStockEntryDetailsByEntry(entry);
//
//	        // Atualiza a data atual (caso uses em campos da view)
//	        this.currentDate = entry.getDate();
//
//	        // Redireciona para a página de atualização
//	        return "stock-entry-update.xhtml?faces-redirect=true";
//
//	    } catch (Exception e) {
//	        FacesContext.getCurrentInstance().addMessage(null,
//	            new FacesMessage(FacesMessage.SEVERITY_ERROR, 
//	            "Erro", "Não foi possível carregar os detalhes da entrada."));
//	        return null;
//	    }
//	}

	// Getter para data atual
	public Date getCurrentDate() {
		return new Date();
	}

	public String goNext() {

		return "stock-entry-confirm.xhtml?faces-redirect=true";
	}

	public StockEntry getItemStockEntry() {
		return itemStockEntry;
	}

	public void setItemStockEntry(StockEntry itemStockEntry) {
		this.itemStockEntry = itemStockEntry;
	}

	public List<StockEntry> getListStockEntry() {
		return listStockEntry;
	}

	public void setListStockEntry(List<StockEntry> listStockEntry) {
		this.listStockEntry = listStockEntry;
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public StockEntryDetails getItemStockEntryDetails() {
		return itemStockEntryDetails;
	}

	public void setItemStockEntryDetails(StockEntryDetails itemStockEntryDetails) {
		this.itemStockEntryDetails = itemStockEntryDetails;
	}

	public Product getSelectedProduct() {
		return selectedProduct;
	}

	public void setSelectedProduct(Product selectedProduct) {
		this.selectedProduct = selectedProduct;
	}

	public List<Product> getListProducts() {
		return listProducts;
	}

	public void setListProducts(List<Product> listProducts) {
		this.listProducts = listProducts;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public Year getYear() {
		return year;
	}

	public void setYear(Year year) {
		this.year = year;
	}

	public List<StockEntry> getFilteredStockEntry() {
		return filteredStockEntry;
	}

	public void setFilteredStockEntry(List<StockEntry> filteredStockEntry) {
		this.filteredStockEntry = filteredStockEntry;
	}

	public Product getItemProduct() {
		return itemProduct;
	}

	public void setItemProduct(Product itemProduct) {
		this.itemProduct = itemProduct;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public boolean isEditing() {
		return editing;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}

	public Year getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(Year currentYear) {
		this.currentYear = currentYear;
	}

	public int getPreviousStock() {
		return previousStock;
	}

	public void setPreviousStock(int previousStock) {
		this.previousStock = previousStock;
	}

	public Long getEditingSaleId() {
		return editingSaleId;
	}

	public void setEditingSaleId(Long editingSaleId) {
		this.editingSaleId = editingSaleId;
	}

	public List<StockEntryDetails> getListEntriesDetails() {
		return listEntriesDetails;
	}

	public void setListEntriesDetails(List<StockEntryDetails> listEntriesDetails) {
		this.listEntriesDetails = listEntriesDetails;
	}

}

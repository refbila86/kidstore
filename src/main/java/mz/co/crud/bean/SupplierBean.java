package mz.co.crud.bean;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import mz.co.crud.model.Product;
import mz.co.crud.model.Supplier;
import mz.co.crud.service.SupplierService;

@Component("supplierBean")
@SessionScope
public class SupplierBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private SupplierService supplierService;

	private Supplier itemSupplier;
	private List<Supplier> suppliers;
	private List<Supplier> listSuppliers;
	private List<Supplier> filteredSuppliers;
	private List<Product> supplierProducts;
	private boolean editMode = false;

	private String searchTerm;
	private String searchCity;

	private List<Supplier> lastSuppliers;

	private Supplier supplier;
	private Long supplierId;

	@Inject
	private DashboardBean dashboardBean;

	@PostConstruct
	public void init() {
		itemSupplier = new Supplier();
		
		loadAllSuppliers();
	}

	public void newSupplier() {
		this.itemSupplier = new Supplier();
	}
	
	public void loadAllSuppliers() {
		listSuppliers = supplierService.findAllWithProducts();
	}

	public void loadSupplier() {
		if (supplierId != null) {
			setSupplier(supplierService.getSupplierWithProducts(supplierId));
		}
	}

	// Load Methods
	public void loadSuppliers() {
		this.setSuppliers(supplierService.findAllWithProducts());
	}

	public void loadSuppliersWithProducts() {
		suppliers = supplierService.findWithProducts();
	}

	// CRUD Operations
	public void saveSupplier() {
		try {
			if (!itemSupplier.isValidNuit()) {
				addMessage("Atenção", "NUIT deve conter 9 dígitos", FacesMessage.SEVERITY_WARN);
				return;
			}

			if (!itemSupplier.isValidEmail()) {
				addMessage("Atenção", "Email inválido", FacesMessage.SEVERITY_WARN);
				return;
			}

			// Check if NUIT already exists
			if (itemSupplier.getId() == null && supplierService.findByNuit(itemSupplier.getNuit()).isPresent()) {
				addMessage("Erro", "NUIT já cadastrado", FacesMessage.SEVERITY_ERROR);
				return;
			}
			
			itemSupplier.getName().toUpperCase();
			supplierService.save(itemSupplier);
			listSuppliers = supplierService.findAllWithProducts();
			setItemSupplier(new Supplier());
			addMessage("Sucesso", "Fornecedor salvo com sucesso!", FacesMessage.SEVERITY_INFO);
			clear();

		} catch (Exception e) {
			addMessage("Erro", "Erro ao salvar fornecedor: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
		}
	}

	public void update() {
		try {
			if (!itemSupplier.isValidNuit()) {
				addMessage("Atenção", "NUIT deve conter 9 dígitos", FacesMessage.SEVERITY_WARN);
				return;
			}

			if (!itemSupplier.isValidEmail()) {
				addMessage("Atenção", "Email inválido", FacesMessage.SEVERITY_WARN);
				return;
			}

			supplierService.update(itemSupplier);
			addMessage("Sucesso", "Fornecedor atualizado com sucesso!", FacesMessage.SEVERITY_INFO);
			clear();
			loadSuppliers();
		} catch (Exception e) {
			addMessage("Erro", "Erro ao atualizar fornecedor: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
		}
	}

	public void delete(Supplier supplier) {
		try {
			if (!supplierService.canDeleteSupplier(supplier.getId())) {
				addMessage("Atenção", "Não é possível excluir fornecedor com produtos cadastrados",
						FacesMessage.SEVERITY_WARN);
				return;
			}

			supplierService.delete(supplier.getId());
			addMessage("Sucesso", "Fornecedor excluído com sucesso!", FacesMessage.SEVERITY_INFO);
			loadSuppliers();
		} catch (Exception e) {
			addMessage("Erro", "Erro ao excluir fornecedor: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
		}
	}

	public void edit(Supplier supplier) {
		this.itemSupplier = supplier;
	}

	public void saveTemporary() {
		dashboardBean.setCurrentPage("/pages/supplier-confirm.xhtml");
	}

	public void clear() {
		itemSupplier = new Supplier();
		searchTerm = null;
		searchCity = null;
		supplierProducts = null;
	}

	// Search Methods
	public void searchByName() {
		if (searchTerm != null && !searchTerm.trim().isEmpty()) {
			suppliers = supplierService.findByName(searchTerm);
		} else {
			loadSuppliers();
		}
	}

	public void searchByCity() {
		if (searchCity != null && !searchCity.trim().isEmpty()) {
			suppliers = supplierService.findByCity(searchCity);
		} else {
			loadSuppliers();
		}
	}

	public void findByNuit(String nuit) {
		supplierService.findByNuit(nuit).ifPresentOrElse(found -> {
			suppliers = List.of(found);
			addMessage("Encontrado", "Fornecedor encontrado", FacesMessage.SEVERITY_INFO);
		}, () -> {
			addMessage("Não encontrado", "Nenhum fornecedor com este NUIT", FacesMessage.SEVERITY_WARN);
			loadSuppliers();
		});
	}

	public void findByEmail(String email) {
		supplierService.findByEmail(email).ifPresentOrElse(found -> {
			suppliers = List.of(found);
			addMessage("Encontrado", "Fornecedor encontrado", FacesMessage.SEVERITY_INFO);
		}, () -> {
			addMessage("Não encontrado", "Nenhum fornecedor com este email", FacesMessage.SEVERITY_WARN);
			loadSuppliers();
		});
	}

	// Product Management
	public void viewSupplierProducts(Supplier supplier) {
		this.itemSupplier = supplier;
		this.supplierProducts = supplier.getProducts();
	}

	public int getProductCount(Supplier supplier) {
		return supplierService.getProductCount(supplier.getId());
	}

	// Contact Update
	public void updateContactInfo(String phone, String email, String address) {
		try {
			supplierService.updateContactInfo(itemSupplier.getId(), phone, email, address);
			addMessage("Sucesso", "Informações de contato atualizadas!", FacesMessage.SEVERITY_INFO);
			loadSuppliers();
		} catch (Exception e) {
			addMessage("Erro", "Erro ao atualizar contato: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
		}
	}

	public List<Supplier> getLastSuppliers() {
		if (lastSuppliers == null) {
			lastSuppliers = supplierService.lastSuppliers(5); // últimos 5
		}
		return lastSuppliers;
	}

	// Statistics
	public List<Object[]> getSuppliersWithProductCount() {
		return supplierService.getSuppliersWithProductCount();
	}

	public int getTotalProducts() {
		if (suppliers == null)
			return 0;
		return suppliers.stream().mapToInt(s -> s.getProducts() != null ? s.getProducts().size() : 0).sum();
	}

	// Helper Methods
	private void addMessage(String summary, String detail, FacesMessage.Severity severity) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
	}

	public boolean hasProducts(Supplier supplier) {
		return supplier.hasProducts();
	}

	public boolean canDelete(Supplier supplier) {
		return supplierService.canDeleteSupplier(supplier.getId());
	}

	// Getters and Setters
	public Supplier getItemSupplier() {
		return itemSupplier;
	}

	public void setItemSupplier(Supplier supplier) {
		this.itemSupplier = supplier;
	}

	public List<Supplier> getSuppliers() {
		return suppliers;
	}

	public void setSuppliers(List<Supplier> suppliers) {
		this.suppliers = suppliers;
	}

	public List<Supplier> getFilteredSuppliers() {
		return filteredSuppliers;
	}

	public void setFilteredSuppliers(List<Supplier> filteredSuppliers) {
		this.filteredSuppliers = filteredSuppliers;
	}

	public List<Product> getSupplierProducts() {
		return supplierProducts;
	}

	public void setSupplierProducts(List<Product> supplierProducts) {
		this.supplierProducts = supplierProducts;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public String getSearchCity() {
		return searchCity;
	}

	public void setSearchCity(String searchCity) {
		this.searchCity = searchCity;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}

	public Long getSupplierId() {
		return supplierId;
	}

	public List<Supplier> getListSuppliers() {
		return listSuppliers;
	}

	public void setListSuppliers(List<Supplier> listSuppliers) {
		this.listSuppliers = listSuppliers;
	}
}
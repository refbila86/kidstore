package mz.co.crud.bean;

import java.io.Serializable;
import java.util.List;

import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import jakarta.faces.context.FacesContext;
import mz.co.crud.model.Product;
import mz.co.crud.model.Sale;
import mz.co.crud.model.User;

@Component("dashboardBean")   
@SessionScope
public class DashboardBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String currentPage = "/pages/welcome.xhtml";

	private String username;

	private boolean sidebarVisible = true;

	private List<Sale> listSales;
	
//	@Inject
//	private LoginBean loginBean;
//	

	// ✅ método genérico usado no menu lateral
	public void loadPage(String page) {

		this.currentPage = "/pages/" + page + ".xhtml";

	}

	// **************PRODUTO*************************************************************
	public void loadProduct() {
		FacesContext context = FacesContext.getCurrentInstance();
		ProductBean productBean = context.getApplication().evaluateExpressionGet(context, "#{productBean}",
				ProductBean.class);
		productBean.getListProduct();
		this.setCurrentPage("/pages/product.xhtml");
	}

	public void loadProductInclude() {
		FacesContext context = FacesContext.getCurrentInstance();
		ProductBean productBean = context.getApplication().evaluateExpressionGet(context, "#{productBean}",
				ProductBean.class);
		productBean.newProduct();
		productBean.loadCategories();
		this.setCurrentPage("/pages/product-include.xhtml");
	}

	public void loadProductConfirm() {
		FacesContext context = FacesContext.getCurrentInstance();
		ProductBean productBean = context.getApplication().evaluateExpressionGet(context, "#{productBean}",
				ProductBean.class);
		productBean.saveTemporary();
		this.setCurrentPage("/pages/product-confirm.xhtml");
	}

	public void loadProductView(Product p) {
		FacesContext context = FacesContext.getCurrentInstance();
		ProductBean productBean = context.getApplication().evaluateExpressionGet(context, "#{productBean}",
				ProductBean.class);
		productBean.view(p);
		this.setCurrentPage("/pages/product-view.xhtml");
		PrimeFaces.current().ajax().update("mainForm:mainContent");
	}

	public void backProductInclude() {
		this.setCurrentPage("/pages/product-include.xhtml");
	}

	public void cancelProductInclude() {
		this.setCurrentPage("/pages/product.xhtml");
	}

	// **************CATEGORIA*************************************************************
	public void loadCategory() {
		FacesContext context = FacesContext.getCurrentInstance();
		CategoryBean categoryBean = context.getApplication().evaluateExpressionGet(context, "#{categoryBean}",
				CategoryBean.class);
		categoryBean.getListCategory();
		this.setCurrentPage("/pages/category.xhtml");
	}

	public void loadCategoryInclude() {
		FacesContext context = FacesContext.getCurrentInstance();
		CategoryBean categoryBean = context.getApplication().evaluateExpressionGet(context, "#{categoryBean}",
				CategoryBean.class);
		categoryBean.newCategory();
		categoryBean.setEditMode(false);
		this.setCurrentPage("/pages/category-include.xhtml");
	}

	public void loadCategoryConfirm() {
		FacesContext context = FacesContext.getCurrentInstance();
		CategoryBean categoryBean = context.getApplication().evaluateExpressionGet(context, "#{categoryBean}",
				CategoryBean.class);
		categoryBean.saveTemporary();
		this.setCurrentPage("/pages/category-confirm.xhtml");
	}

	public void backCategoryInclude() {
		this.setCurrentPage("/pages/category-include.xhtml");
	}

	public void cancelCategoryInclude() {
		this.setCurrentPage("/pages/category.xhtml");
	}

	
	// **************FORNECEDORES*************************************************************
	public void loadSupplier() {
		FacesContext context = FacesContext.getCurrentInstance();
		SupplierBean supplierBean = context.getApplication().evaluateExpressionGet(context, "#{supplierBean}",
				SupplierBean.class);
		supplierBean.loadAllSuppliers();
		this.setCurrentPage("/pages/supplier.xhtml");
	}

	public void loadSupplierInclude() {
		FacesContext context = FacesContext.getCurrentInstance();
		SupplierBean supplierBean = context.getApplication().evaluateExpressionGet(context, "#{supplierBean}",
				SupplierBean.class);
		supplierBean.newSupplier();
		supplierBean.setEditMode(false);
		this.setCurrentPage("/pages/supplier-include.xhtml");
	}

	public void loadSupplierConfirm() {
		FacesContext context = FacesContext.getCurrentInstance();
		SupplierBean supplierBean = context.getApplication().evaluateExpressionGet(context, "#{supplierBean}",
				SupplierBean.class);
		supplierBean.saveTemporary();
		this.setCurrentPage("/pages/supplier-confirm.xhtml");
	}

	public void backSupplierInclude() {
		this.setCurrentPage("/pages/supplier-include.xhtml");
	}

	public void cancelSupplierInclude() {
		this.setCurrentPage("/pages/supplier.xhtml");
	}
	
	// **************CLIENTE*************************************************************
		public void loadCustomer() {
			FacesContext context = FacesContext.getCurrentInstance();
			CustomerBean customerBean = context.getApplication().evaluateExpressionGet(context, "#{customerBean}",
					CustomerBean.class);
			customerBean.getListCustomer();
			this.setCurrentPage("/pages/customer.xhtml");
		}

		public void loadCustomerInclude() {
			FacesContext context = FacesContext.getCurrentInstance();
			CustomerBean customerBean = context.getApplication().evaluateExpressionGet(context, "#{customerBean}",
					CustomerBean.class);
			customerBean.newCustomer();
			customerBean.setEditMode(false);
			this.setCurrentPage("/pages/customer-include.xhtml");
		}

		public void loadCustomerConfirm() {
			FacesContext context = FacesContext.getCurrentInstance();
			CustomerBean customerBean = context.getApplication().evaluateExpressionGet(context, "#{customerBean}",
					CustomerBean.class);
			customerBean.saveTemporary();
			this.setCurrentPage("/pages/customer-confirm.xhtml");
		}

		public void backCustomerInclude() {
			this.setCurrentPage("/pages/customer-include.xhtml");
		}

		public void cancelCustomerInclude() {
			this.setCurrentPage("/pages/customer.xhtml");
		}


	// **************VENDAS*************************************************************
		public void loadSales() {
			FacesContext context = FacesContext.getCurrentInstance();
			SaleBean saleBean = context.getApplication().evaluateExpressionGet(context, "#{saleBean}",
					SaleBean.class);
			saleBean.getListSalesOfDay();
			this.setCurrentPage("/pages/sales.xhtml");
		}

		public void loadSaleInclude() {
			FacesContext context = FacesContext.getCurrentInstance();
			SaleBean saleBean = context.getApplication().evaluateExpressionGet(context, "#{saleBean}",
					SaleBean.class);
			saleBean.newSale();
			saleBean.setEditing(false);
			this.setCurrentPage("/pages/sale-include.xhtml");
		}

		public void loadSaleConfirm() {
			FacesContext context = FacesContext.getCurrentInstance();
			SaleBean saleBean = context.getApplication().evaluateExpressionGet(context, "#{saleBean}",
					SaleBean.class);
			saleBean.goNext();
			this.setCurrentPage("/pages/sale-confirm.xhtml");
		}

		public void backSaleInclude() {
			this.setCurrentPage("/pages/sale-include.xhtml");
		}

		public void cancelSaleInclude() {
			FacesContext context = FacesContext.getCurrentInstance();
			SaleBean saleBean = context.getApplication().evaluateExpressionGet(context, "#{saleBean}",
					SaleBean.class);
			saleBean.resetSale();
			this.setCurrentPage("/pages/sales.xhtml");
		}
		
		public void cancelReportSaleInclude() {
			this.setCurrentPage("/pages/report-sales.xhtml");
		}
 
		// **************ENTRADA DE PRODUTOS*************************************************************
				public void loadStockEntry() {
					FacesContext context = FacesContext.getCurrentInstance();
					StockEntryBean stockEntryBean = context.getApplication().evaluateExpressionGet(context, "#{stockEntryBean}",
							StockEntryBean.class);
					stockEntryBean.getListStockEntry();
					this.setCurrentPage("/pages/stock-entry.xhtml");
				}

				public void loadStockEntryInclude() {
					FacesContext context = FacesContext.getCurrentInstance();
					StockEntryBean stockEntryBean = context.getApplication().evaluateExpressionGet(context, "#{stockEntryBean}",
							StockEntryBean.class);
					stockEntryBean.newStockEntry();
					this.setCurrentPage("/pages/stock-entry-include.xhtml");
				}

				public void loadStockEntryConfirm() {
					FacesContext context = FacesContext.getCurrentInstance();
					StockEntryBean stockEntryBean = context.getApplication().evaluateExpressionGet(context, "#{stockEntryBean}",
							StockEntryBean.class);
					stockEntryBean.goNext();
					this.setCurrentPage("/pages/stock-entry-confirm.xhtml");
				}
				

				public void backStockEntryInclude() {
					this.setCurrentPage("/pages/stock-entry-include.xhtml");
				}

				public void cancelStockEntryInclude() {
					this.setCurrentPage("/pages/stock-entry.xhtml");
				}
				
				// **************USER*************************************************************
				public void loadUser() {
					FacesContext context = FacesContext.getCurrentInstance();
					UserBean userBean = context.getApplication().evaluateExpressionGet(context, "#{userBean}",
							UserBean.class);
					userBean.getListUser();
					this.setCurrentPage("/pages/user.xhtml");
				}

				public void loadUserInclude() {
					FacesContext context = FacesContext.getCurrentInstance();
					UserBean userBean = context.getApplication().evaluateExpressionGet(context, "#{userBean}",
							UserBean.class);
					userBean.getListUser();
					this.setCurrentPage("/pages/user-include.xhtml");
				}

				public void loadUserConfirm() {
					FacesContext context = FacesContext.getCurrentInstance();
					UserBean userBean = context.getApplication().evaluateExpressionGet(context, "#{userBean}",
							UserBean.class);
					userBean.getListUser();
					this.setCurrentPage("/pages/user-confirm.xhtml");
				}

				public void loadUserView(User user) {
					FacesContext context = FacesContext.getCurrentInstance();
					UserBean userBean = context.getApplication().evaluateExpressionGet(context, "#{userBean}",
							UserBean.class);
					userBean.editar(user);
					this.setCurrentPage("/pages/user.xhtml");
					PrimeFaces.current().ajax().update("mainForm:mainContent");
				}

				public void backUserInclude() {
					this.setCurrentPage("/pages/user-include.xhtml");
				}

				public void cancelUserInclude() {
					this.setCurrentPage("/pages/user.xhtml");
				}
				

				// **************CASHIER*************************************************************
				public void loadCashier() {
					FacesContext context = FacesContext.getCurrentInstance();
					CashierBean cashierBean = context.getApplication().evaluateExpressionGet(context, "#{cashierBean}",
							CashierBean.class);
					cashierBean.getListCashier();
					this.setCurrentPage("/pages/cashier.xhtml");
				}

				public void loadCashierInclude() {
					FacesContext context = FacesContext.getCurrentInstance();
					CashierBean cashierBean = context.getApplication().evaluateExpressionGet(context, "#{cashierBean}",
							CashierBean.class);
					cashierBean.newCashier();
					this.setCurrentPage("/pages/cashier-include.xhtml");
				}
				
				public void loadCashierIncludeTwo() {
					FacesContext context = FacesContext.getCurrentInstance();
					CashierBean cashierBean = context.getApplication().evaluateExpressionGet(context, "#{cashierBean}",
							CashierBean.class);
					cashierBean.goToSalesPage();
					this.setCurrentPage("/pages/cashier-include-two.xhtml");
				}

//				public void loadCashierConfirm() {
//					FacesContext context = FacesContext.getCurrentInstance();
//					CashierBean cashierBean = context.getApplication().evaluateExpressionGet(context, "#{cashierBean}",
//							CashierBean.class);
//					cashierBean.getListCashier();
//					this.setCurrentPage("/pages/cashier-confirm.xhtml");
//				}

				public void loadCashierView(User user) {
					FacesContext context = FacesContext.getCurrentInstance();
					UserBean userBean = context.getApplication().evaluateExpressionGet(context, "#{userBean}",
							UserBean.class);
					userBean.editar(user);
					this.setCurrentPage("/pages/cashier.xhtml");
					PrimeFaces.current().ajax().update("mainForm:mainContent");
				}

				public void backCashierInclude() {
					this.setCurrentPage("/pages/cashier-include.xhtml");
				}

				public void cancelCashierInclude() {
					this.setCurrentPage("/pages/user.xhtml");
				}



	public void toggleSidebar() {
		this.sidebarVisible = !this.sidebarVisible;
	}

	public void goHome() {
		this.currentPage = "/pages/welcome.xhtml";
	}

	public void cancel() {
		this.currentPage = "/pages/product.xhtml";
	}

	public String goNext() {
		return "sale-confirm.xhtml?faces-redirect=true";
	}

	public String goBack() {
		return "sale-include.xhtml?faces-redirect=true";
	}

	public String logout() {
		System.out.println("👋 Logout do usuário: " + username);
		return "/login.xhtml?faces-redirect=true";
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isSidebarVisible() {
		return sidebarVisible;
	}

	public void setSidebarVisible(boolean sidebarVisible) {
		this.sidebarVisible = sidebarVisible;
	}

	public List<Sale> getListSales() {
		return listSales;
	}

	public void setListSales(List<Sale> listSales) {
		this.listSales = listSales;
	}
}
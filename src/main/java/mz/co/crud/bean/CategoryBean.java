package mz.co.crud.bean;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import lombok.Data;
import mz.co.crud.model.Category;
import mz.co.crud.service.CategoryService;

@Component ("categoryBean") 
@SessionScope
public class CategoryBean implements Serializable {

	private static final long serialVersionUID = 1L;

	@Autowired
	private CategoryService categoryService;

	private Category itemCategory;
 
	private List<Category> listCategory;

	private boolean editMode = false;
	
	private Long totalCategories;

	@Autowired
	private DashboardBean dashboardBean;
	
	@Autowired
	private ProductBean productBean;

	@PostConstruct
	public void init() {
		itemCategory = new Category();
		setListCategory(categoryService.listar());
		 setTotalCategories(categoryService.totalCategories());
	}

	public void newCategory() {
		this.itemCategory = new Category();
	}

	public void saveTemporary() {
		dashboardBean.setCurrentPage("/pages/product-confirm.xhtml");
	}

	public void saveCategory() {
		itemCategory.setName(getItemCategory().getName());

		categoryService.saveCategory(itemCategory);
		setListCategory(categoryService.listar());

		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Categoria registada com sucesso!"));

		this.setEditMode(false);
		this.setItemCategory(new Category());
		this.productBean.getListCategory();
	}

	public void editCategory(Category category) {
		this.setItemCategory(category);
		this.editMode = true;
		dashboardBean.setCurrentPage("/pages/category-include.xhtml");
	}

	public void removeCategory(Long id) {
		categoryService.removeCategory(id);
		setListCategory(categoryService.listar());
	}

	public void showSuccessMessage() {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Utilizador cadastrado com sucesso!"));
	}

	public Category getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(Category itemCategory) {
		this.itemCategory = itemCategory;
	}

	public List<Category> getListCategory() {
		return listCategory;
	}

	public void setListCategory(List<Category> listCategory) {
		this.listCategory = listCategory;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public Long getTotalCategories() {
		return totalCategories;
	}

	public void setTotalCategories(Long totalCategories) {
		this.totalCategories = totalCategories;
	}

}

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
import mz.co.crud.model.Customer;
import mz.co.crud.service.CustomerService;

@Component ("customerBean")  
@SessionScope
public class CustomerBean implements Serializable {

	/**
	 * @author Refinado Bila
	 */
	private static final long serialVersionUID = 1L;

	@Autowired
	private CustomerService customerService;

	private Customer itemCustomer;

	private List<Customer> listCustomer;

	private Long totalCustomers;

	private boolean editMode = false;

	private List<Customer> lastCustomers;

	@Autowired
	private DashboardBean dashboardBean;

	@PostConstruct
	public void init() {
		this.setListCustomer(customerService.findAll());
		getCustomers();
	}

	public void newCustomer() {
		this.itemCustomer = new Customer();
	}

	public void saveTemporary() {
		dashboardBean.setCurrentPage("/pages/customer-confirm.xhtml");
	}

	public void saveCustomer() {

		itemCustomer.setName(getItemCustomer().getName());
		itemCustomer.setCellphone(getItemCustomer().getCellphone());
		itemCustomer.setAddress(getItemCustomer().getAddress());

		customerService.saveCustomer(itemCustomer);
		setListCustomer(customerService.findAll());

		// Mensagem de sucesso
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Cliente cadastrado com sucesso!"));
		setEditMode(false);
		setItemCustomer(new Customer());
	}

	public List<Customer> getLastCustomers() {
		if (lastCustomers == null) {
			lastCustomers = customerService.lastCustomers(5); 
		}
		return lastCustomers;
	}

	public Long getCustomers() {

		return setTotalCustomers(customerService.countCustomers());
	}

	public void editCustomer(Customer customer) {
		this.setItemCustomer(customer);
		this.editMode = true;
		dashboardBean.setCurrentPage("/pages/customer-include.xhtml");

	}

	public void remover(Long id) {
		customerService.removeCustumer(id);
		setListCustomer(customerService.findAll());
	}

	public void showSuccessMessage() {
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Utilizador cadastrado com sucesso!"));
	}

	public Customer getItemCustomer() {
		return itemCustomer;
	}

	public void setItemCustomer(Customer itemCustomer) {
		this.itemCustomer = itemCustomer;
	}

	public List<Customer> getListCustomer() {
		return listCustomer;
	}

	public void setListCustomer(List<Customer> listCustomer) {
		this.listCustomer = listCustomer;
	}

	public Long getTotalCustomers() {
		return totalCustomers;
	}

	public Long setTotalCustomers(Long totalCustomers) {
		this.totalCustomers = totalCustomers;
		return totalCustomers;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

}

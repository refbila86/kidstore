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
import mz.co.crud.model.User;
import mz.co.crud.model.UserRole;
import mz.co.crud.service.UserService;

@Component("userBean")
@SessionScope
@Data
public class UserBean implements Serializable {

	/**
	 * @author Refinado Bila
	 */
	private static final long serialVersionUID = 1L;

	private String username;
	
	private String password;
	
	private User loggedUser;
	
	private boolean editMode = false;

	@Inject
	private DashboardBean dashboardBean;
	
	@Inject
	private LoginBean loginBean;

	@Autowired
	private UserService userService;

	private User itemUser;

	private List<User> listUser;

	@PostConstruct
	public void init() {
		setListUser(userService.listar());
		this.itemUser = new User();
	}

	public void prepareNewUser() {
		itemUser = new User();
		itemUser.setActive(true);

		if (itemUser.getRole() == null) {
			itemUser.setRole(UserRole.VENDEDOR);
		}
	}

	public UserRole[] getUserRoles() {
		return UserRole.values();
	}

	public void newUser() {
		itemUser = new User();
		itemUser.setActive(true);

		if (itemUser.getRole() == null) {
			itemUser.setRole(UserRole.VENDEDOR);
		}

	}

	public void salvar() {
		itemUser.setCreatedBy(loginBean.getUsername());
		userService.salvar(itemUser);
		setListUser(userService.listar());
		// Mensagem de sucesso
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Utilizador cadastrado com sucesso!"));

		setItemUser(new User());
	}

	public String logout() {
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
		return "/login.xhtml?faces-redirect=true";
	}

	public boolean isLoggedIn() {
		return loggedUser != null;
	}

	public boolean hasRole(UserRole role) {
		return loggedUser != null && loggedUser.getRole() == role;
	}

	public boolean isAdmin() {
		return hasRole(UserRole.ADMIN);
	}

	public void editar(User u) {
		this.setItemUser(u);
		this.dashboardBean.setCurrentPage("/pages/user-include.xhtml");
	}

	public void remover(Long id) {
		userService.remover(id);
		setListUser(userService.listar());
	}

 
	public void removeCategory(Long id) {
		userService.remover(id);
		setListUser(userService.listar());
	}

	public void showSuccessMessage() {

	}

	public User getItemUser() {
		return itemUser;
	}

	public void setItemUser(User itemUser) {
		this.itemUser = itemUser;
	}

	public List<User> getListUser() {
		return listUser;
	}

	public void setListUser(List<User> listUser) {
		this.listUser = listUser;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
}

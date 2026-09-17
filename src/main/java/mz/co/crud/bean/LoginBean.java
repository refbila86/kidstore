package mz.co.crud.bean;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;
import mz.co.crud.model.User;
import mz.co.crud.model.UserRole;
import mz.co.crud.service.UserService;

@Component("loginBean")
@SessionScope
public class LoginBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private String fullname;
	private String password;
	private boolean logged;
	private boolean rememberMe;
	private User loggedUser;
	private String role;

	@Autowired
	private UserService userService;

	@Autowired
	private PermissionBean permissionBean;

	public boolean isAdmin() {
		return loggedUser != null && "ADMIN".equalsIgnoreCase(loggedUser.getRole().name());
	}

	public boolean isUser() {
		return loggedUser != null && !"ADMIN".equalsIgnoreCase(loggedUser.getRole().name());
	}

	public String loginFinal() {
		try {
			User user = userService.authenticate(username, password);

			if (user == null) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Utilizador ou senha inválidos!"));
				return null;
			}

			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuarioLogado", user);

			this.loggedUser = user;
			this.fullname = user.getFullName();
			this.role = user.getRole().getDescription();
			this.password = null;  

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Bem-vindo, " + user.getFullName() + "!"));

			return "/pages/dashboard.xhtml?faces-redirect=true";

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Erro ao fazer login: " + e.getMessage()));
			return null;
		}
	}

	public String login() {
	    try {
	        User user = userService.authenticate(username, password);

	        if (user == null) {
	            FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Utilizador ou senha inválidos!"));
	            return null;
	        }

	        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
	                .getExternalContext().getSession(true);
	        session.setAttribute("usuarioLogado", user);

	        this.loggedUser = user;
	        this.fullname = user.getFullName();
	        this.role = user.getRole().getDescription();
	        this.password = null;

	        FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Bem-vindo, " + user.getFullName() + "!"));

	        return "/pages/dashboard.xhtml?faces-redirect=true";

	    } catch (Exception e) {
	        e.printStackTrace();
	        FacesContext.getCurrentInstance().addMessage(null,
	                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Erro ao fazer login: " + e.getMessage()));
	        return null;
	    }
	}
	public String loginNigga() {
		try {
			User user = userService.authenticate(username, password);

			if (user == null) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Utilizador ou senha inválidos!"));
				return null;
			}

			this.setLoggedUser(user);
			;
			setFullname(user.getFullName());

			this.password = null;
			this.setRole(user.getRole().getDescription());

			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Bem-vindo, " + user.getFullName() + "!"));

			return "/pages/dashboard.xhtml?faces-redirect=true";

		} catch (Exception e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Erro ao fazer login: " + e.getMessage()));
			return null;
		}
	}

	public String loginn() {
		try {
			if ("admin".equals(username) && "admin".equals(password)) {
				logged = true;

				addMessage(FacesMessage.SEVERITY_INFO, "Sucesso!", "Login realizado com sucesso!");

				return "/pages/dashboard.xhtml?faces-redirect=true";

			} else {
				addMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Usuário ou senha inválidos!");
				return null;
			}

		} catch (Exception e) {
			addMessage(FacesMessage.SEVERITY_ERROR, "Erro!", "Erro ao realizar login: " + e.getMessage());
			return null;
		}
	}

	public String logout() {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return "/login.xhtml?faces-redirect=true";
	}

	private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
	}


	public boolean isLoggedIn() {
		return loggedUser != null;
	}

	public boolean hasRole(UserRole role) {
		return loggedUser != null && loggedUser.getRole() == role;
	}


	// Getters e Setters
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

	public boolean isLogged() {
		return logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public User getLoggedUser() {
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public PermissionBean getPermissionBean() {
		return permissionBean;
	}

	public void setPermissionBean(PermissionBean permissionBean) {
		this.permissionBean = permissionBean;
	}

}
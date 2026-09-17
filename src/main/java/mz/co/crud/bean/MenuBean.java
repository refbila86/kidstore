package mz.co.crud.bean;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import jakarta.faces.view.ViewScoped;

@Component("menuBean")
@ViewScoped
public class MenuBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String currentPage = "/pages/welcome.xhtml";

    public void loadPage(String page) {
        this.currentPage = "/pages/" + page;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }
}

package mz.co.crud.bean;

import java.io.Serializable;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import mz.co.crud.model.UserRole;

/**
 * Bean responsável por gerenciar permissões de acesso baseadas em roles
 */
@Component("permissionBean")   
@SessionScope
public class PermissionBean implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private UserRole userRole;
    
    public PermissionBean() {
        // Inicialmente sem role
    }
    
    /**
     * Define o role do utilizador atual (deve ser chamado no login)
     */
    public void setUserRole(UserRole role) {
        this.userRole = role;
    }
    
    public UserRole getUserRole() {
        return userRole;
    }
    
    /**
     * Verifica se o utilizador tem acesso à área de Gestão
     * (Categorias, Produtos, Clientes)
     * ADMIN: SIM
     * GERENTE: SIM
     * VENDEDOR: NÃO
     * CAIXA: NÃO
     */
    public boolean hasGestaoAccess() {
        return userRole != null && userRole.hasGestaoAccess();
    }
    
    /**
     * Verifica se o utilizador tem acesso a Vendas/Saída
     * ADMIN: SIM
     * GERENTE: SIM
     * VENDEDOR: SIM
     * CAIXA: SIM
     */
    public boolean hasSalesAccess() {
        return userRole != null && userRole.hasSalesAccess();
    }
    
    /**
     * Verifica se o utilizador tem acesso a Entradas de Produtos
     * ADMIN: SIM
     * GERENTE: SIM
     * VENDEDOR: NÃO
     * CAIXA: NÃO
     */
    public boolean hasStockEntryAccess() {
        return userRole != null && userRole.hasStockEntryAccess();
    }
    
    /**
     * Verifica se o utilizador tem acesso a Relatórios
     * ADMIN: SIM
     * GERENTE: SIM
     * VENDEDOR: NÃO
     * CAIXA: NÃO
     */
    public boolean hasReportsAccess() {
        return userRole != null && userRole.hasReportsAccess();
    }
    
    /**
     * Verifica se o utilizador tem acesso a Utilitários (Utilizadores)
     * ADMIN: SIM
     * GERENTE: NÃO
     * VENDEDOR: NÃO
     * CAIXA: NÃO
     */
    public boolean hasUtilitariosAccess() {
        return userRole != null && userRole.hasUtilitariosAccess();
    }
    
    /**
     * Verifica se é ADMIN (acesso total)
     */
    public boolean isAdmin() {
        return userRole == UserRole.ADMIN;
    }
    
    /**
     * Verifica se é GERENTE
     */
    public boolean isGerente() {
        return userRole == UserRole.GERENTE;
    }
    
    /**
     * Verifica se é VENDEDOR
     */
    public boolean isVendedor() {
        return userRole == UserRole.VENDEDOR;
    }
    
    /**
     * Verifica se é CAIXA
     */
    public boolean isCaixa() {
        return userRole == UserRole.CAIXA;
    }
    
    /**
     * Retorna a descrição do role atual
     */
    public String getRoleDescription() {
        return userRole != null ? userRole.getDescription() : "Sem permissões";
    }
}
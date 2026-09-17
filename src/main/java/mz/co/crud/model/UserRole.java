package mz.co.crud.model;

public enum UserRole {

	ADMIN("Administrador"), GERENTE("Gerente"), VENDEDOR("Vendedor"), CAIXA("Caixa");

	private String description;

	UserRole(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Verifica se o role tem acesso a gestão (Categorias, Produtos, Clientes)
	 */
	public boolean hasGestaoAccess() {
		return this == ADMIN || this == GERENTE;
	}

	/**
	 * Verifica se o role tem acesso a vendas/saída
	 */
	public boolean hasSalesAccess() {
		return this == ADMIN || this == VENDEDOR || this == GERENTE || this == CAIXA;
	}

	/**
	 * Verifica se o role tem acesso a entradas de produtos
	 */
	public boolean hasStockEntryAccess() {
		return this == ADMIN || this == GERENTE;
	}

	/**
	 * Verifica se o role tem acesso a relatórios
	 */
	public boolean hasReportsAccess() {
		return this == ADMIN || this == GERENTE;
	}

	/**
	 * Verifica se o role tem acesso a utilitários (Utilizadores)
	 */
	public boolean hasUtilitariosAccess() {
		return this == ADMIN;
	}

}

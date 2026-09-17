package mz.co.crud.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "suppliers")
@NamedQueries({ @NamedQuery(name = "Supplier.findAll", query = "SELECT s FROM Supplier s ORDER BY s.name"),
		@NamedQuery(name = "Supplier.findByName", query = "SELECT s FROM Supplier s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))"),
		@NamedQuery(name = "Supplier.findByNuit", query = "SELECT s FROM Supplier s WHERE s.nuit = :nuit"),
		@NamedQuery(name = "Supplier.findByEmail", query = "SELECT s FROM Supplier s WHERE LOWER(s.email) = LOWER(:email)"),
		@NamedQuery(name = "Supplier.findByPhone", query = "SELECT s FROM Supplier s WHERE s.phone LIKE CONCAT('%', :phone, '%')"),
		@NamedQuery(name = "Supplier.findWithProductss", query = "SELECT DISTINCT s FROM Supplier s LEFT JOIN FETCH s.products ORDER BY s.name"),
		@NamedQuery(name = "Supplier.findAllOrderByCreatedAtDesc", query = "SELECT s FROM Supplier s ORDER BY s.createdAt DESC"),
		@NamedQuery(name = "Supplier.findWithProducts", query = "SELECT s FROM Supplier s JOIN FETCH s.products WHERE s.id = :id"),
		@NamedQuery(name = "Supplier.findAllWithProducts", query = "SELECT DISTINCT s FROM Supplier s "
				+ "LEFT JOIN FETCH s.products"),
		@NamedQuery(name = "Supplier.findByCity", query = "SELECT s FROM Supplier s WHERE LOWER(s.address) LIKE LOWER(CONCAT('%', :city, '%'))"),
		@NamedQuery(name = "Supplier.countProducts", query = "SELECT s.id, s.name, COUNT(p) FROM Supplier s LEFT JOIN s.products p GROUP BY s.id, s.name ORDER BY COUNT(p) DESC") })
public class Supplier {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String name;

	@Column(unique = true, length = 9)
	private String nuit;

	@Column(length = 20)
	private String phone;

	@Column(length = 300)
	private String address;

	@Column(length = 150)
	private String email;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Product> products = new ArrayList<>();
//	
//	@OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
//    private List<Product> products;

	// Constructors
	public Supplier() {
	}

	public Supplier(String name, String nuit, String phone, String email) {
		this.name = name;
		this.nuit = nuit;
		this.phone = phone;
		this.email = email;
	}

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNuit() {
		return nuit;
	}

	public void setNuit(String nuit) {
		this.nuit = nuit;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public boolean isValidNuit() {
		return nuit != null && nuit.matches("\\d{9}");
	}

	public boolean isValidEmail() {
		if (email == null || email.isEmpty())
			return false;
		return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
	}

	public boolean hasProducts() {
		return !products.isEmpty();
	}

	 

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Supplier other = (Supplier) obj;
		return Objects.equals(id, other.id);
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}

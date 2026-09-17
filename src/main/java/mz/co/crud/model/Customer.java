package mz.co.crud.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

@Entity
@Table(name = "customer")
@NamedQueries({ @NamedQuery(name = "Customer.findAll", query = "SELECT c FROM Customer c ORDER BY c.name DESC"),
		@NamedQuery(name = "Customer.findByName", query = "SELECT c FROM Customer  c WHERE c.name = :name ORDER BY c.name DESC"),
		@NamedQuery(name = "Customer.findAllOrderByCreatedAtDesc", query = "SELECT c FROM Customer c ORDER BY c.createdAt DESC"),
		@NamedQuery(name = "Customer.countCustomers", query = "SELECT count (c) FROM Customer  c ORDER BY c.name DESC"),
		@NamedQuery(name = "Customer.findById", query = "SELECT c FROM Customer  c WHERE c.id = :customerId ORDER BY c.name DESC") })
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "address", nullable = false)
	private String address = "N/A";

	@Column(name = "cellphone", nullable = false)
	private String cellphone = "N/A";

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	public Customer() {

	}

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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
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
		Customer other = (Customer) obj;
		return Objects.equals(id, other.id);
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}

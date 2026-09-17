package mz.co.crud.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import mz.co.crud.model.Product;
import mz.co.crud.model.Supplier;

@Service
@Transactional
public class SupplierService implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Autowired
	private EntityManager em;

	// Create
	public Supplier save(Supplier supplier) {
		if (supplier.getId() == null) {
			em.persist(supplier);
			return supplier;
		} else {
			return em.merge(supplier);
		}
	}

	public List<Supplier> findAllWithProducts() {
		return em.createNamedQuery("Supplier.findAllWithProducts", Supplier.class).getResultList();
	}

	// Read - usando NamedQueries
	public Optional<Supplier> findByIds(Long id) {
		Supplier supplier = em.find(Supplier.class, id);
		return Optional.ofNullable(supplier);
	}

	public Supplier findById(Long id) {

		return em.find(Supplier.class, id);
	}

	public List<Supplier> findAll() {
		return em.createNamedQuery("Supplier.findAll", Supplier.class).getResultList();
	}

	public List<Supplier> findByName(String name) {
		return em.createNamedQuery("Supplier.findByName", Supplier.class).setParameter("name", name).getResultList();
	}

	public Optional<Supplier> findByNuit(String nuit) {
		List<Supplier> suppliers = em.createNamedQuery("Supplier.findByNuit", Supplier.class).setParameter("nuit", nuit)
				.getResultList();
		return suppliers.isEmpty() ? Optional.empty() : Optional.of(suppliers.get(0));
	}

	public Optional<Supplier> findByEmail(String email) {
		List<Supplier> suppliers = em.createNamedQuery("Supplier.findByEmail", Supplier.class)
				.setParameter("email", email).getResultList();
		return suppliers.isEmpty() ? Optional.empty() : Optional.of(suppliers.get(0));
	}

	public List<Supplier> findByPhone(String phone) {
		return em.createNamedQuery("Supplier.findByPhone", Supplier.class).setParameter("phone", phone).getResultList();
	}

	public List<Supplier> findWithProducts() {
		return em.createNamedQuery("Supplier.findWithProducts", Supplier.class).getResultList();
	}

	public List<Supplier> findByCity(String city) {
		return em.createNamedQuery("Supplier.findByCity", Supplier.class).setParameter("city", city).getResultList();
	}

	public List<Object[]> getSuppliersWithProductCount() {
		return em.createNamedQuery("Supplier.countProducts", Object[].class).getResultList();
	}

	// Update
	public Supplier update(Supplier supplier) {
		return em.merge(supplier);
	}

	// Delete
	public void delete(Long id) {
		Supplier supplier = em.find(Supplier.class, id);
		if (supplier != null) {
			// Remove relationship with products before deleting
			for (Product product : supplier.getProducts()) {
				product.setSupplier(null);
			}
			em.remove(supplier);
		}
	}

	public void delete(Supplier supplier) {
		if (em.contains(supplier)) {
			em.remove(supplier);
		} else {
			Supplier managedSupplier = em.merge(supplier);
			// Remove relationship with products before deleting
			for (Product product : managedSupplier.getProducts()) {
				product.setSupplier(null);
			}
			em.remove(managedSupplier);
		}
	}

	// Business Methods
	public void addProductToSupplier(Long supplierId, Product product) {
		Supplier supplier = em.find(Supplier.class, supplierId);
		if (supplier != null) {
			product.setSupplier(supplier);
			if (product.getId() == null) {
				em.persist(product);
			} else {
				em.merge(product);
			}
		}
	}

	public void removeProductFromSupplier(Long supplierId, Long productId) {
		Product product = em.find(Product.class, productId);
		if (product != null && product.getSupplier() != null && product.getSupplier().getId().equals(supplierId)) {
			product.setSupplier(null);
			em.merge(product);
		}
	}

	public boolean hasProducts(Long supplierId) {
		Supplier supplier = em.find(Supplier.class, supplierId);
		return supplier != null && !supplier.getProducts().isEmpty();
	}

	public void updateContactInfo(Long supplierId, String phone, String email, String address) {
		Supplier supplier = em.find(Supplier.class, supplierId);
		if (supplier != null) {
			if (phone != null)
				supplier.setPhone(phone);
			if (email != null)
				supplier.setEmail(email);
			if (address != null)
				supplier.setAddress(address);
			em.merge(supplier);
		}
	}

	public boolean canDeleteSupplier(Long supplierId) {
		return !hasProducts(supplierId);
	}

	public int getProductCount(Long supplierId) {
		Supplier supplier = em.find(Supplier.class, supplierId);
		return supplier != null ? supplier.getProducts().size() : 0;
	}

	public List<Supplier> lastSuppliers(int limit) {
		return em.createNamedQuery("Supplier.findAllOrderByCreatedAtDesc", Supplier.class).setMaxResults(limit)
				.getResultList();
	}

	public Supplier getSupplierWithProducts(Long id) {
		return em.createNamedQuery("Supplier.findWithProducts", Supplier.class).setParameter("id", id)
				.getSingleResult();
	}
}

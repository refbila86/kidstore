package mz.co.crud.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import mz.co.crud.model.Product;

@Service
@Transactional
public class ProductService implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Autowired
	private EntityManager em;

	public void saveOrUpdateProduct(Product product)
	{
		if (product.getId() == null)
		{
			em.persist(product);
		} else
		{
			em.merge(product);
		}
	}

	public void removeProduct(Long id)
	{
		Product product = em.find(Product.class, id);
		if (product != null)
		{
			em.remove(product);
		} else
		{
			throw new IllegalArgumentException("Produto não encontrado para exclusão.");
		}
	}

	public List<Product> findAll()
	{
		TypedQuery<Product> query = em.createNamedQuery("Product.findAll", Product.class);
		return query.getResultList();
	}

	public List<Product> findProductsWithNoMoviment()
	{
		TypedQuery<Product> query = em.createNamedQuery("Product.findProductsWithNoMoviment", Product.class);
		return query.getResultList();
	}

	public List<Product> findByCategory(Long categoryId)
	{
		TypedQuery<Product> query = em.createNamedQuery("Product.findByCategory", Product.class);
		query.setParameter("catId", categoryId);
		return query.getResultList();
	}

	public List<Object[]> findTopSellingProducts()
	{
		TypedQuery<Object[]> query = em.createNamedQuery("Product.findTopSellingProducts", Object[].class);
		query.setMaxResults(5);
		return query.getResultList();
	}

	public List<Product> findByName(String name)
	{
		return em.createNamedQuery("Product.findByName", Product.class).setParameter("name", name).getResultList();
	}

	public List<Product> findBySize(String size)
	{
		TypedQuery<Product> query = em.createNamedQuery("Product.findBySize", Product.class);
		query.setParameter("size", size);
		return query.getResultList();
	}

	public Product findById(Long id)
	{
		return em.find(Product.class, id);
	}

	public Long countProducts()
	{
		return em.createNamedQuery("Product.countProducts", Long.class).getSingleResult();
	}

	public Long countZeroProducts()
	{
		return em.createNamedQuery("Product.countZeroProducts", Long.class).getSingleResult();
	}

	public List<Product> findByCategory(String category)
	{
		return em.createNamedQuery("Product.findByCategory", Product.class).setParameter("category", category).getResultList();
	}

	public List<Product> findByManufacturer(String manufacturer)
	{
		return em.createNamedQuery("Product.findByManufacturer", Product.class).setParameter("manufacturer", manufacturer).getResultList();
	}

	public List<Product> findBelowMinimumQuantity()
	{
		return em.createNamedQuery("Product.findBelowMinimumQuantity", Product.class).getResultList();
	}

	public List<Product> findByBatch(String batch)
	{
		return em.createNamedQuery("Product.findByBatch", Product.class).setParameter("batch", batch).getResultList();
	}

	public List<Product> findExpiringProducts(LocalDate startDate, LocalDate endDate)
	{
		return em.createNamedQuery("Product.findExpiringProducts", Product.class).setParameter("startDate", startDate).setParameter("endDate", endDate)
				.getResultList();
	}

	public List<Product> findExpiredProducts()
	{
		return em.createNamedQuery("Product.findExpiredProducts", Product.class).setParameter("currentDate", LocalDate.now()).getResultList();
	}

	public List<Product> findBySupplier(Long supplierId)
	{
		return em.createNamedQuery("Product.findBySupplier", Product.class).setParameter("supplierId", supplierId).getResultList();
	}

//	public List<Product> findActiveProducts() {
//		return em.createNamedQuery("Product.findActiveProducts", Product.class)
//				.setParameter("currentDate", LocalDate.now()).getResultList();
//	}

	// Update
	public Product update(Product product)
	{
		return em.merge(product);
	}

	// Delete
	public void delete(Long id)
	{
		Product product = em.find(Product.class, id);
		if (product != null)
		{
			em.remove(product);
		}
	}

	public void delete(Product product)
	{
		if (em.contains(product))
		{
			em.remove(product);
		} else
		{
			em.remove(em.merge(product));
		}
	}

	// Business Methods
	public void updateQuantity(Long productId, Integer newQuantity)
	{
		Product product = em.find(Product.class, productId);
		if (product != null)
		{
			product.setQuantity(newQuantity);
			em.merge(product);
		}
	}

	public void addStock(Long productId, Integer quantity)
	{
		Product product = em.find(Product.class, productId);
		if (product != null)
		{
			product.setQuantity(product.getQuantity() + quantity);
			em.merge(product);
		}
	}

	public void removeStock(Long productId, Integer quantity)
	{
		Product product = em.find(Product.class, productId);
		if (product != null)
		{
			int newQuantity = product.getQuantity() - quantity;
			if (newQuantity < 0)
			{
				throw new IllegalArgumentException("Quantidade insuficiente em estoque");
			}
			product.setQuantity(newQuantity);
			em.merge(product);
		}
	}

	public void updatePrice(Long productId, BigDecimal purchasePrice, BigDecimal salePrice)
	{
		Product product = em.find(Product.class, productId);
		if (product != null)
		{
			product.setPurchasePrice(purchasePrice);
			product.setSalePrice(salePrice);
			em.merge(product);
		}
	}

	public List<Product> findProductsExpiringSoon(int days)
	{
		LocalDate startDate = LocalDate.now();
		LocalDate endDate = startDate.plusDays(days);
		return findExpiringProducts(startDate, endDate);
	}

	public List<Product> findLowStockProducts()
	{
		return findBelowMinimumQuantity();
	}

	public List<Product> findActiveProducts()
	{
		return em.createNamedQuery("Product.findActiveProducts", Product.class).setParameter("currentDate", LocalDate.now()).getResultList();
	}

	// Últimos produtos cadastrados
	public List<Product> lastProducts(int limit)
	{
		return em.createNamedQuery("Product.findAllOrderByCreatedAtDesc", Product.class).setMaxResults(limit).getResultList();
	}

	// Produtos fora de validade
	public List<Product> expiredProducts()
	{
		return em.createNamedQuery("Product.findExpired", Product.class).getResultList();
	}

	public List<Product> nearExpiryProducts(int daysUntilExpiry)
	{
		LocalDate limitDate = LocalDate.now().plusDays(daysUntilExpiry);
		return em.createNamedQuery("Product.findNearExpiry", Product.class).setParameter("expirationDate", limitDate).getResultList();
	}
}

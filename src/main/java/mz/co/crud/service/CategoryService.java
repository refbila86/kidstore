package mz.co.crud.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import mz.co.crud.model.Category;

@Service
@Transactional
public class CategoryService  implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Autowired
	private EntityManager em;

	public List<Category> listar() {
		return em.createNamedQuery("Category.findAll", Category.class).getResultList();
	}

	public Category findByCategory(String category) {
		return em.createNamedQuery("Category.findByCategory", Category.class).setParameter("category", category)
				.getSingleResult();
	}

	public Category findByCategoryId(String categoryId) {
		return em.createNamedQuery("Category.findByCategoryId", Category.class).setParameter("categoryId", categoryId)
				.getSingleResult();
	}

	public void saveCategory(Category category) {
		if (category.getId() == null) {
			em.persist(category);
		} else {
			em.merge(category);
		}
	}

	public void removeCategory(Long id) {
		Category category = em.find(Category.class, id);
		if (category != null) {
			em.remove(category);
		}
	}
	
	 public Long totalCategories() {
	        return em.createNamedQuery("Category.countAll", Long.class)
	                 .getSingleResult();
	    }
	 
		public Category findById(Long id) {
			return  em.find(Category.class, id);
		}
}

package mz.co.crud.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import mz.co.crud.model.Customer;

@Service
@Transactional
public class CustomerService  implements Serializable
{

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private EntityManager em;

	public List<Customer> findAll() {
		return em.createNamedQuery("Customer.findAll", Customer.class).getResultList();
	}

	public Customer findByName(String name) {
		return em.createNamedQuery("Customer.findByName", Customer.class).setParameter("name", name)
				.getSingleResult();
	}
	
	public Customer findById(Long id) {
	    return em.find(Customer.class, id);
	}
	

	public void saveCustomer(Customer customer) {
		if (customer.getId() == null) {
			em.persist(customer);
		} else {
			em.merge(customer);
		}
	}

	public void removeCustumer(Long id) {
		Customer customer = em.find(Customer.class, id);
		if (customer != null) {
			em.remove(customer);
		}
	}
	
	public Long countCustomers() {
		return em.createNamedQuery("Customer.countCustomers", Long.class).getSingleResult();
	}

	 public List<Customer> lastCustomers(int limit) {
	        return em.createNamedQuery("Customer.findAllOrderByCreatedAtDesc", Customer.class)
	                 .setMaxResults(limit)
	                 .getResultList();
	    }
}

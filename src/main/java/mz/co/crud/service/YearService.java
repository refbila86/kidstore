package mz.co.crud.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import mz.co.crud.model.Year;

@Service
@Transactional
public class YearService implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Autowired
	private EntityManager em;
	public Year findByOpened() {
		return em.createNamedQuery("Year.findByOpened", Year.class).getSingleResult();
	}

	public Year findByYear(int yearValue) {
		try {
			return (Year) em.createNamedQuery("Year.findByYear").setParameter("year", yearValue).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public Year findById(Long id) {
		return em.find(Year.class, id);
	}

	public void saveYear(Year year) {
		if (year.getId() == null) {
			em.persist(year);
		} else {
			em.merge(year);
		}
	}

	public Year findOrCreateByYear(int yearValue) {
		Year year = findByYear(yearValue);

		if (year == null) {
			year = new Year();
			year.setYear(yearValue);
			year.setDescription("Ano " + yearValue);
			year.setOpened("Sim");
			em.persist(year);
			em.flush();
		}

		return year;
	}

	public void removeYear(Long id) {
		Year year = em.find(Year.class, id);
		if (year != null) {
			em.remove(year);
		}
	}

	public List<Year> findAll() {
		return em.createNamedQuery("Year.findAll", Year.class).getResultList();
	}

}

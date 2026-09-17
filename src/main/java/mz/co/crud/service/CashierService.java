package mz.co.crud.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import mz.co.crud.model.Cashier;
import mz.co.crud.model.CashierStatus;
import mz.co.crud.model.User;
import mz.co.crud.model.Year;

@Service
@Transactional
public class CashierService implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Autowired
	private EntityManager em;

	
	public List<Cashier> findAll() {
		return em.createNamedQuery("Cashier.findAll", Cashier.class).getResultList();
	}

	// Verificar se existe caixa para a data
	public Cashier findByDate(Date date) {
		List<Cashier> list = em.createNamedQuery("Cashier.findByDate", Cashier.class).setParameter("date", date)
				.getResultList();

		return list.isEmpty() ? null : list.get(0);
	}

	// Buscar datas que ainda não têm caixa (para fechar)
//	public List<LocalDate> findMissingDates() {
//		return em.createNamedQuery("Cashier.findMissingDates", LocalDate.class).getResultList();
//	}
	public List<LocalDate> findMissingDates() {

		List<java.sql.Date> sqlDates = em.createNamedQuery("Cashier.findMissingDates", java.sql.Date.class)
				.getResultList();

		return sqlDates.stream().map(java.sql.Date::toLocalDate).toList();
	}

	public Long countMissingCashierDates() {

		Long totalMissing = em.createNamedQuery("Cashier.countMissingCashierDates", Long.class).getSingleResult();
		return totalMissing;
	}

	// Total do dia vindo das vendas
	public BigDecimal sumSalesOfDay(Date date) {
		BigDecimal total = em.createNamedQuery("Cashier.sumSalesOfDay", BigDecimal.class).setParameter("date", date)
				.getSingleResult();

		return total == null ? BigDecimal.ZERO : total;
	}

	// Criar novo caixa
	public Cashier create(Date date, User user, Year year) {
		Cashier c = new Cashier();
		c.setDate(date);
		c.setStatus(CashierStatus.ABERTO);
		c.setYear(year);
		c.setUser(user);
		c.setTotalOfDay(sumSalesOfDay(date));

		em.persist(c);
		return c;
	}

	// Fechar caixa
	public void closeCashier(Cashier c, User user) {
		c.setDate(new Date());
		c.setStatus(CashierStatus.FECHADO);
		c.setUser(user);

		em.merge(c);
	}

	public Cashier find(Long id) {
		return em.find(Cashier.class, id);
	}

	public Cashier saveOrUpdateCashier(Cashier cashier) {
		if (cashier == null || cashier.getDate() == null) {
			throw new IllegalArgumentException("O objeto Cashier e a data não podem ser nulos.");
		}

		// Procura se já existe um Cashier com a mesma data
		Cashier existing = null;
		try {
			existing = em.createNamedQuery("Cashier.findByDate", Cashier.class).setParameter("date", cashier.getDate())
					.getSingleResult();
		} catch (NoResultException e) {
			// Nenhum registro encontrado — segue para inserção
		}

		if (existing != null) {
			// Já existe uma linha com esta data, então atualiza (merge)
			// Garante que o ID existente será usado
			cashier.setId(existing.getId());
			return em.merge(cashier);
		} else {
			// Não existe — insere novo
			em.persist(cashier);
			return cashier;
		}
	}

}

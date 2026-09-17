package mz.co.crud.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import mz.co.crud.model.Sale;
import mz.co.crud.model.Year;

@Service
@Transactional
public class SaleService implements Serializable
{

	private static final long serialVersionUID = 1L;
 

	@Autowired
	private EntityManager em;

	// Salvar ou atualizar uma venda
	public Sale saveOrUpdate(Sale sale)
	{
		if (sale.getId() == null)
		{
			em.persist(sale);
			return sale;
		} else
		{
			return em.merge(sale);
		}
	}

	public Sale saveSale(Sale sale)
	{
		if (sale.getId() == null)
		{
			sale.setNumericCode(getNextSaleNumber(sale.getYear()));
			sale.setSaleCode(generateSaleCode(sale.getYear()));
		}
		em.persist(sale);

		return sale;
	}

	// Remover venda
	public void removeSale(Long id)
	{
		Sale sale = em.find(Sale.class, id);
		if (sale != null)
		{
			em.remove(sale);
		}
	}

	// Listar todas as vendas
	public List<Sale> findAll()
	{
		return em.createNamedQuery("Sale.findAll", Sale.class).getResultList();
	}

	public List<Sale> findAllWithItems()
	{
		return em.createNamedQuery("Sale.findAllWithItems", Sale.class).getResultList();
	}

	public List<Sale> findByIdWithItemss(Long saleId)
	{
		List<Sale> result = em.createNamedQuery("Sale.findByIdWithItems", Sale.class).getResultList();

		if (result.isEmpty())
		{
			throw new RuntimeException("Venda nao encontrada com codigo: " + saleId);
		}

		return result;

	}

	public Sale findByIdWithItemsss(Long saleId)
	{
		try
		{
			return em.createNamedQuery("Sale.findByIdWithItems", Sale.class).setParameter("id", saleId).getSingleResult();

		} catch (NoResultException e)
		{
			throw new RuntimeException("Venda não encontrada com código: " + saleId);
		}
	}

	public Sale findByIdWithItems(Long id)
	{
		TypedQuery<Sale> query = em.createNamedQuery("Sale.findByIdWithItems", Sale.class);
		query.setParameter("id", id);
		return query.getSingleResult();
	}

	public List<Sale> findByDate(Date date)
	{
		try
		{
			if (date == null)
			{
				return List.of();
			}

			LocalDate localDate;

			if (date instanceof java.sql.Date)
			{
				localDate = ((java.sql.Date) date).toLocalDate();
			} else
			{
				localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			}

			java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

			return em.createNamedQuery("Sale.findByDate", Sale.class).setParameter("date", sqlDate).getResultList();

		} catch (Exception e)
		{
			e.printStackTrace();
			return List.of();
		}
	}

	public List<Sale> findByDates(Date date)
	{
		try
		{
			LocalDate localDate;

			if (date instanceof java.sql.Date)
			{
				localDate = ((java.sql.Date) date).toLocalDate();
			} else
			{
				localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			}

			LocalDateTime startOfDay = localDate.atStartOfDay();
			LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

			Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
			Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

			return em.createNamedQuery("Sale.findByDate", Sale.class).setParameter("startDate", startDate).setParameter("endDate", endDate).getResultList();

		} catch (Exception e)
		{
			e.printStackTrace();
			return List.of();
		}
	}

	public int getNextSaleNumber(Year year)
	{
		Integer max = (Integer) em.createNamedQuery("Sale.findMaxCodeByYear").setParameter("year", year).getSingleResult();
		return (max == null) ? 1 : max + 1;
	}

	public String generateSaleCode(Year year)
	{
		int next = getNextSaleNumber(year);
		return String.format("%04d/%d", next, year.getYear());
	}

	public Sale findByCustomer(String name)
	{
		return em.createNamedQuery("Sale.findByCustomer", Sale.class).setParameter("name", name).getSingleResult();
	}

	public List<Sale> findAllByDate(Date date)
	{
		return em.createNamedQuery("Sale.findAllByDate", Sale.class).setParameter("date", date).getResultList();

	}

	public List<Sale> findDateByDate(String date)
	{
		return em.createNamedQuery("Sale.findDateByDate", Sale.class).setParameter("date", date).getResultList();

	}

	public Sale findByCodeAndYear(String saleCode, Long yearId)
	{
		TypedQuery<Sale> query = em.createNamedQuery("Sale.findByCodeAndYear", Sale.class);
		query.setParameter("saleCode", saleCode);
		query.setParameter("yearId", yearId);
		return query.getSingleResult();
	}

	// Buscar venda por ID
	public Sale findById(Long id)
	{
		return em.createNamedQuery("Sale.findById", Sale.class).setParameter("id", id).getSingleResult();
	}

	public Long countDailySales(Date date)
	{

		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		LocalDateTime startOfDay = localDate.atStartOfDay();
		LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

		Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

		return em.createNamedQuery("Sale.countDailySales", Long.class).setParameter("startDate", startDate).setParameter("endDate", endDate).getSingleResult();

	}

	public BigDecimal sumDailySales(Date date)
	{

		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		LocalDateTime startOfDay = localDate.atStartOfDay();
		LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

		Date startDate = Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
		Date endDate = Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

		return em.createNamedQuery("Sale.sumDailySales", BigDecimal.class).setParameter("startDate", startDate).setParameter("endDate", endDate)
				.getSingleResult();

	}

//	public List<LocalDate> findUniqueSalesDates() {
//	    try {
//	        // Executa a named query
//	        List<Date> dates = em
//	            .createNamedQuery("Sale.findDistinctDates", Date.class)
//	            .getResultList();
//	        
//	        // Converte Date para LocalDate
//	        return dates.stream()
//	            .map(date -> date.toInstant()
//	                .atZone(ZoneId.of("Africa/Maputo"))
//	                .toLocalDate())
//	            .collect(Collectors.toList());
//	            
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return new ArrayList<>();
//	    }
//}

	public List<LocalDate> findUniqueSalesDates()
	{
		try
		{
			// Busca todas as vendas e extrai datas únicas
			List<Sale> allSales = em.createNamedQuery("Sale.findAll", Sale.class).getResultList();

			// Converte para LocalDate e remove duplicatas
			return allSales.stream().map(sale ->
			{
				if (sale.getDateSale() != null)
				{
					if (sale.getDateSale() instanceof java.sql.Date)
					{
						return ((java.sql.Date) sale.getDateSale()).toLocalDate();
					} else
					{
						return sale.getDateSale().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					}
				}
				return null;
			}).filter(date -> date != null).distinct().sorted((d1, d2) -> d2.compareTo(d1)) // Mais recente primeiro
					.collect(Collectors.toList());

		} catch (Exception e)
		{
			e.printStackTrace();
			return new ArrayList<>();
		}

	}

	public List<LocalDate> findDistinctDatesNotInCashieer()
	{
		try
		{
			// Retorna Date diretamente
			List<Date> dates = em.createNamedQuery("Sale.findDistinctDatesNotInCashier", Date.class).getResultList();

			// Converte para LocalDate
			return dates.stream().filter(date -> date != null).map(date ->
			{
				if (date instanceof java.sql.Date)
				{
					return ((java.sql.Date) date).toLocalDate();
				} else
				{
					return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				}
			}).distinct().sorted((d1, d2) -> d2.compareTo(d1)).collect(Collectors.toList());

		} catch (Exception e)
		{
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public List<LocalDate> findDistinctDatesNotInCashier()
	{
		try
		{
			List<Date> dates = em.createNamedQuery("Sale.findDistinctDatesNotInCashier", Date.class).getResultList();

			// Converte para LocalDate
			return dates.stream().filter(date -> date != null).map(date ->
			{
				if (date instanceof java.sql.Date)
				{
					return ((java.sql.Date) date).toLocalDate();
				} else
				{
					return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				}
			}).collect(Collectors.toList()); // Já vem ordenado e distinct da query

		} catch (Exception e)
		{
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	public List<Object[]> findDailySalesLast7Days()
	{

		LocalDate today = LocalDate.now();
		LocalDate startDate = today.minusDays(6);

		Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

		Date end = Date.from(today.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

		try
		{

			String sql = """
					SELECT DATE(date_sale) AS sale_day,
					       SUM(total_sale) AS total
					FROM sale
					WHERE date_sale >= :startDate
					  AND date_sale <= :endDate
					GROUP BY DATE(date_sale)
					ORDER BY DATE(date_sale)
					""";

			return em.createNativeQuery(sql).setParameter("startDate", start).setParameter("endDate", end).getResultList();

		} catch (Exception e)
		{
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
}
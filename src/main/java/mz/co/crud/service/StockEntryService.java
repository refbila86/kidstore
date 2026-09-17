package mz.co.crud.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import mz.co.crud.model.Product;
import mz.co.crud.model.StockEntry;
import mz.co.crud.model.StockEntryDetails;

@Service
@Transactional
public class StockEntryService implements Serializable
{

	private static final long serialVersionUID = 1L;

	@Autowired
	private EntityManager em;

	public StockEntry findById(Long id)
	{
		return em.find(StockEntry.class, id);
	}

	public List<StockEntry> findAll()
	{
		List<StockEntry> stockEntry = new ArrayList<>();
		stockEntry = em.createNamedQuery("StockEntry.findAll", StockEntry.class).getResultList();
		return stockEntry;
	}

	public boolean existsByDate(Date date)
	{
		Long count = em.createNamedQuery("StockEntry.existsByDate", Long.class).setParameter("date", date).getSingleResult();
		return count > 0;
	}

	public List<StockEntryDetails> findByStockEntryId(Long entryId)
	{
		return em.createNamedQuery("StockEntryDetails.findByStockEntryId", StockEntryDetails.class).setParameter("entryId", entryId).getResultList();
	}

	public StockEntry findByEntryDetailsId(Long entryId)
	{
		return em.createNamedQuery("StockEntry.findByEntryDetailsId", StockEntry.class).setParameter("id", entryId).getSingleResult();
	}

	public void saveEntry(StockEntry stockEntry)
	{
		// Definir data se não estiver definida
		if (stockEntry.getEntryDate() == null)
		{
			stockEntry.setEntryDate(new Date());
		}

		// Processar cada item e atualizar stock
		for (StockEntryDetails detail : stockEntry.getListEntriesDetails())
		{
			Product product = em.find(Product.class, detail.getProduct().getId());

			Integer currentStock = product.getCurrentStock() != null ? product.getCurrentStock() : 0;
			Integer quantity = detail.getQuantity() != null ? detail.getQuantity() : 0;
			Integer newQuantity = currentStock + quantity;

			product.setCurrentStock(newQuantity);
			em.merge(product);

			detail.setStockEntry(stockEntry);
		}

		// Sempre criar nova entrada (permitir múltiplas por dia)
		em.persist(stockEntry);
	}

	public void save(StockEntry stockEntry)
	{
		if (existsByDate(stockEntry.getEntryDate()))
		{
			throw new IllegalArgumentException("Já existe uma entrada registada nessa data!- " + stockEntry.getEntryDate());
		}

		for (StockEntryDetails detail : stockEntry.getListEntriesDetails())
		{

			Product product = em.find(Product.class, detail.getProduct().getId());

			Integer currentStock = product.getCurrentStock() != null ? product.getCurrentStock() : 0;

			Integer quantity = detail.getQuantity() != null ? detail.getQuantity() : 0;

			Integer newQuantity = currentStock + quantity;

			product.setCurrentStock(newQuantity);

			em.merge(product);

			detail.setStockEntry(stockEntry);
		}

		em.persist(stockEntry);
	}

	public void saveOrUpdate(StockEntry stockEntry)
	{
		// Definir data se não estiver definida
		if (stockEntry.getEntryDate() == null)
		{
			stockEntry.setEntryDate(new Date());
		}

		// Verificar se já existe uma entrada na mesma data (com detalhes carregados)
		StockEntry existingEntry = findByDateWithDetails(stockEntry.getEntryDate());

		if (existingEntry != null)
		{
			// Entrada já existe: adicionar apenas os novos itens
			for (StockEntryDetails detail : stockEntry.getListEntriesDetails())
			{
				Product product = em.find(Product.class, detail.getProduct().getId());

				Integer currentStock = product.getCurrentStock() != null ? product.getCurrentStock() : 0;
				Integer quantity = detail.getQuantity() != null ? detail.getQuantity() : 0;
				Integer newQuantity = currentStock + quantity;

				product.setCurrentStock(newQuantity);
				em.merge(product);

				// Associar o detalhe à entrada existente
				detail.setStockEntry(existingEntry);
				existingEntry.getListEntriesDetails().add(detail);
				em.persist(detail);
			}

			// Atualizar a entrada existente
			em.merge(existingEntry);

		} else
		{
			// Entrada não existe: criar nova
			for (StockEntryDetails detail : stockEntry.getListEntriesDetails())
			{
				Product product = em.find(Product.class, detail.getProduct().getId());

				Integer currentStock = product.getCurrentStock() != null ? product.getCurrentStock() : 0;
				Integer quantity = detail.getQuantity() != null ? detail.getQuantity() : 0;
				Integer newQuantity = currentStock + quantity;

				product.setCurrentStock(newQuantity);
				em.merge(product);

				detail.setStockEntry(stockEntry);
			}

			em.persist(stockEntry);
		}
	}

	private StockEntry findByDateWithDetails(Date entryDate)
	{
		try
		{
			// Normalizar a data para comparar apenas dia/mês/ano
			Calendar cal = Calendar.getInstance();
			cal.setTime(entryDate);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date startDate = cal.getTime();

			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			Date endDate = cal.getTime();

			return em.createNamedQuery("StockEntry.findByDateWithDetails", StockEntry.class).setParameter("startDate", startDate)
					.setParameter("endDate", endDate).getSingleResult();

		} catch (NoResultException e)
		{
			return null;
		}
	}

	public List<Object[]> getProductSummary()
	{
		Query query = em.createNamedQuery("StockEntry.findProductSummary");
		return query.getResultList();
	}

}

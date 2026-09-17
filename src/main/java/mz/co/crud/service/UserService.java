package mz.co.crud.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import mz.co.crud.model.User;

@Service
@Transactional
public class UserService implements Serializable
{

	private static final long serialVersionUID = 1L;
 

	@Autowired
	private EntityManager em;

	public List<User> listar()
	{
		return em.createNamedQuery("User.findAll", User.class).getResultList();
	}

	public User findByEmail(String email)
	{
		return em.createNamedQuery("User.findByEmail", User.class).setParameter("email", email).getSingleResult();
	}

	public User findByUser(String username)
	{
		return em.createNamedQuery("User.findByUser", User.class).setParameter("username", username).getSingleResult();
	}

	public long countUser()
	{
		return em.createNamedQuery("User.countUser", Long.class).getSingleResult();
	}

	public void salvar(User u)
	{
		if (u.getId() == null)
		{
			em.persist(u);
		} else
		{
			em.merge(u);
		}
	}

	public void remover(Long id)
	{
		User u = em.find(User.class, id);
		if (u != null)
		{
			em.remove(u);
		}
	}

	public User findByUsername(String username)
	{
		try
		{
			TypedQuery<User> query = em.createNamedQuery("User.findByUsername", User.class);
			query.setParameter("username", username);
			return query.getSingleResult();
		} catch (NoResultException e)
		{
			return null;
		}
	}

	public User authenticate(String username, String password)
	{
		User user = findByUsername(username);

		if (user == null)
		{
			return null;
		}

		if (!user.getActive())
		{
			return null;
		}

		// Verifica senha (use BCrypt ou similar em produção!)
		if (verifyPassword(password, user.getPassword()))
		{
			user.setLastLogin(new Date());
			em.merge(user);
			return user;
		}

		return null;
	}

	private boolean verifyPassword(String plainPassword, String hashedPassword)
	{
		// IMPORTANTE: Em produção, use BCrypt!
		// Por enquanto, comparação simples (NUNCA faça isso em produção!)
		return plainPassword.equals(hashedPassword);
	}

	public String hashPassword(String plainPassword)
	{
		// IMPORTANTE: Em produção, use BCrypt!
		// import org.mindrot.jbcrypt.BCrypt;
		// return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
		return plainPassword; // TEMPORÁRIO - NUNCA em produção!
	}
}

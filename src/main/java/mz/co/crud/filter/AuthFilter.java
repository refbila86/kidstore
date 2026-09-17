//package mz.co.crud.filter;
//
//import java.io.IOException;
//
//import jakarta.servlet.Filter;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//public class AuthFilter implements Filter {
//	@Override
//	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//			throws IOException, ServletException {
//
//		HttpServletRequest req = (HttpServletRequest) request;
//		HttpServletResponse res = (HttpServletResponse) response;
//		HttpSession session = req.getSession(false); // não cria sessão
//
//		String loginURI = req.getContextPath() + "/login.xhtml";
//
//		boolean loggedIn = (session != null) && (session.getAttribute("usuarioLogado") != null);
//		boolean loginRequest = req.getRequestURI().equals(loginURI);
//
//		if (loggedIn || loginRequest) {
//			chain.doFilter(request, response); // continua normalmente
//		} else {
//			res.sendRedirect(loginURI); // redireciona para login
//		}
//	}
//}

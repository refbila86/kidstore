//package mz.co.crud.config;
//
//import java.io.IOException;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.FilterConfig;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import mz.co.crud.bean.LoginBean;
//
//
//@WebFilter(urlPatterns = {"/pages/*"})
//public class AuthenticationFilter {
//	
//	
//
//	   public void init(FilterConfig filterConfig) throws ServletException {
//	        // Inicialização se necessário
//	    }
//	   
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//        HttpSession session = httpRequest.getSession(false);
//        
//        LoginBean loginBean = (session != null) 
//            ? (LoginBean) session.getAttribute("loginBean") 
//            : null;
//        
//        boolean isLoggedIn = (loginBean != null && loginBean.isLoggedIn());
//        
//        if (isLoggedIn) {
//            chain.doFilter(request, response);
//        } else {
//            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.xhtml");
//        }
//    }
//	
//	 public void destroy() {
//	        // Limpeza se necessário
//	    }
//}

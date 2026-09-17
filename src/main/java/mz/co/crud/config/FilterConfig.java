//package mz.co.crud.config;
//
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import mz.co.crud.filter.AuthFilter;
//
//@Configuration
//public class FilterConfig {
//
//    @Bean
//    public FilterRegistrationBean<AuthFilter> authFilter() {
//        FilterRegistrationBean<AuthFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(new AuthFilter());
//        registration.addUrlPatterns("/pages/*"); // protege todas as páginas internas
//        registration.setOrder(1);
//        return registration;
//    }
//}
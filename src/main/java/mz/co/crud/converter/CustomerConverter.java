package mz.co.crud.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;
import mz.co.crud.model.Customer;
import mz.co.crud.service.CustomerService;

@FacesConverter(value = "customerConverter")
public class CustomerConverter implements Converter<Customer> {

	private CustomerService getCustomerService() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		WebApplicationContext context = FacesContextUtils.getWebApplicationContext(facesContext);
		return context.getBean(CustomerService.class);
	}

	@Override
	public Customer getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null || value.isEmpty() || value.equals("null")) {
			return null;
		}

		try {
			Long id = Long.valueOf(value);
			return getCustomerService().findById(id);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Customer customer) {
		if (customer == null || customer.getId() == null) {
			return "";
		}
		return customer.getId().toString();
	}
}
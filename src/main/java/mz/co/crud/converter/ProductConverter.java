package mz.co.crud.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;
import mz.co.crud.model.Product;
import mz.co.crud.service.ProductService;

@FacesConverter(value = "productConverter")
public class ProductConverter implements Converter<Product> {

    private ProductService getProductService() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        WebApplicationContext context = FacesContextUtils.getWebApplicationContext(facesContext);
        return context.getBean(ProductService.class);
    }

    @Override
    public Product getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty() || value.equals("null")) {
            return null;
        }
        
        try {
            Long id = Long.valueOf(value);
            return getProductService().findById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Product product) {
        if (product == null || product.getId() == null) {
            return "";
        }
        return product.getId().toString();
    }
}
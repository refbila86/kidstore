package mz.co.crud.converter;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import mz.co.crud.model.Supplier;
import mz.co.crud.service.SupplierService;

@FacesConverter(value = "supplierConverter")
public class SupplierConverter implements Converter<Object> {

    @Inject
    private SupplierService supplierService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            Long id = Long.valueOf(value);
            return supplierService.findById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        if (value == null) {
            return "";
        }

        if (value instanceof Supplier) {
            Supplier supplier = (Supplier) value;

            if (supplier.getId() != null) {
                return supplier.getId().toString();
            }
        }

        return "";
    }
}
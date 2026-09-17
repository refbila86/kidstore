package mz.co.crud.converter;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import mz.co.crud.model.Category;
import mz.co.crud.service.CategoryService;

@FacesConverter(value = "categoryConverter", managed = true)
public class CategoryConverter implements Converter<Object> {

    @Inject
    private CategoryService categoryService;

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            Long id = Long.valueOf(value);
            return categoryService.findById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        if (value == null) {
            return "";
        }

        if (value instanceof Category) {
            Category category = (Category) value;

            if (category.getId() != null) {
                return String.valueOf(category.getId());
            }
        }

        return "";
    }
}
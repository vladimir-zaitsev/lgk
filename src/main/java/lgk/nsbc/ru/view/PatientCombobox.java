package lgk.nsbc.ru.view;

import lgk.nsbc.ru.backend.PatientContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by user on 16.03.2016.
 */
public class PatientCombobox  extends ComboBox {
    private List<String> myPropIds = Collections.emptyList();

    public PatientCombobox(String caption) {
        super(caption);
    }

    public PatientCombobox() {

    }

    @Override
    public void setItemCaptionPropertyId(Object propId) {
        myPropIds = Arrays.asList(((String) propId).split(","));
    }

    @Override
    public String getItemCaption( Object itemId ) {
        StringBuilder sb = new StringBuilder();
        String delimiter = "";
        for (String propId : myPropIds) {
            Property<?> p = getContainerProperty(itemId, propId);
            sb.append(delimiter).append(getMyCaption(p));
            delimiter = " ";
        }
        return sb.toString();
    }

    private String getMyCaption(Property<?> p) {
        String caption = null;
        if (p != null) {
            Object value = p.getValue();
            if (value != null) {
                caption = value.toString();
            }
        }
        return caption != null ? caption : "";
    }

    @Override
    protected Container.Filter buildFilter(String filterString, FilteringMode filteringMode) {
        return new PatientContainer.SuggestionFilter(filterString);
    }
}


package lgk.nsbc.ru.view;

import lgk.nsbc.ru.backend.PatientContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.ComboBox;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 16.03.2016.
 */
public class PatientComboBox extends ComboBox {
    private List<String> myPropIds = Collections.emptyList();

    public PatientComboBox(String caption) {
        super(caption);
    }

    public PatientComboBox() {

    }

    @Override
    public void setItemCaptionPropertyId(Object propId) {
        myPropIds = Arrays.asList(((String) propId).split(","));
    }

    @Override
    public String getItemCaption( Object itemId ) {
        StringBuilder sb = new StringBuilder();
        String delimiter = " ";
        for (String propId : myPropIds) {
            Property<?> p = getContainerProperty(itemId, propId);
			if (p!=null&&p.getValue()!=null) {
				Object propertyValue =  p.getValue();
				String stringValue;
				if (propId.equals("birthday")&&propertyValue instanceof Date) {
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-dd-MM");
					stringValue = simpleDateFormat.format((Date)propertyValue);
				} else {
					stringValue = p.getValue().toString();
				}
				sb.append(stringValue).append(delimiter);
			}
        }
		return sb.toString();
    }

    @Override
    protected Container.Filter buildFilter(String filterString, FilteringMode filteringMode) {
        return new PatientContainer.SuggestionFilter(filterString);
    }
}


package lgk.nsbc.ru;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.renderers.ClickableRenderer;
import lgk.nsbc.ru.client.textfieldrenderer.TextFieldRendererServerRpc;
import lgk.nsbc.ru.client.textfieldrenderer.TextFieldRendererState;


public class TextFieldRenderer<T> extends ClickableRenderer<T>
{
    public TextFieldRenderer()
    {
        super((Class<T>) Object.class);
     
        registerRpc(new TextFieldRendererServerRpc()
        {

            public void onChange(String rowKey, String columnId, String newValue)
            {

                Object itemId = getItemId(rowKey);
                Object columnPropertyId = getColumn(columnId).getPropertyId();

                Item row = getParentGrid().getContainerDataSource().getItem(itemId);

                @SuppressWarnings("unchecked")
				Property<Object> cell = (Property<Object>) row.getItemProperty(columnPropertyId);

                Class<T> targetType = (Class<T>) cell.getType();
                Converter<String, T> converter = (Converter<String, T>) getColumn(columnId).getConverter();
                if (converter != null) {
                	cell.setValue(converter.convertToModel(newValue, targetType, getParentGrid().getLocale()));
                } else if (targetType == String.class) {
                	cell.setValue(newValue);
                }
            }

        });
    }


    @Override
    protected TextFieldRendererState getState()
    {
    	return (TextFieldRendererState) super.getState();
    }
    
}

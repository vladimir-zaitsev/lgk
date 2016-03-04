package lgk.nsbc.ru;

import com.vaadin.ui.renderers.ClickableRenderer;
import com.vaadin.ui.renderers.Renderer;
import lgk.nsbc.ru.client.layoutwithlabel.LayoutWithLabelServerRpc;
import lgk.nsbc.ru.client.layoutwithlabel.LayoutWithLabelState;

/**
 * Created by Роман on 04.03.2016.
 */
public class LayoutWithLabelRenderer<T> extends ClickableRenderer<T> {
	public LayoutWithLabelRenderer() {
		super((Class<T>) Object.class);
		registerRpc(new LayoutWithLabelServerRpc(){

		});
	}

	@Override
	protected LayoutWithLabelState getState()
	{
		return (LayoutWithLabelState) super.getState();
	}
}

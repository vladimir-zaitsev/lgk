package lgk.nsbc.ru.view;

import com.vaadin.ui.CustomComponent;

public abstract class AbstractView<Model> extends CustomComponent {
	protected final Model model;

	public AbstractView(Model model) {
		this.model = model;
	}
}

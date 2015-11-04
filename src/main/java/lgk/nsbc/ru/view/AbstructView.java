package lgk.nsbc.ru.view;

import com.vaadin.ui.CustomComponent;

public abstract class AbstructView<Model> extends CustomComponent {
	protected final Model model;

	public AbstructView(Model model) {
		this.model = model;
	}
}

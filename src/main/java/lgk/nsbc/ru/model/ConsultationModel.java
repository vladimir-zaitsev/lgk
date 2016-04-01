package lgk.nsbc.ru.model;

import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import com.vaadin.data.util.BeanItemContainer;

/**
 * Created by user on 20.02.2016.
 */
public class ConsultationModel {

	private final BeanItemContainer<ConsultationEvent> beanItemContainer = new BeanItemContainer<>(ConsultationEvent.class);

	public BeanItemContainer<ConsultationEvent> getBeanItemContainer() {
		return beanItemContainer;
	}

	public void sortContainer() {
		beanItemContainer.sort(new Object[]{"start","caption"},new boolean[]{true,false});
	}
}
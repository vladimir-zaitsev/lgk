package lgk.nsbc.ru.model;

import com.vaadin.data.util.BeanItemContainer;
import lgk.nsbc.ru.backend.entity.ConsultationDays;

/**
 * Created by Роман on 29.02.2016.
 */
public class ConsultationModel {
	BeanItemContainer<ConsultationDays> beanItemContainer = new BeanItemContainer<ConsultationDays>(ConsultationDays.class);

	public BeanItemContainer<ConsultationDays> getBeanItemContainer() {
		return beanItemContainer;
	}
}

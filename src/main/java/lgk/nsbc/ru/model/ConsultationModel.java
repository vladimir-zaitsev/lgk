package lgk.nsbc.ru.model;

import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import com.vaadin.data.util.BeanItemContainer;

/**
 * Created by user on 20.02.2016.
 */
public class ConsultationModel {



	public final BeanItemContainer<ConsultationEvent> beanItemContainer = new BeanItemContainer<>(ConsultationEvent.class);

	//   public final BasicEventProvider dataSource = new BasicEventProvider();
	// получаем контейнер
	public BeanItemContainer<ConsultationEvent> getConsultationBAsicEventContainer() {
		return beanItemContainer;
	}

}
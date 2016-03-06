package lgk.nsbc.ru.model;

import lgk.nsbc.ru.backend.basicevent.ConsultationBasicEvent;
import com.vaadin.data.util.BeanItemContainer;

/**
 * Created by user on 20.02.2016.
 */
public class ConsultationModel {



	public final BeanItemContainer<ConsultationBasicEvent> beanItemContainer = new BeanItemContainer<>(ConsultationBasicEvent.class);

	//   public final BasicEventProvider dataSource = new BasicEventProvider();
	// получаем контейнер
	public BeanItemContainer<ConsultationBasicEvent> getConsultationBAsicEventContainer() {
		return beanItemContainer;
	}

}
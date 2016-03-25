package lgk.nsbc.ru.view;

import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Patient;

/**
 * Created by user on 16.03.2016.
 */
public interface EditFormView {
    Patient getSelectItem();
    void commitEvent();
    void discardEvent();
    ConsultationEvent getFormEvent();
    void createEventPopup(ConsultationEvent consultationEvent, boolean newEvent);
    ConsultationEvent getConsultationEvent();
	void setSelectItem(Patient patient);
}


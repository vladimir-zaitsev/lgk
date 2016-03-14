package lgk.nsbc.ru.view;

import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

/**
 * Created by user on 08.03.2016.
 */
public class ComboboxView {

	EditConsultationForm editConsultationForm;
	final FieldGroup fieldGroup;
	public ComboboxView(EditConsultationForm editConsultationForm) {
		this.editConsultationForm = editConsultationForm;
		fieldGroup = new FieldGroup();
	}

	public void commitConsultationEvent() {
		try {
			fieldGroup.commit();
		} catch (FieldGroup.CommitException e) {
			e.printStackTrace();
		}
		BasicEvent event = getFormCalendarEvent();
		if (event.getEnd() == null) {
			event.setEnd(event.getStart());
		}
		if (!editConsultationForm.calendarView.consultationModel.beanItemContainer.containsId(event))
			editConsultationForm.calendarView.calendarComponent.addEvent(event);
		editConsultationForm.scheduleEventPopup.close();

	}

	public BasicEvent getFormCalendarEvent() {
		BeanItem<ConsultationEvent> item = (BeanItem<ConsultationEvent>) fieldGroup
			.getItemDataSource();
		CalendarEvent event = item.getBean();
		return (BasicEvent) event;
	}

	public void discardConsultationBasicEvent() {
		fieldGroup.discard();
		editConsultationForm.scheduleEventPopup.close();
	}

	public void bindField(ConsultationEvent basicEvent) {
		BeanItem<ConsultationEvent> item = new BeanItem<>(basicEvent);
		fieldGroup.setItemDataSource(item);
		fieldGroup.bind(editConsultationForm.nameField,"name");
		fieldGroup.bind(editConsultationForm.surnameField,"surname");
		fieldGroup.bind(editConsultationForm.patronymicField,"patronymic");
		fieldGroup.bind(editConsultationForm.birthdayField,"birthday");
		fieldGroup.bind(editConsultationForm.casHisField,"case_history_num");
	}
}

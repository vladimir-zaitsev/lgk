package lgk.nsbc.ru.view;

import lgk.nsbc.ru.backend.basicevent.ConsultationBasicEvent;
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
	public ComboboxView(EditConsultationForm editConsultationForm)
	{

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
		if (!editConsultationForm.calendarView.dataSource.containsEvent(event)) {
			editConsultationForm.calendarView.dataSource.addEvent(event);
		}

		editConsultationForm.calendarView.getUI().removeWindow(editConsultationForm.scheduleEventPopup);

	}

	public BasicEvent getFormCalendarEvent() {
		BeanItem<ConsultationBasicEvent> item = (BeanItem<ConsultationBasicEvent>) fieldGroup
			.getItemDataSource();
		CalendarEvent event = item.getBean();
		return (BasicEvent) event;
	}


	public void discardConsultationBasicEvent()
	{
		fieldGroup.discard();
    //    editConsultationForm.patientCombobox.clear();
		editConsultationForm.calendarView.getUI().removeWindow(editConsultationForm.scheduleEventPopup);
	}

	public void bindField(ConsultationBasicEvent basicEvent)
	{
		BeanItem<ConsultationBasicEvent> item = new BeanItem<ConsultationBasicEvent>(basicEvent);
		fieldGroup.setItemDataSource(item);
		fieldGroup.bind(editConsultationForm.nameField,"name");
		fieldGroup.bind(editConsultationForm.surnameField,"surname");
		fieldGroup.bind(editConsultationForm.patronymicField,"patronymic");
		fieldGroup.bind(editConsultationForm.birthdayField,"birthday");
		fieldGroup.bind(editConsultationForm.casHisField,"case_history_num");

	}

}

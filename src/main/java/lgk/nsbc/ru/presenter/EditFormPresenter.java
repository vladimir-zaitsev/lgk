package lgk.nsbc.ru.presenter;

import lgk.nsbc.ru.backend.ConsultationManager;
import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.Patient;
import lgk.nsbc.ru.model.ConsultationModel;
import lgk.nsbc.ru.view.EditFormView;
import lgk.nsbc.ru.view.EditFormViewImpl;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

import java.util.Date;

/**
 * Created by user on 16.03.2016.
 */
public class EditFormPresenter implements  Presenter {
    EditFormView view;
    private	final ConsultationModel model;
    private final ConsultationManager consultationManager;
    private boolean newEvent;

    // Presenter получает необходимые для работы метода данные о состоянии
    // пользовательского интерфейса через интерфейс Представления
    // и через него же передаёт в Представление данные из Модели и другие результаты своей работы.

    public EditFormPresenter(ConsultationModel model, ConsultationManager consultationManager) {
        //	view = new EditFormViewImpl(this);
        this.model = model;
        this.consultationManager = consultationManager;
    }

    /*
    * создается событие
    * @param дата начала и дата конца
    * return новое событие
     */
    @Override
    public void handleNewEvent(Date start, Date end, boolean newEvent)
    {
        view = new EditFormViewImpl(this);
        Consultation consultation = new Consultation(new Date(), 0, "", "", "", start, end, "");
        ConsultationEvent event = new ConsultationEvent("Новая консультаций", "Здесь что-то будет", consultation);
        event.setStyleName("color2");
        view.createEventPopup(event,newEvent);
    }

    /*
    * редактирование события
    *@param
    */
    @Override
    public void handleEventClick(CalendarEvent calendarEvent, boolean newEvent) {
        view = new EditFormViewImpl(this);
        view.createEventPopup((ConsultationEvent) calendarEvent,newEvent);
    }

    /*
    * в item cохраняем данные
    * если в модели нет его сохраняем в beanItemContainer
    */
    @Override
    public void commitEvent()
    {
        view.commitEvent();
        ConsultationEvent event = view.getFormEvent();
        if ( ! model.beanItemContainer.containsId(event)) {
            model.beanItemContainer.addBean(event);
        }
    }
    @Override
    public void discardEvent()
    {
        view.discardEvent();
    }
    /*
    * удаляем item, если он есть в beanItemContainer
     */
    @Override
    public void deleteEvent() {
        ConsultationEvent event = view.getFormEvent();
        if (model.beanItemContainer.containsId(event))
            model.beanItemContainer.removeItem(event);
    }
    @Override
    public ConsultationEvent selectedItem() {

        Patient patient = view.getSelectItem();
        ConsultationEvent consultationEvent= view.getConsultationEvent();
        Patient patientBean =
                consultationManager.selectPatient(patient.getName(),patient.getSurname(),patient.getPatronymic(),
                        patient.getBirthday());
        Consultation consultation = new Consultation(patientBean,consultationEvent.getStart(),consultationEvent.getEnd());
        ConsultationEvent basicEvent = new ConsultationEvent
                (consultationEvent.getCaption(),consultationEvent.getDescription(),consultation,"");
        return basicEvent;
    }

}

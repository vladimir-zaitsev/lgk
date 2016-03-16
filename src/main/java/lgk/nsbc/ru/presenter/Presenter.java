package lgk.nsbc.ru.presenter;

import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

import java.util.Date;

/**
 * Created by user on 16.03.2016.
 */
public interface Presenter {
    void handleNewEvent(Date start, Date end, boolean newEvent); // Это будет вызываться в CalendarView
    void commitEvent();
    void discardEvent();
    void deleteEvent();
    ConsultationEvent selectedItem();
    void handleEventClick(CalendarEvent calendarEvent, boolean newEvent);
}

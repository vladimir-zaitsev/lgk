package lgk.nsbc.ru.presenter;

import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

import java.util.Date;
import java.util.List;

/**
 * Created by Роман on 13.03.2016.
 */
public interface CalendarPresenter {
	// К слову, зачем нам необходимо вводить режим? Для того, чтобы можно было работать с кнопками,
	// прокрутывающими день/неделю/месяц
	enum Mode {
		DAY, WEEK, MONTH
	}
	Date getTime();
	int getWeek();
	int getYear();

	void handleEventClick(CalendarEvent calendarEvent);
	void handleNewEvent(Date start, Date end);
	void handleDeleteEvent(CalendarEvent calendarEvent);
	void handleRangeSelectEvent(Date start, Date end,boolean isMonthlyMode);
	void setCurrentViewMode(Mode currentViewMode);
	void resetTime(boolean resetEndTime);

	void handleNextButtonClick();
	void handlePreviousButtonClick();
	void handleAddNewEventButtonClick();
	void handleMonthButtonClick();
	void handleHideWeekendsButton();
	void handleFirstHourOfDayChange();
	void handleLastHourOfDayChange();
	List getComboBoxValues();
}

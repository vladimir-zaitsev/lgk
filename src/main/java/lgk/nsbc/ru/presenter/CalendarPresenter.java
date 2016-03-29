package lgk.nsbc.ru.presenter;

import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

import java.util.Date;
import java.util.List;

/**
 * Created by Роман on 13.03.2016.
 */
public interface CalendarPresenter {

	enum Mode {
		DAY, WEEK, MONTH
	}
	Date getTime();

	// Create/Delete/Edit event
	void handleDeleteEvent(CalendarEvent calendarEvent);
	void handleRangeSelectEvent(Date start, Date end,boolean isMonthlyMode);
	void handleAddNewEventButtonClick();

	// Navigation
	void handleNextButtonClick();
	void handlePreviousButtonClick();

	void handleMonthButtonClick();
	void handleWeekButtonClick();
	void handleDayButtonClick();

	void handleCalendarWeekClick(int week,int year);
	void handleCalendarDateClick(Date date);

	// Calendar settings tuning
	void handleHideWeekendsButton();
	void handleFirstHourOfDayChange();
	void handleLastHourOfDayChange();
	List getComboBoxValues();
}

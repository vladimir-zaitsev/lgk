package lgk.nsbc.ru.presenter;

import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;

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
	void handleDeleteEvent(ConsultationEvent consultationEvent);
	void handleRangeSelectEvent(Date start, Date end,boolean isMonthlyMode);
	void handleAddNewEventButtonClick();
	void handleEventClick(ConsultationEvent consultationEvent);

	// Navigation
	void handleNavigationButtonClick(boolean isForward);
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

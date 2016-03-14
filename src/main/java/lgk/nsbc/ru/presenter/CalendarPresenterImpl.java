package lgk.nsbc.ru.presenter;

import com.vaadin.ui.components.calendar.event.CalendarEvent;
import lgk.nsbc.ru.backend.ConsultationManager;
import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.model.ConsultationModel;
import lgk.nsbc.ru.view.CalendarView;
import lgk.nsbc.ru.view.CalendarViewImpl;

import java.util.*;

/**
 * Created by Роман on 14.03.2016.
 */
public class CalendarPresenterImpl implements CalendarPresenter {

	private final ConsultationModel consultationModel;
	private final ConsultationManager consultationManager;
	private final CalendarView calendarView;

	private static final ArrayList<String> PROCEDURES = new ArrayList<>(4);
	private static final List<String> executor = new ArrayList<>(5);
	private static ArrayList<String> hourOfDay = new ArrayList<>(24);

	static {
		for (int i=0;i<24;i++) {
			hourOfDay.add(String.format("%02d:00",i));
		}
		Collections.addAll(PROCEDURES,"Радиохирургия","Заочная консультация","Очная консультация","Оннкология");
		Collections.addAll(executor,"физик", "онколог", "планировщик", "врач", "лечащий врач");
	}

	private GregorianCalendar gregorianCalendar;
	private Date currentMonthsFirstDate;
	private Mode currentViewMode = Mode.WEEK;

	public CalendarPresenterImpl(ConsultationModel consultationModel, ConsultationManager consultationManager) {
		this.consultationModel = consultationModel;
		this.consultationManager = consultationManager;
		Date today = new Date();
		gregorianCalendar = new GregorianCalendar(Locale.getDefault());
		gregorianCalendar.setTime(today);
		int rollAmount = gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
		gregorianCalendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
		currentMonthsFirstDate = gregorianCalendar.getTime();
		start();
		calendarView = new CalendarViewImpl(consultationModel,this);
	}

	public void start() {

		GregorianCalendar calendar = new GregorianCalendar(2016, 1, 1);
		Date startDay = calendar.getTime();
		calendar.add(calendar.MONTH, 1);
		Date endDay = calendar.getTime();


		List<Consultation> consultations = new ArrayList<>(consultationManager.listConsultation(startDay, endDay));
		System.out.println(consultations.size());
		for (int i = 0; i < consultations.size(); i++) {
			Random random = new Random();
			int value = random.nextInt(executor.size());
			ConsultationEvent event = new ConsultationEvent("Радиохирургия", "Some description.", consultations.get(i),
				executor.get(value));
			event.setStyleName("color3");
			event.getStart().setHours(9);
			event.getEnd().setHours(18);
			consultationModel.beanItemContainer.addBean(event);
		}
	}

	@Override
	public void handleDeleteEvent(CalendarEvent calendarEvent) {
		consultationModel.beanItemContainer.removeItem(calendarEvent);
	}

	@Override
	public void handleEventClick(CalendarEvent calendarEvent) {

	}

	@Override
	public void handleNewEvent(Date start, Date end) {
		Consultation consultation = new Consultation(new Date(), 0, "", "", "", start, end, "");
		ConsultationEvent event = new ConsultationEvent("Новая консультаций", "Здесь что-то будет", consultation);
		event.setStyleName("color2");
		consultationModel.beanItemContainer.addBean(event);
	}

	@Override
	public void handleRangeSelectEvent(Date start, Date end,boolean isMonthlyMode) {
		// В режиме месяца работаем с целыми сутками
		if (isMonthlyMode) {
			start = getStartOfDay(gregorianCalendar,start);
			end = getEndOfDay(gregorianCalendar, end);
		}
		handleNewEvent(start,end);
	}

	@Override
	public void setCurrentViewMode(Mode currentViewMode) {
		this.currentViewMode = currentViewMode;
	}

	/*
	 * Resets the calendar time (hour, minute second and millisecond) either to
	 * zero or maximum value.
	 */
	@Override
	public void resetTime(boolean max) {
		if (max) {
			gregorianCalendar.set(GregorianCalendar.HOUR_OF_DAY,
				gregorianCalendar.getMaximum(GregorianCalendar.HOUR_OF_DAY));
			gregorianCalendar.set(GregorianCalendar.MINUTE,
				gregorianCalendar.getMaximum(GregorianCalendar.MINUTE));
			gregorianCalendar.set(GregorianCalendar.SECOND,
				gregorianCalendar.getMaximum(GregorianCalendar.SECOND));
			gregorianCalendar.set(GregorianCalendar.MILLISECOND,
				gregorianCalendar.getMaximum(GregorianCalendar.MILLISECOND));
		} else {
			gregorianCalendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
			gregorianCalendar.set(GregorianCalendar.MINUTE, 0);
			gregorianCalendar.set(GregorianCalendar.SECOND, 0);
			gregorianCalendar.set(GregorianCalendar.MILLISECOND, 0);
		}
	}

	@Override
	public void handleNextButtonClick() {
		switch (currentViewMode) {
			case MONTH:
				rollMonth(1);
				break;
			case WEEK:
				rollWeek(1);
				break;
			case DAY:
				rollDate(1);
				break;
		}
	}

	@Override
	public void handlePreviousButtonClick() {
		switch (currentViewMode) {
			case MONTH:
				rollMonth(-1);
				break;
			case WEEK:
				rollWeek(-1);
				break;
			case DAY:
				rollDate(-1);
				break;
		}
	}

	@Override
	public void handleAddNewEventButtonClick() {
		handleNewEvent(getStartOfDay(gregorianCalendar,new Date()),
			getEndOfDay(gregorianCalendar,new Date()));
	}

	@Override
	public void handleMonthButtonClick() {
		currentViewMode = Mode.MONTH;
		int rollAmount = gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
		gregorianCalendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
		calendarView.setStartDate(gregorianCalendar.getTime());

		gregorianCalendar.add(GregorianCalendar.MONTH, 1);
		gregorianCalendar.add(GregorianCalendar.DATE, -1);
		calendarView.setEndDate(gregorianCalendar.getTime());

		gregorianCalendar.setTime(new Date());
	}

	@Override
	public Date getTime() {
		return gregorianCalendar.getTime();
	}

	@Override
	public int getWeek() {
		return gregorianCalendar.get(GregorianCalendar.WEEK_OF_YEAR);
	}

	@Override
	public int getYear() {
		return gregorianCalendar.get(GregorianCalendar.YEAR);
	}

	@Override
	public void handleHideWeekendsButton() {
		if (calendarView.isHideWeekends()) {
			int firstToShow = (GregorianCalendar.MONDAY - gregorianCalendar
				.getFirstDayOfWeek()) % 7;
			calendarView.setFirstVisibleDayOfWeek(firstToShow + 1);
			calendarView.setLastVisibleDayOfWeek(firstToShow + 5);
		} else {
			calendarView.setFirstVisibleDayOfWeek(1);
			calendarView.setLastVisibleDayOfWeek(7);
		}
	}

	@Override
	public void handleFirstHourOfDayChange() {
		int firstHour = Integer.parseUnsignedInt(calendarView.getFirstHourOfDay().split(":")[0]);
		calendarView.setFirstVisibleHourOfDay(firstHour);
	}

	@Override
	public void handleLastHourOfDayChange() {
		int lastHour = Integer.parseUnsignedInt(calendarView.getLastHourOfDay().split(":")[0]);
		calendarView.setLastVisibleHourOfDay(lastHour);
	}

	@Override
	public List getComboBoxValues() {
		return hourOfDay;
	}

	public CalendarView getCalendarView() {
		return calendarView;
	}

	private Date getEndOfDay(java.util.Calendar calendar, Date date) {
		java.util.Calendar calendarClone = (java.util.Calendar) calendar
			.clone();

		calendarClone.setTime(date);
		calendarClone.set(java.util.Calendar.MILLISECOND,
			calendarClone.getActualMaximum(java.util.Calendar.MILLISECOND));
		calendarClone.set(java.util.Calendar.SECOND,
			calendarClone.getActualMaximum(java.util.Calendar.SECOND));
		calendarClone.set(java.util.Calendar.MINUTE,
			calendarClone.getActualMaximum(java.util.Calendar.MINUTE));
		calendarClone.set(java.util.Calendar.HOUR,
			calendarClone.getActualMaximum(java.util.Calendar.HOUR));
		calendarClone.set(java.util.Calendar.HOUR_OF_DAY,
			calendarClone.getActualMaximum(java.util.Calendar.HOUR_OF_DAY));

		return calendarClone.getTime();
	}
	private Date getStartOfDay(java.util.Calendar calendar, Date date) {
		java.util.Calendar calendarClone = (java.util.Calendar) calendar
			.clone();

		calendarClone.setTime(date);
		calendarClone.set(java.util.Calendar.MILLISECOND, 0);
		calendarClone.set(java.util.Calendar.SECOND, 0);
		calendarClone.set(java.util.Calendar.MINUTE, 0);
		calendarClone.set(java.util.Calendar.HOUR, 0);
		calendarClone.set(java.util.Calendar.HOUR_OF_DAY, 0);

		return calendarClone.getTime();
	}

	private void resetCalendarTime(boolean resetEndTime) {
		resetTime(resetEndTime);
		if (resetEndTime) {
			calendarView.setEndDate(gregorianCalendar.getTime());
		} else {
			calendarView.setStartDate(gregorianCalendar.getTime());
		}
	}

	private void rollMonth(int direction) {
		gregorianCalendar.setTime(currentMonthsFirstDate);
		gregorianCalendar.add(GregorianCalendar.MONTH, direction);
		resetTime(false);
		currentMonthsFirstDate = gregorianCalendar.getTime();
		calendarView.setStartDate(currentMonthsFirstDate);
		gregorianCalendar.add(GregorianCalendar.MONTH, 1);
		gregorianCalendar.add(GregorianCalendar.DATE, -1);
		resetCalendarTime(true);
	}

	private void rollWeek(int direction) {
		gregorianCalendar.add(GregorianCalendar.WEEK_OF_YEAR, direction);
		gregorianCalendar.set(GregorianCalendar.DAY_OF_WEEK,
			gregorianCalendar.getFirstDayOfWeek());
		resetCalendarTime(false);
		resetTime(true);
		gregorianCalendar.add(GregorianCalendar.DATE, 6);
		calendarView.setEndDate(gregorianCalendar.getTime());
	}

	private void rollDate(int direction) {
		gregorianCalendar.add(GregorianCalendar.DATE, direction);
		resetCalendarTime(false);
		resetCalendarTime(true);
	}
}

package lgk.nsbc.ru.presenter;

import lgk.nsbc.ru.backend.ConsultationManager;
import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.model.ConsultationModel;
import lgk.nsbc.ru.view.CalendarView;
import com.vaadin.ui.components.calendar.event.CalendarEvent;

import java.text.DateFormatSymbols;
import java.time.*;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * Created by user on 16.03.2016.
 */
public class CalendarPresenterImpl implements CalendarPresenter {


	private final CalendarView calendarView;
	private  final ConsultationModel consultationModel;
	private  final ConsultationManager consultationManager;
	public static final ArrayList<String> PROCEDURES = new ArrayList<>(4);
	private static final List<String> executor = new ArrayList<>(5);
	private static ArrayList<String> hourOfDay = new ArrayList<>(24);

	static {
		for (int i=0;i<24;i++) {
			hourOfDay.add(String.format("%02d:00",i));
		}
		Collections.addAll(PROCEDURES,"Радиохирургия","Заочная консультация","Очная консультация","Оннкология");
		Collections.addAll(executor,"физик", "онколог", "планировщик", "врач", "лечащий врач");
	}

	private LocalDateTime time = LocalDateTime.of(LocalDate.now(),LocalTime.MIDNIGHT);
	//private GregorianCalendar gregorianCalendar;
	//private Date currentMonthsFirstDate;
	private Mode currentViewMode = Mode.WEEK;

	public CalendarPresenterImpl(CalendarView calendarView,ConsultationModel consultationModel,
								 ConsultationManager consultationManager) {
		this.consultationManager = consultationManager;
		this.consultationModel = consultationModel;
		this.calendarView = calendarView;

		// Starting from monday
		time = time.minusDays(time.getDayOfWeek().getValue()-1);
		calendarView.setStartDate(getTime());
		calendarView.setEndDate(localDateTimeToDate(time.plusWeeks(1).minusSeconds(1)));
		start();
		updateCaptionLabel();
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
			Consultation consultation = consultations.get(i);
			String descr = consultation.getSurname();
			ConsultationEvent event = new ConsultationEvent(descr, "Some description.", consultations.get(i),
				executor.get(value));
			event.setStyleName("color3");
			event.getStart().setHours(0);
			event.getEnd().setHours(0);
			event.setAllDay(true);
			consultationModel.beanItemContainer.addBean(event);
		}
	}

	@Override
	public void handleDeleteEvent(CalendarEvent calendarEvent) {
		consultationModel.beanItemContainer.removeItem(calendarEvent);
	}


	@Override
	public void handleRangeSelectEvent(Date start, Date end,boolean isMonthlyMode) {
		// В режиме месяца работаем с целыми сутками
		if (isMonthlyMode) {
			start = getStartOfDay(start);
			end = getEndOfDay(end);
		}
		calendarView.setDateNewEvent(start,end);
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
		updateCaptionLabel();
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
		updateCaptionLabel();
	}

	@Override
	public void handleAddNewEventButtonClick() {
		calendarView.setDateNewEvent(getStartOfDay(new Date()),
			getEndOfDay(new Date()));
	}

	@Override
	public void handleMonthButtonClick() {
		currentViewMode = Mode.MONTH;
		time = time.minusDays(time.getDayOfMonth()-1);
		calendarView.setStartDate(localDateTimeToDate(time));
		calendarView.setEndDate(localDateTimeToDate(time.plusMonths(1).minusSeconds(1)));
		updateCaptionLabel();
	}

	@Override
	public void handleDayButtonClick() {
		currentViewMode = Mode.DAY;
		time = time.minusDays(time.getDayOfWeek().getValue()-1);
		calendarView.setStartDate(localDateTimeToDate(time));
		calendarView.setEndDate(localDateTimeToDate(time.plusDays(1).minusSeconds(1)));
		updateCaptionLabel();
	}

	@Override
	public void handleWeekButtonClick() {
		currentViewMode = Mode.WEEK;
		time = time.minusDays(time.getDayOfWeek().getValue()-1);
		calendarView.setStartDate(localDateTimeToDate(time));
		calendarView.setEndDate(localDateTimeToDate(time.plusWeeks(1).minusSeconds(1)));
		updateCaptionLabel();
	}

	@Override
	public void handleCalendarDateClick(Date date) {
		currentViewMode = Mode.DAY;
		time = dateToLocalDateTime(date);
		calendarView.setStartDate(localDateTimeToDate(time));
		calendarView.setEndDate(localDateTimeToDate(time.plusDays(1).minusSeconds(1)));
		updateCaptionLabel();
	}

	@Override
	public void handleCalendarWeekClick(int week, int year) {
		currentViewMode = Mode.WEEK;
		WeekFields weekFields = WeekFields.of(Locale.getDefault());
		time = time.withYear(year)
			.with(weekFields.weekOfYear(), week)
			.with(weekFields.dayOfWeek(), 1);
		calendarView.setStartDate(localDateTimeToDate(time));
		calendarView.setEndDate(localDateTimeToDate(time.plusWeeks(1).minusSeconds(1)));
		updateCaptionLabel();
	}

	@Override
	public Date getTime() {
		return localDateTimeToDate(time);
	}

	private static Date localDateTimeToDate(LocalDateTime ldt) {
		return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
	}


	private static Date getEndOfDay(Date date) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(java.util.Calendar.YEAR);
		int month = calendar.get(java.util.Calendar.MONTH);
		int day = calendar.get(java.util.Calendar.DATE);
		calendar.set(year, month, day, 23, 59, 59);
		return calendar.getTime();
	}

	private static Date getStartOfDay(Date date) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(java.util.Calendar.YEAR);
		int month = calendar.get(java.util.Calendar.MONTH);
		int day = calendar.get(java.util.Calendar.DATE);
		calendar.set(year, month, day, 0, 0, 0);
		return calendar.getTime();
	}

	private static LocalDateTime dateToLocalDateTime(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}


	@Override
	public void handleHideWeekendsButton() {
		if (calendarView.isHideWeekends()) {
			calendarView.setFirstVisibleDayOfWeek(1);
			calendarView.setLastVisibleDayOfWeek(5);
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

	private void rollMonth(int direction) {
		if (direction<0) {
			time = time.minusMonths(-direction);
		} else {
			time = time.plusMonths(direction);
		}
		calendarView.setStartDate(localDateTimeToDate(time));
		calendarView.setEndDate(localDateTimeToDate(time.plusMonths(1).minusSeconds(1)));
	}

	private void rollWeek(int direction) {
		if (direction<0) {
			time = time.minusWeeks(-direction);
		} else {
			time = time.plusWeeks(direction);
		}
		calendarView.setStartDate(localDateTimeToDate(time));
		calendarView.setEndDate(localDateTimeToDate(time.plusWeeks(1).minusSeconds(1)));
	}

	private void rollDate(int direction) {
		if (direction<0) {
			time = time.minusDays(-direction);
		} else {
			time = time.plusDays(direction);
		}
		time.plusDays(direction);
		if (time.getDayOfWeek()==DayOfWeek.SATURDAY) {
			time = time.plusDays(2);
		}
		if (time.getDayOfWeek()==DayOfWeek.SUNDAY) {
			time = time.minusDays(2);
		}
		calendarView.setStartDate(localDateTimeToDate(time));
		calendarView.setEndDate(localDateTimeToDate(time.plusDays(1).minusSeconds(1)));
	}

	private void updateCaptionLabel() {
		DateFormatSymbols s = new DateFormatSymbols(Locale.getDefault());
		String month = s.getShortMonths()[time.getMonth().getValue()-1];
		calendarView.setCurrentDateLabel(month + " "
			+ time.getYear());
	}
}
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
	private final EditFormPresenter editFormPresenter;
	static {
		for (int i=0;i<24;i++) {
			hourOfDay.add(String.format("%02d:00",i));
		}
		Collections.addAll(PROCEDURES,"Радиохирургия","Заочная консультация","Очная консультация","Оннкология");
		Collections.addAll(executor,"физик", "онколог", "планировщик", "врач", "лечащий врач");
	}

	private LocalDateTime time = LocalDateTime.of(LocalDate.now(),LocalTime.MIDNIGHT);
	private Mode currentViewMode = Mode.WEEK;

	public CalendarPresenterImpl(CalendarView calendarView,ConsultationModel consultationModel,
								 ConsultationManager consultationManager) {
		this.consultationManager = consultationManager;
		this.consultationModel = consultationModel;
		this.calendarView = calendarView;
		editFormPresenter = new EditFormPresenterImpl(consultationModel,consultationManager);
		// Starting from monday
		time = time.minusDays(time.getDayOfWeek().getValue()-1);
		calendarView.setStartDate(getTime());
		calendarView.setEndDate(localDateTimeToDate(time.plusWeeks(1).minusSeconds(1)));
		start();
		updateCaptionLabel();
	}

	public void start() {
		LocalDateTime consultationTimeRange = LocalDateTime.of(2016,2,1,0,0);
		List<Consultation> consultations = new ArrayList<>(consultationManager.listConsultation(
			localDateTimeToDate(consultationTimeRange), localDateTimeToDate(consultationTimeRange.plusMonths(2))));
		System.out.println(consultations.size());
		for (int i = 0; i < consultations.size(); i++) {
			Random random = new Random();
			int value = random.nextInt(executor.size());
			Consultation consultation = consultations.get(i);
			String descr = consultation.getSurname();

			ConsultationEvent event = new ConsultationEvent(descr, "Some description.", consultations.get(i),
				executor.get(value));
			event.setStart(getStartOfDay(event.getStart()));
			event.setEnd(getEndOfDay(event.getEnd()));
			event.setAllDay(true);
			consultationModel.getBeanItemContainer().addBean(event);
		}
		// Сортируем по дате и фамилии (в будущем можно будет менять в интерфейсе)
		consultationModel.sortContainer();
	}

	@Override
	public void handleDeleteEvent(ConsultationEvent consultationEvent) {
		consultationModel.getBeanItemContainer().removeItem(consultationEvent);
	}


	@Override
	public void handleRangeSelectEvent(Date start, Date end,boolean isMonthlyMode) {
		// В режиме месяца работаем с целыми сутками
		if (isMonthlyMode) {
			start = getStartOfDay(start);
			end = getEndOfDay(end);
		}
		Consultation consultation = new Consultation(null, null, "", "", "", start, end, "");
		ConsultationEvent event = new ConsultationEvent("", "", consultation);
		event.setStyleName("color1");
		consultationModel.getBeanItemContainer().addBean(event);
		editFormPresenter.handleNewEvent(event);
		consultationModel.sortContainer();
	}

	@Override
	public void handleAddNewEventButtonClick() {
		Consultation consultation = new Consultation(null, null, "", "", "", localDateTimeToDate(time),
			localDateTimeToDate(time), "");
		ConsultationEvent event = new ConsultationEvent("", "", consultation);
		event.setStyleName("color1");
		consultationModel.sortContainer();
		consultationModel.getBeanItemContainer().addBean(event);
		editFormPresenter.handleNewEvent(event);
	}

	@Override
	public void handleEventClick(ConsultationEvent consultationEvent) {
		editFormPresenter.handleEventClick(consultationEvent);
		consultationModel.sortContainer();
	}

	@Override
	public void handleNavigationButtonClick(boolean isForward) {
		int direction = isForward ? 1 : -1;
		switch (currentViewMode) {
			case MONTH:
				rollMonth(direction);
				break;
			case WEEK:
				rollWeek(direction);
				break;
			case DAY:
				rollDate(direction);
				break;
		}
		updateCaptionLabel();
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

	private void rollMonth(int direction) {
		time = time.plusMonths(direction);
		calendarView.setStartDate(localDateTimeToDate(time));
		calendarView.setEndDate(localDateTimeToDate(time.plusMonths(1).minusSeconds(1)));
	}

	private void rollWeek(int direction) {
		time = time.plusWeeks(direction);
		calendarView.setStartDate(localDateTimeToDate(time));
		calendarView.setEndDate(localDateTimeToDate(time.plusWeeks(1).minusSeconds(1)));
	}

	private void rollDate(int direction) {
		time = time.plusDays(direction);
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
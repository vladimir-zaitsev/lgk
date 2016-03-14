package lgk.nsbc.ru.view;

import com.vaadin.event.Action;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.ui.components.calendar.CalendarDateRange;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.handler.BasicDateClickHandler;
import com.vaadin.ui.components.calendar.handler.BasicWeekClickHandler;
import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.model.ConsultationModel;
import lgk.nsbc.ru.presenter.ConsultationPresenter;

import java.util.*;

public class CalendarViewOld extends GridLayout implements View {
	// TODO разобраться с сортировкой событий
	// 3 режима, - день, неделя, месяц.
	private enum Mode {
		DAY, WEEK, MONTH
	}
	// Контролируем время
	private GregorianCalendar gregorianCalendar;
	private Date currentMonthsFirstDate;
	public final ConsultationModel consultationModel;
	// Визуальные компоненты:
	// Сам календарь
	public Calendar calendarComponent;
	// Отображение всего месяца
	private Button monthButton;
	// Отображение недели
	private Button weekButton;
	// Отображение одного дня
	private Button dayButton;
	// Прокрутка назад/вперёд
	private Button prevButton;
	private Button nextButton;
	// Добавить новую консультацию
	private Button addNewEvent;
	private CheckBox hideWeekendsButton;
	private ComboBox firstHourOfDay;
	private ComboBox lastHourOfDay;
	// Текущй режим отображения
	private Mode viewMode = Mode.WEEK;
	//public BasicEventProvider dataSource;

	EditConsultationForm EditConsultationForm;
	ConsultationPresenter presenter;

	public ConsultationPresenter getPresenter() {
		return presenter;
	}

	public void setPresenter(ConsultationPresenter presenter) {
		this.presenter = presenter;
	}

	public CalendarViewOld(ConsultationModel consultationModel) {
		this.consultationModel = consultationModel;
		setLocale(Locale.getDefault());
		initCalendar();
		initLayoutContent();
	}

	private CalendarEvent createNewEvent(Date start, Date end) {
		Consultation consultation = new Consultation(new Date(), 0, "", "", "", start, end, "");
		ConsultationEvent event = new ConsultationEvent("Новая консультаций", "Здесь что-то будет", consultation);
		event.setStyleName("color2");
		// Создание нового события ТОЛЬКО в форме! ТОЛЬКО ПОСЛЕ нажатия кнопки принять.
		//consultationModel.beanItemContainer.addBean(event);
		return event;
	}

	private ConsultationEvent getNewEvent(String caption, Date start, Date end) {
		Consultation consultation = new Consultation(new Date(), 0,  "", "", "", start, end, "");
		ConsultationEvent event = new ConsultationEvent(caption, "new event", consultation);
		event.setStyleName("color2");
		// Создание нового события ТОЛЬКО в форме! ТОЛЬКО ПОСЛЕ нажатия кнопки принять.
		//consultationModel.beanItemContainer.addBean(event);
		return event;
	}

	//region INITIALIZATION AND LAYOUT

	// Инициализируем компонент calendarComponent
	private void initCalendar() {
		//dataSource = new BasicEventProvider();
		calendarComponent = new Calendar();
		calendarComponent.setContainerDataSource(consultationModel.beanItemContainer);
		EditConsultationForm = new EditConsultationForm(this);
		calendarComponent.setLocale(getLocale());
		calendarComponent.setFirstVisibleHourOfDay(9);
		calendarComponent.setLastVisibleHourOfDay(18);
		calendarComponent.setTimeFormat(Calendar.TimeFormat.Format24H);

		Date today = getToday();
		gregorianCalendar = new GregorianCalendar(getLocale());
		gregorianCalendar.setTime(today);
		calendarComponent.getInternalCalendar().setTime(today);

		// Calendar getStartDate (and getEndDate) has some strange logic which
		// returns Monday of the current internal time if no start date has been
		// set
		calendarComponent.setStartDate(calendarComponent.getStartDate());
		calendarComponent.setEndDate(calendarComponent.getEndDate());
		int rollAmount = gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
		gregorianCalendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);
		currentMonthsFirstDate = gregorianCalendar.getTime();
		addCalendarEventListeners();
	}

	private void initLayoutContent() {
		setSizeFull();
		setSpacing(true);
		//calendarComponent.setHeight(600,Unit.PIXELS);
		calendarComponent.setSizeFull();
		calendarComponent.setHeight(650,Unit.PIXELS);
		initButtons();
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setHeightUndefined();
		hl.addComponents(prevButton);

		CssLayout group = new CssLayout();
		group.addComponents(dayButton, weekButton, monthButton);
		hl.addComponent(group);

		hl.addComponent(nextButton);
		hl.setComponentAlignment(prevButton, Alignment.TOP_LEFT);
		hl.setComponentAlignment(group, Alignment.TOP_CENTER);
		hl.setComponentAlignment(nextButton, Alignment.TOP_RIGHT);

		HorizontalLayout controlPanel = new HorizontalLayout();
		controlPanel.setHeightUndefined();
		controlPanel.setSpacing(true);
		controlPanel.addComponents(hideWeekendsButton,firstHourOfDay,lastHourOfDay,addNewEvent);
		controlPanel.setComponentAlignment(hideWeekendsButton,Alignment.MIDDLE_LEFT);
		controlPanel.setComponentAlignment(addNewEvent,Alignment.MIDDLE_LEFT);
		addComponent(controlPanel);
		addComponent(hl);
		addComponent(calendarComponent);
		setSpacing(false);
	}

	private void initButtons() {
		monthButton = new Button("Месяц", clickEvent -> switchToMonthView());

		weekButton = new Button("Неделя", clickEvent -> {
			CalendarComponentEvents.WeekClickHandler handler = (CalendarComponentEvents.WeekClickHandler) calendarComponent
				.getHandler(CalendarComponentEvents.WeekClick.EVENT_ID);
			handler.weekClick(new CalendarComponentEvents.WeekClick(calendarComponent, gregorianCalendar
				.get(GregorianCalendar.WEEK_OF_YEAR), gregorianCalendar
				.get(GregorianCalendar.YEAR)));
		});

		dayButton = new Button("День", clickEvent -> {
			BasicDateClickHandler handler = (BasicDateClickHandler) calendarComponent
				.getHandler(CalendarComponentEvents.DateClickEvent.EVENT_ID);
			handler.dateClick(new CalendarComponentEvents.DateClickEvent(calendarComponent,
				gregorianCalendar.getTime()));
		});

		nextButton = new Button("Вперед", clickEvent -> handleNextButtonClick());
		prevButton = new Button("Назад", clickEvent -> handlePreviousButtonClick());
		addNewEvent = new Button("Новая консультация", (clickEvent -> {
			Date start = getToday();
			start.setHours(0);
			start.setMinutes(0);
			start.setSeconds(0);
			Date end = getEndOfDay(gregorianCalendar, start);
			EditConsultationForm.showEventPopup(createNewEvent(start, end), true);
		}));

		hideWeekendsButton = new CheckBox("Выходные");
		//hideWeekendsButton.setImmediate(true);
		hideWeekendsButton
			.addValueChangeListener(valueChangeEvent -> setWeekendsHidden(hideWeekendsButton.getValue()));
		hideWeekendsButton.setValue(true);

		ArrayList<String> arrayList = new ArrayList(24);
		for (int i=0;i<24;i++) {
			arrayList.add(String.format("%02d:00",i));
		}
		firstHourOfDay = new ComboBox("Начало дня",arrayList);
		firstHourOfDay.setInputPrompt("Начало дня");
		firstHourOfDay.setWidth(150,Unit.PIXELS);
		firstHourOfDay.addValueChangeListener(valueChangeEvent -> {
			System.out.println(firstHourOfDay.getValue());
			String value = (String)valueChangeEvent.getProperty().getValue();
			calendarComponent.setFirstVisibleHourOfDay(Integer.parseUnsignedInt(value.split(":")[0]));
		});

		lastHourOfDay = new ComboBox("Конец дня",arrayList);
		lastHourOfDay.setInputPrompt("Конец дня");
		lastHourOfDay.setWidth(150,Unit.PIXELS);
		lastHourOfDay.addValueChangeListener(valueChangeEvent -> {
			System.out.println(lastHourOfDay.getValue());
			String value = (String)valueChangeEvent.getProperty().getValue();
			calendarComponent.setLastVisibleHourOfDay(Integer.parseUnsignedInt(value.split(":")[0]));
		});
	}

	private void addCalendarEventListeners() {
		// Запретить изменение размерова событий мышкой
		calendarComponent.setHandler((CalendarComponentEvents.EventResizeHandler)null);
		// Register week clicks by changing the schedules start and end dates.
		calendarComponent.setHandler(new BasicWeekClickHandler() {

			@Override
			public void weekClick(CalendarComponentEvents.WeekClick event) {
				// let BasicWeekClickHandler handle calendar dates, and update
				// only the other parts of UI here
				super.weekClick(event);
				switchToWeekView();
			}
		});

		calendarComponent.setHandler((EventClick eventClick) ->
			EditConsultationForm.showEventPopup(eventClick.getCalendarEvent(), false));

		calendarComponent.setHandler(new BasicDateClickHandler() {

			@Override
			public void dateClick(CalendarComponentEvents.DateClickEvent event) {
				// let BasicDateClickHandler handle calendar dates, and update
				// only the other parts of UI here
				super.dateClick(event);
				switchToDayView();
			}
		});
		calendarComponent.setHandler(this::handleRangeSelect);

		calendarComponent.addActionHandler(new Action.Handler() {
			Action addEventAction    = new Action("Новая консультация");
			Action deleteEventAction = new Action("Удалить консультацию");
			/* Стоит обратить внимание на механику actions. Доступные действия определяются не на
			* клиенте, а на сервере, заранее. Листаем недели, - возвращают филды с заданными возможными
			* действиями.*/
			@Override
			public Action[] getActions(Object target, Object sender) {
				// Цель, - CalendarDateRange,Источник, - CalendarDateRange и ничего другое
				if (!(target instanceof CalendarDateRange&&sender instanceof Calendar))
					return null;
				CalendarDateRange dateRange = (CalendarDateRange) target;
				Calendar calendar = (Calendar) sender;

				// Все события за 30 минут (мы работаем с одним полем в 30 минут?)
				List<CalendarEvent> events =
					calendar.getEvents(dateRange.getStart(),
						dateRange.getEnd());
				// Можно помозговать и придумать более умную логику
				if (events.size() == 0)
					return new Action[] {addEventAction};
				else
					return new Action[] {addEventAction, deleteEventAction};
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				// The sender is the Calendar object
				Calendar calendar = (Calendar) sender;

				if (action == addEventAction) {
					// Check that the click was not done on an event
					if (target instanceof Date) {
						Date date = (Date) target;
						// Add an event from now to plus one hour
						GregorianCalendar start = new GregorianCalendar();
						start.setTime(date);
						GregorianCalendar end   = new GregorianCalendar();
						end.setTime(date);
						end.add(java.util.Calendar.MINUTE, 30);
						EditConsultationForm.showEventPopup(createNewEvent(start.getTime(), end.getTime()), true);
					} else
					new Notification("Невозможно добавить событий",
						"Возможно, вы не туда указали?").show(Page.getCurrent());
				} else if (action == deleteEventAction) {
					// Check if the action was clicked on top of an event
					if (target instanceof CalendarEvent) {
						CalendarEvent event = (CalendarEvent) target;
						calendar.removeEvent(event);
					} else
						new Notification("Невозможно удалить событие",
							"Возможно, вы указали на несуществующее событие?").show(Page.getCurrent());
				}
			}
		});
	}
	//endregion INITIALIZATION AND LAYOUT

	//region HANDLERS

	private void handleNextButtonClick() {
		switch (viewMode) {
			case MONTH:
				nextMonth();
				break;
			case WEEK:
				nextWeek();
				break;
			case DAY:
				nextDay();
				break;
		}
	}

	private void handlePreviousButtonClick() {
		switch (viewMode) {
			case MONTH:
				previousMonth();
				break;
			case WEEK:
				previousWeek();
				break;
			case DAY:
				previousDay();
				break;
		}
	}

	private void handleRangeSelect(RangeSelectEvent event) {
		Date start = event.getStart();
		Date end = event.getEnd();

        /*
         * If a range of dates is selected in monthly mode, we want it to end at
         * the end of the last day.
         */
		if (event.isMonthlyMode()) {
			end = getEndOfDay(gregorianCalendar, end);
		}
		EditConsultationForm.showEventPopup(createNewEvent(start, end), true);
	}

	private void setWeekendsHidden(boolean weekendsHidden) {
		if (weekendsHidden) {
			int firstToShow = (GregorianCalendar.MONDAY - gregorianCalendar
				.getFirstDayOfWeek()) % 7;
			calendarComponent.setFirstVisibleDayOfWeek(firstToShow + 1);
			calendarComponent.setLastVisibleDayOfWeek(firstToShow + 5);
		} else {
			calendarComponent.setFirstVisibleDayOfWeek(1);
			calendarComponent.setLastVisibleDayOfWeek(7);
		}

	}

	//endregion HANDLERS <<

	//region NAVIGATION BETWEEN DAYS <<

	private void nextMonth() {
		rollMonth(1);
	}

	private void previousMonth() {
		rollMonth(-1);
	}

	private void nextWeek() {
		rollWeek(1);
	}

	private void previousWeek() {
		rollWeek(-1);
	}

	private void nextDay() {
		rollDate(1);
	}

	private void previousDay() {
		rollDate(-1);
	}

	private void rollMonth(int direction) {
		gregorianCalendar.setTime(currentMonthsFirstDate);
		gregorianCalendar.add(GregorianCalendar.MONTH, direction);
		resetTime(false);
		currentMonthsFirstDate = gregorianCalendar.getTime();
		calendarComponent.setStartDate(currentMonthsFirstDate);
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
		calendarComponent.setEndDate(gregorianCalendar.getTime());
	}

	private void rollDate(int direction) {
		gregorianCalendar.add(GregorianCalendar.DATE, direction);
		resetCalendarTime(false);
		resetCalendarTime(true);
	}

	//endregion NAVIGATION BETWEEN DAYS <<

	//region SWITCHES TO OTHER WIEW

	public void switchToDayView() {
		viewMode = Mode.DAY;
	}

	public void switchToWeekView() {
		viewMode = Mode.WEEK;
	}

	public void switchToMonthView() {
		viewMode = Mode.MONTH;

		int rollAmount = gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH) - 1;
		gregorianCalendar.add(GregorianCalendar.DAY_OF_MONTH, -rollAmount);

		calendarComponent.setStartDate(gregorianCalendar.getTime());

		gregorianCalendar.add(GregorianCalendar.MONTH, 1);
		gregorianCalendar.add(GregorianCalendar.DATE, -1);

		calendarComponent.setEndDate(gregorianCalendar.getTime());

		gregorianCalendar.setTime(getToday());
	}

	//endregion SWITCHES TO OTHER WIEW <<

	//region WORKING WITH DATES

	private Date getToday() {
		return new Date();
	}

	private void resetCalendarTime(boolean resetEndTime) {
		resetTime(resetEndTime);
		if (resetEndTime) {
			calendarComponent.setEndDate(gregorianCalendar.getTime());
		} else {
			calendarComponent.setStartDate(gregorianCalendar.getTime());
		}
	}

	/*
	 * Resets the calendar time (hour, minute second and millisecond) either to
	 * zero or maximum value.
	 */
	private void resetTime(boolean max) {
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

	private static Date getEndOfDay(java.util.Calendar calendar, Date date) {
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

	private static Date getStartOfDay(java.util.Calendar calendar, Date date) {
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

	private Date resolveFirstDateOfWeek(Date today,
										java.util.Calendar currentCalendar) {
		int firstDayOfWeek = currentCalendar.getFirstDayOfWeek();
		currentCalendar.setTime(today);
		while (firstDayOfWeek != currentCalendar
			.get(java.util.Calendar.DAY_OF_WEEK)) {
			currentCalendar.add(java.util.Calendar.DATE, -1);
		}
		return currentCalendar.getTime();
	}

	private Date resolveLastDateOfWeek(Date today,
									   java.util.Calendar currentCalendar) {
		currentCalendar.setTime(today);
		currentCalendar.add(java.util.Calendar.DATE, 1);
		int firstDayOfWeek = currentCalendar.getFirstDayOfWeek();
		// Roll to weeks last day using firstdayofweek. Roll until FDofW is
		// found and then roll back one day.
		while (firstDayOfWeek != currentCalendar
			.get(java.util.Calendar.DAY_OF_WEEK)) {
			currentCalendar.add(java.util.Calendar.DATE, 1);
		}
		currentCalendar.add(java.util.Calendar.DATE, -1);
		return currentCalendar.getTime();
	}

	//endregion WORKING WITH DATES

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent event) {
	}
}
package lgk.nsbc.ru.view;

import com.vaadin.event.Action;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.*;
import com.vaadin.ui.components.calendar.CalendarDateRange;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.handler.BasicDateClickHandler;
import com.vaadin.ui.components.calendar.handler.BasicWeekClickHandler;
import lgk.nsbc.ru.model.ConsultationModel;
import lgk.nsbc.ru.presenter.CalendarPresenter;

import java.util.*;

/**
 * Created by Роман on 13.03.2016.
 */
public class CalendarViewImpl extends AbstractView<ConsultationModel> implements View,CalendarView {
	// UI components
	private GridLayout gridLayout = new GridLayout();
	public Calendar calendarComponent = new Calendar();
	private Button dayButton = new Button("День");
	private Button weekButton = new Button("Неделя");
	private Button monthButton = new Button("Месяц");
	private Button prevButton = new Button("Назад");
	private Button nextButton = new Button("Вперед");
	private Button addNewEventButton = new Button("Новая консультация");
	private CheckBox hideWeekendsButton = new CheckBox("Выходные");
	private ComboBox firstHourOfDay = new ComboBox("Начало дня");
	private ComboBox lastHourOfDay = new ComboBox("Конец дня");
	private CalendarPresenter calendarPresenter;

	public CalendarViewImpl(ConsultationModel consultationModel, CalendarPresenter calendarPresenter) {
		super(consultationModel);
		this.calendarPresenter = calendarPresenter;
		calendarComponent.setContainerDataSource(consultationModel.beanItemContainer);
		calendarComponent.setLocale(Locale.getDefault());
		calendarComponent.setFirstVisibleHourOfDay(9);
		calendarComponent.setLastVisibleHourOfDay(18);
		calendarComponent.setTimeFormat(Calendar.TimeFormat.Format24H);
		calendarComponent.getInternalCalendar().setTime(calendarPresenter.getTime());
		calendarComponent.setStartDate(calendarComponent.getStartDate());
		calendarComponent.setEndDate(calendarComponent.getEndDate());

		// Запретить изменение размеров событий мышкой
		calendarComponent.setHandler((EventResizeHandler)null);
		// Назначить действие при нажатии на событие
		calendarComponent.setHandler((EventClick eventClick) ->
			calendarPresenter.handleEventClick(eventClick.getCalendarEvent()));
		// Назначить действие при создании событий внутри календаря
		calendarComponent.setHandler((RangeSelectEvent event) -> {
			calendarPresenter.handleRangeSelectEvent(event.getStart(),event.getEnd(),event.isMonthlyMode());
		});
		// Переопределяем нижеидущие handlars пока только для изменения currentViewMode
		// TODO проблема в логике super.weekClick() (Отображается текущая неделя)
		calendarComponent.setHandler(new BasicWeekClickHandler() {
			@Override
			public void weekClick(CalendarComponentEvents.WeekClick event) {
				super.weekClick(event);
				calendarPresenter.setCurrentViewMode(CalendarPresenter.Mode.WEEK);
			}
		});
		calendarComponent.setHandler(new BasicDateClickHandler() {
			@Override
			public void dateClick(CalendarComponentEvents.DateClickEvent event) {
				super.dateClick(event);
				calendarPresenter.setCurrentViewMode(CalendarPresenter.Mode.DAY);
			}
		});
		calendarComponent.addActionHandler(new Action.Handler() {
			Action addEventAction    = new Action("Новая консультация");
			Action deleteEventAction = new Action("Удалить консультацию");
			/* Стоит обратить внимание на механику actions. Доступные действия определяются не на
			* клиенте, а на сервере, заранее. Листаем недели, - возвращают филды с заданными возможными
			* действиями.*/
			@Override
			public Action[] getActions(Object target, Object sender) {
				// Цель, - CalendarDateRange,Источник, - CalendarDateRange и ничего другое
				if (!(target instanceof CalendarDateRange &&sender instanceof Calendar))
					return null;
				CalendarDateRange dateRange = (CalendarDateRange) target;
				Calendar calendar = (Calendar) sender;
				// Все события за 30 минут (мы работаем с одним полем в 30 минут?)
				List<CalendarEvent> events = calendar.getEvents(dateRange.getStart(), dateRange.getEnd());
				// Можно помозговать и придумать более умную логику
				if (events.size() == 0)
					return new Action[] {addEventAction};
				else
					return new Action[] {deleteEventAction};
			}

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				// Сверху уже проверили, что sender - Calendar
				Calendar calendar = (Calendar) sender;

				if (action == addEventAction) {
					// Необходимо проверить, что мы не пытаемся добавить на событие.
					if (target instanceof Date) {
						Date date = (Date) target;
						// Создаем событие +30 минут
						GregorianCalendar start = new GregorianCalendar();
						start.setTime(date);
						GregorianCalendar end   = new GregorianCalendar();
						end.setTime(date);
						end.add(java.util.Calendar.MINUTE, 30);
						calendarPresenter.handleNewEvent(start.getTime(), end.getTime());
					} else
						new Notification("Невозможно добавить событий",
							"Возможно, вы не туда указали?").show(Page.getCurrent());
				} else if (action == deleteEventAction) {
					// Проверить, что выбрал именно удалить событие
					if (target instanceof CalendarEvent) {
						CalendarEvent event = (CalendarEvent) target;
						calendarPresenter.handleDeleteEvent(event);
					} else
						new Notification("Невозможно удалить событие",
							"Возможно, вы указали на несуществующее событие?").show(Page.getCurrent());
				}
			}
		});

		initButtons();
		initLayoutContent();
	}

	/**
	 * <p>Инициализирует кнопки, добавляя им действия и задавая начальные состояния</p>
	 * <p>Ссылается на логику от presenter. За исключением...
	 * </p>
	 */
	private void initButtons() {
		// TODO логику нажатия дня и недели придется менять
		dayButton.addClickListener(clickEvent -> {
			BasicDateClickHandler handler = (BasicDateClickHandler) calendarComponent
				.getHandler(CalendarComponentEvents.DateClickEvent.EVENT_ID);
			handler.dateClick(new CalendarComponentEvents.DateClickEvent(calendarComponent,
				calendarPresenter.getTime()));
		});

		weekButton.addClickListener(clickEvent -> {
			CalendarComponentEvents.WeekClickHandler handler = (CalendarComponentEvents.WeekClickHandler) calendarComponent
				.getHandler(CalendarComponentEvents.WeekClick.EVENT_ID);
			handler.weekClick(new CalendarComponentEvents.WeekClick(calendarComponent,
				calendarPresenter.getWeek(), calendarPresenter.getYear()));
		});

		monthButton.addClickListener( clickEvent -> calendarPresenter.handleMonthButtonClick());

		prevButton.addClickListener(clickEvent -> calendarPresenter.handleNextButtonClick());

		nextButton.addClickListener(clickEvent -> calendarPresenter.handlePreviousButtonClick());

		addNewEventButton.addClickListener(clickEvent -> calendarPresenter.handleAddNewEventButtonClick());

		hideWeekendsButton.addValueChangeListener(valueChangeEvent -> calendarPresenter.handleHideWeekendsButton());

		firstHourOfDay.setInputPrompt("Начало дня");
		firstHourOfDay.setWidth(150,Unit.PIXELS);
		firstHourOfDay.addValueChangeListener(valueChangeEvent -> calendarPresenter.handleFirstHourOfDayChange());
		firstHourOfDay.addItems(calendarPresenter.getComboBoxValues());

		lastHourOfDay.setInputPrompt("Конец дня");
		lastHourOfDay.setWidth(150,Unit.PIXELS);
		lastHourOfDay.addValueChangeListener(valueChangeEvent -> calendarPresenter.handleLastHourOfDayChange());
		lastHourOfDay.addItems(calendarPresenter.getComboBoxValues());
	}

	private void initLayoutContent() {
		gridLayout.setSizeFull();
		calendarComponent.setSizeFull();
		calendarComponent.setHeight(650,Unit.PIXELS);
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
		controlPanel.addComponents(hideWeekendsButton,firstHourOfDay,lastHourOfDay,addNewEventButton);
		controlPanel.setComponentAlignment(hideWeekendsButton,Alignment.MIDDLE_LEFT);
		controlPanel.setComponentAlignment(addNewEventButton,Alignment.MIDDLE_LEFT);
		gridLayout.addComponent(controlPanel);
		gridLayout.addComponent(hl);
		gridLayout.addComponent(calendarComponent);
		setCompositionRoot(gridLayout);
	}

	@Override
	public void setEndDate(Date end) {
		calendarComponent.setEndDate(end);
	}

	@Override
	public void setStartDate(Date start) {
		calendarComponent.setStartDate(start);
	}

	@Override
	public boolean isHideWeekends() {
		return hideWeekendsButton.getValue();
	}

	@Override
	public void setFirstVisibleDayOfWeek(int firstDay) {
		calendarComponent.setFirstVisibleDayOfWeek(firstDay);
	}

	@Override
	public void setLastVisibleDayOfWeek(int lastDay) {
		calendarComponent.setLastVisibleDayOfWeek(lastDay);
	}

	@Override
	public String getFirstHourOfDay() {
		return (String)firstHourOfDay.getValue();
	}

	@Override
	public void setFirstVisibleHourOfDay(int firstHour) {
		calendarComponent.setFirstVisibleHourOfDay(firstHour);
	}

	@Override
	public void setLastVisibleHourOfDay(int lastHour) {
		calendarComponent.setLastVisibleHourOfDay(lastHour);
	}

	@Override
	public String getLastHourOfDay() {
		return (String)lastHourOfDay.getValue();
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
	}
}

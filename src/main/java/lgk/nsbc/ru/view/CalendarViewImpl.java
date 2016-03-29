package lgk.nsbc.ru.view;

import lgk.nsbc.ru.backend.ConsultationManager;
import lgk.nsbc.ru.model.ConsultationModel;
import lgk.nsbc.ru.presenter.CalendarPresenter;
import lgk.nsbc.ru.presenter.CalendarPresenterImpl;
import lgk.nsbc.ru.presenter.EditFormPresenter;
import lgk.nsbc.ru.presenter.Presenter;
import com.vaadin.event.Action;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.ui.components.calendar.CalendarDateRange;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.handler.BasicDateClickHandler;
import com.vaadin.ui.components.calendar.handler.BasicWeekClickHandler;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by user on 16.03.2016.
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
	private Label currentDateLabel = new Label("");
	private Button addNewEventButton = new Button("Новая консультация");
	private CheckBox hideWeekendsButton = new CheckBox("Выходные");
	private ComboBox firstHourOfDay = new ComboBox("Начало дня");
	private ComboBox lastHourOfDay = new ComboBox("Конец дня");

	CalendarPresenter calendarPresenter;
	EditFormPresenter presenter;
	final ConsultationManager consultationManager;

	public CalendarViewImpl(ConsultationModel consultationModel, ConsultationManager consultationManager) {
		super(consultationModel);
		this.consultationManager = consultationManager;
		this.calendarPresenter = new CalendarPresenterImpl(this,consultationModel,consultationManager);
		calendarComponent.setContainerDataSource(consultationModel.beanItemContainer);
		calendarComponent.setLocale(Locale.getDefault());
		calendarComponent.setFirstVisibleHourOfDay(9);
		calendarComponent.setLastVisibleHourOfDay(18);
		calendarComponent.setTimeFormat(Calendar.TimeFormat.Format24H);
		calendarComponent.getInternalCalendar().setTime(calendarPresenter.getTime());
		calendarComponent.setStartDate(calendarComponent.getStartDate());
		calendarComponent.setEndDate(calendarComponent.getEndDate());

		// Запретить изменение размеров событий мышкой
		calendarComponent.setHandler((CalendarComponentEvents.EventResizeHandler)null);
		// Во первых, уже есть кнопки, меняющие вид. Во вторых, они работают правильнее.
		calendarComponent.setHandler((CalendarComponentEvents.ForwardHandler)null);
		calendarComponent.setHandler((CalendarComponentEvents.BackwardHandler)null);

		// Назначить действие при нажатии на событие
		calendarComponent.setHandler((CalendarComponentEvents.EventClick eventClick) ->
			presenter.handleEventClick(eventClick.getCalendarEvent(),false));
		// Назначить действие при создании событий внутри календаря
		calendarComponent.setHandler((CalendarComponentEvents.RangeSelectEvent event) ->
			calendarPresenter.handleRangeSelectEvent(event.getStart(),event.getEnd(),event.isMonthlyMode()));

		calendarComponent.setHandler((CalendarComponentEvents.WeekClick eventClick) ->
			calendarPresenter.handleCalendarWeekClick(eventClick.getWeek(),eventClick.getYear()));

		calendarComponent.setHandler((CalendarComponentEvents.DateClickEvent eventClick) ->
			calendarPresenter.handleCalendarDateClick(eventClick.getDate()));

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
				Date start = dateRange.getStart();
				start.setHours(0);
				Date end = dateRange.getEnd();
				end.setHours(23);
				List<CalendarEvent> events = calendar.getEvents(start,end);
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
						GregorianCalendar end = new GregorianCalendar();
						end.setTime(date);
						end.add(java.util.Calendar.MINUTE, 30);
						presenter.handleNewEvent(start.getTime(),end.getTime(),true);
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

	public void setEditFormPresenter(Presenter presenter)
	{
		this.presenter = (EditFormPresenter) presenter;
	}

	/**
	 * <p>Инициализирует кнопки, добавляя им действия и задавая начальные состояния</p>
	 * <p>Ссылается на логику от presenter. За исключением...
	 * </p>
	 */
	private void initButtons() {

		dayButton.addClickListener( clickEvent -> calendarPresenter.handleDayButtonClick());

		weekButton.addClickListener( clickEvent -> calendarPresenter.handleWeekButtonClick());

		monthButton.addClickListener( clickEvent -> calendarPresenter.handleMonthButtonClick());

		prevButton.addClickListener(clickEvent -> calendarPresenter.handlePreviousButtonClick());

		nextButton.addClickListener(clickEvent -> calendarPresenter.handleNextButtonClick());

		addNewEventButton.addClickListener(clickEvent -> calendarPresenter.handleAddNewEventButtonClick());

		hideWeekendsButton.addValueChangeListener(valueChangeEvent -> calendarPresenter.handleHideWeekendsButton());
		hideWeekendsButton.setValue(true);

		firstHourOfDay.setNullSelectionAllowed(false);
		firstHourOfDay.setInputPrompt("Начало дня");
		firstHourOfDay.setWidth(150,Unit.PIXELS);
		firstHourOfDay.addValueChangeListener(valueChangeEvent -> calendarPresenter.handleFirstHourOfDayChange());
		firstHourOfDay.addItems(calendarPresenter.getComboBoxValues());

		lastHourOfDay.setNullSelectionAllowed(false);
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

		CssLayout group = new CssLayout();
		group.addComponents(dayButton, weekButton, monthButton);

		hl.addComponents(prevButton,currentDateLabel,group,nextButton);
		hl.setComponentAlignment(prevButton, Alignment.TOP_LEFT);
		hl.setComponentAlignment(currentDateLabel,Alignment.TOP_CENTER);
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
	public void setDateNewEvent(Date start, Date end) {
		presenter.handleNewEvent(start,end,true);
	}

	@Override
	public void setCurrentDateLabel(String caption) {
		currentDateLabel.setValue(caption);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
	}
}

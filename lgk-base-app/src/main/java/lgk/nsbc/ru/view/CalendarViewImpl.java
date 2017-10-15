package lgk.nsbc.ru.view;

import com.vaadin.data.Property;
import com.vaadin.event.Action;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.ui.*;
import com.vaadin.ui.components.calendar.CalendarComponentEvents;
import com.vaadin.ui.components.calendar.CalendarDateRange;
import com.vaadin.ui.components.calendar.ContainerEventProvider;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import lgk.nsbc.ru.backend.ConsultationManager;
import lgk.nsbc.ru.backend.PatientsManager;
import lgk.nsbc.ru.backend.PeopleManager;
import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Patient;
import lgk.nsbc.ru.model.ConsultationModel;
import lgk.nsbc.ru.presenter.CalendarPresenter;
import lgk.nsbc.ru.presenter.CalendarPresenterImpl;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by user on 16.03.2016.
 */
public class CalendarViewImpl extends AbstractView<ConsultationModel> implements CalendarView {
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
	private PatientCombobox patientSearch = new PatientCombobox("Поиск консультации по пациенту:");

	CalendarPresenter calendarPresenter;

	public CalendarViewImpl(ConsultationModel consultationModel
		,PeopleManager peopleManager
		,PatientsManager patientsManager
		,ConsultationManager consultationManager
	) {
		super(consultationModel);
		// Создадим в начале
		this.calendarPresenter = new CalendarPresenterImpl(this,consultationModel
			,peopleManager
			,patientsManager
		    ,consultationManager);
		ContainerEventProvider eventProvider = new ContainerEventProvider(consultationModel.getBeanItemContainer());
		calendarComponent.setEventProvider(eventProvider);
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
			calendarPresenter.handleEventClick((ConsultationEvent)eventClick.getCalendarEvent()));
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
				end.setHours(24);
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
						calendarPresenter.handleRangeSelectEvent(start.getTime(),end.getTime(),false);
					} else
						new Notification("Невозможно добавить событий",
							"Возможно, вы не туда указали?").show(Page.getCurrent());
				} else if (action == deleteEventAction) {
					// Проверить, что выбрал именно удалить событие
					if (target instanceof ConsultationEvent) {
						ConsultationEvent event = (ConsultationEvent) target;
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

		dayButton.addClickListener( clickEvent -> calendarPresenter.handleDayButtonClick());

		weekButton.addClickListener( clickEvent -> calendarPresenter.handleWeekButtonClick());

		monthButton.addClickListener( clickEvent -> calendarPresenter.handleMonthButtonClick());

		prevButton.addClickListener(clickEvent -> calendarPresenter.handleNavigationButtonClick(false));

		nextButton.addClickListener(clickEvent -> calendarPresenter.handleNavigationButtonClick(true));

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

		patientSearch.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
		patientSearch.setItemCaptionPropertyId("surname,name,patronymic,birthday");
		patientSearch.setImmediate(true);
		patientSearch.setContainerDataSource(calendarPresenter.getPatientSearchContainer());
		patientSearch.addValueChangeListener((Property.ValueChangeListener) event -> calendarPresenter.handlePatientSearch((Patient) event.getProperty().getValue()));
		// Не пашет?
		patientSearch.addItemSetChangeListener(event -> patientSearch.clear());
		patientSearch.addContextClickListener(event -> patientSearch.clear());
	}

	private void initLayoutContent() {
		gridLayout.setSizeFull();
		gridLayout.setSpacing(true);
		calendarComponent.setSizeFull();
		calendarComponent.setHeight(650,Unit.PIXELS);
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.setHeightUndefined();
		hl.setSpacing(true);

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
		patientSearch.setWidth("400px");
		controlPanel.addComponents(hideWeekendsButton,firstHourOfDay,lastHourOfDay,addNewEventButton,patientSearch);
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
	public void setCurrentDateLabel(String caption) {
		currentDateLabel.setValue(caption);
	}

	@Override
	public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
	}
}

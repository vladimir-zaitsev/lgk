package lgk.nsbc.ru.view;

import lgk.nsbc.ru.backend.PatientContainer;
import lgk.nsbc.ru.backend.basicevent.ConsultationBasicEvent;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import lgk.nsbc.ru.backend.entity.Patient;
import lgk.nsbc.ru.presenter.ConsultationPresenter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 23.02.2016.
 */
public class EditConsultationForm
{
	private TextField captionField;
	public Window scheduleEventPopup;
	private final FormLayout scheduleEventFieldLayout = new FormLayout();
	private FieldGroup scheduleEventFieldGroup = new FieldGroup();
	private Button deleteEventButton;
	private Button applyEventButton;
	private DateField startDateField;
	private DateField endDateField;
	private Calendar calendarComponent;
	//private BasicEventProvider dataSource;
	private boolean useSecondResolution;
	CalendarView calendarView;

	//NEW
	public final PatientCombobox patientCombobox;
	private final PatientContainer patientContainer ;
	private final ComboboxView comboboxView;
	public  final TextField nameField;
	public final TextField surnameField;
	public final TextField patronymicField;
	public final DateField  birthdayField;
	public final TextField casHisField;
	public ConsultationBasicEvent basicEvent;
	private CalendarEvent calendarEvent;
	private NativeSelect selectProcedure;

	public static final List<String> diagnosis = new ArrayList<>(Arrays.asList("Сосудистые заболевания",
		"АВМ-Артерио-венозная мальформация","Другое сосудистое заболевание","Доброкачественные опухоли",
		"Менингиома","Множественные метастазы"));
	public EditConsultationForm(CalendarView calendarView)
	{
		this.calendarView = calendarView;
		this.calendarComponent = calendarView.calendarComponent;
		//this.dataSource = calendarView.dataSource;
		patientCombobox = new PatientCombobox("Быстрый ввод");
		patientContainer = new PatientContainer();
		comboboxView = new ComboboxView(this);
		nameField = createTextField("Имя");
		surnameField = createTextField("Фамилия");
		patronymicField = createTextField("Отчество");
		birthdayField = createDateField("Дата рождения");
		casHisField = createTextField("Номер истории");
	}

	public void onItemSelected() {
		patientCombobox.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				Notification.show("Selected item: " + event.getProperty().getValue(), Notification.Type.HUMANIZED_MESSAGE);
				Patient patient = (Patient) event.getProperty().getValue();
				if ( patient == null)
				{
					patientCombobox.setValue(null);
				}
				else {
					patientContainer.setSelectedPatientBean(patient);
					if (calendarView.getPresenter() != null) {
						basicEvent = calendarView.getPresenter().onItemSelected(calendarEvent, patient);
						System.out.println(basicEvent);
						comboboxView.bindField(basicEvent);
					}
				}
			}
		});
		patientCombobox.setContainerDataSource(patientContainer);
	}

	// Показывание всплывающего окна
	public void showEventPopup(CalendarEvent event, boolean newEvent) {
		calendarEvent = event;
		if (event == null) {
			return;
		}
		updateCalendarEventPopup(newEvent); // Создали форму в которой есть formlayout и кнопки удалить принять отмена
		updateCalendarEventForm(event); // связываение с beanItemcontainer

		if (!calendarView.getUI().getWindows().contains(scheduleEventPopup)) {
			calendarView.getUI().addWindow(scheduleEventPopup);
		}
	}

	private void updateCalendarEventPopup(boolean newEvent) {
		if (scheduleEventPopup == null) {
			createCalendarEventPopup(); // cоздали окно
		}
		if (newEvent) { // в случае если newEvent = true добавляем новое событие
			scheduleEventPopup.setCaption("Новая консультация");
		} else {
			scheduleEventPopup.setCaption("Редактирование консультаций"); // редактируем событие
		}
		//Очищаем комбобокс в любом случае
		patientCombobox.removeAllItems();
		deleteEventButton.setVisible(!newEvent);
	}


	/* Initializes a modal window to edit schedule event. */
	private void createCalendarEventPopup() {
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);

		scheduleEventPopup = new Window(null, layout);
		scheduleEventPopup.setSizeFull();
		scheduleEventPopup.setModal(true);
		scheduleEventPopup.center();
		scheduleEventFieldLayout.addStyleName("light");
		scheduleEventFieldLayout.setMargin(false);

		layout.addComponent(scheduleEventFieldLayout);  // добавили formlayout в котором будут нужные поля

		applyEventButton = new Button("Применить", clickEvent1 -> {
			try {
				// TODO СОМНИТЕЛЬНОЕ МЕСТО
				if (basicEvent != null) {
					comboboxView.commitConsultationEvent();
				}
				else {
					commitCalendarEvent();
				}
			} catch (FieldGroup.CommitException e) {
				e.printStackTrace();
			}
		});

		applyEventButton.addStyleName("primary");
		Button cancel = new Button("Отмена", clickEvent -> discardCalendarEvent());
		deleteEventButton = new Button("Удалить", clickEvent -> deleteCalendarEvent());
		deleteEventButton.addStyleName("borderless");
		scheduleEventPopup.addCloseListener(closeEvent -> discardCalendarEvent());

		patientCombobox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
		patientCombobox.setItemCaptionPropertyId("name,surname,patronymic,birthday");
		patientCombobox.setImmediate(true);
		onItemSelected();

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addStyleName("v-window-bottom-toolbar");
		buttons.setWidth("100%");
		buttons.setSpacing(true);
		buttons.addComponent(deleteEventButton);
		buttons.addComponent(applyEventButton);
		buttons.setExpandRatio(applyEventButton, 1);
		buttons.setComponentAlignment(applyEventButton, Alignment.TOP_RIGHT);
		buttons.addComponent(cancel);
		layout.addComponent(buttons);
	}

	private void updateCalendarEventForm(CalendarEvent event) { // Связывание data sourse c field c помощью FieldGroup
		BeanItem<CalendarEvent> item = new BeanItem<>(event);
		scheduleEventFieldLayout.removeAllComponents(); // зачем это?
		scheduleEventFieldGroup = new FieldGroup();
		initFormFields(scheduleEventFieldLayout, event.getClass());
		scheduleEventFieldGroup.setBuffered(true);
		scheduleEventFieldGroup.setItemDataSource(item);
	}

	private void initFormFields(Layout formLayout, Class<? extends CalendarEvent> eventClass) // Cоздаем поля
	{
		startDateField = createDateField("Конец события");
		endDateField = createDateField("Начало события");
		final CheckBox allDayField = createCheckBox("All-day");
		allDayField.addValueChangeListener(event ->
			setFormDateResolution(allDayField.getValue()?Resolution.DAY:Resolution.MINUTE));
		selectProcedure = createNativeSelect(ConsultationPresenter.PROCEDURES, "Вид консультации");
		captionField = createTextField("Заголовок");
		captionField.setInputPrompt("Название события");
		final TextField executorField = createTextField("Исполнитель");
		executorField.setInputPrompt("человек,отвественный за процедуру");
		final TextArea descriptionField = createTextArea("Описание");
		nameField.setInputPrompt("имя пациента");
		surnameField.setInputPrompt("фамилия пациента");
		patronymicField.setInputPrompt("отчество пациента");

		HorizontalLayout hlLayout = new HorizontalLayout(startDateField,endDateField);
		formLayout.addComponents(hlLayout,captionField,patientCombobox,descriptionField,selectProcedure,executorField);
		HorizontalLayout horizontalLayout = new HorizontalLayout(nameField,surnameField,
			patronymicField,birthdayField,casHisField);
		formLayout.addComponent(horizontalLayout);

		scheduleEventFieldGroup.bind(startDateField, "start");
		scheduleEventFieldGroup.bind(endDateField, "end");
		scheduleEventFieldGroup.bind(captionField, "caption");
		scheduleEventFieldGroup.bind(descriptionField, "description");
		scheduleEventFieldGroup.bind(executorField, "executor");
		scheduleEventFieldGroup.bind(nameField,"name");
		scheduleEventFieldGroup.bind(surnameField,"surname");
		scheduleEventFieldGroup.bind(patronymicField,"patronymic");
		scheduleEventFieldGroup.bind(birthdayField,"birthday");
		scheduleEventFieldGroup.bind(casHisField,"case_history_num");
	}


	private void setFormDateResolution(Resolution resolution) {
		if (startDateField != null && endDateField != null) {
			startDateField.setResolution(resolution);
			endDateField.setResolution(resolution);
		}
	}

	@SuppressWarnings("unchecked")
	private BasicEvent getFormCalendarEvent() {
		BeanItem<CalendarEvent> item = (BeanItem<CalendarEvent>) scheduleEventFieldGroup
			.getItemDataSource();
		CalendarEvent event = item.getBean();
		return (BasicEvent) event;
	}

	private NativeSelect createNativeSelect(List<String> strings, String caption) {
		NativeSelect nativeSelect = new NativeSelect(caption, strings);
		nativeSelect.setRequired(true);
		nativeSelect.setBuffered(true);
		return nativeSelect;
	}

	private CheckBox createCheckBox(String caption) {
		CheckBox cb = new CheckBox(caption);
		cb.setImmediate(true);
		return cb;
	}

	private TextField createTextField(String caption) {
		TextField f = new TextField(caption);
		f.setNullRepresentation("");
		return f;
	}

	private TextArea createTextArea(String caption) {
		TextArea f = new TextArea(caption);
		f.setNullRepresentation("");
		return f;
	}

	private DateField createDateField(String caption) {
		DateField f = new DateField(caption);
		if (useSecondResolution) {
			f.setResolution(Resolution.SECOND);
		} else {
			f.setResolution(Resolution.MINUTE);
		}
		return f;
	}

    /* Adds/updates the event in the data source and fires change event. */
	private void commitCalendarEvent() throws FieldGroup.CommitException {
		scheduleEventFieldGroup.commit();
		BasicEvent event = getFormCalendarEvent();
		if (event.getEnd() == null) {
			event.setEnd(event.getStart());
		}
		if (!calendarView.consultationModel.beanItemContainer.containsId(event))
			calendarComponent.addEvent(event);
		scheduleEventPopup.close();
	}
	/* Removes the event from the data source and fires change event. */
	private void deleteCalendarEvent() {
		BasicEvent event = getFormCalendarEvent();
		System.out.println();
		if (calendarView.consultationModel.beanItemContainer.containsId(event))
			calendarComponent.removeEvent(event);
		scheduleEventPopup.close();
	}

	private void discardCalendarEvent() {
		scheduleEventFieldGroup.discard();
		scheduleEventPopup.close();
	}
}
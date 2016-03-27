package lgk.nsbc.ru.view;

import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.converter.StringToDateConverter;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.Sizeable;
import lgk.nsbc.ru.backend.PatientContainer;
import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.Patient;
import lgk.nsbc.ru.presenter.CalendarPresenterImpl;
import lgk.nsbc.ru.presenter.EditFormPresenter;
import lgk.nsbc.ru.presenter.Presenter;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import org.vaadin.hene.expandingtextarea.ExpandingTextArea;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class EditFormViewImpl implements EditFormView {

	EditFormPresenter presenter;

	private Window eventPopup;

	private FormLayout eventFormLayout;

	private PatientCombobox combobox;

	private PatientContainer patientContainer;

	private FieldGroup fieldGroup;

	private Button deleteEventButton;

	private Button applyEventButton;

	private Patient patient;

	@PropertyId("caption")
	private TextField captionField;

	@PropertyId("description")
	private ExpandingTextArea descriptionField;

	@PropertyId("executor")
	private TextField executorField;

	@PropertyId("name")
	private TextField nameField;

	@PropertyId("surname")
	private TextField surnameField;

	@PropertyId("patronymic")
	private TextField patronymicField;

	@PropertyId("birthday")
	private TextField birthdayField;

	@PropertyId("case_history_num")
	private TextField casHisField;

	@PropertyId("start")
	private DateField startDateField;

	@PropertyId("end")
	private DateField endDateField;

	private ConsultationEvent consulEvent;

	private CheckBox allDayField;

	private boolean useSecondResolution;

	public EditFormViewImpl(Presenter presenter) {
		this.presenter = (EditFormPresenter) presenter;
	}

	@Override
	public void createEventPopup(ConsultationEvent consultationEvent, boolean newEvent)
	{
		if (eventPopup == null) {
			initForm(consultationEvent, newEvent);
			bindConsultationEventForm(consulEvent);
		}
		if (newEvent)
		{
			eventPopup.setCaption("Новая консультация");
		}
		else
		{
			eventPopup.setCaption("Редактирование консультации");
		}
		UI.getCurrent().addWindow(eventPopup);
	}
	private void initForm(ConsultationEvent consultationEvent, boolean newEvent) {
		consulEvent = consultationEvent;
		VerticalLayout layout = new VerticalLayout();
		layout.setSpacing(true);

		eventPopup = new Window(null, layout);
		eventPopup.setSizeFull();
		eventPopup.setModal(true);
		eventPopup.center();
		eventPopup.setHeight("90%");
		eventPopup.setWidth("100%");
		eventPopup.setWidth(900.0f, Sizeable.Unit.PIXELS);
		eventPopup.addStyleName("v-window");

		eventFormLayout = new FormLayout();
		eventFormLayout.setSpacing(true);
		eventFormLayout.setSizeFull();
		eventFormLayout.setMargin(false);
		layout.addComponent(eventFormLayout);

		combobox = new PatientCombobox("Быстрый ввод");
		combobox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
		combobox.setItemCaptionPropertyId("surname,name,patronymic,birthday");
		combobox.setWidth("400px");
		combobox.addStyleName("v-filterselect");
		combobox.setImmediate(true);
		patientContainer = new PatientContainer();

		if (!newEvent) {
			combobox.setContainerDataSource(patientContainer);
			Patient patient = new Patient();
			patient.setName(consulEvent.getName());
			patient.setSurname(consulEvent.getSurname());
			patient.setPatronymic(consulEvent.getPatronymic());
			combobox.setValue(patient);
		}
		combobox.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent event) {
				Notification.show("Selected item: " + event.getProperty().getValue(), Notification.Type.HUMANIZED_MESSAGE);
				Patient patient = (Patient) event.getProperty().getValue();
				setSelectItem(patient);
				patientContainer.setSelectedPatientBean(patient);
				consulEvent = presenter.selectedItem();
				bindConsultationEventForm(consulEvent);
			}
		});
		combobox.setContainerDataSource(patientContainer);

		applyEventButton = new Button("Применить", clickEvent ->
		{
			presenter.commitEvent();
			eventPopup.close();
		});

		applyEventButton.addStyleName("primary");

		Button cancel = new Button("Отмена", clickEvent -> presenter.discardEvent());
		cancel.addStyleName("primary");
		deleteEventButton = new Button("Удалить", clickEvent -> {
			presenter.deleteEvent();
			eventPopup.close();
		});
		deleteEventButton.addStyleName("primary");

		eventPopup.addCloseListener(closeEvent -> presenter.discardEvent());

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

		startDateField = createDateField("Конец события");
		endDateField = createDateField("Начало события");
		allDayField = createCheckBox("Полный день");
		allDayField.setImmediate(true);
		allDayField.addValueChangeListener(event -> setFormDateResolution(allDayField.getValue() ? Resolution.DAY : Resolution.MINUTE));
		NativeSelect selectProcedure = createNativeSelect(CalendarPresenterImpl.PROCEDURES, "Вид консультации");

		captionField = createTextField("Заголовок");
		captionField.setHeightUndefined();
		captionField.setInputPrompt("Название события");
		executorField = createTextField("Исполнитель");
		executorField.setInputPrompt("человек,отвественный за процедуру");
		executorField.setWidth("300px");


		// Не работает addon-ExpandingTextArea!
		descriptionField = new ExpandingTextArea("Описание");
		descriptionField.setImmediate(true);
		descriptionField.setWidth("300px");
		final NativeSelect maxRows = new NativeSelect("Максимальное количество строк");
		maxRows.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
				descriptionField.setMaxRows((Integer) maxRows.getValue());
			}
		});
		maxRows.addItem(2);
		maxRows.addItem(5);
		maxRows.addItem(10);

		nameField = createTextField("Имя");
		nameField.setInputPrompt("имя пациента");
		nameField.addValidator(new BeanValidator(Consultation.class,"name"));

		surnameField = createTextField("Фамилия");
		surnameField.setInputPrompt("фамилия пациента");
		surnameField.addValidator(new BeanValidator(Consultation.class,"surname"));


		patronymicField = createTextField("Отчество");
		patronymicField.setInputPrompt("отчество пациента");
		patronymicField.addValidator(new BeanValidator(Consultation.class,"patronymic"));

		casHisField = new TextField("Номер истории");

		birthdayField = new TextField("Дата рождения");
		StringToDateConverter dateConverter = new StringToDateConverter(){
			@Override
			protected DateFormat getFormat(Locale locale) {
				return DateFormat.getDateInstance();
			}
		};
		birthdayField.setConverter(dateConverter);

		HorizontalLayout hlLayout = new HorizontalLayout(startDateField, endDateField);
		hlLayout.setSpacing(true);
		hlLayout.setHeightUndefined();
		HorizontalLayout horizontalLayout = new HorizontalLayout(nameField, surnameField,
			patronymicField, birthdayField, casHisField);
		horizontalLayout.setSpacing(true);
		horizontalLayout.setHeightUndefined();
		HorizontalLayout layout1 = new HorizontalLayout(descriptionField,maxRows);
		layout1.setSpacing(true);
		layout1.setHeightUndefined();
		VerticalLayout verticalLayout = new VerticalLayout();
		verticalLayout.addComponents(captionField,hlLayout,
			selectProcedure,combobox,horizontalLayout,executorField,layout1);
		eventFormLayout.addComponent(verticalLayout);

	}

	private void bindConsultationEventForm(ConsultationEvent event) {

		fieldGroup = new FieldGroup();
		BeanItem<ConsultationEvent> item = new BeanItem<ConsultationEvent>(consulEvent);
		fieldGroup.setBuffered(true);
		fieldGroup.setItemDataSource(item);
		fieldGroup.bindMemberFields(this);
	}

	private void setFormDateResolution(Resolution resolution) {
		if (startDateField != null && endDateField != null) {
			startDateField.setResolution(resolution);
			endDateField.setResolution(resolution);
		}
	}

	private TextField createTextField(String caption)
	{
		TextField f = new TextField(caption);
		f.setNullRepresentation("");
		return f;
	}

	private NativeSelect createNativeSelect(ArrayList<String> strings, String caption) {
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

	private DateField createDateField(String caption) {
		DateField f = new DateField(caption);
		if (useSecondResolution) {
			f.setResolution(Resolution.SECOND);
		} else {
			f.setResolution(Resolution.MINUTE);
		}
		return f;
	}
	@Override
	public void setSelectItem(Patient patient)
	{
		this.patient = patient;
	}

	@Override
	public Patient getSelectItem() {
		return patient;
	}

	@Override
	public ConsultationEvent getConsultationEvent() {
		return consulEvent;
	}


	@Override
	public void commitEvent()
	{
		try {
			fieldGroup.commit();


		} catch (FieldGroup.CommitException e)
		{
			// Какое исключение кидать
		}
	}

	@Override
	public void discardEvent()
	{
		fieldGroup.discard();
		eventPopup.close();
	}

	/*
    * находит item, в котором хранятся данные, введенные пользователем в field
    *  return событие
	 */
	@Override
	public ConsultationEvent getFormEvent() {
		BeanItem<ConsultationEvent> item = (BeanItem<ConsultationEvent>) fieldGroup
			.getItemDataSource();
		ConsultationEvent event = item.getBean();
		return  event;
	}
}

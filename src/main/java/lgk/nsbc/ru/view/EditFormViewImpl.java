package lgk.nsbc.ru.view;

import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.converter.StringToDateConverter;
import lgk.nsbc.ru.backend.PatientContainer;
import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Patient;
import lgk.nsbc.ru.presenter.CalendarPresenterImpl;
import lgk.nsbc.ru.presenter.EditFormPresenterImpl;
import lgk.nsbc.ru.presenter.EditFormPresenter;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;
import org.vaadin.hene.expandingtextarea.ExpandingTextArea;

import java.text.DateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;


public class EditFormViewImpl implements EditFormView {

	private Window eventPopup = new Window();
	private FormLayout eventFormLayout = new FormLayout();
	private PatientCombobox combobox = new PatientCombobox("Быстрый ввод");
	private PatientContainer patientContainer = new PatientContainer();
	private FieldGroup fieldGroup = new FieldGroup();
	private Button deleteEventButton = new Button("Удалить");
	private Button applyEventButton = new Button("Применить");
	private Button cancelButton = new Button("Отмена");
	private NativeSelect selectProcedure = new NativeSelect("Вид консультации",CalendarPresenterImpl.PROCEDURES);
	private CheckBox allDayField = new CheckBox("Полный день");

	@PropertyId("description")
	private ExpandingTextArea descriptionField = new ExpandingTextArea("Описание");

	@PropertyId("executor")
	private TextField executorField = new TextField("Исполнитель");

	@PropertyId("name")
	private TextField nameField = new TextField("Имя");

	@PropertyId("surname")
	private TextField surnameField = new TextField("Фамилия");

	@PropertyId("patronymic")
	private TextField patronymicField = new TextField("Отчество");

	@PropertyId("birthday")
	private TextField birthdayField = new TextField("Дата рождения");

	@PropertyId("case_history_num")
	private TextField caseHistoryNumTextField = new TextField("Номер истории");

	@PropertyId("start")
	private DateField startDateField = new DateField("Начало события");

	@PropertyId("end")
	private DateField endDateField = new DateField("Конец события");

	EditFormPresenterImpl presenter;
	private ConsultationEvent consultationEvent;
	private Patient patient;

	public EditFormViewImpl(EditFormPresenter editFormPresenter,ConsultationEvent consultationEvent, boolean newEvent) {
		this.presenter = (EditFormPresenterImpl) editFormPresenter;
		this.consultationEvent = consultationEvent;
		initForm(newEvent);
		bindConsultationEventForm();
		UI.getCurrent().addWindow(eventPopup);
	}

	private void initForm(boolean newEvent) {
		if (newEvent) {
			eventPopup.setCaption("Новая консультация");
		}
		else {
			eventPopup.setCaption("Редактирование консультации");
		}
		eventPopup.addCloseListener(closeEvent -> presenter.discardEvent());
		eventPopup.setModal(true);

		combobox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
		combobox.setItemCaptionPropertyId("surname,name,patronymic,birthday");
		combobox.setImmediate(true);
		if (!newEvent) {
			combobox.setContainerDataSource(patientContainer);
			Patient patient = new Patient();
			patient.setName(consultationEvent.getName());
			patient.setSurname(consultationEvent.getSurname());
			patient.setPatronymic(consultationEvent.getPatronymic());
			combobox.setValue(patient);
		}
		combobox.addValueChangeListener((Property.ValueChangeListener) event -> {
			Notification.show("Selected item: " + event.getProperty().getValue(), Notification.Type.HUMANIZED_MESSAGE);
			Patient patient1 = (Patient) event.getProperty().getValue();
			setSelectItem(patient1);
			patientContainer.setSelectedPatientBean(patient1);
			this.consultationEvent = presenter.selectedItem();
			bindConsultationEventForm();
		});
		combobox.setContainerDataSource(patientContainer);
		combobox.focus();

		applyEventButton.addClickListener(clickEvent -> {
			presenter.commitEvent();
			eventPopup.close();
		});
		applyEventButton.addStyleName("primary");

		cancelButton.addClickListener(clickEvent -> presenter.discardEvent());
		cancelButton.addStyleName("primary");

		deleteEventButton.addClickListener(clickEvent -> {
			presenter.deleteEvent();
			eventPopup.close();
		});
		deleteEventButton.addStyleName("primary");

		startDateField.setResolution(Resolution.MINUTE);
		endDateField.setResolution(Resolution.MINUTE);
		allDayField.setImmediate(true);
		allDayField.addValueChangeListener(event -> setFormDateResolution(allDayField.getValue() ? Resolution.DAY : Resolution.MINUTE));

		executorField.setInputPrompt("человек,отвественный за процедуру");
		executorField.setNullRepresentation("");

		nameField.setInputPrompt("имя пациента");

		surnameField.setInputPrompt("фамилия пациента");

		patronymicField.setInputPrompt("отчество пациента");

		caseHistoryNumTextField = new TextField("Номер истории");
		caseHistoryNumTextField.setNullRepresentation("");

		birthdayField.setNullRepresentation("");
		StringToDateConverter dateConverter = new StringToDateConverter(){
			@Override
			protected DateFormat getFormat(Locale locale) {
				return DateFormat.getDateInstance();
			}
		};
		birthdayField.setConverter(dateConverter);

		initLayout();
	}

	private void initLayout() {
		GridLayout allComponents = new GridLayout();
		allComponents.setSpacing(true);
		allComponents.setMargin(true);

		HorizontalLayout eventDateRange = new HorizontalLayout(startDateField, endDateField,allDayField);
		eventDateRange.setComponentAlignment(allDayField,Alignment.MIDDLE_LEFT);
		eventDateRange.setSpacing(true);

		HorizontalLayout patientName = new HorizontalLayout(nameField, surnameField,
			patronymicField, birthdayField, caseHistoryNumTextField);
		patientName.setSpacing(true);

		HorizontalLayout patientData = new HorizontalLayout(birthdayField, caseHistoryNumTextField);
		patientData.setSpacing(true);

		combobox.setWidth("400px");
		VerticalLayout properties = new VerticalLayout(eventDateRange,
			selectProcedure,combobox,patientName,executorField,descriptionField);

		eventFormLayout.addComponent(properties);
		eventFormLayout.setSpacing(true);

		HorizontalLayout buttons = new HorizontalLayout(deleteEventButton,applyEventButton,cancelButton);
		patientName.setWidthUndefined();
		buttons.setSpacing(true);

		allComponents.addComponents(eventDateRange,patientName,patientData,properties,buttons);
		eventPopup.setContent(allComponents);
	}

	private void bindConsultationEventForm() {
		BeanItem<ConsultationEvent> item = new BeanItem<>(consultationEvent);
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
		return consultationEvent;
	}

	@Override
	public void commitEvent() {
		try {
			fieldGroup.commit();
		} catch (FieldGroup.CommitException ex) {
			Logger.getGlobal().log(Level.SEVERE,"Problems with commit",ex);
		}
	}

	@Override
	public void discardEvent() {
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

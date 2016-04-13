package lgk.nsbc.ru.view;

import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.converter.StringToDateConverter;
import com.vaadin.data.validator.*;
import com.vaadin.shared.ui.MarginInfo;
import lgk.nsbc.ru.backend.HeadManager;
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

import java.math.BigInteger;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;


public class EditFormViewImpl implements EditFormView{

	private Window eventPopup = new Window();
	private FormLayout eventFormLayout = new FormLayout();
	private PatientCombobox combobox = new PatientCombobox("Быстрый ввод");
	private FieldGroup fieldGroup = new FieldGroup();
	private Button deleteEventButton = new Button("Удалить");
	private Button applyEventButton = new Button("Применить");
	private Button cancelButton = new Button("Отмена");
	private NativeSelect selectProcedure = new NativeSelect("Вид консультации",CalendarPresenterImpl.PROCEDURES);
	private CheckBox allDayField = new CheckBox("Полный день");

	@PropertyId("description")
	private TextArea descriptionField = new TextArea("Описание");

	@PropertyId("executor")
	private TextField executorField = new TextField("Исполнитель");

	@PropertyId("name")
	private TextField nameField = new TextField("Имя");

	@PropertyId("surname")
	private TextField surnameField = new TextField("Фамилия");

	@PropertyId("patronymic")
	private TextField patronymicField = new TextField("Отчество");

	@PropertyId("birthday")
	private DateField birthdayField = new DateField("Дата рождения");

	@PropertyId("case_history_num")
	private TextField caseHistoryNumTextField = new TextField("Номер истории");

	@PropertyId("start")
	private DateField startDateField = new DateField("Начало события");

	@PropertyId("end")
	private DateField endDateField = new DateField("Конец события");

	EditFormPresenter presenter;
	ConsultationEvent consultationEvent;
	HeadManager headManager;

	public EditFormViewImpl(EditFormPresenter editFormPresenter,
							ConsultationEvent consultationEvent, HeadManager headManager, boolean newEvent) {
		this.consultationEvent = consultationEvent;
		this.presenter = editFormPresenter;
		this.headManager = headManager;
		initForm(newEvent);
		bindConsultationEvent();
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
	    PatientContainer patientContainer = new PatientContainer(headManager.getPatientsManager());
		if (!newEvent) {
			combobox.setContainerDataSource(patientContainer);
			combobox.setValue(consultationEvent.getCurrentPatient());
		}
		combobox.addValueChangeListener((Property.ValueChangeListener) event -> {
			presenter.handleSelectPatient((Patient) event.getProperty().getValue());
			patientContainer.setSelectedPatientBean(consultationEvent.getCurrentPatient());
			Notification.show("Selected item: " + consultationEvent.getCurrentPatient(), Notification.Type.HUMANIZED_MESSAGE);
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
		startDateField.setValidationVisible(true);
		startDateField.addValidator(new DateRangeValidator("Недопустимая дата",null,null,Resolution.MINUTE));

		endDateField.setResolution(Resolution.MINUTE);
		endDateField.setValidationVisible(true);
		endDateField.addValidator(new DateRangeValidator("Недопустимая дата",null,null,Resolution.MINUTE));
		allDayField.setImmediate(true);
		allDayField.addValueChangeListener(event -> {
			Resolution resolution = allDayField.getValue() ? Resolution.DAY : Resolution.MINUTE;
			if (startDateField != null && endDateField != null) {
				startDateField.removeAllValidators();
				startDateField.setResolution(resolution);
				startDateField.addValidator(new DateRangeValidator("Недопустимая дата",null,null,resolution));
				endDateField.setResolution(resolution);
				endDateField.removeAllValidators();
				endDateField.addValidator(new DateRangeValidator("Недопустимая дата",null,null,resolution));
			}
		});

		executorField.setInputPrompt("Человек,отвественный за процедуру");
		executorField.setNullRepresentation("");
		executorField.setValidationVisible(true);
		executorField.addValidator(new StringLengthValidator("Недопустимое имя",0,254,true));
		executorField.addValidator(new RegexpValidator("[А-Яа-я ]+",true,"Недопустимое значение"));

		nameField.setInputPrompt("имя пациента");
		nameField.setNullRepresentation("");
		nameField.setValidationVisible(true);
		nameField.addValidator(new StringLengthValidator("Недопустимое имя",0,254,true));
		nameField.addValidator(new RegexpValidator("[А-Яа-я ]+",true,"Недопустимое значение"));

		surnameField.setInputPrompt("фамилия пациента");
		surnameField.setNullRepresentation("");
		surnameField.setValidationVisible(true);
		surnameField.addValidator(new StringLengthValidator("Недопустимое имя",0,254,true));
		surnameField.addValidator(new RegexpValidator("[А-Яа-я ]+",true,"Недопустимое значение"));

		patronymicField.setInputPrompt("отчество пациента");
		patronymicField.setNullRepresentation("");
		patronymicField.setValidationVisible(true);
		patronymicField.addValidator(new StringLengthValidator("Недопустимое имя",0,254,true));
		patronymicField.addValidator(new RegexpValidator("[А-Яа-я ]+",true,"Недопустимое значение"));

		caseHistoryNumTextField = new TextField("Номер истории");
		caseHistoryNumTextField.setNullRepresentation("");
		caseHistoryNumTextField.setValidationVisible(true);
		caseHistoryNumTextField.addValidator(new IntegerRangeValidator("Недопустимое значение", 1,Integer.MAX_VALUE));

		birthdayField.setResolution(Resolution.DAY);
		birthdayField.addValidator(new DateRangeValidator("Недопустимая дата",null,null,Resolution.DAY));
		birthdayField.setValidationVisible(true);

		selectProcedure.select(CalendarPresenterImpl.PROCEDURES.get(0));
		initLayout();
	}

	private void initLayout() {
		GridLayout allComponents = new GridLayout();
		allComponents.setSpacing(true);
		MarginInfo marginInfo = new MarginInfo(true);
		marginInfo.setMargins(false,false,false,true);
		allComponents.setMargin(marginInfo);

		HorizontalLayout eventDateRange = new HorizontalLayout(startDateField, endDateField,allDayField);
		eventDateRange.setComponentAlignment(allDayField,Alignment.MIDDLE_LEFT);
		eventDateRange.setSpacing(true);

		HorizontalLayout patientName = new HorizontalLayout(nameField, surnameField, patronymicField);
		patientName.setSpacing(true);

		HorizontalLayout patientData = new HorizontalLayout(birthdayField, caseHistoryNumTextField,selectProcedure);
		patientData.setSpacing(true);

		descriptionField.setWidth("100%");
		combobox.setWidth("100%");
		executorField.setWidth("100%");
		VerticalLayout properties = new VerticalLayout(eventDateRange,combobox,patientName,executorField,descriptionField);

		eventFormLayout.addComponent(properties);
		eventFormLayout.setSpacing(true);

		HorizontalLayout buttons = new HorizontalLayout(deleteEventButton,applyEventButton,cancelButton);
		buttons.setSpacing(true);

		allComponents.addComponents(eventDateRange,patientName,patientData,properties,buttons);
		// Да что такое с этим сраным ваадином и layout'ом, чё я не так то делаю??? Что
		// ему никак без css не укажешь нормальные пропорции без scroolbar'ов
		eventPopup.setHeight("600px");
		eventPopup.setWidth("700px");
		eventPopup.setContent(allComponents);
	}



	@Override
	public void bindConsultationEvent() {
		BeanItem<ConsultationEvent> item = new BeanItem<>(consultationEvent);
		fieldGroup.setBuffered(true);
		fieldGroup.setItemDataSource(item);
		fieldGroup.bindMemberFields(this);
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
}
package lgk.nsbc.ru.view;

import lgk.nsbc.ru.backend.PatientContainer;
import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Patient;
import lgk.nsbc.ru.presenter.ConsultationPresenter;
import lgk.nsbc.ru.presenter.EditFormPresenter;
import lgk.nsbc.ru.presenter.Presenter;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.*;

import java.util.ArrayList;


public class EditFormViewImpl implements EditFormView {

    EditFormPresenter presenter;
    public Window eventPopup;

    private FormLayout eventFormLayout;
    private  PatientCombobox combobox;
    private PatientContainer patientContainer;
    private FieldGroup fieldGroup;
    private Button deleteEventButton;
    private Button applyEventButton;
    private Patient patient;
    private TextField captionField;
    private TextArea descriptionField;
    private TextField executorField;
    private TextField nameField;
    private TextField surnameField;
    private TextField patronymicField;
    private DateField birthdayField;
    private TextField casHisField;

    private ConsultationEvent consulEvent;

    private DateField startDateField;
    private DateField endDateField;

    private boolean useSecondResolution;


    public EditFormViewImpl(Presenter presenter) {
        this.presenter = (EditFormPresenter) presenter;
    }

    @Override
    public void createEventPopup (ConsultationEvent consultationEvent, boolean newEvent) {
        if (eventPopup == null) {
            initForm(consultationEvent);
            updateConsultationEventForm(consulEvent);
        }
        if (newEvent) {
            eventPopup.setCaption("Новая консультация");
        } else {
            eventPopup.setCaption("Редактирование консультации");
        }

        UI.getCurrent().addWindow(eventPopup);
    }

    private void initForm(ConsultationEvent consultationEvent) {
        consulEvent = consultationEvent;
        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);

        eventPopup = new Window(null, layout);
        eventPopup.setSizeFull();
        eventPopup.setModal(true);
        eventPopup.center();
        eventPopup.addStyleName("light");

        eventFormLayout = new FormLayout();
        eventFormLayout.setMargin(false);

        layout.addComponent(eventFormLayout);

        combobox = new PatientCombobox("Быстрый ввод");
        combobox.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        combobox.setItemCaptionPropertyId("name,surname,patronymic,birthday");
        patientContainer = new PatientContainer();
        combobox.setImmediate(true);
        combobox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                Notification.show("Selected item: " + event.getProperty().getValue(), Notification.Type.HUMANIZED_MESSAGE);
                Patient patient = (Patient) event.getProperty().getValue();
                setSelectItem(patient);
                consulEvent = presenter.selectedItem();
                patientContainer.setSelectedPatientBean(patient);
                updateConsultationEventForm(consulEvent);
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

        deleteEventButton = new Button("Удалить", clickEvent -> {
            presenter.deleteEvent();
            eventPopup.close();
        });
        deleteEventButton.addStyleName("borderless");

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
        CheckBox allDayField = createCheckBox("All-day");
        allDayField.addValueChangeListener(event -> setFormDateResolution(allDayField.getValue() ? Resolution.DAY : Resolution.MINUTE));
        NativeSelect selectProcedure = createNativeSelect(ConsultationPresenter.PROCEDURES, "Вид консультации");

        captionField = createTextField("Заголовок");
        captionField.setInputPrompt("Название события"
        );
        executorField = createTextField("Исполнитель");
        executorField.setInputPrompt("человек,отвественный за процедуру");

        descriptionField = createTextArea("Описание");

        nameField = createTextField("Имя");
        nameField.setInputPrompt("имя пациента");

        surnameField = createTextField("Фамилия");
        surnameField.setInputPrompt("фамилия пациента");

        patronymicField = createTextField("Отчество");
        patronymicField.setInputPrompt("отчество пациента");

        casHisField = createTextField("Номер истории");

        birthdayField = createDateField("Дата рождения");

        HorizontalLayout hlLayout = new HorizontalLayout(startDateField, endDateField);
        eventFormLayout.addComponents(hlLayout, captionField, combobox, descriptionField, selectProcedure, executorField);
        HorizontalLayout horizontalLayout = new HorizontalLayout(nameField, surnameField,
                patronymicField, birthdayField, casHisField);
        eventFormLayout.addComponent(horizontalLayout);

    }
    private void updateConsultationEventForm(ConsultationEvent event) {

        fieldGroup = new FieldGroup();
        BeanItem<ConsultationEvent> item = new BeanItem<ConsultationEvent>(consulEvent);
        fieldGroup.setBuffered(true);
        fieldGroup.setItemDataSource(item);

        fieldGroup.bind(startDateField, "start");
        fieldGroup.bind(endDateField, "end");
        fieldGroup.bind(captionField, "caption");
        fieldGroup.bind(descriptionField, "description");
        fieldGroup.bind(executorField, "executor");
        fieldGroup.bind(nameField,"name");
        fieldGroup.bind(surnameField,"surname");
        fieldGroup.bind(patronymicField,"patronymic");
        fieldGroup.bind(birthdayField,"birthday");
        fieldGroup.bind(casHisField,"case_history_num");
    }

    private void setFormDateResolution(Resolution resolution) {
        if (startDateField != null && endDateField != null) {
            startDateField.setResolution(resolution);
            endDateField.setResolution(resolution);
        }
    }

    private TextArea createTextArea(String caption) {
        TextArea f = new TextArea(caption);
        f.setNullRepresentation("");
        return f;

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
    * находит item, в котором есть данные, введенные пользователем в field
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

package lgk.nsbc.ru.presenter;

import lgk.nsbc.ru.backend.ConsultationManager;
import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.Patient;
import lgk.nsbc.ru.model.ConsultationModel;
import lgk.nsbc.ru.view.EditFormView;
import lgk.nsbc.ru.view.EditFormViewImpl;

/**
 * Created by user on 16.03.2016.
 */
public class EditFormPresenterImpl implements EditFormPresenter {
    EditFormView editFormView;
    private	final ConsultationModel model;
    private final ConsultationManager consultationManager;

    public EditFormPresenterImpl(ConsultationModel model, ConsultationManager consultationManager) {
        this.model = model;
        this.consultationManager = consultationManager;
    }

    /*
    * создается событие
    * @param дата начала и дата конца
    * return новое событие
     */
    @Override
    public void handleNewEvent(ConsultationEvent consultationEvent) {
        editFormView = new EditFormViewImpl(this,consultationEvent,true);
    }
	/*
    * редактирование события
    *@param
    */
    @Override
    public void handleEventClick(ConsultationEvent consultationEvent) {
        editFormView = new EditFormViewImpl(this,consultationEvent,false);
    }
    /*
    * в item cохраняем данные
    * если в модели нет его сохраняем в beanItemContainer
    */
    @Override
    public void commitEvent() {
        editFormView.commitEvent();
    }
    @Override
    public void discardEvent()
    {
        editFormView.discardEvent();
    }
    /*
    * удаляем item, если он есть в beanItemContainer
     */
    @Override
    public void deleteEvent() {
        ConsultationEvent event = editFormView.getFormEvent();
        if (model.getBeanItemContainer().containsId(event))
            model.getBeanItemContainer().removeItem(event);
    }
    @Override
    public void selectedItem() {
        Patient selectedPatient = editFormView.getSelectItem();
        ConsultationEvent consultationEvent = editFormView.getConsultationEvent();
        Patient fromDBPatient = consultationManager.selectPatient(selectedPatient);
		consultationEvent.setNewPatient(fromDBPatient);
    }
}

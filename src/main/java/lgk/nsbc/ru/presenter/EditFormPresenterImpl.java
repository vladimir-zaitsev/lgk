package lgk.nsbc.ru.presenter;

import lgk.nsbc.ru.backend.*;
import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Patient;
import lgk.nsbc.ru.backend.entity.People;
import lgk.nsbc.ru.model.ConsultationModel;
import lgk.nsbc.ru.view.EditFormView;
import lgk.nsbc.ru.view.EditFormViewImpl;

import java.util.function.Consumer;

/**
 * Created by user on 16.03.2016.
 */
public class EditFormPresenterImpl implements EditFormPresenter {
	private EditFormView editFormView;
	private ConsultationEvent consultationEvent;
	private InsertManager insertManager;
	private HeadManager headManager;
	private Consumer<ConsultationEvent> deleteEvent;
	private boolean newEvent;

	public EditFormPresenterImpl(HeadManager headManager,Consumer<ConsultationEvent> deleteEvent )
	{
		this.headManager = headManager;
		this.deleteEvent = deleteEvent;
	}

	@Override
	public void handleNewEvent(ConsultationEvent consultationEvent) {
		this.consultationEvent = consultationEvent;
		editFormView = new EditFormViewImpl(this, consultationEvent,headManager,true);
	}

	@Override
	public void handleEventClick(ConsultationEvent consultationEvent) {
		this.consultationEvent = consultationEvent;
		editFormView = new EditFormViewImpl(this,consultationEvent,headManager,false);

	}

	@Override
	public void commitEvent() {
		editFormView.commitEvent();
	    saveData();
	}

	@Override
	public void discardEvent()
	{
		editFormView.discardEvent();
	}

	@Override
	public void deleteEvent() {
		deleteEvent.accept(consultationEvent);
	}

	@Override
	public void handleSelectPatient(Patient patient) {
		consultationEvent.setNewPatient(headManager.getPatientsManager().selectPatient(patient));
		editFormView.bindConsultationEvent();
	}
	@Override
	public void saveData()
	{
		Patient patient = consultationEvent.getCurrentPatient();
		// Выполняются данные действия, если пациент не существует в базе
		insertManager = new InsertManager(consultationEvent,headManager);
		if (patient.getN() == null)
		{
			insertManager.insertData();
		}
		// Выполняются данные действия, если пациент  существует в базе
		if (patient.getN() != null)
		{
			insertManager.insertConsultation();
		}
	}

}

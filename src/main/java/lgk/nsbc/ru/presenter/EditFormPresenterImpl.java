package lgk.nsbc.ru.presenter;

import lgk.nsbc.ru.backend.*;
import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Consultation;
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
	private final PeopleManager peopleManager;
	private  final PatientsManager patientsManager;
	private final  ConsultationManager consultationManager;
	private  ProcedureManager procedureManager;
	private Consumer<ConsultationEvent> deleteEvent;
	private boolean newEvent;

	public EditFormPresenterImpl(PeopleManager peopleManager
		,PatientsManager patientsManager
		,ConsultationManager consultationManager
		,Consumer<ConsultationEvent> deleteEvent ) {
		this.peopleManager = peopleManager;
		this.patientsManager = patientsManager;
		this.consultationManager = consultationManager;
		this.deleteEvent = deleteEvent;
	}

	@Override
	public void handleNewEvent(ConsultationEvent consultationEvent) {
		this.consultationEvent = consultationEvent;
		newEvent = true;
		editFormView = new EditFormViewImpl(this, consultationEvent,patientsManager,newEvent);
	}

	@Override
	public void handleEventClick(ConsultationEvent consultationEvent) {
		this.consultationEvent = consultationEvent;
		System.out.println(consultationEvent.getConsultation().getN());
		newEvent = false;
		editFormView = new EditFormViewImpl(this,consultationEvent,patientsManager,newEvent);

	}

	@Override
	public void commitEvent() {
		editFormView.commitEvent();
		if (newEvent) {
			saveConsultation();
		}
	}

	@Override
	public void discardEvent() {
		editFormView.discardEvent();
	}

	@Override
	public void deleteEvent() {
		deleteEvent.accept(consultationEvent);
		deleteConsultation();
	}

	@Override
	public void handleSelectPatient(Patient patient) {
		consultationEvent.setNewPatient(patientsManager.selectPatient(patient));
		editFormView.bindConsultationEvent();
	}
	@Override
	public void saveConsultation() {
		procedureManager = new ProcedureManager(consultationEvent
			,peopleManager
			,patientsManager
			,consultationManager);
		Patient patient = consultationEvent.getCurrentPatient();
		if (patient.getN() == null) {
			procedureManager.insertConsultation();
		}
		else {
			procedureManager.insertOnlyConsultation();
		}
	}

	@Override
	public void deleteConsultation() {
		Consultation consultation = consultationEvent.getConsultation();
		consultationManager.deleteConsultation(consultation);
	}
}

package lgk.nsbc.ru.presenter;

import lgk.nsbc.ru.backend.ConsultationManager;
import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Patient;
import lgk.nsbc.ru.view.EditFormView;
import lgk.nsbc.ru.view.EditFormViewImpl;

import java.util.function.Consumer;

/**
 * Created by user on 16.03.2016.
 */
public class EditFormPresenterImpl implements EditFormPresenter {
	private EditFormView editFormView;
	private ConsultationEvent consultationEvent;
	private ConsultationManager consultationManager;
	private Consumer<ConsultationEvent> deleteEvent;

	public EditFormPresenterImpl(ConsultationManager consultationManager, Consumer<ConsultationEvent> deleteEvent) {
		this.consultationManager = consultationManager;
		this.deleteEvent = deleteEvent;
	}

	@Override
	public void handleNewEvent(ConsultationEvent consultationEvent) {
		this.consultationEvent = consultationEvent;
		editFormView = new EditFormViewImpl(this, consultationEvent, true);
	}

	@Override
	public void handleEventClick(ConsultationEvent consultationEvent) {
		this.consultationEvent = consultationEvent;
		editFormView = new EditFormViewImpl(this, consultationEvent, false);
	}

	@Override
	public void commitEvent() {
		editFormView.commitEvent();
	}

	@Override
	public void discardEvent() {
		editFormView.discardEvent();
	}

	@Override
	public void deleteEvent() {
		deleteEvent.accept(consultationEvent);
	}

	@Override
	public void handleSelectPatient(Patient patient) {
		consultationEvent.setNewPatient(consultationManager.selectPatient(patient));
		editFormView.bindConsultationEvent();
	}
}

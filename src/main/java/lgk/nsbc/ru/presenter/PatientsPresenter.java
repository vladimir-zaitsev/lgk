package lgk.nsbc.ru.presenter;

import lgk.nsbc.ru.backend.PatientsManager;
import lgk.nsbc.ru.model.PatientsModelImpl;

public class PatientsPresenter {
	private final PatientsModelImpl model;
	private final PatientsManager manager;

	public PatientsPresenter(PatientsModelImpl model, PatientsManager manager) {
		this.model = model;
		this.manager = manager;
	}

	public void start() {
		model.patientsList.removeAllItems();
		model.patientsList.addAll(manager.listPatients());
	}
}

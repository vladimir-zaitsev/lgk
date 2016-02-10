package lgk.nsbc.ru.presenter;

import com.vaadin.ui.Notification;
import lgk.nsbc.ru.backend.PatientsManager;
import lgk.nsbc.ru.backend.SessionManager;
import lgk.nsbc.ru.model.PatientsModelImpl;

public class PatientsPresenter {
	private final PatientsModelImpl model;
	private final PatientsManager manager;
	private SessionManager sessionManager;
	private String lgkSessionId;

	public PatientsPresenter(PatientsModelImpl model, PatientsManager manager, SessionManager sessionManager, String lgkSessionId) {
		this.model = model;
		this.manager = manager;
		this.sessionManager = sessionManager;
		this.lgkSessionId = lgkSessionId;
	}

	public void start() {
		model.session = sessionManager.loadSession(lgkSessionId);

		model.patientsList.removeAllItems();
		model.patientsList.addAll(manager.listPatients());

		if(model.session != null) {
			Notification.show("Здравствуйте, " + model.session.getName() + " " + model.session.getSurname() + "!");
		}
	}
}

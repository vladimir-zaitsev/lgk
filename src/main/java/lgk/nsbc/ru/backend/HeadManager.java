package lgk.nsbc.ru.backend;

/**
 * Created by user on 11.04.2016.
 */
/*
* Главный менеджер, который содержит всех менеджеров
*/
public class HeadManager {

	private PeopleManager peopleManager;
	private PatientsManager patientsManager;
	private ConsultationManager consultationManager;

	public HeadManager(PeopleManager peopleManager,
					   PatientsManager patientsManager,
					   ConsultationManager consultationManager,
					   GeneratorManager generatorManager,
					   RegistrationManager registrationManager,
					   SessionManager sessionManager) {
		this.peopleManager = peopleManager;
		this.patientsManager = patientsManager;
		this.consultationManager = consultationManager;
		this.generatorManager = generatorManager;
		this.registrationManager = registrationManager;
		this.sessionManager = sessionManager;
	}

	private GeneratorManager generatorManager;
	private RegistrationManager registrationManager;
	private  SessionManager sessionManager;

	public GeneratorManager getGeneratorManager() {
		return generatorManager;
	}

	public PeopleManager getPeopleManager() {
		return peopleManager;
	}

	public PatientsManager getPatientsManager() {
		return patientsManager;
	}

	public ConsultationManager getConsultationManager() {
		return consultationManager;
	}

	public RegistrationManager getRegistrationManager() {
		return registrationManager;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

}

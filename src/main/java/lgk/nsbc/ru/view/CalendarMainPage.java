package lgk.nsbc.ru.view;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import lgk.nsbc.ru.backend.*;
import lgk.nsbc.ru.model.ConsultationModel;

/**
 * Created by Роман on 29.03.2016.
 */
public class CalendarMainPage {
	public CalendarMainPage(UI ui, String  lgkSessionId) {
		ConsultationModel consultationModel = new ConsultationModel();
		SessionManager sessionManager = new SessionManager(lgkSessionId);
		RegistrationManager registrationManager  = new RegistrationManager(sessionManager);
		PeopleManager peopleManager = new PeopleManager();
		PatientsManager patientsManager = new PatientsManager();
		ConsultationManager consultationManager = new ConsultationManager();
		GeneratorManager generatorManager = new GeneratorManager();

		HeadManager headManager  = new HeadManager(peopleManager,patientsManager,consultationManager,
			generatorManager,registrationManager,sessionManager);

		CalendarView calendarView = new CalendarViewImpl(consultationModel,headManager);

		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeightUndefined();
		tabSheet.addTab((CalendarViewImpl)calendarView,"Радиохирургия");
		ui.setContent(tabSheet);
	}


}

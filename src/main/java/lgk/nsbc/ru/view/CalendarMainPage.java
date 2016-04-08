package lgk.nsbc.ru.view;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import lgk.nsbc.ru.backend.ConsultationManager;
import lgk.nsbc.ru.backend.PatientsManager;
import lgk.nsbc.ru.backend.PeopleManager;
import lgk.nsbc.ru.model.ConsultationModel;

/**
 * Created by Роман on 29.03.2016.
 */
public class CalendarMainPage {
	public CalendarMainPage(UI ui) {
		ConsultationModel consultationModel = new ConsultationModel();
		PeopleManager peopleManager = new PeopleManager();
		PatientsManager patientsManager = new PatientsManager(peopleManager);
		ConsultationManager consultationManager = new ConsultationManager(patientsManager);
		CalendarView calendarView = new CalendarViewImpl(consultationModel,consultationManager);

		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeightUndefined();
		tabSheet.addTab((CalendarViewImpl)calendarView,"Радиохирургия");
		ui.setContent(tabSheet);
	}
}

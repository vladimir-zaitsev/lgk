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
		PeopleManager peopleManager = new PeopleManager(lgkSessionId);
		PatientsManager patientsManager = new PatientsManager(lgkSessionId);
		ConsultationManager consultationManager = new ConsultationManager(lgkSessionId);
		CalendarView calendarView = new CalendarViewImpl(consultationModel
			,peopleManager
			,patientsManager
			,consultationManager);

		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeightUndefined();
		tabSheet.addTab((CalendarViewImpl)calendarView,"Радиохирургия");
		ui.setContent(tabSheet);
	}


}

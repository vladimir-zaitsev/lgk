package lgk.nsbc.ru.view;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import lgk.nsbc.ru.backend.ConsultationManager;
import lgk.nsbc.ru.model.ConsultationModel;
import lgk.nsbc.ru.presenter.EditFormPresenter;
import lgk.nsbc.ru.presenter.Presenter;

/**
 * Created by Роман on 29.03.2016.
 */
public class CalendarMainPage {
	public CalendarMainPage(UI ui) {
		ConsultationModel consultationModel = new ConsultationModel();
		ConsultationManager consultationManager = new ConsultationManager();
		Presenter editFormPresenter = new EditFormPresenter(consultationModel,consultationManager);
		CalendarView calendarView = new CalendarViewImpl(consultationModel,consultationManager);
		calendarView.setEditFormPresenter(editFormPresenter);

		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeightUndefined();
		tabSheet.addTab((CalendarViewImpl)calendarView,"Радиохирургия");
		ui.setContent(tabSheet);
	}
}

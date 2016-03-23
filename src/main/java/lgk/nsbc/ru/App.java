package lgk.nsbc.ru;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import lgk.nsbc.ru.backend.ConsultationManager;
import lgk.nsbc.ru.model.ConsultationModel;
import lgk.nsbc.ru.presenter.CalendarPresenterImpl;
import lgk.nsbc.ru.presenter.EditFormPresenter;
import lgk.nsbc.ru.presenter.Presenter;
import lgk.nsbc.ru.view.CalendarView;
import lgk.nsbc.ru.view.CalendarViewImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 */
@Theme("mytheme")
//@Widgetset("lgk.nsbc.ru.MyAppWidgetset")
public class App extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
		String lgkSessionId = getPage().getUriFragment();
		getPage().setUriFragment(null, false);

		/*PatientsModelImpl model = new PatientsModelImpl(new I18nManager());
		PatientsList view = new PatientsList(model);
		setContent(view);

		PatientsManager patientsManager = new PatientsManager();
		SessionManager sessionManager = new SessionManager();
		PatientsPresenter patientsPresenter = new PatientsPresenter(model, patientsManager, sessionManager, lgkSessionId);
		patientsPresenter.start();*/
		/*ConsultationModel consultationModel = new ConsultationModel();
		CalendarViewOld calendarView = new CalendarViewOld(consultationModel);

		ConsultationManager consultationManager = new ConsultationManager();
		ConsultationPresenter consultationPresenter = new ConsultationPresenter(consultationModel,consultationManager);
		calendarView.setPresenter(consultationPresenter);
		consultationPresenter.start();*/
		ConsultationModel consultationModel = new ConsultationModel();
		ConsultationManager consultationManager = new ConsultationManager();
		Presenter editFormPresenter = new EditFormPresenter(consultationModel,consultationManager);
		CalendarView calendarView = new CalendarViewImpl(consultationModel,consultationManager);
		calendarView.setEditFormPresenter(editFormPresenter);
		CalendarView calendarView1 = new CalendarViewImpl(consultationModel,consultationManager);
		calendarView1.setEditFormPresenter(editFormPresenter);
		CalendarView calendarView2 = new CalendarViewImpl(consultationModel,consultationManager);
		calendarView2.setEditFormPresenter(editFormPresenter);
		CalendarView calendarView3 = new CalendarViewImpl(consultationModel,consultationManager);
		calendarView3.setEditFormPresenter(editFormPresenter);

		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeightUndefined();
		tabSheet.addTab((CalendarViewImpl)calendarView,"Радиохирургия");
		tabSheet.addTab((CalendarViewImpl)calendarView1,"Очные");
		tabSheet.addTab((CalendarViewImpl)calendarView2,"Заочные");
		tabSheet.addTab((CalendarViewImpl)calendarView3,"Онкология");
		setContent(tabSheet);
		//ConsultationManager consultationManager = new ConsultationManager();
		//ConsultationPresenter consultationPresenter = new ConsultationPresenter(consultationModel,consultationManager);
		//consultationPresenter.start();
	}

	@WebServlet(urlPatterns = "/*", name = "AppServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = App.class, productionMode = false)
	public static class AppServlet extends VaadinServlet {
		@Override
		protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			super.service(request, response);
		}
	}
}

package lgk.nsbc.ru;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import lgk.nsbc.ru.backend.I18nManager;
import lgk.nsbc.ru.backend.PatientsManager;
import lgk.nsbc.ru.model.PatientsModelImpl;
import lgk.nsbc.ru.presenter.PatientsPresenter;
import lgk.nsbc.ru.view.PatientsList;

import javax.servlet.annotation.WebServlet;

/**
 *
 */
@Theme("mytheme")
//@Widgetset("lgk.nsbc.ru.MyAppWidgetset")
public class App extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
		PatientsModelImpl model = new PatientsModelImpl(new I18nManager());
		PatientsList view = new PatientsList(model);
		setContent(view);

		PatientsManager patientsManager = new PatientsManager();
		PatientsPresenter patientsPresenter = new PatientsPresenter(model, patientsManager);
		patientsPresenter.start();
	}

	@WebServlet(urlPatterns = "/*", name = "AppServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = App.class, productionMode = false)
	public static class AppServlet extends VaadinServlet {
	}
}

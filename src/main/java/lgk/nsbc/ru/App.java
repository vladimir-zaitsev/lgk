package lgk.nsbc.ru;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import lgk.nsbc.ru.backend.I18nManager;
import lgk.nsbc.ru.backend.PatientsManager;
import lgk.nsbc.ru.backend.SessionManager;
import lgk.nsbc.ru.model.PatientsModelImpl;
import lgk.nsbc.ru.presenter.PatientsPresenter;
import lgk.nsbc.ru.view.PatientsList;

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

		PatientsModelImpl model = new PatientsModelImpl(new I18nManager());
		PatientsList view = new PatientsList(model);
		setContent(view);

		PatientsManager patientsManager = new PatientsManager();
		SessionManager sessionManager = new SessionManager();
		PatientsPresenter patientsPresenter = new PatientsPresenter(model, patientsManager, sessionManager, lgkSessionId);
		patientsPresenter.start();
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

package lgk.nsbc.ru;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.*;
import lgk.nsbc.ru.model.ConsultationModel;
import lgk.nsbc.ru.presenter.ConsultationPresenter;
import lgk.nsbc.ru.view.ConsultationView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 *
 */
@Theme("mytheme")
@Widgetset("lgk.nsbc.ru.MyAppWidgetset")
public class App extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {/*
		String lgkSessionId = getPage().getUriFragment();
		getPage().setUriFragment(null, false);

		PatientsModelImpl model = new PatientsModelImpl(new I18nManager());
		PatientsList view = new PatientsList(model);
		setContent(view);

		PatientsManager patientsManager = new PatientsManager();
		SessionManager sessionManager = new SessionManager();
		PatientsPresenter patientsPresenter = new PatientsPresenter(model, patientsManager, sessionManager, lgkSessionId);
		patientsPresenter.start();*/
		ConsultationModel model = new ConsultationModel();
		ConsultationPresenter presenter = new ConsultationPresenter(model);
		presenter.start();
		ConsultationView view = new ConsultationView(model);
		setContent(view);
		/*Grid grid= new Grid("");
		grid.addColumn("text",String.class);
		TextFieldRenderer<String> textFieldRenderer = new TextFieldRenderer<>();
		grid.getColumn("text").setRenderer(textFieldRenderer);
		grid.addRow("1");
		grid.addRow("2");
		grid.addRow("3");
		grid.addRow("4");
		grid.addRow("5");
		grid.addRow("6");
		grid.addRow("7");
		grid.addRow("8");
		setContent(grid);*/
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

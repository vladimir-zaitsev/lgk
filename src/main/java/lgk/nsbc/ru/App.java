package lgk.nsbc.ru;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import lgk.nsbc.ru.backend.ConsultationManager;
import lgk.nsbc.ru.model.ConsultationModel;
import lgk.nsbc.ru.presenter.ConsultationPresenter;
import lgk.nsbc.ru.view.CalendarView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

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
		ConsultationModel consultationModel = new ConsultationModel();
		CalendarView calendarView = new CalendarView(consultationModel);
		//setContent(calendarView);
		ArrayList<String> arrayList = new ArrayList(24);
		for (int i=0;i<24;i++) {
			arrayList.add(String.format("%02d:00",i));
		}
		ComboBox comboBox = new ComboBox("",arrayList);
		comboBox.setInputPrompt("Начало дня");
		comboBox.setWidth(150,Unit.PIXELS);
		comboBox.addValueChangeListener(valueChangeEvent -> {
			String value = (String)valueChangeEvent.getProperty().getValue();
			//Integer integer = new Integer.parseInt(value.split(":")[0]);
			System.out.println(Integer.parseUnsignedInt(value.split(":")[0]));

		});
		TabSheet tabSheet = new TabSheet();
		tabSheet.setHeightUndefined();
		tabSheet.addTab(calendarView,"Радиохирургия");
		tabSheet.addTab(comboBox,"Очные");
		tabSheet.addTab(new Button("кнопка"),"Заочные");
		tabSheet.addTab(new Button("кнопка"),"Онкология");
		setContent(tabSheet);
		ConsultationManager consultationManager = new ConsultationManager();
		ConsultationPresenter consultationPresenter = new ConsultationPresenter(consultationModel,consultationManager);
		consultationPresenter.start();
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

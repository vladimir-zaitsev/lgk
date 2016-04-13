package lgk.nsbc.ru;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import lgk.nsbc.ru.view.CalendarMainPage;

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
		if (lgkSessionId == null) {
			lgkSessionId = "9ec2104e97256dc639754ae07b5eb7bf";
		}
		getPage().setUriFragment(null, false);
		new CalendarMainPage(this,lgkSessionId);
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

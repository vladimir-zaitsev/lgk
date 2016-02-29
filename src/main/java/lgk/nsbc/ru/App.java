package lgk.nsbc.ru;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.ImageRenderer;
import lgk.nsbc.ru.backend.ConsultationFactory;
import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.ConsultationDay;
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
//@Widgetset("lgk.nsbc.ru.MyAppWidgetset")
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
		/*
		ConsultationFactory factory = new ConsultationFactory();
		BeanItemContainer<ConsultationDay> beanItemContainer = new BeanItemContainer<ConsultationDay>(ConsultationDay.class);
		// Заполнение рандомными данными
		for (int i=0;i<18;i++) {
			Date startDate = new Date();
			startDate.setSeconds(0);
			startDate.setMonth(2);
			startDate.setDate(0);
			startDate.setHours(9+i/2);
			startDate.setMinutes(30*(i%2));
			Date endDate = new Date(startDate.getTime());
			endDate.setMinutes(30*((i+1)%2));
			endDate.setHours(9+(i+1)/2);
			ConsultationDay consultationDay = new ConsultationDay(startDate
				,ConsultationFactory.getConsultation(startDate,endDate),
				ConsultationFactory.getConsultation(startDate,endDate),
				ConsultationFactory.getConsultation(startDate,endDate),
				ConsultationFactory.getConsultation(startDate,endDate),null);
			beanItemContainer.addBean(consultationDay);
		}
		Grid grid = new Grid();

		GeneratedPropertyContainer container = new GeneratedPropertyContainer(beanItemContainer);
		container.addGeneratedProperty("newTHINGY", new PropertyValueGenerator<String>() {
			@Override
			public Class<String> getType() {
				return String.class;
			}

			@Override
			public String getValue(Item item, Object o, Object o1) {
				Consultation prop =  (Consultation)item.getItemProperty("ochno").getValue();
				prop.getName();
				return prop.getDiagnosis()==null?null:prop.getDiagnosis();
			}
		});
		grid.setContainerDataSource(container);
		grid.setFrozenColumnCount(1);
		grid.setSizeFull();
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.setEditorEnabled(true);
		grid.setColumnReorderingAllowed(true);
		grid.setColumnOrder("time","rs","ochno","zaochno","oncology","other");
		HeaderRow row = grid.addHeaderRowAt(0);
		row.join("rs","ochno","zaochno","oncology","other").setText("Вторник 01.03.2016");
		DateRenderer renderer = new DateRenderer("%1$tH:%tM",getLocale());
		grid.getColumn("time").setRenderer(renderer);
		setContent(grid);
		*/
		/*VerticalLayout verticalLayout = new VerticalLayout();
		summary(verticalLayout);
		setContent(verticalLayout);*/

	}

	@WebServlet(urlPatterns = "/*", name = "AppServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = App.class, productionMode = false)
	public static class AppServlet extends VaadinServlet {
		@Override
		protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			super.service(request, response);
		}
	}

	public void summary(VerticalLayout layout){
		Grid grid=new Grid();
		grid.setCaption("My Grid");
		grid.setWidth("680px");
		grid.setHeight("380px");
		grid.setStyleName("gridwithpics128px");
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		grid.addColumn("picture",Resource.class).setRenderer(new ImageRenderer());
		grid.addColumn("name",String.class);
		grid.addColumn("born",Date.class);
		grid.addColumn("link",String.class);
		grid.addColumn("button",String.class).setRenderer(new ButtonRenderer(e -> Notification.show("Clicked " + grid.getContainerDataSource().getContainerProperty(e.getItemId(),"name"))));
		Grid.Column bornColumn=grid.getColumn("born");
		bornColumn.setRenderer(new DateRenderer("%1$tB %1$te, %1$tY",Locale.ENGLISH));
		Grid.Column linkColumn=grid.getColumn("link");
		linkColumn.setRenderer(new HtmlRenderer(),new Converter<String,String>(){
				private static final long serialVersionUID=6394779294728581811L;
				@Override public String convertToModel(    String value,    Class<? extends String> targetType,    Locale locale) throws Converter.ConversionException {
					return "not implemented";
				}
				@Override public String convertToPresentation(    String value,    Class<? extends String> targetType,    Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
					return "<a href='http://en.wikipedia.org/wiki/" + value + "' target='_top'>more info</a>";
				}
				@Override public Class<String> getModelType(){
					return String.class;
				}
				@Override public Class<String> getPresentationType(){
					return String.class;
				}
			}
		);
		grid.addRow(new ThemeResource("img/copernicus-128px.jpg"),"Nicolaus Copernicus",new GregorianCalendar(1473,2,19).getTime(),"Nicolaus_Copernicus","Delete");
		grid.addRow(new ThemeResource("img/galileo-128px.jpg"),"Galileo Galilei",new GregorianCalendar(1564,2,15).getTime(),"Galileo_Galilei","Delete");
		grid.addRow(new ThemeResource("img/kepler-128px.jpg"),"Johannes Kepler",new GregorianCalendar(1571,12,27).getTime(),"Johannes_Kepler","Delete");
		grid.setCellStyleGenerator(cell -> "picture".equals(cell.getPropertyId()) ? "imagecol" : null);
		layout.addComponent(grid);
	}
}

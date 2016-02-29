package lgk.nsbc.ru.view;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.DateRenderer;
import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.ConsultationDays;
import lgk.nsbc.ru.model.ConsultationModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Роман on 29.02.2016.
 */
public class ConsultationView extends AbstructView<ConsultationModel>{
	private SimpleDateFormat format = new SimpleDateFormat("EEEE, dd.MM.yy");
	private CheckBox hideOhcno;
	private CheckBox hideZaohcno;
	private CheckBox hideRS;
	private CheckBox hideOncology;
	private Grid grid;
	int daysCount;

	public ConsultationView(ConsultationModel consultationModel) {
		super(consultationModel);
		initGrid();
		initLayoutContent();
	}

	private void initGrid() {
		BeanItemContainer<ConsultationDays> beanItemContainer = model.getBeanItemContainer();
		GeneratedPropertyContainer container = new GeneratedPropertyContainer(beanItemContainer);
		grid = new Grid(container);
		Grid.HeaderRow row = grid.addHeaderRowAt(0);
		daysCount = beanItemContainer.getIdByIndex(0).getRs().size();
		for (int i=0;i<daysCount;i++) {
			final int dayIndx = i;
			container.addGeneratedProperty("rs"+i, new PropertyValueGenerator<String>() {
				@Override
				public Class<String> getType() {
					return String.class;
				}

				@Override
				public String getValue(Item item, Object o, Object o1) {
					Consultation consultation =  ((ArrayList<Consultation>)item.getItemProperty("rs").getValue()).get(dayIndx);
					return consultation.getName()==null?null:consultation.getName();
				}
			});
			container.addGeneratedProperty("ochno"+i, new PropertyValueGenerator<String>() {
				@Override
				public Class<String> getType() {
					return String.class;
				}

				@Override
				public String getValue(Item item, Object o, Object o1) {
					Consultation consultation =  ((ArrayList<Consultation>)item.getItemProperty("ochno").getValue()).get(dayIndx);
					return consultation.getName()==null?null:consultation.getName();
				}
			});
			container.addGeneratedProperty("zaochno"+i, new PropertyValueGenerator<String>() {
				@Override
				public Class<String> getType() {
					return String.class;
				}

				@Override
				public String getValue(Item item, Object o, Object o1) {
					Consultation consultation =  ((ArrayList<Consultation>)item.getItemProperty("zaochno").getValue()).get(dayIndx);
					return consultation.getName()==null?null:consultation.getName();
				}
			});
			container.addGeneratedProperty("oncology"+i, new PropertyValueGenerator<String>() {
				@Override
				public Class<String> getType() {
					return String.class;
				}

				@Override
				public String getValue(Item item, Object o, Object o1) {
					Consultation consultation =  ((ArrayList<Consultation>)item.getItemProperty("oncology").getValue()).get(dayIndx);
					return consultation.getName()==null?null:consultation.getName();
				}
			});
			grid.getColumn("rs"+i).setHeaderCaption("Радиохирургия");
			grid.getColumn("ochno"+i).setHeaderCaption("Очная");
			grid.getColumn("zaochno"+i).setHeaderCaption("Заочная");
			grid.getColumn("oncology"+i).setHeaderCaption("Онкология");
			Date consulDate = ((ConsultationDays)container.getIdByIndex(0)).getRs().get(dayIndx).getProcbegintime();
			row.join("rs"+i,"ochno"+i,"zaochno"+i,"oncology"+i).setText(format.format(consulDate));
		}
		grid.setFrozenColumnCount(1);
		grid.setSizeFull();
		grid.setHeight("100%");
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		//Grid.HeaderRow row = grid.addHeaderRowAt(0);
		DateRenderer renderer = new DateRenderer("%1$tH:%tM", Locale.getDefault());
		grid.getColumn("time").setRenderer(renderer);
		grid.removeColumn("rs");
		grid.removeColumn("ochno");
		grid.removeColumn("zaochno");
		grid.removeColumn("oncology");
		grid.removeColumn("other");
	}

	private void initLayoutContent() {
		hideZaohcno = new CheckBox("Заочные");
		hideZaohcno.addValueChangeListener(valueChangeEvent -> {
			if (hideZaohcno.getValue()) {
				for (int i=0;i<daysCount;i++) {
					grid.getColumn("zaochno"+i).setHidden(true);
				}
			} else {
				for (int i=0;i<daysCount;i++) {
					grid.getColumn("zaochno"+i).setHidden(false);
				}
			}
		});
		hideOhcno = new CheckBox("Очные");
		hideOhcno.addValueChangeListener(valueChangeEvent -> {
			if (hideOhcno.getValue()) {
				for (int i=0;i<daysCount;i++) {
					grid.getColumn("ochno"+i).setHidden(true);
				}
			} else {
				for (int i=0;i<daysCount;i++) {
					grid.getColumn("ochno"+i).setHidden(false);
				}
			}
		});
		hideOncology = new CheckBox("Онкология");
		hideOncology.addValueChangeListener(valueChangeEvent -> {
			if (hideOncology.getValue()) {
				for (int i=0;i<daysCount;i++) {
					grid.getColumn("oncology"+i).setHidden(true);
				}
			} else {
				for (int i=0;i<daysCount;i++) {
					grid.getColumn("oncology"+i).setHidden(false);
				}
			}
		});
		hideRS = new CheckBox("Радиохирургия");
		hideRS.addValueChangeListener(valueChangeEvent -> {
			if (hideRS.getValue()) {
				for (int i=0;i<daysCount;i++) {
					grid.getColumn("rs"+i).setHidden(true);
				}
			} else {
				for (int i=0;i<daysCount;i++) {
					grid.getColumn("rs"+i).setHidden(false);
				}
			}
		});
		HorizontalLayout horizontalLayout = new HorizontalLayout(hideRS,hideOhcno,hideZaohcno,hideOncology);
		horizontalLayout.setSpacing(true);
		VerticalLayout verticalLayout = new VerticalLayout(horizontalLayout,grid);
		grid.setHeight(780,Unit.PIXELS);

		setCompositionRoot(verticalLayout);
	}
}

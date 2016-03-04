package lgk.nsbc.ru.view;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import lgk.nsbc.ru.TextFieldRenderer;
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
					//return "<p>This is a paragraph</p>\n" +
					//	"<p>This is another paragraph</p> <b> WOW SEXY </b> <br> HEY";
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
			grid.getColumn("rs"+i).setHeaderCaption("Радиохирургия").setRenderer(new TextFieldRenderer<String>());
			grid.getColumn("ochno"+i).setHeaderCaption("Очная").setRenderer(new TextFieldRenderer<String>());
			grid.getColumn("zaochno"+i).setHeaderCaption("Заочная").setRenderer(new TextFieldRenderer<String>());
			grid.getColumn("oncology"+i).setHeaderCaption("Онкология").setRenderer(new TextFieldRenderer<String>());
			Date consulDate = ((ConsultationDays)container.getIdByIndex(0)).getRs().get(dayIndx).getProcbegintime();
			row.join("rs"+i,"ochno"+i,"zaochno"+i,"oncology"+i).setText(format.format(consulDate));
		}
		container.removeContainerProperty("rs");
		container.removeContainerProperty("ochno");
		container.removeContainerProperty("zaochno");
		container.removeContainerProperty("oncology");
		container.removeContainerProperty("other");
		/*grid.setCellStyleGenerator(cell -> {
			/*if (cell.getProperty().getValue() instanceof String)
			if (!((String)cell.getProperty().getValue()).startsWith("пон")) {
				return "exp1";
			}
			return "exp1";
			return "exp1cell";
		});*/
		grid.setRowStyleGenerator(row1 -> {
			if (((String)row1.getItem().getItemProperty("rs0").getValue()).startsWith("<p>This is")) {
				return "exp1";
			}
			return null;
		});
		//grid.getColumn("rs1").setRenderer(new TextFieldRenderer<String>());
		//grid.getColumn("rs0").setRenderer(new HtmlRenderer());
		grid.setFrozenColumnCount(1);
		grid.setSelectionMode(Grid.SelectionMode.NONE);
		DateRenderer renderer = new DateRenderer("%1$tH:%tM", Locale.getDefault());
		grid.getColumn("time").setRenderer(renderer);
		grid.setSizeFull();
		grid.setHeightMode(HeightMode.ROW);
		grid.setHeightByRows(container.size());
		//grid.getHeaderRow(0).setStyleName("simplerow");
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
		setCompositionRoot(verticalLayout);
	}
}

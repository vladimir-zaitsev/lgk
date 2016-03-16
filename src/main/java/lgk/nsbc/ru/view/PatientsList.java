package lgk.nsbc.ru.view;

import com.vaadin.data.util.converter.StringToDateConverter;
import com.vaadin.ui.Grid;
import lgk.nsbc.ru.backend.entity.Patient;
import lgk.nsbc.ru.model.PatientsModel;
import lgk.nsbc.ru.model.PatientsModelImpl;

import java.text.DateFormat;
import java.util.Locale;

import static lgk.nsbc.ru.backend.entity.Patient.Props.*;

public class PatientsList extends AbstractView<PatientsModel> {

	public final Grid listGrid = new Grid("Список пациентов", model.getPatientsList());
	{
		listGrid.setSizeFull();
		listGrid.setColumnOrder(n.toString(), surname.toString(), name.toString(), patronymic.toString(), sex.toString(), birthday.toString());

		for (Patient.Props prop: Patient.Props.values()) {
			Grid.Column col = listGrid.getColumn(prop.toString());
			String caption = model.getCaption(Patient.relationName, prop.toString());
			if(caption != null) {
				col.setHeaderCaption(caption);
			}
		}

		StringToDateConverter dateConverter = new StringToDateConverter(){
			@Override
			protected DateFormat getFormat(Locale locale) {
				return DateFormat.getDateInstance();
			}
		};
		Grid.Column birthdayCol = listGrid.getColumn(birthday.toString());
		birthdayCol.setConverter(dateConverter);
	}

	public PatientsList(PatientsModelImpl patientsModel) {
		super(patientsModel);
		setCompositionRoot(listGrid);
	}
}

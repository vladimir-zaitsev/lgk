package lgk.nsbc.ru.view;

import com.vaadin.ui.Grid;
import lgk.nsbc.ru.model.PatientsModel;
import lgk.nsbc.ru.model.PatientsModelImpl;

public class PatientsList extends AbstructView<PatientsModel> {

	public final Grid listGrid = new Grid("Список пациентов", model.getPatientsList());

	public PatientsList(PatientsModelImpl patientsModel) {
		super(patientsModel);
	}
}

package lgk.nsbc.ru.model;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import lgk.nsbc.ru.backend.entity.Patient;

public class PatientsModelImpl implements PatientsModel {
	public final BeanItemContainer<Patient> patientsList = new BeanItemContainer<>(Patient.class);

	@Override
	public Container.Indexed getPatientsList() {
		return patientsList;
	}
}

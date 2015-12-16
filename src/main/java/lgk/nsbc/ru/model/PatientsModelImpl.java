package lgk.nsbc.ru.model;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import lgk.nsbc.ru.backend.I18nManager;
import lgk.nsbc.ru.backend.entity.Patient;

public class PatientsModelImpl implements PatientsModel {
	private final I18nManager i18n;

	public PatientsModelImpl(I18nManager i18n) {
		this.i18n = i18n;
	}

	public final BeanItemContainer<Patient> patientsList = new BeanItemContainer<>(Patient.class);

	@Override
	public Container.Indexed getPatientsList() {
		return patientsList;
	}

	@Override
	public String getCaption(String relationName, String fieldName) {
		return i18n.getCaption(relationName, fieldName);
	}
}

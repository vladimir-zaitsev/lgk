package lgk.nsbc.ru.model;

import com.vaadin.data.Container;

public interface PatientsModel {
	Container.Indexed getPatientsList();

	String getCaption(String relationName, String s);
}

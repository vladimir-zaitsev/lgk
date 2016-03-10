package lgk.nsbc.ru.backend;

import  lgk.nsbc.ru.backend.entity.Patient;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.UnsupportedFilterException;

import java.util.ArrayList;
import java.util.List;


public class PatientContainer extends BeanItemContainer<Patient> {


	private List<Patient> patients = new ArrayList<>();

	private PatientsManager patientManager = new PatientsManager();

	public PatientContainer() throws IllegalArgumentException {
		super(Patient.class);

	}

	/*
	*
	*
	*/
	@Override
	protected void addFilter(Filter filter) throws UnsupportedFilterException {
		SuggestionFilter suggestionFilter = (SuggestionFilter) filter;
		filterItems(suggestionFilter.getFilterString());
	}

	/*
	*
	*
	 */
	private void filterItems(String filterString)
	{
		removeAllItems();
		if (filterString.length() >= 3) {
			patients = patientManager.listPatients(filterString);
		}
		addAll(patients);
	}

	/**
	 *
	 *
	 *
	 */
	public void setSelectedPatientBean(Patient patientBean) {
		removeAllItems();
		addBean(patientBean);

	}

	/**
	 *
	 *
	 *
	 *
	 *
	 */
	public static class SuggestionFilter implements Container.Filter {

		private String filterString;

		public SuggestionFilter(String filterString) {
			this.filterString = filterString;
		}

		public String getFilterString() {
			return filterString;
		}

		@Override
		public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {

			return false;
		}

		@Override
		public boolean appliesToProperty(Object propertyId) {

			return false;
		}
	}
}

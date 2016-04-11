package lgk.nsbc.ru.backend;

import com.vaadin.data.util.BeanItem;
import  lgk.nsbc.ru.backend.entity.Patient;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.UnsupportedFilterException;

import java.util.ArrayList;
import java.util.List;


public class PatientContainer extends BeanItemContainer<Patient> {

	protected Object missingBoxValue;
	private List<Patient> patients = new ArrayList<>();
	private PatientsManager patientsManager;

	public PatientContainer(PatientsManager patientsManager) throws IllegalArgumentException {
		super(Patient.class);
		this.patientsManager = patientsManager;
	}

	@Override
	protected void addFilter(Filter filter) throws UnsupportedFilterException {
		SuggestionFilter suggestionFilter = (SuggestionFilter) filter;
		filterItems(suggestionFilter.getFilterString());
	}

	private void filterItems(String filterString)
	{
		removeAllItems();
		if (filterString.length() >= 3) {
			patients = patientsManager.listPatients(filterString);
		}
		addAll(patients);
	}

	public void setSelectedPatientBean(Patient patientBean) {
		removeAllItems();
		addBean(patientBean);

	}

	@Override
	public boolean containsId(Object itemId) {
		boolean containsFlag = super.containsId(itemId);
		if (!containsFlag) {
			missingBoxValue = itemId;
		}
		return true;
	}

	@Override
	public List<Patient> getItemIds() {
		List<Patient> itemIds = super.getItemIds();
		if (missingBoxValue != null && !itemIds.contains(missingBoxValue)) {
			List<Patient> newItemIds = new ArrayList<>(itemIds);
			newItemIds.add((Patient) missingBoxValue);
			for (Patient itemId : itemIds) {
				newItemIds.add(itemId);
			}
			itemIds = newItemIds;
		}

		return itemIds;
	}

	@Override
	public BeanItem<Patient> getItem(Object itemId) {
		if (missingBoxValue == itemId) {
			return new BeanItem(itemId);
		}

		return super.getItem(itemId);
	}

	@Override
	public int size() {
		int size = super.size();
		if (missingBoxValue != null) {
			size++;
		}
		return size;
	}

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

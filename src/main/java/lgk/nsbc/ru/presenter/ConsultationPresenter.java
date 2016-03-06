package lgk.nsbc.ru.presenter;

import lgk.nsbc.ru.backend.ConsultationManager;
import lgk.nsbc.ru.backend.basicevent.ConsultationBasicEvent;
import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.model.ConsultationModel;
import lgk.nsbc.ru.view.EditConsultationForm;

import java.util.*;

/**
 * Created by user on 20.02.2016.
 */
public class ConsultationPresenter {


	public final ConsultationModel consultationModel;

	public final ConsultationManager consultationManager;

	public ConsultationBasicEvent consultationBasicEvent;

	public EditConsultationForm editConsultationForm;

	private List<Consultation> consultations = new ArrayList<>();

	private  List<Consultation> patient = new ArrayList<>();

	private List<String> executor = new ArrayList<>(Arrays.asList("физик", "онколог", "планировщик", "врач", "лечащий врач"));


	public ConsultationPresenter(ConsultationModel consultationModel, ConsultationManager consultationManager) {
		this.consultationModel = consultationModel;
		this.consultationManager = consultationManager;
	}

	public void start() {

		GregorianCalendar calendar = new GregorianCalendar(2016, 1, 1);
		Date startDay = calendar.getTime();
		calendar.add(calendar.MONTH, 1);
		Date endDay = calendar.getTime();


		consultations = new ArrayList<>(consultationManager.listConsultation(startDay, endDay));

		for (int i = 0; i < consultations.size(); i++) {
			Random random = new Random();
			int value = random.nextInt(executor.size());
			consultationBasicEvent = new ConsultationBasicEvent("Радиохирургия", "Some description.", consultations.get(i),
				executor.get(value));
			consultationBasicEvent.setStyleName("mycolor");
			consultationBasicEvent.getStart().setHours(9);
			consultationBasicEvent.getEnd().setHours(18);
			consultationModel.beanItemContainer.addBean(consultationBasicEvent);
		}

	}

    /*
    @Override
    public void clickCheck(ConsultationBasicEvent consultationBasicEvent)
    {
        String name = consultationBasicEvent.getName();
        String surname = consultationBasicEvent.getSurname();
        String  patronymic = consultationBasicEvent.getPatronymic();
        int case_history_num = consultationBasicEvent.getCase_history_num();
        patient= new ArrayList<>(consultationManager.listpatient(name, surname, patronymic, case_history_num));
        if(patient.size() == 0)
        {
        }
        */





}
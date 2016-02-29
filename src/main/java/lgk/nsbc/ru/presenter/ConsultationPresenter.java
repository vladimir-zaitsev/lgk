package lgk.nsbc.ru.presenter;

import com.vaadin.data.util.BeanItemContainer;
import lgk.nsbc.ru.backend.ConsultationFactory;
import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.ConsultationDays;
import lgk.nsbc.ru.model.ConsultationModel;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Роман on 29.02.2016.
 */
public class ConsultationPresenter {
	private final ConsultationModel consultationModel;

	public ConsultationPresenter(ConsultationModel consultationModel) {
		this.consultationModel = consultationModel;
	}

	public void start() {
		// Забабахаю недельку консультаций.
		BeanItemContainer<ConsultationDays> beanItemContainer = consultationModel.getBeanItemContainer();
		for (int i=0;i<18;i++) {
			ConsultationDays consultationDays = new ConsultationDays();
			Date date = new Date();
			date.setHours(9 + i / 2);
			date.setMinutes(30 * (i % 2));
			consultationDays.setTime(date);
			ArrayList<Consultation> rs = new ArrayList<>();
			ArrayList<Consultation> ochno = new ArrayList<>();
			ArrayList<Consultation> oncology = new ArrayList<>();
			ArrayList<Consultation> zaochno = new ArrayList<>();
			for (int j=0;j<5;j++) {
				Date startDate = new Date();
				startDate.setSeconds(0);
				startDate.setMonth(1);
				startDate.setDate(j+1);
				startDate.setHours(9 + i / 2);
				startDate.setMinutes(30 * (i % 2));
				Date endDate = new Date(startDate.getTime());
				endDate.setMinutes(30 * ((i + 1) % 2));
				endDate.setHours(9 + (i + 1) / 2);
				rs.add(ConsultationFactory.getConsultation(startDate,endDate));
				ochno.add(ConsultationFactory.getConsultation(startDate,endDate));
				oncology.add(ConsultationFactory.getConsultation(startDate,endDate));
				zaochno.add(ConsultationFactory.getConsultation(startDate,endDate));
			}
			consultationDays.setRs(rs);
			consultationDays.setOchno(ochno);
			consultationDays.setOncology(oncology);
			consultationDays.setZaochno(zaochno);
			beanItemContainer.addBean(consultationDays);
		}
	}
}

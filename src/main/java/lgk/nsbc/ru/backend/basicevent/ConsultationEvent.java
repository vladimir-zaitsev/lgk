package lgk.nsbc.ru.backend.basicevent;

import lgk.nsbc.ru.backend.entity.Consultation;
import com.vaadin.ui.components.calendar.event.BasicEvent;
import lgk.nsbc.ru.backend.entity.Patient;

import java.util.Date;

/**
 * Created by user on 20.02.2016.
 */
public class ConsultationEvent extends BasicEvent {

	private Consultation consultation = new Consultation();

	public ConsultationEvent() {}

	public ConsultationEvent(Consultation consultation, String description) {
		super(consultation.getSurname(),description, consultation.getProcbegintime(), consultation.getProcendtime());
		this.consultation = consultation;
	}

	public ConsultationEvent(Consultation consultation) {
		super(consultation.getSurname(),"", consultation.getProcbegintime(), consultation.getProcendtime());
		this.consultation = consultation;
	}


	public Long getN()
	{
		return consultation.getN();
	}

	public void  setN (Long n)
	{
		consultation.setN(n);
		fireEventChange();
	}
	public Date getBirthday() {
		return consultation.getBirthday();
	}

	public void setBirthday(Date birthday) {
		consultation.setBirthday(birthday);
	}

	public Integer getCase_history_num() {
		return consultation.getCase_history_num();
	}

	public void setCase_history_num(Integer cas_history_num) {
		consultation.setCase_history_num(cas_history_num);
		fireEventChange();
	}

	public int getDiagnosis() {
		return consultation.getDiagnosis();
	}

	public void setDiagnosis(int diagnosis) {
		consultation.setDiagnosis(diagnosis);
		fireEventChange();
	}

	public String getName() {
		return consultation.getName();
	}

	public void setName(String name) {
		consultation.setName(name);
		fireEventChange();
	}


	public String getPatronymic() {
		return consultation.getPatronymic();
	}

	public void setPatronymic(String patronymic) {
		consultation.setPatronymic(patronymic);
		fireEventChange();
	}


	public String getSurname() {return consultation.getSurname();}

	public void setSurname(String surname) {
		consultation.setSurname(surname);
		// Пусть в качестве caption будет фамилия
		setCaption(surname);
		fireEventChange();
	}


	public void setNewPatient(Patient patient) {
		consultation.setNewPatient(patient);
	}

	public Patient getCurrentPatient() {
		return consultation.getCurrentPatient();
	}

	public Consultation getConsultation() {
		return consultation;
	}
}
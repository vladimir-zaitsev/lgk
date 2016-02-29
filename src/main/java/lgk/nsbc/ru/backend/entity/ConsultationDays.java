package lgk.nsbc.ru.backend.entity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Роман on 29.02.2016.
 */
public class ConsultationDays {
	// Размер массива соответствует отображаемому количеству дней
	// Время статично для всех дней (При условии СУЩЕСТВОВАНИЯ статичной сетки времени)
	private Date time;
	private ArrayList<Consultation> rs;
	private ArrayList<Consultation> ochno;
	private ArrayList<Consultation> oncology;
	private ArrayList<Consultation> zaochno;
	// Другие врачи
	private ArrayList<ArrayList<Consultation>> other;

	public ConsultationDays() {
	}



	public ArrayList<Consultation> getOchno() {
		return ochno;
	}

	public void setOchno(ArrayList<Consultation> ochno) {
		this.ochno = ochno;
	}

	public ArrayList<Consultation> getOncology() {
		return oncology;
	}

	public void setOncology(ArrayList<Consultation> oncology) {
		this.oncology = oncology;
	}

	public ArrayList<ArrayList<Consultation>> getOther() {
		return other;
	}

	public void setOther(ArrayList<ArrayList<Consultation>> other) {
		this.other = other;
	}

	public ArrayList<Consultation> getRs() {
		return rs;
	}

	public void setRs(ArrayList<Consultation> rs) {
		this.rs = rs;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public ArrayList<Consultation> getZaochno() {
		return zaochno;
	}

	public void setZaochno(ArrayList<Consultation> zaochno) {
		this.zaochno = zaochno;
	}
}

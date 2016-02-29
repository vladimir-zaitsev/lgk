package lgk.nsbc.ru.backend.entity;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Роман on 28.02.2016.
 */
// Абсолютно всё равно на то, правильно ли это, или нет. Сделаю так, как позволяет воображение
public class ConsultationDay {
	private Date time;
	private Consultation rs;
	private Consultation ochno;
	private Consultation oncology;
	private Consultation zaochno;
	private ArrayList<Consultation> other;

	public ConsultationDay(Date time, Consultation rs, Consultation ochno, Consultation oncology, Consultation zaochno, ArrayList<Consultation> other) {
		this.time = time;
		this.rs = rs;
		this.ochno = ochno;
		this.oncology = oncology;
		this.zaochno = zaochno;
		this.other = other;
	}

	public Consultation getOchno() {
		return ochno;
	}

	public void setOchno(Consultation ochno) {
		this.ochno = ochno;
	}

	public Consultation getOncology() {
		return oncology;
	}

	public void setOncology(Consultation oncology) {
		this.oncology = oncology;
	}

	public ArrayList<Consultation> getOther() {
		return other;
	}

	public void setOther(ArrayList<Consultation> other) {
		this.other = other;
	}

	public Consultation getRs() {
		return rs;
	}

	public void setRs(Consultation rs) {
		this.rs = rs;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Consultation getZaochno() {
		return zaochno;
	}

	public void setZaochno(Consultation zaochno) {
		this.zaochno = zaochno;
	}
}

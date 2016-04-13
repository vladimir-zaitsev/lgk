package lgk.nsbc.ru.backend.entity;

import java.util.Date;


public class Consultation
{
	private Patient patient = new Patient();
	/** Оставлю тут заметочку о том, что в ConsultaionEvent будут другие даты. По всей
	 * видимости будут нужны два набора дат, те, которые будут в календаре, и те, что
	 * вытянуты из базы.
	 * */
	private Date procbegintime;
	private Date procendtime;
	private Long n;
	public Consultation() {}

	public Consultation(Patient patient, Date procbegintime, Date procendtime) {
		this.patient = patient;
		this.procbegintime = procbegintime;
		this.procendtime = procendtime;
	}

	public void setN (Long n)
	{
		this.n = n;
	}

	public Long getN ()
	{
		return n;
	}
	public Date getProcbegintime() {
		return procbegintime;
	}

	public void setProcbegintime(Date procbegintime) {
		this.procbegintime = procbegintime;
	}

	public Date getProcendtime() {
		return procendtime;
	}

	public void setProcendtime(Date procendtime) {
		this.procendtime = procendtime;
	}

	public String getSurname() {
		return patient.getSurname();
	}

	public void setSurname(String surname) {
		patient.setSurname(surname);
	}

	public String getName() {
		return patient.getName();
	}

	public void setName(String name) {
		patient.setName(name);
	}

	public String getPatronymic() {
		return patient.getPatronymic();
	}

	public void setPatronymic(String patronymic) {
		patient.setPatronymic(patronymic);
	}

	public Integer getCase_history_num() {
		return patient.getCase_history_num();
	}

	public void setCase_history_num(Integer case_history_num) {
		patient.setCase_history_num(case_history_num);
	}

	public int getDiagnosis() {
		return patient.getDiagnosis();
	}

	public void setDiagnosis(int diagnosis) {
		patient.setDiagnosis(diagnosis);
	}

	public Date getBirthday() {return patient.getBirthday();}

	public void setBirthday(Date birthday) { patient.setBirthday(birthday);}

	public void setNewPatient(Patient patient) {
		this.patient = patient;
	}

	public Patient getCurrentPatient() {
		return patient;
	}

	@Override
	public String toString() {
		return "Consultation{" +
			"n=" + getN()+
			", procbegintime=" + getProcbegintime() +
			", procendtime=" + getProcendtime() +
			", surname='" + getSurname() + '\'' +
			", name='" + getName() + '\'' +
			", patronymic='" + getPatronymic() + '\'' +
			", cas_history_num='" + getCase_history_num() + '\'' +
			", diagnosis='" + getDiagnosis() + '\'' +
			", birthday=" + getBirthday() +
			'}';
	}

}
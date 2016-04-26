package lgk.nsbc.ru.backend.entity;

import java.util.Date;

public class Patient {

	public enum Props
	{
		n
		,op_create
		,nbc_organizations_n
		,nbc_staff_n
		,case_history_num
		,case_history_date
		,bas_people_n
		,represent
		,represent_telephone
		,diagnosis
		,nbc_diagnosis_n
		,full_diagnosis
		,stationary
		,allergy
		,information_source
		,folder
		,disorder_history
		,nbc_diag_2015_n
		,nbc_diag_loc_n
	}
	private People people = new People();
	private Long n;
	private Integer case_history_num;
	private int diagnosis;
	private int nbc_organizations_n;

	public Patient() {}

	public Patient(People people,Integer case_history_num,int nbc_organizations_n)
	{
		this.people = people;
		this.case_history_num = case_history_num;
		this. nbc_organizations_n = nbc_organizations_n;


	}

	public Long getN() {
		return n;
	}

	public void setN(Long n) { this.n = n; }

	public Integer getCase_history_num() {
		return case_history_num;
	}

	public void setCase_history_num(Integer case_history_num) {
		this.case_history_num = case_history_num;
	}

	public int  getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(int diagnosis) {
		this.diagnosis = diagnosis;
	}

	public int getNbc_organizations_n() {
		return nbc_organizations_n;
	}

	public void setNbc_organizations_n(int nbc_organizations_n) {
		this.nbc_organizations_n = nbc_organizations_n;
	}

	public String getName() {
		return people.getName();
	}

	public void setName(String name) {
		people.setName(name);
	}

	public String getSurname() {
		return people.getSurname();
	}

	public void setSurname(String surname) {
		people.setSurname(surname);
	}

	public String getPatronymic() {
		return people.getPatronymic();
	}

	public void setPatronymic(String patronymic) {
		people.setPatronymic(patronymic);
	}

	public String getSex() {
		return people.getSex();
	}

	public void setSex(String sex) {
		people.setSex(sex);
	}


	public Date getBirthday() {
		return people.getBirthday();
	}

	public void setBirthday(Date birthday) {
		people.setBirthday(birthday);
	}

	public People getCurrentPeople()
	{
		return  people;
	}
	public static String getRelationName() {
		return relationName;
	}


	@Override
	public String toString() {
		return "Patient{" +
			"n=" + n +
			", surname='" + getSurname() + '\'' +
			", name='" + getName() + '\'' +
			", patronymic='" + getPatronymic() + '\'' +
			", cas_history_num='" + getCase_history_num() + '\'' +
			", diagnosis='" + getDiagnosis() + '\'' +
			", birthday='" + getBirthday() + '\''+
			", nbc_organizations_n=" + nbc_organizations_n +
			'}';
	}

	public static final String relationName = "BAS_PEOPLE";
}

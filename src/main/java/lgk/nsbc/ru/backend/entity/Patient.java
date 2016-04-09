package lgk.nsbc.ru.backend.entity;

import java.util.Date;

public class Patient {
	public enum Props{
		n
		,name
		,surname
		,patronymic
		,sex
		,birthday
	}

	private People people = new People();
	private String name; //(255)
	private String surname; //(255)
	private String patronymic; //(255)
	private String sex;
	private Date birthday;
	private Integer case_history_num;
	private String diagnosis;

	public Patient() {}

	public Patient(String name, String surname, String patronymic, Date birthday, Integer case_history_num, String sex) {
		this.name = name;
		this.surname = surname;
		this.patronymic = patronymic;
		this.birthday = birthday;
		this.case_history_num = case_history_num;
		this.sex = sex;
	}

	public Long getN() {
		return people.getN();
	}

	public void setN(Long n) {
		people.setN(n);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getPatronymic() {
		return patronymic;
	}

	public void setPatronymic(String patronymic) {
		this.patronymic = patronymic;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Integer getCase_history_num() {
		return case_history_num;
	}

	public void setCase_history_num(Integer case_history_num) {
		this.case_history_num = case_history_num;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	@Override
	public String toString() {
		return "Patient{" +
			"n=" + getN() +
			", name='" + getName() + '\'' +
			", surname='" + getSurname() + '\'' +
			", patronymic='" + getPatronymic() + '\'' +
			", sex='" + getSex() + '\'' +
			", birthday=" + getBirthday() +
			"}\n";
	}

	public static final String relationName = "BAS_PEOPLE";
}

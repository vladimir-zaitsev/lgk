package lgk.nsbc.ru.backend.entity;

import java.util.Date;

/**
 * Created by user on 08.04.2016.
 */
public class People {

	private Long n;
	private String name;
	private String surname;
	private String patronymic;
	private String sex;
	private Date birthday;

	public People() { }

	public Long getN() { return n; }

	public void setN(Long n)
	{
		this.n = n;
	}

	public String getName() { return name; }

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

	@Override
	public String toString() {
		return "People{" +
			"n=" + n +
			", name='" + name + '\'' +
			", surname='" + surname + '\'' +
			", patronymic='" + patronymic + '\'' +
			", sex='" + sex + '\'' +
			", birthday=" + birthday +
			'}';
	}

}

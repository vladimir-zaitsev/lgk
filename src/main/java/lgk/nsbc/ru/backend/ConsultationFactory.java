package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.entity.Consultation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by Роман on 28.02.2016.
 */

// Class generate some fake data (for testing)
public class ConsultationFactory {
	private static ArrayList<String> diagnoses;
	private static ArrayList<String> patronymics = new ArrayList<>(Arrays.asList("Евгеньевич",
		"Петрович","Андреевич","Сергеевич","Анатольевич","Романович","Игоревич","Васильевич","Иванович"));
	private static ArrayList<String> firstName = new ArrayList<>(Arrays.asList("Роман","Андрей","Василий",
		"Евгений","Игорь","Кадзуто","Сергей","Леонид","Каанд","Груга","Фредерик","Антоха","ПожирательЛун","Колбас",
		"Зуд","Вася","Кот"));
	private static ArrayList<String> lastName = new ArrayList<>(Arrays.asList("Котанов","Борисов","Плугин","Поджаркин"
		,"Анавонет","ШолторПолтор","Убийцев","Шоколадкин","Лужан","Лужко","Картошкин"));

	static {
		try (Connection connection = DB.getConnection()) {
			// Тянем диагнозы из базы
			diagnoses = new ArrayList<>();
			Statement state =  connection.createStatement();
			ResultSet rs = state.executeQuery("SELECT TEXT FROM nbc_patients_diagnosis");
			while (rs.next()) {
				diagnoses.add(rs.getString("TEXT"));
			}
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static Consultation getConsultation(Date startDate,Date endDate) {
		Random generator = new Random();
		Date birthday = generateRandomBirthday();
		Integer randCase =  generator.nextInt(9324);
		String diagnos = diagnoses.get(generator.nextInt(diagnoses.size()-1));
		String patronumic = patronymics.get(generator.nextInt(patronymics.size()-1));
		String lastname = lastName.get(generator.nextInt(lastName.size()-1));
		String firstname = firstName.get(generator.nextInt(firstName.size()-1));
		Consultation consultation = new Consultation(birthday,randCase,diagnos,patronumic,firstname,startDate,endDate,lastname);
		return consultation;
	}

	private static Date generateRandomBirthday() {
		Date birthday = new Date();
		Random generator = new Random();
		birthday.setYear(1950+generator.nextInt(50));
		birthday.setMonth(generator.nextInt(11));
		birthday.setDate(generator.nextInt(29));
		return birthday;
	}
}

package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.Patient;
import lgk.nsbc.ru.backend.entity.People;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by user on 11.04.2016.
 */
public class InsertManager {

	private final ConsultationEvent consultationEvent;
	private final HeadManager headManager;

	public  InsertManager(ConsultationEvent consultationEvent,HeadManager headManager)
	{
		this.consultationEvent = consultationEvent;
		this.headManager = headManager;
	}
	/* Добавить данные  в таблицы (nbc_patients, nbc_proc, bas_people) для пациента, который не существует в базе
	* в течении одной транзакции
	*/
	public void insertData() {
		try (Connection con = DB.getConnection())
		{
			con.setAutoCommit(false);


			People people = consultationEvent.getCurrentPatient().getCurrentPeople();
			Long genIdPeople = headManager.getGeneratorManager().genIdPeople();
			// Устанавливаем ID человеку
			consultationEvent.getConsultation().getCurrentPatient().getCurrentPeople().setN(genIdPeople);
			// Генерируем ID операции
			Long genIdOperPeople = headManager.getGeneratorManager().genIdOperation();
			headManager.getPeopleManager().insertPeople(con, people, genIdPeople,genIdOperPeople);
			headManager.getRegistrationManager().registrOperPeople(con,genIdOperPeople);

			Patient patient = consultationEvent.getCurrentPatient();
			Long genIdPatient = headManager.getGeneratorManager().genIdPatient();
			// Устанавливаем ID для пациента
			consultationEvent.getConsultation().getCurrentPatient().setN(genIdPatient);
			Long genIdOperPatient = headManager.getGeneratorManager().genIdOperation();
			headManager.getPatientsManager().insertPatient(con, patient, genIdPeople, genIdPatient,
				genIdOperPatient);
			headManager.getRegistrationManager().registrOperPatients(con,genIdOperPatient);

			Consultation consultation = consultationEvent.getConsultation();
			Long genIdConsultation = headManager.getGeneratorManager().genIdConsultation();
			// Устанавливаем ID для консультации
			consultationEvent.getConsultation().setN(genIdConsultation);

			Long genIdOperConsult = headManager.getGeneratorManager().genIdOperation();
			headManager.getConsultationManager().insertConsultation(con, consultation, genIdConsultation,
				genIdOperConsult,genIdPatient);
			headManager.getRegistrationManager().registrOperConsultation(con,genIdOperConsult);

			con.commit();
		} catch (SQLException e)
		{
			throw new IllegalStateException(e);
		}
	}

	/*
	* Добавление данных в таблицу nbc_proc для существующего пациента в базе
	 */
	public void insertConsultation()
	{
		try (Connection con = DB.getConnection())
		{
			con.setAutoCommit(false);

			Consultation consultation = consultationEvent.getConsultation();
			Long genIdConsultation = headManager.getGeneratorManager().genIdConsultation();
			// Устанавливаем ID для консультации
			 consultationEvent.getConsultation().setN(genIdConsultation);

			Long genIdOperConsult =headManager.getGeneratorManager().genIdOperation();
			headManager.getConsultationManager().insertConsultation(con, consultation, genIdConsultation,
				genIdOperConsult,consultationEvent.getCurrentPatient().getN());
			headManager.getRegistrationManager().registrOperConsultation(con,genIdOperConsult);

			con.commit();
		} catch (SQLException e)
		{
			throw new IllegalStateException(e);
		}
	}
}

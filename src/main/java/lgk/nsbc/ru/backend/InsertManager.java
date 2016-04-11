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
	/* Добавить данные  в таблицы (nbc_patients, nbc_proc, bas_people)
	* в течении одной транзакции
	*/
	public void insertData() {
		try (Connection con = DB.getConnection())
		{
			con.setAutoCommit(false);

			People people = consultationEvent.getCurrentPatient().getCurrentPeople();
			Long genIdPeople = headManager.getGeneratorManager().genIdPeople();
			Long genIdOperPeople = headManager.getGeneratorManager().genIdOperation();
			headManager.getPeopleManager().insertPeople(con, people, genIdPeople,genIdOperPeople);
			headManager.getRegistrationManager().registrOperPeople(con,genIdOperPeople);

			Patient patient = consultationEvent.getCurrentPatient();
			Long genIdPatient = headManager.getGeneratorManager().genIdPatient();
			Long genIdOperPatient = headManager.getGeneratorManager().genIdOperation();
			headManager.getPatientsManager().insertPatient(con, patient, genIdPeople, genIdPatient,
				genIdOperPatient);
			headManager.getRegistrationManager().registrOperPatients(con,genIdOperPatient);

			Consultation consultation = consultationEvent.getConsultation();
			Long genIdConsultation = headManager.getGeneratorManager().genIdConsultation();
			Long genIdOperConsult =headManager.getGeneratorManager().genIdOperation();
			headManager.getConsultationManager().insertConsultation(con, consultation, genIdConsultation,
				genIdOperConsult,genIdPatient);
			headManager.getRegistrationManager().registrOperConsultation(con,genIdOperConsult);

			con.commit();
		} catch (SQLException e)
		{
			throw new IllegalStateException(e);
		}
	}
}

package lgk.nsbc.ru.backend;

import com.vaadin.ui.Notification;
import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.db.DB;
import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.Patient;
import lgk.nsbc.ru.backend.entity.People;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by user on 24.04.2016.
 */
public class ProcedureManager {

	private ConsultationEvent consultationEvent;
	private final PeopleManager peopleManager;
	private final PatientsManager patientsManager;
	private final  ConsultationManager consultationManager;

	public ProcedureManager(ConsultationEvent consultationEvent
		,PeopleManager peopleManager
		,PatientsManager patientsManager
		,ConsultationManager consultationManager
		)
	{
		this.consultationEvent = consultationEvent;
		this.peopleManager = peopleManager;
		this.patientsManager = patientsManager;
		this.consultationManager = consultationManager;
	}

	public void insertConsultation()
	{
		try (
			Connection con = DB.getConnection()
		) {
			con.setAutoCommit(false);

			People people = consultationEvent.getCurrentPatient().getCurrentPeople();
			peopleManager.insertPeople(con,people);

			Patient patient = consultationEvent.getCurrentPatient();
			patientsManager.insertPatient(con,patient);

			Consultation consultation = consultationEvent.getConsultation();
			consultationManager.insertConsultation(con,consultation);

			con.commit();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE,"Problems with Database",e);
			Notification.show("Problems with Database", Notification.Type.WARNING_MESSAGE);
		}

	}

	public  void  insertOnlyConsultation()
	{
		try (
			Connection con = DB.getConnection()
		) {
			con.setAutoCommit(false);

			Consultation consultation = consultationEvent.getConsultation();
			consultationManager.insertConsultation(con,consultation);

			con.commit();

		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE,"Problems with Database",e);
			Notification.show("Problems with Database", Notification.Type.WARNING_MESSAGE);
		}
	}


	public  void  updateConsultation()
	{



	}




}

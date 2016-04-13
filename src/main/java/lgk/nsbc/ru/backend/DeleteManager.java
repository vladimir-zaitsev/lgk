package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.basicevent.ConsultationEvent;
import lgk.nsbc.ru.backend.entity.Consultation;

import java.sql.Connection;
import java.sql.SQLException;


public class DeleteManager
{

	private final ConsultationEvent consultationEvent;
	private final HeadManager headManager;

	public DeleteManager (ConsultationEvent consultationEvent, HeadManager headManager)
	{
		this.consultationEvent = consultationEvent;
		this.headManager = headManager;
	}

	public void deleteConsul()
	{
		try (
			Connection con = DB.getConnection()
		) {
			con.setAutoCommit(false);

			Consultation consultation = consultationEvent.getConsultation();
            headManager.getConsultationManager().deleteConsultation(con,consultation);

			con.commit();
		}
		catch (SQLException e) {
			throw new IllegalStateException(e);
		}


	}
}

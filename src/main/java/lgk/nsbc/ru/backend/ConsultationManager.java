package lgk.nsbc.ru.backend;

import com.vaadin.ui.Notification;
import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.Patient;
import lgk.nsbc.ru.backend.entity.People;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ConsultationManager
{
	private  final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private final QueryRunner qr = new QueryRunner();

	public static final String procTableName = "nbc_proc";
	public static final String generatorName = "nbc_proc_n";
	public static final String commandInsert = "nbc_proc_put";
	public static final String commandDelete = "nbc_proc_del";
	private static final String genNameOperation = "sys_operation_n";
	private final String lgkSessId;

	public  ConsultationManager(String lgkSessId)
		{
		this.lgkSessId = lgkSessId;
	}


	public List<Consultation> listConsultation(Date fromDate, Date toDate) // дата будет браться от 01.01.2016 и 20.02.2016
	{
		try (
			Connection con = DB.getConnection()
		) {
			String sql =
				"SELECT nbc_proc.n,\n" +
					"procbegintime," +
					"procendtime,\n" +
					"surname," +
					"name," +
					"patronymic,\n" +
					"case_history_num,\n" +
					"diagnosis,"+
					"birthday\n" +
					"FROM bas_people\n" +
					"JOIN nbc_patients  on  bas_people.n = nbc_patients.bas_people_n\n" +
					"LEFT JOIN  nbc_proc on  nbc_proc.nbc_patients_n = nbc_patients.n\n" +
					"WHERE nbc_proc.proc_type = 4\n" +
					"AND nbc_proc.procbegintime between ? and ?\n" +
					"AND nbc_proc.procendtime is not NULL";

			BeanListHandler<Consultation> handler = new BeanListHandler<>(Consultation.class);
			return qr.query(con, sql, handler
				, new java.sql.Timestamp(fromDate.getTime())
				, new java.sql.Timestamp(toDate.getTime())
			);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * Добавление консультации в таблицу nbc_proc
	 * @param con,consultation,genIdPatient
	 **/
	public void insertConsultation(
		Connection con
		,Consultation consultation
	) throws SQLException
	{

		String sql =
			"INSERT INTO "+procTableName+"\n"+
		    "( " +
		    Consultation.Props.n.toString()+",\n"+
		    Consultation.Props.op_create.toString()+",\n"+
		    Consultation.Props.nbc_patients_n.toString()+",\n"+
		    Consultation.Props.proc_type.toString()+",\n"+
		    Consultation.Props.procbegintime.toString()+",\n"+
		    Consultation.Props.procendtime.toString()+",\n"+
		    Consultation.Props.time_approx.toString()+",\n"+
		    Consultation.Props.comment.toString()+",\n"+
		    Consultation.Props.recommendation.toString()+",\n"+
		    Consultation.Props.nbc_stud_n.toString()+",\n"+
		    Consultation.Props.stud_comment.toString()+",\n"+
		    Consultation.Props.rt_device.toString()+",\n"+
		    Consultation.Props.rt_tech.toString()+",\n"+
		    Consultation.Props.parent_proc.toString()+",\n"+
		    Consultation.Props.nbc_organizations_n.toString()+
			" )" +"\n"+
			"VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n";

		Long genIdConsultation =
			GeneratorManager.instace.genId(procTableName+"_n")
		;
		Long genIdOperation	 =
			GeneratorManager.instace.genId(genNameOperation);
		;
		RegistrationManager.instace.regOperation(con,genIdOperation,commandInsert,lgkSessId);

		Object[] params = new Object[]
			{
				genIdConsultation,
				genIdOperation,
				consultation.getCurrentPatient().getN(),
				4,
				new java.sql.Timestamp(consultation.getProcbegintime().getTime()),
				new java.sql.Timestamp(consultation.getProcendtime().getTime()),
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null
			};
		qr.update(con, sql, params);
		consultation.setN(genIdConsultation);
	}

	/**
	 * Удалить консультацию из таблицы nbc_proc
	 * @param consultation
	 **/
	public void deleteConsultation(Consultation consultation)
	{
		try (
			Connection con = DB.getConnection()
		) {
			String sql =
				"DELETE FROM " + procTableName + "\n" +
					"WHERE N = ?\n";
			Long genIdOperation
				= GeneratorManager.instace.genId(genNameOperation)
			;
			RegistrationManager.instace.regOperation(con,genIdOperation,commandDelete,lgkSessId);
			Object[] params = new Object[]{consultation.getN()};
			qr.update(con, sql, params);
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE,"Problems with Database",e);
			Notification.show("Problems with Database", Notification.Type.WARNING_MESSAGE);
		}
	}

	 /**
	 * Обновить данные о консультации  в таблице nbc_proc
	 * @param consultation - консультация
	 **/
	public void updateConsultation(Consultation consultation)
	{
		String sql = "UPDATE "+procTableName +" SET"+"\n" +
			Consultation.Props.op_create.toString()+" = ?,\n"+
			Consultation.Props.nbc_patients_n.toString()+" = ?,\n"+
			Consultation.Props.proc_type.toString()+" = ?,\n"+
			Consultation.Props.procbegintime.toString()+" = ?,\n"+
			Consultation.Props.procendtime.toString()+" = ?,\n"+
			Consultation.Props.time_approx.toString()+" = ?,\n"+
			Consultation.Props.comment.toString()+" = ?,\n"+
			Consultation.Props.recommendation.toString()+" = ?,\n"+
			Consultation.Props.nbc_stud_n.toString()+" = ?,\n"+
			Consultation.Props.stud_comment.toString()+" = ?,\n"+
			Consultation.Props.rt_device.toString()+" = ?,\n"+
			Consultation.Props.rt_tech.toString()+" = ?,\n"+
			Consultation.Props.parent_proc.toString()+" = ?,\n"+
			Consultation.Props.nbc_organizations_n.toString()+" = ?\n"+
			"WHERE "+Consultation.Props.n.toString()+" = ?\n";
		try (
			Connection con = DB.getConnection()
		)
		{
			Long genIdOperation =
				GeneratorManager.instace.genId(genNameOperation)
			;
			RegistrationManager.instace.regOperation(con,genIdOperation,commandInsert,lgkSessId);
			// TODO изменить параметры
			Object[] params = new Object[]{};
			qr.update(con, sql, params);
			con.commit();
		} catch (SQLException e)
		{ }
	}
}
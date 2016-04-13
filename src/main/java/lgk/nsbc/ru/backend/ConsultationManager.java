package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.Patient;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ConsultationManager
{
	private  final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private final QueryRunner qr = new QueryRunner();

	public List<Consultation> listConsultation(Date fromDate, Date toDate) // дата будет браться от 01.01.2016 и 20.02.2016
	{
		try (
			Connection con = DB.getConnection()
		) {
			con.setAutoCommit(false);
			String sql =
				"SELECT\n" +
					"nbc_proc.n, " +
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
	 * @param con,consultation,genIdConsultation,genIdOperation,genIdPatient
	 * @return Успешность добавления
	 **/
	public boolean insertConsultation(Connection con, Consultation consultation,Long genIdConsultation,
									    Long genIdOperation, Long genIdPatient)
	{
		String sql = "INSERT into NBC_PROC\n" +
			"(N, " +
			"OP_CREATE, " +
			"NBC_PATIENTS_N, " +
			"PROC_TYPE, PROCBEGINTIME, PROCENDTIME, TIME_APPROX,\n" +
			"COMMENT, RECOMMENDATION, NBC_STUD_N, STUD_COMMENT,RT_DEVICE,RT_TECH,\n" +
			"PARENT_PROC, NBC_ORGANIZATIONS_N)\n" +
			"VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n";
		try
		{
			Object[] params = new Object[]{
				genIdConsultation,
				genIdOperation,
				genIdPatient,
				4, // Тип консультация же, незя ставить null, с этим работать будет нельзя
				new java.sql.Timestamp(consultation.getProcbegintime().getTime()),
				new java.sql.Timestamp(consultation.getProcendtime().getTime()),
				null,null,null,null,null,null,null,null,null};
			int updateRows = qr.update(con, sql, params);
			if (updateRows == 0) {
				con.rollback();
				return false;
			}
			return true;
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Удалить консультацию из таблицы nbc_proc
	 * @param  consultation - консультация
	 * @return Успешность удаления
	 **/
	public boolean deleteConsultation(Consultation consultation)
	{
		String sql = "DELETE FROM nbc_proc\n" +
			"WHERE N = ?\n";
		try (Connection con = DB.getConnection()) {
			Object[] params = new Object[]{consultation.getN()};
			int updateRows = qr.update(con, sql, params);
			if(updateRows == 0) {
				con.rollback();
				return false;
			}
			con.commit();
			return true;
		}
		catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	 /**
	 * Обновить данные о консультации  в таблице nbc_proc
	 * @param consultation - консультация
	 * @return Успешность обновления
	 **/
	public boolean updateConsultation(Consultation consultation)
	{
		String sql = "UPDATE nbc_proc SET\n" +
			"OP_CREATE = ?,\n" +
			"NBC_PATIENTS_N = ?,\n" +
			"PROC_TYPE = ?,\n" +
			"PROCBEGINTIME = ?,\n" +
			"PROCENDTIME = ?,\n " +
			"TIME_APPROX = ?,\n" +
			"COMMENT = ?,\n" +
			"RECOMMENDATION = ?,\n" +
			"NBC_STUD_N = ?,\n" +
			"STUD_COMMENT = ?,\n" +
			"RT_DEVICE = ?,\n" +
			"RT_TECH = ?,\n" +
			"PARENT_PROC = ?,\n" +
			"NBC_ORGANIZATIONS_N = ?\n"+
			"WHERE N = ?\n"; // ID
		try (
			Connection con = DB.getConnection()
		)
		{
			// TODO изменить параметры????
			Object[] params = new Object[]{consultation.getN(),null};
			int updateRows = qr.update(con, sql, params);
			if(updateRows == 0)
			{
				con.rollback();
				return false;
			}
			con.commit();
			return true;
		}
		catch (SQLException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
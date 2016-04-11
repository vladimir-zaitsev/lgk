package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.Patient;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PatientsManager {

	private final QueryRunner qr = new QueryRunner();
	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");


	public List<Patient> listPatients()
	{
		try (
			Connection con = DB.getConnection()
		)
		{

			BeanListHandler<Patient> handler = new BeanListHandler<>(Patient.class);
			return qr.query(con, "select first 5 * from "+Patient.relationName, handler);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Ищет данные пациентов из таблицы nbc_patients
	 *@param filterPrefix - строчка, по которой ищем пациентов
	 *@return cписок пациентов
	 **/
	public List<Patient> listPatients(String filterPrefix) {
		try (
			Connection con = DB.getConnection()
		) {

			String sql =
				"SELECT nbc_patients.n,\n" +
					"name,\n"+
					"patronymic,\n" +
					"surname," +
					"birthday\n" +
					"FROM nbc_patients\n" +
					"JOIN bas_people ON bas_people.n = nbc_patients.bas_people_n\n" +
					"WHERE UPPER(surname) LIKE ?\n";
			BeanListHandler<Patient> handler = new BeanListHandler<>(Patient.class);
			return qr.query(con, sql, handler, filterPrefix.toUpperCase() + "%");
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Найти пациента по N из таблицы nbc_patients
	 * @param patient - пациент
	 * @return пациента
	 **/
	public Patient selectPatient(Patient patient) {

		try (Connection con = DB.getConnection())
		{
			StringBuilder sql =  new StringBuilder();
			sql.append("SELECT nbc_patients.n,\n")
				.append("name,\n")
				.append("sex,\n")
				.append("surname,\n")
				.append("patronymic,\n")
				.append("birthday,\n")
				.append("diagnosis,\n")
				.append("case_history_num,\n")
				.append("nbc_organizations_n\n")
				.append("FROM nbc_patients\n")
				.append("JOIN bas_people on bas_people.n = nbc_patients.bas_people_n\n")
				.append("WHERE nbc_patients.n = ?\n");
			Object[] params = new Object[]{patient.getN()};
			BeanHandler<Patient> handler = new BeanHandler<>(Patient.class);
			return qr.query(con,sql.toString(),handler,params);
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Добавить данные о пациенте в таблицу nbc_patients
	 * @param patient - пациент
	 * @return Успешность добавления
	 **/
	public boolean insertPatient(Connection con,Patient patient,Long genIdPeople,Long genIdPatient,
								 Long genIdOperation)
	{
		// TODO подумать про enum
		String sql = "INSERT into nbc_patients\n" +
			"(N, OP_CREATE, NBC_ORGANIZATIONS_N, NBC_STAFF_N,CASE_HISTORY_NUM,CASE_HISTORY_DATE,\n" +
			"BAS_PEOPLE_N, REPRESENT,REPRESENT_TELEPHONE, DIAGNOSIS, NBC_DIAGNOSIS_N,\n"+
			"FULL_DIAGNOSIS, STATIONARY, ALLERGY, INFORMATION_SOURCE, FOLDER, DISORDER_HISTORY,\n"+
			"NBC_DIAG_2015_N, NBC_DIAG_LOC_N)\n"+
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n";
		try
		{
			Object[] params = new Object[]{genIdPatient,genIdOperation,null,null,
				null,null,genIdPeople,null,null,null,null,null,
				null,null,null,null,null,null,null};
			int updateRows = qr.update(con, sql, params);
			if (updateRows == 0)
			{
				con.rollback();
				return false;
			}
			return true;

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
	/**
	 * Удалить данные о пациенте из таблицы nbc_patients
	 * @param patient - пациент
	 * @return Успешность добавления
	 **/
	public  boolean deletePatient(Patient patient)
	{
		try (
			Connection con = DB.getConnection()
		)
		{
			con.setAutoCommit(false);
			String sql = "DELETE FROM nbc_patients\n" +
				"WHERE n = ?\n";
			Object[] params = new Object[]{patient.getN()};
			int updateRows = qr.update(con, sql, params);
			if (updateRows == 0)
			{
				con.rollback();
				return false;
			}
			con.commit();
			return true;
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

	}
	/**
	 * Обновить данные пациента из таблицы nbc_patients
	 * @param patient - пациент
	 * @return Успешность добавления
	 **/
	public boolean updatePatient(Patient patient)
	{
		String sql = "UPDATE nbc_patients SET\n" +
			"OP_CREATE = ?\n" +
			"NBC_ORGANIZATIONS_N = ?\n"+
			"NBC_STAFF_N = ?\n" +
			"CASE_HISTORY_NUM = ?\n"+
			"CASE_HISTORY_DATE = ?\n"+
			"BAS_PEOPLE_N = ?\n"+
			"REPRESENT = ?,\n"+
			"REPRESENT_TELEPHONE = ?,\n"+
			"DIAGNOSIS = ?,\n" +
			"NBC_DIAGNOSIS_N = ?,\n" +
			"FULL_DIAGNOSIS = ?,\n" +
			"STATIONARY = ?,\n" +
			"ALLERGY = ?,\n" +
			"INFORMATION_SOURCE = ?,\n" +
			"FOLDER, DISORDER_HISTORY = ?,\n" +
			"NBC_DIAG_2015_N = ?,\n"+
			"NBC_DIAG_LOC_N = ?,\n" +
			"WHERE N = ?"; // ID;
		try (
			Connection con = DB.getConnection()
		)
		{
			con.setAutoCommit(false);
			// TODO Параметры ???
			Object[] params = new Object[]{};
			int updateRows = qr.update(con, sql, params);
			if (updateRows == 0)
			{
				con.rollback();
				return false;
			}
			con.commit();
			return true;
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

}

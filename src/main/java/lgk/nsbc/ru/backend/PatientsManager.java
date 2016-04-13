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
					"birthday,\n" +
					"nbc_organizations_n\n" +
					"FROM nbc_patients\n" +
					"JOIN bas_people ON bas_people.n = nbc_patients.bas_people_n\n" +
					"WHERE UPPER(surname) LIKE ?\n" +
				    "AND nbc_organizations_n = ?";
			Object[] params = new Object[]{filterPrefix.toUpperCase() + "%",selectOrganization(con)};
			BeanListHandler<Patient> handler = new BeanListHandler<>(Patient.class);
			return qr.query(con, sql, handler,params);
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
				.append("case_history_num\n")
				.append("FROM nbc_patients\n")
				.append("JOIN bas_people on bas_people.n = nbc_patients.bas_people_n\n")
				.append("WHERE nbc_patients.n = ?\n")
			    .append("AND nbc_organizations_n = ?\n");
			Object[] params = new Object[]{patient.getN(),selectOrganization(con)};
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
	 * @param con,patient,genIdPatient,genIdOperation
	 * @return Успешность добавления
	 **/
	public boolean insertPatient(Connection con,Patient patient,Long genIdPeople,Long genIdPatient,
								 Long genIdOperation)
	{
		String sql = "INSERT into nbc_patients\n" +
			"(N, OP_CREATE, NBC_ORGANIZATIONS_N, NBC_STAFF_N,CASE_HISTORY_NUM,CASE_HISTORY_DATE,\n" +
			"BAS_PEOPLE_N, REPRESENT,REPRESENT_TELEPHONE, DIAGNOSIS, NBC_DIAGNOSIS_N,\n"+
			"FULL_DIAGNOSIS, STATIONARY, ALLERGY, INFORMATION_SOURCE, FOLDER, DISORDER_HISTORY,\n"+
			"NBC_DIAG_2015_N, NBC_DIAG_LOC_N)\n"+
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n";
		try
		{
			Object[] params = new Object[]{
				genIdPatient,
				genIdOperation,
				selectOrganization(con),
				null,
				null,
				null,
				genIdPeople,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null,
				null};
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

	// В каком случае надо удалять пациента, пока непонятно
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

	// Нахождение организации
	public Long selectOrganization(Connection con)
	{
		String sql = "SELECT SYS_CONST.data_bigint as n\n"+
			"FROM sys_const\n"+
			"LEFT JOIN nbc_organizations on nbc_organizations.n = sys_const.data_bigint\n"+
			"WHERE sys_const.name = ?";
		try
		{
		    String name = "NBC_ORGANIZATIONS_MAIN_N";
			Object[] params = new Object[]{name};
			return qr.query(con, sql, result -> {
				result.next();
				return result.getLong("n");
			},params);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

}

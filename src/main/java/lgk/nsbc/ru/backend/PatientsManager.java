package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.Patient;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PatientsManager {

	private final PeopleManager peopleManager;

	private final QueryRunner qr = new QueryRunner();
	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	public  PatientsManager(PeopleManager peopleManager)
	{
		this.peopleManager = peopleManager;
	}

	public List<Patient> listPatients() {
		try (
			Connection con = DB.getConnection()
		) {

			BeanListHandler<Patient> handler = new BeanListHandler<>(Patient.class);
			return qr.query(con, "select first 5 * from "+Patient.relationName, handler);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	/*
    * вытаскивыем данные пациента(name, patronymic, surname)из базы
    *@param строчка, по которой ищем пациента
    * @return List пациентов
    */
	public List<Patient> listPatients(String filterPrefix) {
		try (
			Connection con = DB.getConnection()
		) {

			String sql =
				"SELECT n,name, patronymic, surname,birthday\n" +
					"FROM bas_people\n" +
					"WHERE UPPER(surname) LIKE ?\n"
				;
			BeanListHandler<Patient> handler = new BeanListHandler<>(Patient.class);
			return qr.query(con, sql, handler, filterPrefix.toUpperCase() + "%");
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

	}
	// Получаем генерируемое id отдельным запросом
	private Long patientId()
	{
		String sql =
			"SELECT gen_id(nbc_patients_n, 1) as ID\n" +
				"FROM rdb$database\n";
		try (
			Connection con = DB.getConnection()
		) {
			return qr.query(con, sql, result -> {
				result.next(); // вернется значение из первой строки
				return result.getLong("ID");
			});
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	// Создание OP_N
	public long operCreatePatients()
	{
		String sql =
			"SELECT gen_id(sys_operation_n, 1) as IdOper\n" +
				"FROM rdb$database\n";
		try (
			Connection con = DB.getConnection()
		) {
			return qr.query(con, sql, result -> {
				result.next();
				return result.getLong("IdOper");
			});
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	// Создаем пациента с FK-bas_people_n
	public boolean insertPatient(Consultation consultation)
	{
		String sql = "INSERT into nbc_patients\n" +
			"(N, OP_CREATE, NBC_ORGANIZATIONS_N, NBC_STAFF_N,CASE_HISTORY_NUM,CASE_HISTORY_DATE,\n" +
			"BAS_PEOPLE_N, REPRESENT,REPRESENT_TELEPHONE, DIAGNOSIS, NBC_DIAGNOSIS_N,\n"+
			"FULL_DIAGNOSIS, STATIONARY, ALLERGY, INFORMATION_SOURCE, FOLDER, DISORDER_HISTORY,\n"+
			"NBC_DIAG_2015_N, NBC_DIAG_LOC_N)\n"+
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n";
		try (
			Connection con = DB.getConnection()
		)
		{
			con.setAutoCommit(false);
			Object[] params = new Object[]{patientId(),operCreatePatients(),null,null,
				consultation.getCase_history_num(),consultation.getProcbegintime(),
				peopleManager.searchId(consultation),null,null,null,null,null,
				null,null,null,null,null,null,null};
			int updateRows = qr.update(con, sql, params); // количество обновленных строчек
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


	// TODO непонятно как быть с регистрацией операции
	/*
	// Регистрация операции
	public boolean operInsertPatients(String lgkSessId) {
		String sql = "INSERT INTO sys_operations\n" +
			"(N, SESSION_N, COMMAND_NAME, MOMENT)\n" +
			"VALUES (?,?,?,?);";
		try (
			Connection con = DB.getConnection()
		) {
			con.setAutoCommit(false);

			String command = "NBC_PATIENTS_PUT";
			String moment = "NOW";
			Object[] params = new Object[]{operationN, sessionManager.checkSession(lgkSessId),
				command,moment};
			int updateRows = qr.update(con, sql, params);
			if (updateRows == 0) {
				con.rollback();
				return false;
			}
			con.commit();
			return true;
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	*/

	public Long searchId (Consultation consultation) {

		try (Connection con = DB.getConnection()) {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT nbc_patients.n as ID\n")
				.append("FROM nbc_patients\n")
				.append("JOIN bas_people on bas_people.n = nbc_patients.bas_people_n\n")
				.append("WHERE name = ?\n")
				.append("AND surname = ?\n")
				.append("AND patronymic = ?\n");
			Object[] params;
			if (consultation.getBirthday() != null) {
				String birthday = formatter.format(consultation.getBirthday());
				sql.append("AND birthday = ?\n");
				params = new Object[]{consultation.getName(), consultation.getSurname(),
					consultation.getPatronymic(), birthday};
			} else {
				params = new Object[]{consultation.getName(), consultation.getSurname(),
					consultation.getPatronymic()};
			}
			return qr.query(con, sql.toString(), result -> {
				result.next();
				return result.getLong("ID");
			}, params);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}


	// Удаление пациента по PK - N
	public  boolean deletePatient(Consultation consultation)
	{
		try (
			Connection con = DB.getConnection()
		)
		{
			con.setAutoCommit(false);
			String sql = "DELETE FROM nbc_patients\n" +
				"WHERE n = ?\n";
			Object[] params = new Object[]{searchId(consultation)};
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


	// TODO Непонятно какие данные будут обновляться, так как еще не продумано что будет в форме редактирования
	public boolean updatePatient(Consultation consultation)
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
			// Параметры
			Object[] params = new Object[]{consultation.getCase_history_num(),searchId(consultation)};
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

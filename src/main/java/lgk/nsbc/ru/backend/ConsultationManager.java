package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.Patient;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 20.02.2016.
 */


public class ConsultationManager {

	private  final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private  final PatientsManager patientsManager;
	private final QueryRunner qr = new QueryRunner();

	public ConsultationManager(PatientsManager patientsManager)
	{
		this.patientsManager = patientsManager;

	}

	// Метод, позволяющий вытаскивать даты по заданным датам
	public List<Consultation> listConsultation(Date fromDate, Date toDate) // дата будет браться от 01.01.2016 и 20.02.2016
	{

		try (
			Connection con = DB.getConnection()
		) {
			con.setAutoCommit(false);
			String sql =
				"SELECT\n" +
					"	procbegintime, procendtime,\n" +
					"	surname,name,patronymic,\n" +
					"	case_history_num,\n" +
					"	diagnosis,birthday\n" +
					"FROM bas_people\n" +
					"JOIN nbc_patients  on  bas_people.n = nbc_patients.bas_people_n\n" +
					"LEFT JOIN  nbc_proc on  nbc_proc.nbc_patients_n = nbc_patients.n\n" +
					"WHERE nbc_proc.proc_type = 4\n" +
					"	AND nbc_proc.procbegintime between ? and ?\n" +
					"	AND nbc_proc.procendtime is not NULL";
			BeanListHandler<Consultation> handler = new BeanListHandler<>(Consultation.class);

			return qr.query(con, sql, handler
				, new java.sql.Timestamp(fromDate.getTime())
				, new java.sql.Timestamp(toDate.getTime())
			);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}
	/*
   * вытаскивает данные уже существующего пациента из базы
   *@param параметры пациента, которого нашли ФИО, дата рождения
   *@return
  */
	// Метод изменен
	public Patient selectPatient(String name, String surname, String patronymic, Date birthday)
	{
		try (Connection con = DB.getConnection()) {
			QueryRunner qr = new QueryRunner();
			StringBuilder sql =  new StringBuilder();
			sql.append("SELECT\n")
				.append("name,\n")
				.append("surname,\n")
				.append("patronymic,\n")
				.append("birthday,\n")
				.append("diagnosis,\n")
				.append("case_history_num\n")
				.append("FROM bas_people\n")
				.append("JOIN nbc_patients on bas_people.n = nbc_patients.bas_people_n\n")
				.append("WHERE name = ?\n")
				.append("AND surname = ?\n")
				.append("AND patronymic = ?\n");
			Object[] params;
			if (birthday != null) {
				String birthdayPatient = formatter.format(birthday);
				sql.append("AND birthday = ?\n");
				params = new Object[]{name,surname,patronymic,birthdayPatient};
			} else {
				params = new Object[]{name,surname,patronymic};
			}
			BeanHandler<Patient> handler = new BeanHandler<>(Patient.class);
			return qr.query(con,sql.toString(),handler,params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// Сгенерируем ключ (primary key) для консультации
	public Long consultationId() {

		String sql =
			"SELECT gen_id(nbc_proc_n, 1) as ID\n" +
				"FROM rdb$database\n";

		try (
			Connection con = DB.getConnection()
		) {
			final QueryRunner qr = new QueryRunner();
			return qr.query(con, sql, result -> {
				result.next(); // вернется значение из первой строки
				return result.getLong("ID");
			});
		} catch (SQLException e)
		{
			throw new IllegalStateException(e);
		}
	}

	// Создание OP_N
	public long operCreateСonsul()
	{
		String sql =
			"SELECT gen_id(sys_operation_n, 1) as IdOp\n" +
				"FROM rdb$database\n";
		try (
			Connection con = DB.getConnection()
		) {
			return qr.query(con, sql, result -> {
				result.next();
				return result.getLong("IdOp");
			});
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
	public boolean insertConsultation(Consultation consultation)
	{
		String sql = "INSERT into NBC_PROC\n" +
			"(N, OP_CREATE, NBC_PATIENTS_N, PROC_TYPE, PROCBEGINTIME, PROCENDTIME, TIME_APPROX,\n" +
			"COMMENT, RECOMMENDATION, NBC_STUD_N, STUD_COMMENT, RT_DEVICE, RT_TECH,\n" +
			"PARENT_PROC, NBC_ORGANIZATIONS_N)\n" +
			"VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n";
		try (
			Connection con = DB.getConnection()
		) {
			Object[] params = new Object[]{consultationId(),operCreateСonsul(),
				patientsManager.searchId(consultation),null,consultation.getProcbegintime(),
				consultation.getProcendtime(),null,null,null,null,null,null,null,null,null};
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

	/*
	// Регистрация операции
	public boolean operInsertConsultation(String lgkSessId) {
		String sql = "INSERT into SYS_OPERATIONS\n" +
			"(N, SESSION_N, COMMAND_NAME, MOMENT)\n" +
			"VALUES (?,?,?,?)\n";
		try (
			Connection con = DB.getConnection()
		) {
			con.setAutoCommit(false);
			String command = "NBC_PROC_PUT";
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

	//  Удаление и редактирование консультации

	// TODO как искать номер процедуры ?
	// Тут много нюансов которые нужно выяснять
	public long searchConsultation(Consultation consultation)
	{

		try (Connection con = DB.getConnection()) {
			QueryRunner qr = new QueryRunner();
			StringBuilder sql =  new StringBuilder();
			sql.append("SELECT nbc_proc.n as idProc \n")
				.append("FROM nbc_proc\n")
				.append("JOIN nbc_patients ON nbc_patients.n = nbc_proc.nbc_patients_n\n")
				.append("JOIN bas_people ON bas_people.n = nbc_patients.bas_people_n\n")
				.append("WHERE name = ?\n")
				.append("AND surname = ?\n")
				.append("AND patronymic = ?\n");
			Object[] params;
			if (consultation.getBirthday()!= null) {
				String birthday = formatter.format(consultation.getBirthday());
				sql.append("AND birthday = ?\n");
				params = new Object[]{consultation.getName(),consultation.getSurname(),
					consultation.getPatronymic(),birthday};
			} else {
				params = new Object[]{consultation.getName(),consultation.getSurname(),
					consultation.getPatronymic()};
			}
			return  qr.query(con, sql.toString(), result -> {
				result.next();
				return result.getLong("idProc");},params);

		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
	public boolean deleteConsultation(Consultation consultation)
	{
		String sql = "DELETE FROM nbc_proc\n" +
			"WHERE N = ?\n";
		try (
			Connection con = DB.getConnection()
		) {
			Object[] params = new Object[]{searchConsultation(consultation)};
			int updateRows = qr.update(con, sql, params); // количество удаленных строчек
			if(updateRows == 0)
			{
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


	// TODO Как обноовлять данные в процедуре
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
			// Изенить параметры
			Object[] params = new Object[]{null,searchConsultation(consultation)};
			int updateRows = qr.update(con, sql, params);
			if(updateRows == 0)
			{
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


}
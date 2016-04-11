package lgk.nsbc.ru.backend;

import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by user on 10.04.2016.
 */
public class RegistrationManager
{

	private final QueryRunner qr = new QueryRunner();
	private final SessionManager sessionManager ;
	public RegistrationManager (SessionManager sessionManager)
	{
		this.sessionManager = sessionManager;
	}
	/**
	 * Добавить данные регистрации операции в таблицу sys_operations
	 * @param con,genIdOperation : 1) полученное соединение
	 * 2) Id созданной операции
	 * @return Успешность добавления операции
	 **/
	public boolean  registrOperPeople(Connection con, Long genIdOperation) {
		String sql =
			"INSERT into SYS_OPERATIONS\n" +
				"(N, SESSION_N, COMMAND_NAME, MOMENT)\n" +
				"VALUES (?,?,?,?)\n";
		try
		{
			String command = "BAS_PEOPLE_PUT";
			Object[] params = new Object[]{genIdOperation,sessionManager.IdSession(con),
				command,Timestamp.valueOf(LocalDateTime.now())};
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
	 * Добавить данные регистрации операции  в таблицу sys_operations
	 * @param con,genIdOperation : 1) полученное соединение
	 * 2) Id - созданной операции
	 * @return Успешность добавления операции
	 **/
	public boolean  registrOperPatients(Connection con,Long genIdOperation) {
		String sql = "INSERT INTO sys_operations\n" +
			"(N, SESSION_N, COMMAND_NAME, MOMENT)\n" +
			"VALUES (?,?,?,?);";
		try
		{
			String command = "NBC_PATIENTS_PUT";
			Object[] params = new Object[]{genIdOperation,sessionManager.IdSession(con),
				command,Timestamp.valueOf(LocalDateTime.now())};
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
	 * Добавить данные регистрации операции  в таблицу sys_operations
	 * @param con,genIdOperation : 1)полученное соединение
	 * 2)Id - созданной операции
	 * @return Успешность добавления операции
	 **/
	public boolean registrOperConsultation(Connection con, Long genIdOperation) {
		String sql = "INSERT into SYS_OPERATIONS\n" +
			"(N, SESSION_N, COMMAND_NAME, MOMENT)\n" +
			"VALUES (?,?,?,?)\n";
		try
		{
			String command = "NBC_PROC_PUT";
			Object[] params = new Object[]{genIdOperation,sessionManager.IdSession(con),
				command,Timestamp.valueOf(LocalDateTime.now())};
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

}

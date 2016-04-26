package lgk.nsbc.ru.backend;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

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
	public static final RegistrationManager instace = new RegistrationManager();
	private final ScalarHandler<Long> rs = new ScalarHandler<>();
	public static final String operationTableName = "sys_operations";

	/**
	 * Добавить данные регистрации операции в таблицу sys_operations
	 * @param con,genIdOperation,command,lgkSessId
	 **/
	public void regOperation(Connection con
		,Long genIdOperation
		,String command
		, String lgkSessId
	) throws  SQLException
	{
		String sql =
			"INSERT INTO "+operationTableName+"\n" +
		    "(N, SESSION_N, COMMAND_NAME, MOMENT)\n" +
		    "VALUES (?,?,?,?)\n";
			Object[] params =
				new Object[]{genIdOperation
				,SessionManager.instance.IdSession(con,lgkSessId)
				,command
				,Timestamp.valueOf(LocalDateTime.now())};
			qr.update(con, sql, params);
	}

}

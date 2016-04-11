package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.entity.Session;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Управление сессией в базе
 */
public class SessionManager {
	final private QueryRunner qr = new QueryRunner();
	final private BeanHandler<Session> handler = new BeanHandler<>(Session.class);
	private String lgkSessId;

	public SessionManager(String lgkSessId)
	{
		this.lgkSessId = lgkSessId;
	}

	public Session loadSession(){
		String sql =
			"SELECT\n" +
				"sys_agents.n,\n" +
				"sys_agents.name AS login,\n" +
				"sys_agents.nbc_org_n_deafult AS DeafultOrgN,\n" +
				"bas_people.name,\n" +
				"bas_people.surname\n" +
				"FROM sys_sessions\n" +
				"JOIN sys_agents ON sys_agents.n = sys_sessions.agent_n\n" +
				"JOIN bas_people ON bas_people.n = sys_agents.people_n\n" +
				"WHERE sid = ?\n"
			;
		try (
			Connection con = DB.getConnection()
		) {
			return qr.query(con, sql, handler, lgkSessId);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
	/**
	 * Получить N сессии из таблицы sys_sessions
	 * @param  con - соединение
	 * @return N (Primary key)
	 **/
	public Long IdSession(Connection con)
	{
		String sql =
			"SELECT n \n" +
				"FROM sys_sessions\n" +
				"WHERE sid = ?\n";
		try
		{
			final QueryRunner qr = new QueryRunner();
			return qr.query(con, sql, result -> {
				result.next();
				return result.getLong("n");
			},lgkSessId);
		} catch (SQLException e)
		{
			throw new IllegalStateException(e);
		}
	}
}

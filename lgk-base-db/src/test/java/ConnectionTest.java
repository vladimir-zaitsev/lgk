import lgk.nsbc.ru.backend.db.DB;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Автопроверка соединения с БД
 */
public class ConnectionTest {
	@Test
	public void testConnection() throws SQLException {
		Connection connection = DB.getConnection();
		connection.close();
	}
}

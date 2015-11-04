package lgk.nsbc.ru.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DB {
	public static Connection getConnection() throws SQLException {
		String url = "jdbc:postgresql://node26051-nsbc-lgk.mycloud.by/lgk";
		String user_name = "java";
		String user_password = "1";
		try {
			return DriverManager.getConnection(url, user_name, user_password);
		} catch (SQLException e) {
			Logger.getGlobal().severe("can't connect to database (" + url + ")");
			throw e;
		}
	}
}

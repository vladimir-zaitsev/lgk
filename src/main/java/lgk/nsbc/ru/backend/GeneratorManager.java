package lgk.nsbc.ru.backend;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by user on 10.04.2016.
 */
public class GeneratorManager {
	public static final GeneratorManager instace = new GeneratorManager();
	private final QueryRunner qr = new QueryRunner();
	private final ScalarHandler<Long> rs = new ScalarHandler<>();

	public Long genId(String generatorName) {
		StringBuilder sql =
			new StringBuilder("SELECT gen_id(")
				.append(generatorName)
				.append(", 1) as id FROM rdb$database");
		try (
			Connection con = DB.getConnection()
		) {
			return qr.query(con, sql.toString(), rs);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

}

package lgk.nsbc.ru.backend;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Тянем названия из базы
 */
public class I18nManager {
	public String getCaption(String relationName, String fieldName){
		try (
			Connection con = DB.getConnection()
		) {
			QueryRunner qr = new QueryRunner();
			return qr.query(con,
				"with PARAMS as (\n" +
					"select\n" +
						" CAST(? AS VARCHAR(254)) as RELATION_NAME\n" +
						",CAST(? AS VARCHAR(254)) as FIELD_NAME\n" +
					"from rdb$database\n" +
				")\n" +
				"select coalesce(\n" +
					"(\n" +
						"select CAPTION\n" +
						"from SYS_TRANSLATIONS\n" +
						"cross join PARAMS\n" +
						"where\n" +
							"upper(SYS_TRANSLATIONS.RELATION_NAME) = PARAMS.RELATION_NAME\n" +
							"and upper(SYS_TRANSLATIONS.FIELD_NAME) = PARAMS.FIELD_NAME\n" +
							"and BAS_DIC_LANG_N = 2 -- AND LINE = 0\n" +
					"),\n" +
					"(\n" +
						"select XI$RUS_FIELD_NAME\n" +
						"from RDB$RELATION_FIELDS\n" +
						"cross join PARAMS\n" +
						"where\n" +
							"RDB$RELATION_NAME IN (PARAMS.RELATION_NAME, PARAMS.RELATION_NAME||'_LST')\n" +
							"and RDB$FIELD_NAME = PARAMS.FIELD_NAME\n" +
							"and XI$RUS_FIELD_NAME IS NOT null\n" +
					")\n" +
				") as caption\n" +
				"from rdb$database"
				, new ScalarHandler<String>()
				, relationName.toUpperCase()
				, fieldName.toUpperCase()
			);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}
}

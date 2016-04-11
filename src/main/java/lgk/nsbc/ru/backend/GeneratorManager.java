package lgk.nsbc.ru.backend;

import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by user on 10.04.2016.
 */
public class GeneratorManager {

	private final QueryRunner qr = new QueryRunner();

	/**
	 * Получить сгенерируемое ID человека
	 * @return N (primary key) таблицы bas_people
	 */
	public Long genIdPeople()
	{
		String sql =
			"SELECT gen_id(bas_people_n, 1) as Id_People\n" +
				"FROM rdb$database\n";
		try (
			Connection con = DB.getConnection()
		) {
			return qr.query(con, sql, result -> {
				result.next();
				return result.getLong("Id_People");
			});
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Получить сгенерируемое ID пациента
	 * @return N (primary key) таблицы nbc_patients
	 **/
	public Long genIdPatient()
	{
		String sql =
			"SELECT gen_id(nbc_patients_n, 1) as Id_Patient\n" +
				"FROM rdb$database\n";
		try (
			Connection con = DB.getConnection()
		) {
			return qr.query(con, sql, result -> {
				result.next();
				return result.getLong("Id_Patient");
			});
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Получить сгенерируемое ID консультации
	 * @return N (primary key) таблицы nbc_proc
	 **/
	public Long  genIdConsultation()
	{
		String sql =
			"SELECT gen_id(nbc_proc_n, 1) as Id_Consultation\n" +
				"FROM rdb$database\n";

		try (
			Connection con = DB.getConnection()
		) {
			final QueryRunner qr = new QueryRunner();
			return qr.query(con, sql, result -> {
				result.next();
				return result.getLong("Id_Consultation");
			});
		} catch (SQLException e)
		{
			throw new IllegalStateException(e);
		}
	}
	/**
	 * Получить сгенерируемое ID операции
	 * @return N (primary key) из таблицы sys_operations
	 **/
	public long genIdOperation()
	{
		String sql =
			"SELECT gen_id(sys_operation_n, 1) as Id_Op\n" +
				"FROM rdb$database\n";
		try (
			Connection con = DB.getConnection()
		) {
			return qr.query(con, sql, result -> {
				result.next();
				return result.getLong("Id_Op");
			});
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}
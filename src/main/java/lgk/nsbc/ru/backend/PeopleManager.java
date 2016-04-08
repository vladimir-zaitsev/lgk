package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.entity.Consultation;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created by user on 08.04.2016.
 */
public class PeopleManager
{
	private final QueryRunner qr = new QueryRunner();
	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	// Получаем сгенерируемое id
	public Long peopleId()
	{
		String sql =
			"SELECT gen_id(bas_people_n, 1) as IdPeople\n" +
				"FROM rdb$database\n";
		try (
			Connection con = DB.getConnection()
		) {
			return qr.query(con, sql, result -> {
				result.next(); // вернется значение из первой строки
				return result.getLong("IdPeople");
			});
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
	// Заносим новую запись человека  в базу с полученным id
	public boolean insertPeople(Consultation consultation)
	{
		String sql = "INSERT INTO bas_people\n" +
			"(N, OP_CREATE, SURNAME, NAME, PATRONYMIC, BIRTHDAY, SEX,\n" +
			"CITIZENSHIP, JOB, OBIT)\n" +
			"VALUES (?,?,?,?,?,?,?,?,?,?)\n";
		try (
			Connection con = DB.getConnection()
		)
		{
			con.setAutoCommit(false);
			String birthdayPeople =  formatter.format(consultation.getBirthday());
			Object[] params = new Object[]{peopleId(),operCreatePeople(),consultation.getName(),
				consultation.getSurname(), consultation.getPatronymic(),birthdayPeople,
				null,null,null,null};
			int updateRows = qr.update(con, sql, params); // количество обновленных строчек
			if (updateRows == 0)
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



	/*
    //  Регистрация операции
	public boolean operInsertPeople() {
		String sql = "INSERT INTO sys_operations\n" +
			"(N, SESSION_N, COMMAND_NAME, MOMENT)\n" +
			"VALUES (?,?,?,?)\n";
		try (
			Connection con = DB.getConnection()
		) {
			con.setAutoCommit(false);

			String command = "BAS_PEOPLE_PUT";
			String moment = "NOW";
			Object[] params = new Object[]{},
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
	// Создание OP_N
	public long operCreatePeople()
	{
		String sql =
			"SELECT gen_id(sys_operation_n, 1) as Id\n" +
				"FROM rdb$database\n";
		try (
			Connection con = DB.getConnection()
		) {
			return qr.query(con, sql, result -> {
				result.next();
				return result.getLong("Id");
			});
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	// Ищем id для удаления и обновления и поиска данных о человеке в таблице
	// TODO каким образом находить BAS_PEOPLE_N для inserta в таблицу NBC_PATIENTS
	public Long searchId (Consultation consultation)
	{
		try (Connection con = DB.getConnection())
		{
			StringBuilder sql =  new StringBuilder();
			sql.append("SELECT n as ID\n")
				.append("FROM bas_people\n")
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
				params = new Object[]{consultation.getName(), consultation.getSurname(),
					consultation.getPatronymic()};
			}

			return qr.query(con, sql.toString(), result -> {
				result.next();
				return result.getLong("ID");
			},params);
		} catch (SQLException e)
		{
			throw new IllegalStateException(e);
		}
	}

	public boolean deletePeople(Consultation consultation)
	{
		String sql =
			"DELETE FROM bas_people\n" +
				"WHERE n = ?\n";
		try (
			Connection con = DB.getConnection()
		) {
			Object[] params = new Object[]{searchId(consultation)};
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

	// Обновление данных
	// TODO Непонятно какие данные будут обновляться, так как еще не продумано что будет в форме редактирования
	public boolean updatePeople(Consultation consultation)
	{
		String sql =
			"UPDATE bas_people SET\n" +
				"OP_CREATE = ?\n" +
				"NAME = ?,\n" +
				"SURNAME = ?,\n" +
				"PATRONYMIC  = ?,\n " +
				"BIRTHDAY  = ?,\n" +
				"SEX = ?,\n" +
				"CITIZENSHIP = ?,\n"+
				"JOB = ?,\n" +
				"OBIT = ?,\n" +
				"WHERE N = ?\n"; // ID
		try (
			Connection con = DB.getConnection()
		)
		{
			String birthdayPeople = formatter.format(consultation.getBirthday());
			Object[] params = new Object[]{consultation.getName(), consultation.getSurname(),
				consultation.getPatronymic(), birthdayPeople,null,null,null,null,
				searchId(consultation)};
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

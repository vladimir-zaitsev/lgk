package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.entity.People;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Created by user on 08.04.2016.
 */
public class PeopleManager {
	private final QueryRunner qr = new QueryRunner();
	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Добавить данные о человеке в таблицу bas_people
	 * @param con,people,genIdPeople,genIdOperation
	 * @return Успешность добавления
	 **/
	public boolean insertPeople( Connection con,People people,Long genIdPeople,Long genIdOperation) {
		String sql =
			"INSERT INTO bas_people\n" +
				"(N, OP_CREATE, NAME, SURNAME,PATRONYMIC, BIRTHDAY, SEX,\n" +
				"CITIZENSHIP, JOB, OBIT)\n" +
				"VALUES (?,?,?,?,?,?,?,?,?,?)\n";
		try {
			String birthdayPeople = null;
			if (people.getBirthday()!=null) {
				birthdayPeople = formatter.format(people.getBirthday());
			}
			Object[] params = new Object[]{
				genIdPeople,
				genIdOperation,
				people.getName(),
				people.getSurname(),
				people.getPatronymic(),
				birthdayPeople,
				null,
				null,
				null,
				null};
			int updateRows = qr.update(con, sql, params);
			if (updateRows == 0) {
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


	// Когда удалять человека????
	/**
	 * Удалить человека из  таблицы bas_people
	 * @param people - человек
	 * @return Успешность удаления
	 **/
	public boolean deletePeople(People people) {
		String sql =
			"DELETE FROM bas_people\n" +
				"WHERE n = ?\n";
		try (Connection con = DB.getConnection()) {
			Object[] params = new Object[]{};
			int updateRows = qr.update(con, sql, params); // количество удаленных строчек
			if(updateRows == 0) {
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
	/**
	 * Обновить данные человека в таблице bas_people
	 * @param people - человек
	 * @return Успешность обновления
	 **/
	public boolean updatePeople(People people) {
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
		try (Connection con = DB.getConnection()) {
			String birthdayPeople = formatter.format(people.getBirthday());
			Object[] params = new Object[]{people.getName(), people.getSurname(),
				people.getPatronymic(), birthdayPeople,null,null,null,null,
				people.getN()};
			int updateRows = qr.update(con, sql, params);
			if(updateRows == 0) {
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

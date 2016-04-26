package lgk.nsbc.ru.backend;

import com.vaadin.ui.Notification;
import lgk.nsbc.ru.backend.entity.People;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by user on 08.04.2016.
 */
public class PeopleManager {

	private final QueryRunner qr = new QueryRunner();
	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	public static final String peopleTableName ="bas_people";
	private static final String genNameOperation = "sys_operation_n";
	public static final String commandInsert = "bas_people_put";
	public static final String commandDelete = "bas_people_del";

	public final String lgkSessId;

	public  PeopleManager(String lgkSessId)
	{
		this.lgkSessId = lgkSessId;
	}
	/**
	 * Добавить данные о человеке в таблицу bas_people
	 * @param con,people
	 * @return id - человека
	 **/
	public void insertPeople(Connection con
		,People people
	) throws SQLException
	{
		final Long
			genIdPeople = GeneratorManager.instace.genId(peopleTableName+"_n")
		;
		final Long
			genIdOperation = GeneratorManager.instace.genId(genNameOperation)
		;
		RegistrationManager.instace.regOperation(con,genIdOperation,commandInsert,lgkSessId);
		String sql =
			"INSERT INTO "+peopleTableName+"\n"+
				"( " +
				People.Props.n.toString()+",\n"+
				People.Props.op_create.toString()+",\n"+
				People.Props.name.toString()+ ",\n"+
				People.Props.surname.toString()+ ",\n"+
				People.Props.patronymic.toString()+ ",\n"+
				People.Props.birthday.toString()+ ",\n"+
				People.Props.sex.toString()+ ",\n"+
				People.Props.citizenship.toString()+",\n"+
				People.Props.job.toString()+ ",\n"+
				People.Props.obit.toString()+
				" )" +"\n"+
				"VALUES (?,?,?,?,?,?,?,?,?,?)\n";
		String birthdayPeople = null;
		if (people.getBirthday()!= null) {
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
			null
		};
		qr.update(con, sql, params);
		people.setN(genIdPeople);
	}

	/**
	 * Удалить человека из  таблицы bas_people
	 * @param people - человек
	 **/
	public void deletePeople(People people) {
		String sql =
			"DELETE FROM "+ peopleTableName+"\n"+
				"WHERE n = ?\n";

		try (Connection con = DB.getConnection()
		) {
			Long genIdOperation =
				GeneratorManager.instace.genId(genNameOperation)
			;
			RegistrationManager.instace.regOperation(con,genIdOperation,commandDelete,lgkSessId);
			Object[] params = new Object[]{people.getN()};
			qr.update(con, sql, params);
			con.commit();

		}
		catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE,"Problems with database",e);
			Notification.show("Problems with database", Notification.Type.WARNING_MESSAGE);
		}
	}
	/**
	 * Обновить данные человека в таблице bas_people
	 * @param people - человек
	 **/
	public void updatePeople(People people)
	{
		String sql =
			"UPDATE " + peopleTableName + " SET" + "\n" +
				People.Props.op_create.toString() + " = ?,\n" +
				People.Props.name.toString() + " = ?,\n"  +
				People.Props.surname.toString() + " = ?,\n" +
				People.Props.patronymic.toString() +" = ?,\n"+
				People.Props.birthday.toString() +" = ?,\n" +
				People.Props.sex.toString() + " = ?,\n" +
				People.Props.citizenship.toString() +" = ?,\n"+
				People.Props.job.toString() +" = ?,\n" +
				People.Props.obit.toString() + " = ?\n"+
				"WHERE "+ People.Props.n.toString()+ " = ?\n";
		try (Connection con = DB.getConnection()
		) {
			Long genIdOperation =
				GeneratorManager.instace.genId(genNameOperation)
			;
			RegistrationManager.instace.regOperation(con,genIdOperation,commandInsert,lgkSessId);
			String birthdayPeople = null;
			if (people.getBirthday() != null) {
				birthdayPeople = formatter.format(people.getBirthday());
			}
			Object[] params = new Object[]{people.getName()
				, people.getSurname()
				, people.getPatronymic()
				, birthdayPeople
				, null
				, null
				, null
				, null
				, people.getN()
			};
			qr.update(con, sql, params);
			con.commit();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE,"Problems with database",e);
			Notification.show("Problems with database", Notification.Type.WARNING_MESSAGE);
		}
	}
}

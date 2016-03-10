package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.entity.Patient;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class PatientsManager {

	public Collection<? extends Patient> listPatients() {
		try (
			Connection con = DB.getConnection()
		) {
			QueryRunner qr = new QueryRunner();
			BeanListHandler<Patient> handler = new BeanListHandler<>(Patient.class);
			return qr.query(con, "select first 5 * from "+Patient.relationName, handler);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	/*
    * вытаскивыем данные пациента(name, patronymic, surname)из базы
    *@param строчка, по которой ищем пациента
    * @return List пациентов
    */
	public List<Patient> listPatients(String filterPrefix) {
		try (
			Connection con = DB.getConnection()
		) {

			String sql =
				"SELECT name, patronymic, surname,birthday\n" +
					"FROM bas_people\n" +
					"WHERE UPPER(surname) LIKE ?\n"
				;
			QueryRunner qr = new QueryRunner();
			BeanListHandler<Patient> handler = new BeanListHandler<>(Patient.class);
			return qr.query(con, sql, handler, filterPrefix.toUpperCase() + "%");
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}

	}
}

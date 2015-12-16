package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.entity.Patient;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

public class PatientsManager {

	public Collection<? extends Patient> listPatients() {
		try (
			Connection con = DB.getConnection()
		) {
			QueryRunner qr = new QueryRunner();
			BeanListHandler<Patient> handler = new BeanListHandler<>(Patient.class);
			return qr.query(con, "select first 5 * from "+Patient.relationName, handler);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}

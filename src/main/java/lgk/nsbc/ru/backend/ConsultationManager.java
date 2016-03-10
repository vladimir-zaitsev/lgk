package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.Patient;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

/**
 * Created by user on 20.02.2016.
 */


public class ConsultationManager {


	public Collection<? extends Consultation> listConsultation(Date fromDate, Date toDate) // дата будет браться от 01.01.2016 и 20.02.2016
	{

		try (
			Connection con = DB.getConnection()
		) {
			QueryRunner qr = new QueryRunner();
			String sql = "SELECT\n" +
				"procbegintime, procendtime,\n" +
				"surname,name,patronymic,\n" +
				"case_history_num,\n" +
				"diagnosis,birthday\n" +
				" FROM bas_people\n" +
				" JOIN nbc_patients  on  bas_people.n = nbc_patients.bas_people_n\n" +
				" LEFT JOIN  nbc_proc on  nbc_proc.nbc_patients_n = nbc_patients.n\n" +
				" WHERE nbc_proc.proc_type = 4\n" +
				"\n" +
				"AND  nbc_proc.procbegintime between '%s' and '%s'\n" +
				"AND nbc_proc.procendtime is not NULL";

			String to = Util.getDate(toDate);
			String from = Util.getDate(fromDate);
			sql = String.format(sql, from, to);
			BeanListHandler<Consultation> handler = new BeanListHandler<>(Consultation.class); //
			return qr.query(con, sql, handler);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	/*
  * вытаскивает данные уже существующего пациента из базы
  *@param параметры пациента, которого нашли ФИО, дата рождения
  *@return
  */
	public Patient selectpatient (String name,String surname,String patronymic,Date birthday)
	{
		try (
			Connection con = DB.getConnection()
		) {
			QueryRunner qr = new QueryRunner();
			String sql =
				"SELECT\n" +
					"     name\n" +
					"    ,surname\n" +
					"    ,patronymic\n" +
					"    ,birthday\n" +
					"    ,diagnosis\n" +
					"    ,case_history_num\n" +
					"FROM bas_people\n" +
					"JOIN nbc_patients  on  bas_people.n = nbc_patients.bas_people_n\n" +
					"LEFT JOIN  nbc_proc on  nbc_proc.nbc_patients_n = nbc_patients.n\n" +
					"WHERE nbc_proc.proc_type = 4\n" +
					"AND name = ?\n" +
					"AND surname = ?\n" +
					"AND patronymic = ?\n"+
					"AND birthday = ?\n";
			String birthdayPatient =  Util.getDate(birthday);
			Object[] params = new Object[]{name,surname,patronymic,birthdayPatient};
			BeanHandler<Patient> handler = new BeanHandler<>(Patient.class);
			return qr.query(con,sql,handler,params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

}
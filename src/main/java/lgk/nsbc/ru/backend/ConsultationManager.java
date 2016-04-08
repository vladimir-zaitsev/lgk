package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.entity.Consultation;
import lgk.nsbc.ru.backend.entity.Patient;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 20.02.2016.
 */


public class ConsultationManager {

	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	public List<Consultation> listConsultation(Date fromDate, Date toDate) // дата будет браться от 01.01.2016 и 20.02.2016
	{
		try (
			Connection con = DB.getConnection()
		) {
			QueryRunner qr = new QueryRunner();
			con.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
				sql.append("SELECT\n")
					.append("	procbegintime, procendtime,\n")
					.append("	surname,name,patronymic,\n")
					.append("	case_history_num,\n")
					.append("	diagnosis,birthday\n")
					.append("FROM bas_people\n")
					.append("JOIN nbc_patients  on  bas_people.n = nbc_patients.bas_people_n\n")
					.append("LEFT JOIN  nbc_proc on  nbc_proc.nbc_patients_n = nbc_patients.n\n")
					.append("WHERE nbc_proc.proc_type = 4\n")
					.append("	AND nbc_proc.procbegintime between ? and ?\n")
					.append("	AND nbc_proc.procendtime is not NULL");
			BeanListHandler<Consultation> handler = new BeanListHandler<>(Consultation.class);

			return qr.query(con, sql.toString(), handler
				, new java.sql.Timestamp(fromDate.getTime())
				, new java.sql.Timestamp(toDate.getTime())
			);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

	}

	/*
  * вытаскивает данные уже существующего пациента из базы
  *@param параметры пациента, которого нашли ФИО, дата рождения
  *@return
  */
	// Это никогда не будет нормально работать, сама идея запроса неверная.
	public Patient selectPatient(String name, String surname, String patronymic, Date birthday)
	{
		try (Connection con = DB.getConnection()) {
			QueryRunner qr = new QueryRunner();
			StringBuilder sql =  new StringBuilder();
				sql.append("SELECT\n")
					.append("     name\n")
					.append("    ,surname\n")
					.append("    ,patronymic\n")
					.append("    ,birthday\n")
					.append("    ,diagnosis\n")
					.append("    ,case_history_num\n")
					.append("FROM bas_people\n")
					.append("JOIN nbc_patients  on  bas_people.n = nbc_patients.bas_people_n\n")
					.append("LEFT JOIN  nbc_proc on  nbc_proc.nbc_patients_n = nbc_patients.n\n")
					.append("WHERE name = ?\n")
					.append("AND surname = ?\n")
					.append("AND patronymic = ?\n");
			Object[] params;
			if (birthday!=null) {
				String birthdayPatient = formatter.format(birthday);
				sql.append("AND birthday = ?\n");
				params = new Object[]{name,surname,patronymic,birthdayPatient};
			} else {
				params = new Object[]{name,surname,patronymic};
			}
			BeanHandler<Patient> handler = new BeanHandler<>(Patient.class);
			return qr.query(con,sql.toString(),handler,params);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
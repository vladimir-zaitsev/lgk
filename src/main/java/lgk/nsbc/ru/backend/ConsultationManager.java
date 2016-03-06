package lgk.nsbc.ru.backend;

import lgk.nsbc.ru.backend.entity.Consultation;
import org.apache.commons.dbutils.QueryRunner;
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
    // Добавление данных в таблицу пациентов
    // Добавление не всех данных из таблицы а часть?
    public int insertConsultation(Consultation consultation) {
        try (
                Connection con = DB.getConnection()
        ) {
            QueryRunner qr = new QueryRunner();
            String sql = "insert INTO  nbc_patients() VALUES(?,?,?,?) ";
            Object[] params = new Object[]{consultation.getProcbegintime(), consultation.getProcendtime(), consultation.getName(),
                    consultation.getSurname(), consultation.getPatronymic(), consultation.getDiagnosis(),
                    consultation.getCase_history_num(), consultation.getBirthday()};
            int updateRows = qr.update(con, sql, params); // количество обновлевленных строчек
            return updateRows;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // ПРоверка что существует пациент
   // еще должен быть какой то параметр(номер истории болезни),потому что есть человек у которого совпадают фвмилия имя  отчество
    public  Collection<? extends Consultation> listpatient (String name,String surname,String patronymic,int case_history_num)
    {
        try (
                Connection con = DB.getConnection()
        ) {
            QueryRunner qr = new QueryRunner();
            String sql = "select   where ";
            Object[] params = new Object[]{};
            BeanListHandler<Consultation> handler = new BeanListHandler<>(Consultation.class);
            return qr.query(con, sql,  handler);
            // Что возвращать ? количество строк или данные выбирая и еще другик данные
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    */



}
package lgk.nsbc.ru.backend;

import com.vaadin.ui.Notification;
import lgk.nsbc.ru.backend.db.DB;
import lgk.nsbc.ru.backend.entity.Patient;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PatientsManager {

	public static final String patientsTableName = "nbc_patients";
	public static final String generatorName = "nbc_patients_n";
	public static final String commandInsert = "nbc_patients_put";
	private static final String genNameOperation = "sys_operation_n";
	public static final String commandDelete = "nbc_patients_del";
	private final QueryRunner qr = new QueryRunner();
	private final ScalarHandler<Long> rs = new ScalarHandler<>();
	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	public final String lgkSessId;

	public  PatientsManager (String lgkSessId)
	{
		this.lgkSessId = lgkSessId;
	}

	public List<Patient> listPatients()
	{
		try (
			Connection con = DB.getConnection()
		)
		{

			BeanListHandler<Patient> handler = new BeanListHandler<>(Patient.class);
			return qr.query(con, "select first 5 * from "+Patient.relationName, handler);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Ищет данные пациентов из таблицы nbc_patients
	 *@param filterPrefix - строчка, по которой ищем пациентов
	 *@return cписок пациентов
	 **/
	public List<Patient> listPatients(String filterPrefix) {
		try (
			Connection con = DB.getConnection()
		) {

			String sql =
				"SELECT nbc_patients.n,\n" +
					"name,\n"+
					"patronymic,\n" +
					"surname," +
					"birthday,\n" +
					"nbc_organizations_n\n" +
					"FROM nbc_patients\n" +
					"JOIN bas_people ON bas_people.n = nbc_patients.bas_people_n\n" +
					"WHERE UPPER(surname) LIKE ?\n" +
				    "AND nbc_organizations_n = ?";
			Object[] params = new Object[]{filterPrefix.toUpperCase() + "%",selectOrganization(con)};
			BeanListHandler<Patient> handler = new BeanListHandler<>(Patient.class);
			return qr.query(con, sql, handler,params);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Найти пациента по N из таблицы nbc_patients
	 * @param patient - пациент
	 * @return пациента
	 **/
	public Patient selectPatient(Patient patient) {

		try (Connection con = DB.getConnection())
		{
			StringBuilder sql =  new StringBuilder();
			sql.append("SELECT nbc_patients.n,\n")
				.append("name,\n")
				.append("sex,\n")
				.append("surname,\n")
				.append("patronymic,\n")
				.append("birthday,\n")
				.append("diagnosis,\n")
				.append("case_history_num\n")
				.append("FROM nbc_patients\n")
				.append("JOIN bas_people on bas_people.n = nbc_patients.bas_people_n\n")
				.append("WHERE nbc_patients.n = ?\n")
			    .append("AND nbc_organizations_n = ?\n");
			Object[] params = new Object[]{patient.getN(),selectOrganization(con)};
			BeanHandler<Patient> handler = new BeanHandler<>(Patient.class);
			return qr.query(con,sql.toString(),handler,params);
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}


	/**
	 * Добавить данные о пациенте в таблицу nbc_patients
	 * @param con,patient
	 * @return id-пациента
	 **/
	public void insertPatient(Connection con
		,Patient patient
	) throws SQLException
	{
		final Long genIdPatient =
			GeneratorManager.instace.genId(patientsTableName+"_n")
		;
		final Long genIdOperation =
			GeneratorManager.instace.genId(genNameOperation)
		;
		RegistrationManager.instace.regOperation(con,genIdOperation,commandInsert,lgkSessId);
		String sql =
			"INSERT INTO "+patientsTableName+"\n"+
		    "( " +
		    Patient.Props.n.toString()+",\n"+
		    Patient.Props.op_create.toString()+",\n"+
		    Patient.Props.nbc_organizations_n.toString()+",\n"+
		    Patient.Props.nbc_staff_n.toString()+",\n"+
	     	Patient.Props.case_history_num.toString()+",\n"+
		    Patient.Props.case_history_date.toString()+",\n"+
		    Patient.Props.bas_people_n.toString()+",\n"+
		    Patient.Props.represent.toString()+",\n"+
		    Patient.Props.represent_telephone.toString()+",\n"+
		    Patient.Props.diagnosis.toString()+",\n"+
		    Patient.Props.nbc_diagnosis_n.toString()+",\n"+
		    Patient.Props.full_diagnosis.toString()+",\n"+
		    Patient.Props.stationary.toString()+",\n"+
		    Patient.Props.allergy.toString()+",\n"+
		    Patient.Props.information_source.toString()+",\n"+
		    Patient.Props.folder.toString()+",\n"+
		    Patient.Props.disorder_history.toString()+",\n"+
		    Patient.Props.nbc_diag_2015_n.toString()+",\n"+
		    Patient.Props.nbc_diag_loc_n.toString()+"\n"+
		    " )" +"\n"+
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)\n";
		Object[] params = new Object[]{
			genIdPatient,
			genIdOperation,
			selectOrganization(con),
			null,
			null,
			null,
			patient.getCurrentPeople().getN(),
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null,
			null
		};
		qr.update(con, sql, params);
		patient.setN(genIdPatient);
	}
	// Нахождение организации
	public Long selectOrganization(Connection con) throws SQLException
	{
		String sql = "SELECT SYS_CONST.data_bigint as n\n"+
			"FROM sys_const\n"+
			"LEFT JOIN nbc_organizations on nbc_organizations.n = sys_const.data_bigint\n"+
			"WHERE sys_const.name = ?";
			String name = "NBC_ORGANIZATIONS_MAIN_N";
			Object[] params = new Object[]{name};
			return qr.query(con, sql,rs,params);
	}

	 /**
	 * Удалить данные о пациенте из таблицы nbc_patients
	 * @param patient - пациент
	 **/
	public void deletePatient(Patient patient)
	{
		try (
			Connection con = DB.getConnection()
		) {
			con.setAutoCommit(false);
			String sql = "DELETE FROM "+patientsTableName+"\n" +
				"WHERE n = ?\n";
			Long genIdOperation =
				GeneratorManager.instace.genId(genNameOperation)
			;
			RegistrationManager.instace.regOperation(con,genIdOperation,commandDelete,lgkSessId);
			Object[] params = new Object[]{patient.getN()};
			 qr.update(con, sql, params);
			con.commit();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE,"Problems with database",e);
			Notification.show("Problems with database", Notification.Type.WARNING_MESSAGE);
		}

	}
	/**
	 * Обновить данные пациента из таблицы nbc_patients
	 * @param patient - пациент
	 **/
	public void updatePatient(Patient patient)
	{
		String sql = "UPDATE "+patientsTableName+" SET" + "\n" +
			Patient.Props.op_create.toString()+" = ?,\n"+
			Patient.Props.nbc_organizations_n.toString()+" = ?,\n"+
			Patient.Props.nbc_staff_n.toString()+" = ?,\n"+
			Patient.Props.case_history_num.toString()+" = ?,\n"+
			Patient.Props.case_history_date.toString()+" = ?,\n"+
			Patient.Props.bas_people_n.toString()+" = ?,\n"+
			Patient.Props.represent.toString()+" = ?,\n"+
			Patient.Props.represent_telephone.toString()+" = ?,\n"+
			Patient.Props.diagnosis.toString()+" = ?,\n"+
			Patient.Props.nbc_diagnosis_n.toString()+" = ?,\n"+
			Patient.Props.full_diagnosis.toString()+" = ?,\n"+
			Patient.Props.stationary.toString()+" = ?,\n"+
			Patient.Props.allergy.toString()+" = ?,\n"+
			Patient.Props.information_source.toString()+" = ?,\n"+
			Patient.Props.folder.toString()+" = ?,\n"+
			Patient.Props.disorder_history.toString()+" = ?,\n"+
			Patient.Props.nbc_diag_2015_n.toString()+" = ?,\n"+
			Patient.Props.nbc_diag_loc_n.toString()+" = ?\n"+
			"WHERE " + Patient.Props.n.toString()+" = ?\n";
		try (
			Connection con = DB.getConnection()
		)
		{
			con.setAutoCommit(false);
			Long genIdOperation =
				GeneratorManager.instace.genId(genNameOperation)
			;
			RegistrationManager.instace.regOperation(con,genIdOperation,commandInsert,lgkSessId);
			// TODO Параметры
			Object[] params = new Object[]{};
			qr.update(con, sql, params);
			con.commit();
		} catch (SQLException e) {
			Logger.getGlobal().log(Level.SEVERE,"Problems with database",e);
			Notification.show("Problems with database", Notification.Type.WARNING_MESSAGE);
		}
	}
}

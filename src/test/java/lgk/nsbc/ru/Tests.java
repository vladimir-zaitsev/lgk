package lgk.nsbc.ru;

import lgk.nsbc.ru.backend.DB;
import lgk.nsbc.ru.backend.I18nManager;
import lgk.nsbc.ru.backend.PatientsManager;
import lgk.nsbc.ru.backend.entity.Patient;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class Tests {

	//@Ignore
	@Test
	public void conn() throws SQLException {
		//noinspection UnnecessarySemicolon
		try (
			Connection con = DB.getConnection();
		){
			PreparedStatement st = con.prepareStatement("select first ? * from bas_people");
			st.setInt(1, 5);
			ResultSet rs = st.executeQuery();
			String s = null;
			while (rs.next()){
				//System.out.println(rs.getString("name"));
				s = rs.getString("name");
			}
			rs.close();

			Assert.assertEquals("5-я Татьяна","Татьяна", s);
		}
	}

	@Ignore
	@Test
	public void patients(){
		Collection<? extends Patient> patients = new PatientsManager().listPatients();
		System.out.println(patients);
	}

	@Ignore
	@Test
	public void caption(){
		System.out.println(new I18nManager().getCaption(Patient.relationName, Patient.Props.birthday.toString()));
	}



}

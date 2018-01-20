/**
 * 
 */
package com.amazonaws.lambda.apigatewayexample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *  
 * 
 * @author shardulsrivastava
 *
 */
public class RegistrationService {

	public String saveRegistration(RegistrationDetails registrationDetails) {

		List<String> errorMessage = new ArrayList<String>();
		try {
			Class.forName("org.postgresql.Driver");
		} catch (java.lang.ClassNotFoundException e) {
			errorMessage.add(e.getMessage());
		}

		String url = "jdbc:postgresql://baasu.db.elephantsql.com:5432/hoplxtxd";
		String username = "hoplxtxd";
		String password = "c48EBAe4f-gZ17sY9FPfoYllIh0mZacU";

		String SQL = "INSERT INTO course_registration(message,phoneno,email,lastname,firstname,coursename,created_on ) "
				+ "VALUES(?,?,?,?,?,?,?)";

		long id = 0;

		try (Connection conn = DriverManager.getConnection(url, username, password);
				PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

			pstmt.setString(1, registrationDetails.getMessage());
			pstmt.setString(2, registrationDetails.getPhoneNumber());
			pstmt.setString(3, registrationDetails.getEmail());
			pstmt.setString(4, registrationDetails.getLastName());
			pstmt.setString(5, registrationDetails.getFirstName());
			pstmt.setString(6, registrationDetails.getCourseName());
			pstmt.setTimestamp(7, registrationDetails.getCreatedOn());

			int affectedRows = pstmt.executeUpdate();
			// check the affected rows
			if (affectedRows > 0) {
				// get the ID back
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						id = rs.getLong(1);
					}
				} catch (SQLException ex) {
					errorMessage.add(ex.getMessage());
					System.out.println(ex);
				}
			}
		} catch (SQLException ex) {
			errorMessage.add(ex.getMessage());

		}

		return getErrorFields(errorMessage);
	}

	public String getErrorFields(List<String> error) {
		String errorString = error.toString();
		errorString = errorString.substring(1, errorString.length() - 1);
		return errorString;
	}
}

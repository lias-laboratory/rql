package fr.ensma.lias.rql.dao.userdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import fr.ensma.lias.rql.dao.postgressession.IPostgresSession;
import fr.ensma.lias.rql.dto.User;

/**
 * @author Bilal REZKELLAH
 */
public class UserDAO implements IUserDAO {

	@Inject
	IPostgresSession ips;

	@Override
	public boolean ifUserNameExist(String userName) throws SQLException {
		Connection conn = ips.getConnection();
		String query = "SELECT * FROM users WHERE username = ? ";
		PreparedStatement pstmt = conn.prepareStatement(query);
		pstmt.setString(1, userName);
		ResultSet rs = pstmt.executeQuery();
		return rs.isBeforeFirst();
	}

	@Override
	public String getPasswordHash(String userName) throws SQLException {
		Connection conn = ips.getConnection();
		String query = "SELECT hashpassword FROM users WHERE username = ? ";
		PreparedStatement pstmt = conn.prepareStatement(query);
		pstmt.setString(1, userName);
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		return rs.getString("hashpassword");
	}

	@Override
	public List<User> getAllUsers(String userid) throws SQLException {
		Connection conn = ips.getConnection();
		List<User> userList = new ArrayList<User>();
		User user;
		String query = "SELECT username,Lastname,Firstname FROM users WHERE username != ?";
		PreparedStatement pstmt = conn.prepareStatement(query);
		pstmt.setString(1, userid);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			user = new User();
			user.setUsername(rs.getString("username"));
			user.setLastName(rs.getString("lastname"));
			user.setFirstName(rs.getString("firstname"));
			userList.add(user);
		}
		return userList;
	}

	@Override
	public String createCollaborator(String projectID, String userID) throws SQLException {
		String SQL = "INSERT INTO collaborator (projectid,userid) " + "VALUES(?,?)";
		long id = 0;
		Connection conn = ips.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
		pstmt.setInt(1, Integer.valueOf(projectID));
		pstmt.setString(2, userID);
		int affectedRows = pstmt.executeUpdate();
		// check the affected rows
		if (affectedRows > 0) {
			// get the ID back
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getLong(1);
			}
		}
		return Long.toString(id);
	}

	@Override
	public boolean deleteCollabortor(String projectID, String userID) throws SQLException {
		String SQL = "DELETE FROM collaborator WHERE projectid = ? and userid = ?";
		Connection conn = ips.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setInt(1, Integer.valueOf(projectID));
		pstmt.setString(2, userID);
		return pstmt.execute();
	}

	@Override
	public List<User> getCollaborators(String projectID, String userID) throws SQLException {
		Connection conn = ips.getConnection();
		User user;
		List<User> userList = new ArrayList<User>();
		String query = "select username,lastname,firstname from collaborator c, users u where c.userid = u.username and c.userid != ? and c.projectid = ?";
		PreparedStatement pstmt = conn.prepareStatement(query);
		pstmt.setString(1, userID);
		pstmt.setInt(2, Integer.valueOf(projectID));
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			user = new User();
			user.setUsername(rs.getString("username"));
			user.setFirstName(rs.getString("firstname"));
			user.setLastName(rs.getString("lastname"));
			userList.add(user);
		}
		return userList;
	}
}

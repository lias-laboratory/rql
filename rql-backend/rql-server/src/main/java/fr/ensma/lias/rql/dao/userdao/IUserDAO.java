package fr.ensma.lias.rql.dao.userdao;

import java.sql.SQLException;
import java.util.List;

import fr.ensma.lias.rql.dto.User;

/**
 * @author Bilal REZKELLAH
 */
public interface IUserDAO {

	boolean ifUserNameExist(String userName) throws SQLException;

	String getPasswordHash(String userName) throws SQLException;

	List<User> getAllUsers(String userid) throws SQLException;

	String createCollaborator(String projectID, String userID) throws SQLException;

	boolean deleteCollabortor(String projectID, String userID) throws SQLException;

	List<User> getCollaborators(String projectID, String userID) throws SQLException;

}

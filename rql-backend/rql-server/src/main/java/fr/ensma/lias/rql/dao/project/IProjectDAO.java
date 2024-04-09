package fr.ensma.lias.rql.dao.project;

import java.sql.SQLException;
import java.util.List;

import fr.ensma.lias.rql.dto.Project;

/**
 * @author Bilal REZKELLAH
 */
public interface IProjectDAO {

	List<Project> getAllProjectByUser(String UserID) throws SQLException;

	String createProject(Project project) throws SQLException;

	Project getProjectById(String projectID) throws SQLException;

	boolean deleteProjectbyId(String projectID) throws SQLException;

	boolean updateProject(Project project) throws SQLException;

}

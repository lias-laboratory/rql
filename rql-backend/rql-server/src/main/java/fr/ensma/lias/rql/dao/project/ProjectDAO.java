package fr.ensma.lias.rql.dao.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import fr.ensma.lias.rql.cfg.IConfiguration;
import fr.ensma.lias.rql.dao.postgressession.IPostgresSession;
import fr.ensma.lias.rql.dao.userdao.IUserDAO;
import fr.ensma.lias.rql.dto.Project;

/**
 * @author Bilal REZKELLAH
 */
public class ProjectDAO implements IProjectDAO {

	@Inject
	IConfiguration cfg;

	@Inject
	IPostgresSession ips;

	@Inject
	IUserDAO iuser;

	@Override
	public List<Project> getAllProjectByUser(String UserID) throws SQLException {
		Project project;
		List<Project> projectList = new ArrayList<Project>();
		Connection conn = ips.getConnection();
		String query = "SELECT * FROM PROJECT WHERE userid = ? union select p.* from project p,collaborator c where c.userid = ? and c.projectid = p.projectid ";
		PreparedStatement pstmt = conn.prepareStatement(query);
		pstmt.setString(1, UserID);
		pstmt.setString(2, UserID);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			project = new Project();
			project.setName(rs.getString("projectname"));
			project.setCreationDate(rs.getString("creationdate"));
			project.setId(rs.getString("projectid"));
			project.setCollaborators(iuser.getCollaborators(rs.getString("projectid"), UserID));
			project.setUserid(rs.getString("userid"));
			project.setDescription(rs.getString("description"));
			projectList.add(project);
		}
		return projectList;
	}

	@Override
	public String createProject(Project project) throws SQLException {
		String SQL = "INSERT INTO PROJECT (projectname,creationdate,userid,description) " + "VALUES(?,?,?,?)";
		long id = 0;
		Connection conn = ips.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
		pstmt.setString(1, project.getName());
		pstmt.setString(2, project.getCreationDate());
		pstmt.setString(3, project.getUserid());
		pstmt.setString(4, project.getDescription());
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
	public Project getProjectById(String projectID) throws SQLException {
		Project project;
		Connection conn = ips.getConnection();
		String query = "SELECT * FROM PROJECT WHERE projectid = ? ";
		PreparedStatement pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, Integer.valueOf(projectID));
		ResultSet rs = pstmt.executeQuery();
		rs.next();
		project = new Project();
		project.setName(rs.getString("projectname"));
		project.setCreationDate(rs.getString("creationdate"));
		project.setId(rs.getString("projectid"));
		project.setUserid(rs.getString("userid"));
		project.setDescription(rs.getString("description"));

		return project;
	}

	@Override
	public boolean deleteProjectbyId(String projectID) throws SQLException {
		String SQL = "DELETE FROM PROJECT WHERE projectid = ? ";
		String SQL2 = "DELETE FROM DATABASE WHERE projectid = ? ";
		String SQL3 = "DELETE FROM collaborator WHERE projectid = ? ";
		String SQL4 = "DELETE FROM favorite WHERE projectid = ? ";

		Connection conn = ips.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL2);
		pstmt.setInt(1, Integer.valueOf(projectID));
		pstmt.execute();
		pstmt = conn.prepareStatement(SQL3);
		pstmt.setInt(1, Integer.valueOf(projectID));
		pstmt.execute();
		pstmt = conn.prepareStatement(SQL4);
		pstmt.setInt(1, Integer.valueOf(projectID));
		pstmt.execute();
		pstmt = conn.prepareStatement(SQL);
		pstmt.setInt(1, Integer.valueOf(projectID));
		return pstmt.execute();
	}

	@Override
	public boolean updateProject(Project project) throws SQLException {
		String SQL = "UPDATE PROJECT SET projectname = ?, description = ? WHERE projectid = ? ";
		Connection conn = ips.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, project.getName());
		pstmt.setInt(3, Integer.valueOf(project.getId()));
		pstmt.setString(2, project.getDescription());
		return pstmt.execute();
	}
}

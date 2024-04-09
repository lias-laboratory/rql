package fr.ensma.lias.rql.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import fr.ensma.lias.rql.api.ProjectResource;
import fr.ensma.lias.rql.cfg.IConfiguration;
import fr.ensma.lias.rql.core.IRql;
import fr.ensma.lias.rql.dao.dbconfiguration.IConfigurationDAO;
import fr.ensma.lias.rql.dao.dbschema.favorite.IFavoriteDAO;
import fr.ensma.lias.rql.dao.postgressession.IPostgresSession;
import fr.ensma.lias.rql.dao.project.IProjectDAO;
import fr.ensma.lias.rql.dao.userdao.IUserDAO;
import fr.ensma.lias.rql.dto.Favorite;
import fr.ensma.lias.rql.dto.Project;
import fr.ensma.lias.rql.dto.User;
import fr.ensma.lias.rql.model.DataBaseConfig;

/**
 * @author Bilal REZKELLAH
 */
public class ProjectResourceImp implements ProjectResource {

	@Inject
	IProjectDAO iproject;

	@Inject
	IUserDAO iuser;

	@Inject
	IFavoriteDAO ifavorite;

	@Inject
	IPostgresSession ips;

	@Inject
	IConfigurationDAO icd;

	@Inject
	IConfiguration cfg;

	@Inject
	IRql irql;

	@Override
	public List<Project> getAllProjectByUserID(String userID) {

		try {
			return iproject.getAllProjectByUser(userID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(response);
		}
	}

	@Override
	public String createNewProject(Project project) {
		String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
		project.setCreationDate(date);
		String projectID = "";
		try {
			if (project.getDbType().toLowerCase().equals("postgresql")) {
				projectID = iproject.createProject(project);
				String dbName = project.getName() + projectID;
				DataBaseConfig dbc = new DataBaseConfig(projectID,
						irql.normalizeName(dbName, project.getDbType().toLowerCase()).toLowerCase(),
						cfg.getConfiguration().rqlDbHostMetier(), 5432, "postgres", "postgres",
						project.getDbType().toLowerCase());
				icd.createConfig(dbc);
				for (User user : project.getCollaborators()) {
					iuser.createCollaborator(projectID, user.getUsername());
				}
				String SQL = "CREATE DATABASE " + irql.normalizeName(dbName, project.getDbType().toLowerCase());
				Connection conn = DriverManager.getConnection(
						"jdbc:postgresql://" + cfg.getConfiguration().rqlDbHostMetier() + ":"
								+ cfg.getConfiguration().rqlDbPort() + "/" + "postgres",
						cfg.getConfiguration().rqlDbAdminLogin(), cfg.getConfiguration().rqlDbAdminPwd());
				PreparedStatement pstmt = conn.prepareStatement(SQL);
				pstmt.execute();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projectID;
	}

	@Override
	public Project getProjectById(String id) {
		try {
			return iproject.getProjectById(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(response);
		}
	}

	@Override
	public boolean deleteProjectById(String id) {
		try {
			return iproject.deleteProjectbyId(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(response);
		}
	}

	@Override
	public boolean updateProject(Project project) {
		// TODO Auto-generated method stub
		try {
			return iproject.updateProject(project);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(response);
		}
	}

	@Override
	public List<Favorite> getAllFavoriteByProject(String id) {
		// TODO Auto-generated method stub
		try {
			return ifavorite.getAllFavoriteByProject(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(response);
		}
	}

	@Override
	public boolean deleteFavoriteById(String id) {
		try {
			return ifavorite.deleteFavorite(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(response);
		}
	}

	@Override
	public String createNewFavorite(Favorite favorite) {
		try {
			String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
			favorite.setCreationDate(date);
			if (irql.isRqlQuery(favorite.getQuery())) {
				favorite.setType("RQL");
			} else {
				favorite.setType("SQL");
			}
			return ifavorite.createFavorite(favorite);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(response);
		}
	}

	@Override
	public List<Favorite> getAllFavoriteSqlQueries(String id) {
		try {
			return ifavorite.getAllFavoriteSQLQueries(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(response);
		}
	}

}

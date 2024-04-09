package fr.ensma.lias.rql.service;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import fr.ensma.lias.rql.api.UserResource;
import fr.ensma.lias.rql.dao.userdao.IUserDAO;
import fr.ensma.lias.rql.dto.User;


/**
 * @author Bilal REZKELLAH
 */
public class UserResourceImp implements UserResource {

	@Inject
	IUserDAO iuser;

	@Override
	public List<User> getAllCollaborarors(String userid) {
		try {
			return iuser.getAllUsers(userid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(response);
		}
	}

	@Override
	public boolean deleteCollaborator(String projectid, String username) {
		try {
			return iuser.deleteCollabortor(projectid, username);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(response);
		}
	}

	@Override
	public String addCollaborator(String projectid, String username) {
		try {
			return iuser.createCollaborator(projectid, username);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
			throw new WebApplicationException(response);
		}
	}

	@Override
	public String getCollaborators(String projectid, String username) {
		// TODO Auto-generated method stub
		return null;
	}
}

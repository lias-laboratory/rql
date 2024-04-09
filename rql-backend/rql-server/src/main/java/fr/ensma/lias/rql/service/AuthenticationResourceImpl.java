package fr.ensma.lias.rql.service;

import java.sql.SQLException;
import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import fr.ensma.lias.rql.AuthToken;
import fr.ensma.lias.rql.RqlUtil;
import fr.ensma.lias.rql.api.AuthenticationResource;
import fr.ensma.lias.rql.api.NotYetImplementedException;
import fr.ensma.lias.rql.cfg.IConfiguration;
import fr.ensma.lias.rql.cfg.RqlConfig;
import fr.ensma.lias.rql.dao.userdao.IUserDAO;
import fr.ensma.lias.rql.dto.Credentials;

/**
 * @author Bilal REZKELLAH
 */
public class AuthenticationResourceImpl implements AuthenticationResource {

	@Inject
	IUserDAO iuser;

	@Inject
	IConfiguration refConfiguration;

	@Override
	public Response login(Credentials credentials) {
		if (credentials == null || credentials.getUsername() == null || credentials.getUsername().isEmpty()
				|| credentials.getPassword() == null || credentials.getPassword().isEmpty()) {
			throw new WebApplicationException("Parameter is missing.", Status.BAD_REQUEST);
		}
		final RqlConfig configuration = refConfiguration.getConfiguration();
		try {
			if (!iuser.ifUserNameExist(credentials.getUsername())) {
				throw new WebApplicationException("User is not unauthorized.", Status.UNAUTHORIZED);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WebApplicationException("Server Error.", Status.BAD_REQUEST);

		}

		String hashPassword = null;
		try {
			hashPassword = iuser.getPasswordHash(credentials.getUsername());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WebApplicationException("Server Error.", Status.BAD_REQUEST);

		}
		if (RqlUtil.checkPassword(credentials.getPassword(), hashPassword)) {
			AuthToken currentToken = new AuthToken(credentials.getUsername(), new Date());
			final String encryptToken = RqlUtil.encryptToken(currentToken, configuration.encryptPassword(),
					configuration.encryptNoise());
			return Response.ok(encryptToken).build();
		} else {
			throw new WebApplicationException("Password doesn't not match.", Status.UNAUTHORIZED);
		}
	}

	@Override
	public Response logout() {
		System.out.println("AuthenticationResourceImpl.logout()");
		throw new NotYetImplementedException();
	}
}

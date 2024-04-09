package fr.ensma.lias.rql.api;

import static fr.ensma.lias.rql.api.ApiPaths.AUTHENTICATION;
import static fr.ensma.lias.rql.api.ApiPaths.LOGIN;
import static fr.ensma.lias.rql.api.ApiPaths.LOGOUT;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.ensma.lias.rql.dto.Credentials;

/**
 * @author Bilal REZKELLAH
 */
@Path(AUTHENTICATION)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AuthenticationResource {

	@Path(LOGIN)
	@POST
	Response login(Credentials credentials);

	@Path(LOGOUT)
	@DELETE
	Response logout();
}

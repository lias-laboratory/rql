package fr.ensma.lias.rql.api;

import static fr.ensma.lias.rql.api.ApiParameters.ID;
import static fr.ensma.lias.rql.api.ApiParameters.QUERY_TYPE;
import static fr.ensma.lias.rql.api.ApiPaths.HEADER;
import static fr.ensma.lias.rql.api.ApiPaths.TABLE;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fr.ensma.lias.rql.api.TokenAuthenticated;
import fr.ensma.lias.rql.dto.SchemaResult;

/**
 * @author Bilal REZKELLAH
 */
@Path(TABLE)
@Produces(MediaType.APPLICATION_JSON)
public interface SchemaResource {

	@TokenAuthenticated
	@GET
	SchemaResult getAllTables(@QueryParam("projectID") String projectID);

	@TokenAuthenticated
	@Path("{id}")
	@GET
	List<String> getAttributesList(@PathParam(ID) String id, @QueryParam(QUERY_TYPE) String queryType,
			@QueryParam("projectID") String projectID);

	@TokenAuthenticated
	@Path(HEADER)
	@GET
	List<String> getAllTablesHeader(@QueryParam("projectID") String projectID);
}

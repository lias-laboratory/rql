package fr.ensma.lias.rql.api;

import static fr.ensma.lias.rql.api.ApiParameters.CONFIDENCE;
import static fr.ensma.lias.rql.api.ApiParameters.ID;
import static fr.ensma.lias.rql.api.ApiParameters.LEFT_ATTRIBUTES;
import static fr.ensma.lias.rql.api.ApiParameters.RIGHT_ATTRIBUTES;
import static fr.ensma.lias.rql.api.ApiParameters.SUPPORT;
import static fr.ensma.lias.rql.api.ApiPaths.RULE;
import static fr.ensma.lias.rql.api.ApiPaths.RULECHECK;
import static fr.ensma.lias.rql.api.ApiPaths.RESULTSET;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import fr.ensma.lias.rql.api.TokenAuthenticated;

/**
 * @author Bilal REZKELLAH
 */
@Path("/excel")
@Produces("application/vnd.ms-excel")
public interface XlsxResource {

	@Path("{id}" + RULE)
	@GET
	@TokenAuthenticated
	Response getRule(@PathParam(ID) String id, @QueryParam(SUPPORT) String support,
			@QueryParam(CONFIDENCE) String confidence);

	@Path("{id}" + RULECHECK)
	@GET
	@TokenAuthenticated
	Response getCounterExample(@PathParam(ID) String id, @QueryParam(LEFT_ATTRIBUTES) String leftAttributes,
			@QueryParam(RIGHT_ATTRIBUTES) String rightAttribute, @QueryParam(SUPPORT) String support,
			@QueryParam(CONFIDENCE) String confidence, @QueryParam("projectID") String projectID);

	@TokenAuthenticated
	@Path("{id}" + RESULTSET)
	@GET
	Response getResultSet(@PathParam(ID) String id, @QueryParam("projectID") String projectID);
}

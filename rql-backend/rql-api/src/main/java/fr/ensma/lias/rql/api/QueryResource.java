package fr.ensma.lias.rql.api;

import static fr.ensma.lias.rql.api.ApiParameters.CONFIDENCE;
import static fr.ensma.lias.rql.api.ApiParameters.ID;
import static fr.ensma.lias.rql.api.ApiParameters.LEFT_ATTRIBUTES;
import static fr.ensma.lias.rql.api.ApiParameters.RIGHT_ATTRIBUTES;
import static fr.ensma.lias.rql.api.ApiParameters.SUPPORT;
import static fr.ensma.lias.rql.api.ApiPaths.QUERY;
import static fr.ensma.lias.rql.api.ApiPaths.RESULTSET;
import static fr.ensma.lias.rql.api.ApiPaths.RULE;
import static fr.ensma.lias.rql.api.ApiPaths.RULECHECK;
import static fr.ensma.lias.rql.api.ApiPaths.TEST;
import static fr.ensma.lias.rql.api.ApiPaths.TYPE;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataParam;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.ensma.lias.rql.dto.CheckRuleResult;
import fr.ensma.lias.rql.dto.RQLResult;
import fr.ensma.lias.rql.dto.SQLResult;
import fr.ensma.lias.rql.dto.TypeQuery;

/**
 * @author Bilal REZKELLAH
 */
@Path(QUERY)
@Produces(MediaType.APPLICATION_JSON)
public interface QueryResource {

	/**
	 * Returns the type and the id of the query and save it on the DataBase
	 * 
	 * @param query The SQL or the RQL query to
	 */
	@TokenAuthenticated
	@Path(TYPE)
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	TypeQuery createQuery(@FormDataParam("query") String query, @FormDataParam("projectID") String projectID);

	/**
	 * Returns all Rules calculated after executing the RQL query corresponding to
	 * the ID.
	 * 
	 * @param id The ID of the query in the database
	 */

	@TokenAuthenticated
	@Path("{id}" + RULE)
	@GET
	RQLResult getAllRulesID(@PathParam(ID) String id, @QueryParam(SUPPORT) String support,
			@QueryParam(CONFIDENCE) String confidence);

	/**
	 * Returns a boolean value that indicate if the rule is true or not and if it's
	 * not true it returns a table of counter examples.
	 * 
	 * @param id             The ID of the query in the database
	 * @param leftAttributes The left Attributes of the rule to check
	 * 
	 * @param rightAttribute The right Attributes of the rule to check
	 */

	@TokenAuthenticated
	@Path("{id}" + RULECHECK)
	@GET
	CheckRuleResult checkForThisRule(@PathParam(ID) String id, @QueryParam(LEFT_ATTRIBUTES) String leftAttributes,
			@QueryParam(RIGHT_ATTRIBUTES) String rightAttribute, @QueryParam(SUPPORT) String support,
			@QueryParam(CONFIDENCE) String confidence, @QueryParam("projectID") String projectID);

	/**
	 * Returns all rows of the table if query is a Select SQL query and returns null
	 * if it's another type of SQL query
	 * 
	 * @param id The ID of the SQL query
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */

	@TokenAuthenticated
	@Path("{id}" + RESULTSET)
	@GET
	SQLResult getResultSet(@PathParam("id") String id, @QueryParam("projectID") String projectID)
			throws JsonParseException, JsonMappingException, IOException;

	@TokenAuthenticated
	@Path(TEST)
	@GET
	String isGoodConstructed(@QueryParam("query") String query);

}

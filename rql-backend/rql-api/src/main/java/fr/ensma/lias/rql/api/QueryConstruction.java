package fr.ensma.lias.rql.api;

import static fr.ensma.lias.rql.api.ApiParameters.ATTRIBUTES_LIST;
import static fr.ensma.lias.rql.api.ApiParameters.CONDITIONAL_WHERE;
import static fr.ensma.lias.rql.api.ApiParameters.IS_ALL_DATA;
import static fr.ensma.lias.rql.api.ApiParameters.IS_TABLE;
import static fr.ensma.lias.rql.api.ApiParameters.SQL_QUERY;
import static fr.ensma.lias.rql.api.ApiParameters.IS_CONDITIONAL;
import static fr.ensma.lias.rql.api.ApiParameters.QUERY_TYPE;
import static fr.ensma.lias.rql.api.ApiParameters.SUBSET_WHERE;
import static fr.ensma.lias.rql.api.ApiParameters.TABLE_NAME;
import static fr.ensma.lias.rql.api.ApiParameters.TOLERENCE;
import static fr.ensma.lias.rql.api.ApiPaths.QUERY_CONSTRUCTION;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * @author Bilal REZKELLAH
 */
@Path(QUERY_CONSTRUCTION)
public interface QueryConstruction {

	@TokenAuthenticated
	@GET
	String getQuery(@QueryParam(QUERY_TYPE) String QueryType, @QueryParam(IS_TABLE) String isTable,
			@QueryParam(IS_ALL_DATA) String isALLData, @QueryParam(SQL_QUERY) String sqlQuery,
			@QueryParam(TABLE_NAME) String tableName, @QueryParam(SUBSET_WHERE) String subsetWhere,
			@QueryParam(ATTRIBUTES_LIST) String attributesList, @QueryParam(IS_CONDITIONAL) String isConditional,
			@QueryParam(CONDITIONAL_WHERE) String conditionalWhere, @QueryParam(TOLERENCE) String tolerence);

}

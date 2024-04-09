package fr.ensma.lias.rql.service;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import fr.ensma.lias.rql.api.QueryConstruction;
import fr.ensma.lias.rql.core.IRql;

/**
 * @author Bilal REZKELLAH
 */
public class QueryConstructionImp implements QueryConstruction {

	@Inject
	IRql irql;

	@Override
	public String getQuery(String QueryType, String isTable, String isALLData, String sqlQuery, String tableName,
			String subsetWhere, String attributesList, String isConditional, String conditionalWhere,
			String tolerence) {
		if (QueryType == null || QueryType.isEmpty()) {
			throw new WebApplicationException("Parameter is missing.", Status.NOT_FOUND);
		}
		String rqlQuery = "";
		rqlQuery = irql.constructQuery(QueryType, isTable, isALLData, sqlQuery, tableName, subsetWhere, attributesList,
				isConditional.equals("true"), conditionalWhere, tolerence);

		return rqlQuery;
	}

}

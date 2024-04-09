package fr.ensma.lias.rql.core;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.ensma.lias.rql.dto.CheckRuleResult;
import fr.ensma.lias.rql.dto.RQLResult;
import fr.ensma.lias.rql.dto.SQLResult;
import fr.ensma.lias.rql.dto.SchemaResult;
import fr.ensma.lias.rql.model.DataBaseConfig;
import fr.ensma.lias.rql.rqlgrammar.ParseException;
import fr.ensma.lias.rql.sql.SQLResultsRow;

/**
 * @author Bilal REZKELLAH
 */
public interface IRql {

	SQLResult executeSqlQuery(String sqlquery, DataBaseConfig dbc)
			throws SQLException, JsonParseException, JsonMappingException, IOException;

	SQLResultsRow executeSqlQuery2(String sqlquery, DataBaseConfig dbc) throws SQLException;

	Boolean isRqlQuery(String query);

	RQLResult executeRqlQuery(String id, double support, double confidence) throws Exception;

	SchemaResult getDataBaseSchema(DataBaseConfig dbc) throws SQLException, IOException;

	List<String> getDataBaseTablesHeader(DataBaseConfig dbc) throws SQLException, IOException;

	List<String> getTableAttributesList(String id, String queryType, DataBaseConfig dbc)
			throws SQLException, IOException;

	String constructQuery(String QueryType, String isTable, String isALLData, String sqlQuerie, String tableName,
			String subsetWhere, String attributesList, boolean isConditional, String conditionalWhere,
			String tolerence);

	CheckRuleResult checkRule(long debut, DataBaseConfig dbc, String query, String leftAttributes,
			String rightAttribute, double support, double confidence) throws ParseException;

	void testQuery(String query) throws ParseException;

	String normalizeName(String source, String dbt);

}

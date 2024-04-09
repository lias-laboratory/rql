package fr.ensma.lias.rql.dao.query;

import fr.ensma.lias.rql.baseresults.BaseResult;
import fr.ensma.lias.rql.dto.TypeQuery;
import fr.ensma.lias.rql.model.DataBaseConfig;
import fr.ensma.lias.rql.rqlgrammar.RQLParser;

/**
 * @author Bilal REZKELLAH
 */
public interface IQueryDAO {

	TypeQuery createRQLQuery(String query, DataBaseConfig dbc) throws Exception;

	String getQueryByID(String id);

	BaseResult getBaseByID(String id);

	TypeQuery createSQLQuery(String query) throws Exception;

	RQLParser getParserByID(String id);

}

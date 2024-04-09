package fr.ensma.lias.rql.dao.query;

import java.sql.ResultSet;

import javax.inject.Inject;

import fr.ensma.lias.rql.baseresults.BaseResult;
import fr.ensma.lias.rql.cfg.IConfiguration;
import fr.ensma.lias.rql.cfg.SqlCoreConf;
import fr.ensma.lias.rql.dao.inmemory.InMemory;
import fr.ensma.lias.rql.dto.QueryEnumType;
import fr.ensma.lias.rql.dto.TypeQuery;
import fr.ensma.lias.rql.model.DataBaseConfig;
import fr.ensma.lias.rql.rqlgrammar.RQLParser;
import fr.ensma.lias.rql.sql.SQLManager;

/**
 * @author Bilal REZKELLAH
 */
public class QueryDAO implements IQueryDAO {

	@Inject
	InMemory myInstance;

	@Inject
	IConfiguration cfg;

	@Override
	public TypeQuery createRQLQuery(String query, DataBaseConfig dbc) throws Exception {
		RQLParser parse = RQLParser.parse(query);
		SQLManager sql = new SQLManager(new SqlCoreConf(dbc.getDataBaseName(), dbc.getDataBaseHost(),
				dbc.getDataBasePort(), dbc.getDataBaseType(), dbc.getDataBaseUser(), dbc.getDataBasePassword()));
		System.out.println(parse.mysqlQuery());
		System.out.println(parse.oracleQuery());
		String myQuery = "";
		if (dbc.getDataBaseType().equals("oracle")) {
			myQuery = parse.oracleQuery();
		} else if (dbc.getDataBaseType().equals("postgresql")) {
			myQuery = parse.postgresQuery();
		} else if (dbc.getDataBaseType().equals("mysql")) {
			myQuery = parse.oracleQuery();
		} else {
		}
		ResultSet result = sql.execute(myQuery);
		BaseResult base = new BaseResult();
		base.resultsToList(result);
		long start = System.currentTimeMillis();
		String queryKey = Long.toString(start);
		System.out.println(queryKey);
		myInstance.getRqlParserDB().put(queryKey, parse);
		myInstance.getRqlQueryDB().put(queryKey, query);
		myInstance.getRqlBaseDB().put(queryKey, base);
		sql.closeDB();
		return new TypeQuery(QueryEnumType.RQL, myQuery, queryKey, base.getContent(), parse.getAttributeList());
	}

	@Override
	public TypeQuery createSQLQuery(String query) throws Exception {
		long start = System.currentTimeMillis();
		String queryKey = Long.toString(start);
		System.out.println(queryKey);
		System.out.println(myInstance.getSqlQueryDB().size());
		myInstance.getSqlQueryDB().put(queryKey, query);
		return new TypeQuery(QueryEnumType.SQL, null, queryKey, null, null);
	}

	@Override
	public String getQueryByID(String id) {
		String query = myInstance.getRqlQueryDB().get(id);
		return query;
	}

	@Override
	public BaseResult getBaseByID(String id) {
		BaseResult base = myInstance.getRqlBaseDB().get(id);
		return base;
	}

	@Override
	public RQLParser getParserByID(String id) {
		return myInstance.getRqlParserDB().get(id);
	}

}

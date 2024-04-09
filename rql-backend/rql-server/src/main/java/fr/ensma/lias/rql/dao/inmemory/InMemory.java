package fr.ensma.lias.rql.dao.inmemory;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import fr.ensma.lias.rql.baseresults.BaseResult;
import fr.ensma.lias.rql.rqlgrammar.RQLParser;

/**
 * @author Bilal REZKELLAH
 */
@Singleton
public class InMemory {

	private Map<String, String> RqlQueryDB;

	private Map<String, String> SqlQueryDB;

	private Map<String, BaseResult> RqlBaseDB;

	private Map<String, RQLParser> rqlParserDB;

	public Map<String, RQLParser> getRqlParserDB() {
		return rqlParserDB;
	}

	public void setRqlParserDB(Map<String, RQLParser> rqlParserDB) {
		this.rqlParserDB = rqlParserDB;
	}

	public Map<String, String> getRqlQueryDB() {
		return RqlQueryDB;
	}

	public void setRqlQueryDB(Map<String, String> rqlQueryDB) {
		RqlQueryDB = rqlQueryDB;
	}

	public Map<String, BaseResult> getRqlBaseDB() {
		return RqlBaseDB;
	}

	public void setRqlBaseDB(Map<String, BaseResult> rqlBaseDB) {
		RqlBaseDB = rqlBaseDB;
	}

	public InMemory() {
		RqlQueryDB = new HashMap<String, String>();
		SqlQueryDB = new HashMap<String, String>();
		RqlBaseDB = new HashMap<String, BaseResult>();
		rqlParserDB = new HashMap<String, RQLParser>();
	}

	public Map<String, String> getSqlQueryDB() {
		return SqlQueryDB;
	}

	public void setSqlQueryDB(Map<String, String> sqlQueryDB) {
		SqlQueryDB = sqlQueryDB;
	}
}

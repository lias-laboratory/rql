package fr.ensma.lias.rql.dto;

/**
 * @author Bilal REZKELLAH
 */
public class QueryResult {

	private RQLResult rules; // rules

	private SQLResult sqlTuples; // SQLTuples

	public RQLResult getrules() {
		return rules;
	}

	public void setrules(RQLResult rules) {
		this.rules = rules;
	}

	public SQLResult getsqlTuples() {
		return sqlTuples;
	}

	public void setsqlTuples(SQLResult sqlTuples) {
		this.sqlTuples = sqlTuples;
	}

	public QueryResult(RQLResult rules, SQLResult sqlTuples) {
		this.rules = rules;
		this.sqlTuples = sqlTuples;
	}

	public QueryResult() {

	}

}

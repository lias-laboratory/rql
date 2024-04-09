package fr.ensma.lias.rql.core;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.ensma.lias.rql.RQLManager;
import fr.ensma.lias.rql.RQLResults;
import fr.ensma.lias.rql.Rule;
import fr.ensma.lias.rql.cfg.IConfiguration;
import fr.ensma.lias.rql.cfg.SqlCoreConf;
import fr.ensma.lias.rql.dao.query.IQueryDAO;
import fr.ensma.lias.rql.database.DataBaseSchema;
import fr.ensma.lias.rql.database.Table;
import fr.ensma.lias.rql.dbimport.MySqlNameNormalizer;
import fr.ensma.lias.rql.dbimport.NameNormalizer;
import fr.ensma.lias.rql.dbimport.OracleNameNormalizer;
import fr.ensma.lias.rql.dbimport.PostgresNameNormalizer;
import fr.ensma.lias.rql.dto.CheckRuleResult;
import fr.ensma.lias.rql.dto.CheckRuleSummary;
import fr.ensma.lias.rql.dto.RQLResult;
import fr.ensma.lias.rql.dto.RQLRow;
import fr.ensma.lias.rql.dto.RuleGenerationSummary;
import fr.ensma.lias.rql.dto.SQLResult;
import fr.ensma.lias.rql.dto.SchemaResult;
import fr.ensma.lias.rql.model.DataBaseConfig;
import fr.ensma.lias.rql.queryconstruction.QueryConstructor;
import fr.ensma.lias.rql.rqlgrammar.ParseException;
import fr.ensma.lias.rql.rqlgrammar.RQLParser;
import fr.ensma.lias.rql.rulecheck.CheckResult;
import fr.ensma.lias.rql.rulecheck.RuleChecker;
import fr.ensma.lias.rql.sql.SQLManager;
import fr.ensma.lias.rql.sql.SQLResultsObject;
import fr.ensma.lias.rql.sql.SQLResultsRow;

/**
 * @author Bilal REZKELLAH
 */
public class RqlImp implements IRql {

	Properties props = new Properties();

	@Inject
	IConfiguration cfg;

	@Inject
	IQueryDAO iquery;

	@Override
	public SQLResult executeSqlQuery(String sqlquery, DataBaseConfig dbc)
			throws SQLException, JsonParseException, JsonMappingException, IOException {

		SQLManager sql = new SQLManager(new SqlCoreConf(dbc.getDataBaseName(), dbc.getDataBaseHost(),
				dbc.getDataBasePort(), dbc.getDataBaseType(), dbc.getDataBaseUser(), dbc.getDataBasePassword()));
		SQLResultsObject sqlResults = sql.executeQuery(sqlquery);
		SQLResult sqlResult = new SQLResult();
		sqlResult.setHeader(sqlResults.getHeader());
		sqlResult.setIsSelect(sqlResults.getIsSelect());
		for (int i = 0; i < sqlResults.getRows().size(); i++) {
			Object sqlRow = new Object();
			sqlRow = sqlResults.getRows().get(i);
			sqlResult.getRows().add(sqlRow);
		}
		return sqlResult;
	}

	@Override
	public Boolean isRqlQuery(String query) {
		return (query.contains(RqlConstant.FIND_RULES) || query.contains("FINDRULE"));
	}

	@Override
	public RQLResult executeRqlQuery(String id, double support, double confidence) throws Exception {
		long debut = System.currentTimeMillis();
		RuleGenerationSummary summ;
		RQLResults rqlResults = new RQLResults();
		RQLRow row;
		List<RQLRow> exact = new ArrayList<RQLRow>();
		RQLParser parse = iquery.getParserByID(id);
		File resultDir = new File(cfg.getConfiguration().tmpResultsDir());
		resultDir.mkdir();
		String agFile = resultDir.getCanonicalPath() + File.separator + id + "rql_agree_set.txt";
		RQLManager rql = new RQLManager(agFile);
		rql.resultsToFile2(iquery.getBaseByID(id).getContent(), parse.getAttributeList().size());
		rql.executeDFG(agFile, agFile + "_rules.txt", parse.getAttributeList().size(), support, confidence,
				cfg.getConfiguration().pathToShd());
		rqlResults = rql.translate(resultDir.getCanonicalPath() + File.separator + id + "rules.txt",
				agFile + "_rules.txt", parse.getAttributeList());

		for (Rule currentRules : rqlResults.getExactRules()) {
			row = new RQLRow();
			row.setLeftAttributes(String.join("   ", currentRules.getLeftAttributes()));
			row.setRightAttribute(currentRules.getRightAttributes());
			row.setSupport(currentRules.getSupport() * 100);
			row.setConfidence(currentRules.getConfidence() * 100);
			row.setLift(currentRules.getLift());
			exact.add(row);
		}

		summ = new RuleGenerationSummary(System.currentTimeMillis() - debut, parse.getAttributeList(),
				parse.getTupVarList());
		RQLResult rqlResult = new RQLResult(exact, summ);
		return rqlResult;
	}

	@Override
	public SchemaResult getDataBaseSchema(DataBaseConfig dbc) throws SQLException, IOException {
		List<Table> tables = new ArrayList<Table>();
		SQLManager sql = new SQLManager(new SqlCoreConf(dbc.getDataBaseName(), dbc.getDataBaseHost(),
				dbc.getDataBasePort(), dbc.getDataBaseType(), dbc.getDataBaseUser(), dbc.getDataBasePassword()));
		DataBaseSchema schema = sql.getDataBaseSchema();
		sql.closeDB();
		tables = schema.getTables();
		SchemaResult tablesResults = new SchemaResult(tables);

		return tablesResults;
	}

	@Override
	public CheckRuleResult checkRule(long debut, DataBaseConfig dbc, String id, String leftAttributes,
			String rightAttribute, double support, double confidence) throws ParseException {
		String query = iquery.getQueryByID(id);
		RQLParser parser = iquery.getParserByID(id);
		RuleChecker ruleChecker = new RuleChecker(new SqlCoreConf(dbc.getDataBaseName(), dbc.getDataBaseHost(),
				dbc.getDataBasePort(), dbc.getDataBaseType(), dbc.getDataBaseUser(), dbc.getDataBasePassword()));
		CheckResult checkResult = ruleChecker.CheckRule(parser, query, leftAttributes, rightAttribute, support,
				confidence);
		CheckRuleSummary summary = new CheckRuleSummary(System.currentTimeMillis() - debut, parser.getAttributeList(),
				parser.getTupVarList(), checkResult.getCounterExamplesNB(), parser.getTupVarList().size(),
				checkResult.getIsTrue(), checkResult.getSupport(), checkResult.getConfidence());
		return new CheckRuleResult(checkResult, summary);
	}

	@Override
	public List<String> getDataBaseTablesHeader(DataBaseConfig dbc) throws SQLException, IOException {
		List<String> tablesHeader;
		SQLManager sql = new SQLManager(new SqlCoreConf(dbc.getDataBaseName(), dbc.getDataBaseHost(),
				dbc.getDataBasePort(), dbc.getDataBaseType(), dbc.getDataBaseUser(), dbc.getDataBasePassword()));
		tablesHeader = sql.getDataBaseTablesHeader();
		sql.closeDB();
		return tablesHeader;
	}

	@Override
	public List<String> getTableAttributesList(String id, String queryType, DataBaseConfig dbc)
			throws SQLException, IOException {
		List<String> attributesList;
		SQLManager sql = new SQLManager(new SqlCoreConf(dbc.getDataBaseName(), dbc.getDataBaseHost(),
				dbc.getDataBasePort(), dbc.getDataBaseType(), dbc.getDataBaseUser(), dbc.getDataBasePassword()));
		attributesList = sql.getAttributesList(id, queryType);
		sql.closeDB();
		return attributesList;
	}

	@Override
	public String constructQuery(String QueryType, String isTable, String isALLData, String sqlQuerie, String tableName,
			String subsetWhere, String attributesList, boolean isConditional, String conditionalWhere,
			String tolerence) {
		String rqlQuery;
		QueryConstructor queryConstructor = new QueryConstructor();
		rqlQuery = queryConstructor.constructquery(QueryType, isTable, isALLData, sqlQuerie, tableName, subsetWhere,
				attributesList, isConditional, conditionalWhere, tolerence);
		return rqlQuery;
	}

	@Override
	public void testQuery(String query) throws ParseException {
		RQLParser parse = RQLParser.parse(query);
		System.out.println(parse.postgresQuery());
	}

	@Override
	public SQLResultsRow executeSqlQuery2(String sqlquery, DataBaseConfig dbc) throws SQLException {
		SQLManager sql = new SQLManager(new SqlCoreConf(dbc.getDataBaseName(), dbc.getDataBaseHost(),
				dbc.getDataBasePort(), dbc.getDataBaseType(), dbc.getDataBaseUser(), dbc.getDataBasePassword()));
		SQLResultsRow sqlResults = sql.executeQuery2(sqlquery);
		return sqlResults;
	}

	@Override
	public String normalizeName(String source, String dbType) {
		NameNormalizer nNormalizer = null;
		if (dbType.equals("postgresql")) {
			nNormalizer = new PostgresNameNormalizer();
			source = nNormalizer.colNameNormalizer(source);
		} else if (dbType.equals("oracle")) {
			nNormalizer = new OracleNameNormalizer();
			source = nNormalizer.colNameNormalizer(source);
		} else if (dbType.equals("mysql")) {
			nNormalizer = new MySqlNameNormalizer();
			source = nNormalizer.colNameNormalizer(source);
		} else {
		}
		return source;
	}
}

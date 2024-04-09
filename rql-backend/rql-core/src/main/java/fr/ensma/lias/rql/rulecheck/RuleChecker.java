package fr.ensma.lias.rql.rulecheck;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import fr.ensma.lias.rql.cfg.SqlCoreConf;
import fr.ensma.lias.rql.database.Database;
import fr.ensma.lias.rql.database.MysqlDB;
import fr.ensma.lias.rql.database.OracleDB;
import fr.ensma.lias.rql.database.PostgresqlDB;
import fr.ensma.lias.rql.rqlgrammar.RQLParser;
import fr.ensma.lias.rql.sql.SQLResultsRow;
import fr.ensma.lias.rql.sql.SQLRow;

/**
 * @author Bilal REZKELLAH
 */
public class RuleChecker {

	private String rqlQuery;

	private String leftAttributes;

	private String rightAttribute;

	private String isTrue;

	private String query1 = "";

	private String query2 = "";

	private String query3 = "";

	private String querySupportX = "";

	private String querySupportXYBar = "";

	private SqlCoreConf sqlCoreConf;

	private Database db = null;

	public CheckResult CheckRule(RQLParser parse, String rqlQuery, String leftAttributes, String rightAttribute,
			double support, double confidence) {
		List<String> listEntetes = new ArrayList<String>();
		List<String> recurentList = new ArrayList<String>();
		List<SQLRow> counterExp = new ArrayList<SQLRow>();
		List<SQLRow> counterExp2 = new ArrayList<SQLRow>();
		List<SQLRow> counterExp3 = new ArrayList<SQLRow>();
		List<SQLRow> separated = new ArrayList<SQLRow>();
		List<SQLRow> separated2 = new ArrayList<SQLRow>();
		List<SQLRow> separated3 = new ArrayList<SQLRow>();
		SQLRow sqlRow;
		List<SQLRow> rows = new ArrayList<SQLRow>();
		;
		SQLResultsRow sqlResults = new SQLResultsRow(listEntetes, rows, true);
		SQLResultsRow sqlResults2 = new SQLResultsRow(listEntetes, rows, true);
		SQLResultsRow sqlResults3 = new SQLResultsRow(listEntetes, rows, true);

		try {
			System.out.println(leftAttributes);
			ArrayList<String> leftAttributesList = parseAttributeList(leftAttributes);
			ArrayList<String> rightAttributesList = parseAttributeList(rightAttribute);
			RQLParser parser = parse;
			parser.improve();
			if (this.sqlCoreConf.getRqlDbType().equals("oracle")) {
				db = new OracleDB();
			} else if (this.sqlCoreConf.getRqlDbType().equals("postgresql")) {
				db = new PostgresqlDB();
			} else if (this.sqlCoreConf.getRqlDbType().equals("mysql")) {
				db = new MysqlDB();
			}
			db.open(this.sqlCoreConf.getRqlDbHost(), this.sqlCoreConf.getRqlDbPort(), this.sqlCoreConf.getRqlDbName(),
					this.sqlCoreConf.getRqlDbAdminLogin(), this.sqlCoreConf.getRqlDbAdminPwd());
			System.out.println("\nconnexion etablie \n");

			/* this QUERY is used to get the support of the lhs of the checked rule */
			querySupportX = "SELECT count(*)";
			querySupportX += "\nFROM " + parser.getSqlStatement() + "\nWHERE ";
			if (parser.getSqlConditions() != null) {
				querySupportX += parser.getSqlConditions() + "\nAND ";
			}
			// (conditions[left]) AND NOT (conditions[right])
			querySupportX += "(";
			for (int i = 0; i < leftAttributesList.size(); i++) {
				if (i > 0) {
					querySupportX += " AND ";
				}
				querySupportX += "(" + parser.sqlCondition(leftAttributesList.get(i)) + ")";
			}

			querySupportX += ")";
			System.out.println("querySupportX" + querySupportX);
			/* this QUERY is used to get the support the checked rule */
			querySupportXYBar = "SELECT count(*)";
			querySupportXYBar += "\nFROM " + parser.getSqlStatement() + "\nWHERE ";
			if (parser.getSqlConditions() != null) {
				querySupportXYBar += parser.getSqlConditions() + "\nAND ";
			}
			// (conditions[left]) AND NOT (conditions[right])
			querySupportXYBar += "(";
			for (int i = 0; i < leftAttributesList.size(); i++) {
				if (i > 0) {
					querySupportXYBar += " AND ";
				}
				querySupportXYBar += "(" + parser.sqlCondition(leftAttributesList.get(i)) + ")";
			}

			querySupportXYBar += ")\nAND CASE WHEN (";
			for (int i = 0; i < rightAttributesList.size(); i++) {
				if (i > 0) {
					querySupportXYBar += " AND ";
				}
				querySupportXYBar += "(" + parser.sqlCondition(rightAttributesList.get(i)) + ")";
			}
			querySupportXYBar += ") THEN 1 ELSE 0 END = 0";
			/* end of query generation */
			ResultSet supXSet = db.executeQuery(querySupportX);
			supXSet.next();
			double supX = supXSet.getInt(1);
			ResultSet supXYBarSet = db.executeQuery(querySupportXYBar);
			supXYBarSet.next();
			double supXYBar = supXYBarSet.getInt(1);
			/* this query is used to get the nbligne of the sql statement from */
			String queryNBLigne = "select count(*) from " + parser.getSqlStatement();
			if (parser.getSqlConditions() != null) {
				queryNBLigne += " where " + parser.getSqlConditions();
			}
			ResultSet NBLigneSet = db.executeQuery(queryNBLigne);
			NBLigneSet.next();
			double NBLigne = NBLigneSet.getInt(1);
			System.out.println(NBLigne);
			DecimalFormat df = new DecimalFormat("#.###");
			double supportXY = (supX - supXYBar) / NBLigne;
			double supportX = supX / NBLigne;
			double confidenceXY = (supportXY / supportX);
			System.out.println("2");

			if (supportXY != 0 && supportXY >= support && confidenceXY != 0 && confidenceXY >= confidence) {
				return new CheckResult("true", df.format(supportXY).replace(',', '.'),
						df.format(confidenceXY).replace(',', '.'), (int) supXYBar, sqlResults, sqlResults2,
						sqlResults3);
			} else {
				query1 = "SELECT ";
				// SELECT t1.att1, t1.att2, ... , t2.att1, t2.att2, ... , tn.attm
				List<String> tupVarList = parser.getTupVarList();
				for (int i = 0; i < tupVarList.size(); i++) {
					if (i != 0) {
						query1 += ", ";
					}

					for (int j = 0; j < parser.getAttributeList().size(); j++) {
						if (j != 0) {
							query1 += ", ";
						}
						query1 += tupVarList.get(i) + "." + parser.getAttributeList().get(j);
					}
				}
				query1 += "\nFROM " + parser.getSqlStatement() + "\nWHERE ";

				// WHERE [sqlConditions AND]
				if (parser.getSqlConditions() != null) {
					query1 += parser.getSqlConditions() + "\nAND ";
				}

				// (conditions[left]) AND NOT (conditions[right])
				query1 += "(";
				for (int i = 0; i < leftAttributesList.size(); i++) {
					if (i > 0) {
						query1 += " AND ";
					}
					query1 += "(" + parser.sqlCondition(leftAttributesList.get(i)) + ")";
				}

				query1 += ")\nAND CASE WHEN (";
				for (int i = 0; i < rightAttributesList.size(); i++) {
					if (i > 0) {
						query1 += " AND ";
					}
					query1 += "(" + parser.sqlCondition(rightAttributesList.get(i)) + ")";
				}
				// AND rownum <= 10
				if (this.sqlCoreConf.getRqlDbType().equals("oracle")) {
					query1 += ") THEN 1 ELSE 0 END = 0 \nAND rownum <= 10";
				} else if (this.sqlCoreConf.getRqlDbType().equals("postgresql")) {
					query1 += ") THEN 1 ELSE 0 END = 0 Limit 10";
				} else if (this.sqlCoreConf.getRqlDbType().equals("mysql")) {
					query1 += ") THEN 1 ELSE 0 END = 0 Limit 10";
				}
				/* end of query generation */

				/* query generation without attributs projection */
				query2 = "SELECT ";
				// SELECT t1.att1, t1.att2, ... , t2.att1, t2.att2, ... , tn.attm
				for (int i = 0; i < tupVarList.size(); i++) {
					if (i != 0) {
						query2 += ", ";
					}
					query2 += tupVarList.get(i) + ".*";
				}
				query2 += "\nFROM " + parser.getSqlStatement() + "\nWHERE ";

				// WHERE [sqlConditions AND]
				if (parser.getSqlConditions() != null) {
					query2 += parser.getSqlConditions() + "\nAND ";
				}

				// (conditions[left]) AND NOT (conditions[right])
				query2 += "(";
				for (int i = 0; i < leftAttributesList.size(); i++) {
					if (i > 0) {
						query2 += " AND ";
					}
					query2 += "(" + parser.sqlCondition(leftAttributesList.get(i)) + ")";
				}

				query2 += ")\nAND CASE WHEN (";
				for (int i = 0; i < rightAttributesList.size(); i++) {
					if (i > 0) {
						query2 += " AND ";
					}
					query2 += "(" + parser.sqlCondition(rightAttributesList.get(i)) + ")";
				}
				// AND rownum <= 1
				if (this.sqlCoreConf.getRqlDbType().equals("oracle")) {
					query2 += ") THEN 1 ELSE 0 END = 0 \nAND rownum <= 10";
				} else if (this.sqlCoreConf.getRqlDbType().equals("postgresql")) {
					query2 += ") THEN 1 ELSE 0 END = 0 Limit 10";
				} else if (this.sqlCoreConf.getRqlDbType().equals("mysql")) {
					query2 += ") THEN 1 ELSE 0 END = 0 Limit 10";
				}
				/* end of query generation */

				/* query generation with attributs projection on left and right */
				query3 = "SELECT ";
				// SELECT t1.att1, t1.att2, ... , t2.att1, t2.att2, ... , tn.attm
				for (int i = 0; i < tupVarList.size(); i++) {
					if (i != 0) {
						query3 += ", ";
					}
					int k = 0;
					for (int j = 0; j < leftAttributesList.size() + rightAttributesList.size(); j++) {
						if (j != 0) {
							query3 += ", ";
						}
						if (j < leftAttributesList.size()) {
							query3 += tupVarList.get(i) + "." + leftAttributesList.get(j);
						} else {
							query3 += tupVarList.get(i) + "." + rightAttributesList.get(k);
							k++;
						}
					}
				}
				query3 += "\nFROM " + parser.getSqlStatement() + "\nWHERE ";

				// WHERE [sqlConditions AND]
				if (parser.getSqlConditions() != null) {
					query3 += parser.getSqlConditions() + "\nAND ";
				}

				// (conditions[left]) AND NOT (conditions[right])
				query3 += "(";
				for (int i = 0; i < leftAttributesList.size(); i++) {
					if (i > 0) {
						query3 += " AND ";
					}
					query3 += "(" + parser.sqlCondition(leftAttributesList.get(i)) + ")";
				}

				query3 += ")\nAND CASE WHEN (";
				for (int i = 0; i < rightAttributesList.size(); i++) {
					if (i > 0) {
						query3 += " AND ";
					}
					query3 += "(" + parser.sqlCondition(rightAttributesList.get(i)) + ")";
				}
				// AND rownum <= 1
				if (this.sqlCoreConf.getRqlDbType().equals("oracle")) {
					query3 += ") THEN 1 ELSE 0 END = 0 \nAND rownum <= 10";
				} else if (this.sqlCoreConf.getRqlDbType().equals("postgresql")) {
					query3 += ") THEN 1 ELSE 0 END = 0 Limit 10";
				} else if (this.sqlCoreConf.getRqlDbType().equals("mysql")) {
					query3 += ") THEN 1 ELSE 0 END = 0 Limit 10";
				}
				/* end of query generation */

				ResultSet resultsSql = db.executeQuery(query1);
				ResultSetMetaData rsmd = resultsSql.getMetaData();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					listEntetes.add(rsmd.getColumnName(i));
				}
				sqlRow = new SQLRow(listEntetes);
				counterExp.add(sqlRow);
				while (resultsSql.next()) {
					recurentList = new ArrayList<String>();
					for (int i = 1; i <= rsmd.getColumnCount(); i++) {
						recurentList.add(resultsSql.getString(i));
					}
					sqlRow = new SQLRow(recurentList);
					counterExp.add(sqlRow);
				}
				if (counterExp.size() <= 1) {
					isTrue = "false";
				} else {
					isTrue = "false";

					List<String> recurent1 = new ArrayList<String>();
					List<String> recurent2 = new ArrayList<String>();
					recurent1 = counterExp.get(0).getCels();
					int size = recurent1.size() / parser.getTupVarList().size();
					for (int i = 0; i < size; i++) {
						recurent2.add(recurent1.get(i));
					}
					sqlRow = new SQLRow(recurent2);
					separated.add(sqlRow);
					int size2;

					size2 = counterExp.size();

					for (int i = 1; i < size2; i++) {
						recurent1 = new ArrayList<String>(counterExp.get(i).getCels());
						recurent2 = new ArrayList<String>();
						for (int k = 0; k < parser.getTupVarList().size(); k++) {
							recurent2 = new ArrayList<String>();
							for (int j = k * size; j < k * size + size; j++) {
								recurent2.add(recurent1.get(j));
							}
							sqlRow = new SQLRow(recurent2);
							separated.add(sqlRow);
						}
					}
					rows = new ArrayList<SQLRow>();
					for (int i = 1; i < separated.size(); i++) {
						rows.add(separated.get(i));
					}
					sqlResults = new SQLResultsRow(separated.get(0).getCels(), rows, true);
				}

				ResultSet resultsSql2 = db.executeQuery(query2);
				ResultSetMetaData rsmd2 = resultsSql2.getMetaData();
				listEntetes = new ArrayList<String>();
				for (int i = 1; i <= rsmd2.getColumnCount(); i++) {
					listEntetes.add(rsmd2.getColumnName(i));
				}
				sqlRow = new SQLRow(listEntetes);
				counterExp2.add(sqlRow);
				while (resultsSql2.next()) {
					recurentList = new ArrayList<String>();
					for (int i = 1; i <= rsmd2.getColumnCount(); i++) {
						recurentList.add(resultsSql2.getString(i));
					}
					sqlRow = new SQLRow(recurentList);
					counterExp2.add(sqlRow);
				}
				if (counterExp2.size() <= 1) {
				} else {
					List<String> recurent1 = new ArrayList<String>();
					List<String> recurent2 = new ArrayList<String>();
					if (counterExp.size() != 0) {
						recurent1 = counterExp2.get(0).getCels();
						int size = recurent1.size() / parser.getTupVarList().size();
						for (int i = 0; i < size; i++) {
							recurent2.add(recurent1.get(i));
						}
						sqlRow = new SQLRow(recurent2);
						separated2.add(sqlRow);
						int size2;

						size2 = counterExp2.size();

						for (int i = 1; i < size2; i++) {
							recurent1 = new ArrayList<String>(counterExp2.get(i).getCels());
							recurent2 = new ArrayList<String>();
							for (int k = 0; k < parser.getTupVarList().size(); k++) {
								recurent2 = new ArrayList<String>();
								for (int j = k * size; j < k * size + size; j++) {
									recurent2.add(recurent1.get(j));
								}
								sqlRow = new SQLRow(recurent2);
								separated2.add(sqlRow);
							}

						}
					}
					rows = new ArrayList<SQLRow>();
					for (int i = 1; i < separated2.size(); i++) {
						rows.add(separated2.get(i));
					}
					sqlResults2 = new SQLResultsRow(separated2.get(0).getCels(), rows, true);
				}

				ResultSet resultsSql3 = db.executeQuery(query3);
				ResultSetMetaData rsmd3 = resultsSql3.getMetaData();
				listEntetes = new ArrayList<String>();
				for (int i = 1; i <= rsmd3.getColumnCount(); i++) {
					listEntetes.add(rsmd3.getColumnName(i));
				}
				sqlRow = new SQLRow(listEntetes);
				counterExp3.add(sqlRow);
				while (resultsSql3.next()) {
					recurentList = new ArrayList<String>();
					for (int i = 1; i <= rsmd3.getColumnCount(); i++) {
						recurentList.add(resultsSql3.getString(i));
					}
					sqlRow = new SQLRow(recurentList);
					counterExp3.add(sqlRow);
				}
				if (counterExp3.size() <= 1) {
				} else {
					List<String> recurent1 = new ArrayList<String>();
					List<String> recurent2 = new ArrayList<String>();
					if (counterExp3.size() != 0) {
						recurent1 = counterExp3.get(0).getCels();
						int size = recurent1.size() / parser.getTupVarList().size();
						for (int i = 0; i < size; i++) {
							recurent2.add(recurent1.get(i));
						}
						sqlRow = new SQLRow(recurent2);
						separated3.add(sqlRow);
						int size2;

						size2 = counterExp3.size();

						for (int i = 1; i < size2; i++) {
							recurent1 = new ArrayList<String>(counterExp3.get(i).getCels());
							recurent2 = new ArrayList<String>();
							for (int k = 0; k < parser.getTupVarList().size(); k++) {
								recurent2 = new ArrayList<String>();
								for (int j = k * size; j < k * size + size; j++) {
									recurent2.add(recurent1.get(j));
								}
								sqlRow = new SQLRow(recurent2);
								separated3.add(sqlRow);
							}

						}
					}
					rows = new ArrayList<SQLRow>();
					for (int i = 1; i < separated3.size(); i++) {
						rows.add(separated3.get(i));
					}
					sqlResults3 = new SQLResultsRow(separated3.get(0).getCels(), rows, true);
					System.out.println(sqlResults3.getHeader());
					for (int i = 0; i < sqlResults3.getRows().size(); i++) {
						System.out.println(sqlResults3.getRows().get(i).getCels());
					}
				}
				System.out.println("3");
				return new CheckResult(isTrue, df.format(supportXY).replace(',', '.'),
						df.format(confidenceXY).replace(',', '.'), (int) supXYBar, sqlResults, sqlResults2,
						sqlResults3);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new CheckResult("NoValideAttributes", null, null, 0, sqlResults, sqlResults2, sqlResults3);
		}

	}

	public RuleChecker(SqlCoreConf sqlConf) {
		super();
		this.sqlCoreConf = sqlConf;
	}

	public String getRqlQuery() {
		return rqlQuery;
	}

	public void setRqlQuery(String rqlQuery) {
		this.rqlQuery = rqlQuery;
	}

	public String getLeftAttributes() {
		return leftAttributes;
	}

	public void setLeftAttributes(String leftAttributes) {
		this.leftAttributes = leftAttributes;
	}

	public String getRightAttribute() {
		return rightAttribute;
	}

	public void setRightAttribute(String rightAttribute) {
		this.rightAttribute = rightAttribute;
	}

	private static ArrayList<String> parseAttributeList(String attributes) {
		ArrayList<String> result = new ArrayList<String>();
		String attribute = new String(attributes);

		attribute += ' ';

		if (attribute.indexOf(' ') == -1) {
			result.add(attribute);
		} else {
			String tempAttribute = attribute.substring(0, attribute.indexOf(' '));

			while (attribute.length() > 0) {
				result.add(tempAttribute);
				attribute = attribute.replace(tempAttribute + ' ', "");

				if (attribute.length() > 0) {
					tempAttribute = attribute.substring(0, attribute.indexOf(' '));
				}
			}
		}

		return result;
	}
}

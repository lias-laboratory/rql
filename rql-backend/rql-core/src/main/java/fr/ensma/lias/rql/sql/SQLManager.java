package fr.ensma.lias.rql.sql;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ensma.lias.rql.cfg.SqlCoreConf;
import fr.ensma.lias.rql.database.Column;
import fr.ensma.lias.rql.database.DataBaseSchema;
import fr.ensma.lias.rql.database.Database;
import fr.ensma.lias.rql.database.MysqlDB;
import fr.ensma.lias.rql.database.OracleDB;
import fr.ensma.lias.rql.database.PostgresqlDB;
import fr.ensma.lias.rql.database.Table;

/**
 * @author Bilal REZKELLAH
 */
public class SQLManager {

	private String sqlQuery;

	private SqlCoreConf sqlCoreConf;

	private Database db = null;

	private List<SQLRow> finalList = new ArrayList<SQLRow>();

	private List<String> recurentList = new ArrayList<String>();

	private List<String> listEntete = new ArrayList<String>();

	public List<String> getListEntete() {
		return listEntete;
	}

	public void setListEntete(List<String> listEntete) {
		this.listEntete = listEntete;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}

	public List<SQLRow> getFinalList() {
		return finalList;
	}

	public void setFinalList(List<SQLRow> finalList) {
		this.finalList = finalList;
	}

	public List<String> getRecurentList() {
		return recurentList;
	}

	public void setRecurentList(List<String> recurentList) {
		this.recurentList = recurentList;
	}

	public SQLManager(SqlCoreConf sqlCoreConf) {
		this.sqlCoreConf = sqlCoreConf;
	}

	public void openDB() throws SQLException {
		if (sqlCoreConf.getRqlDbType().equals("oracle")) {
			db = new OracleDB();
		} else if (sqlCoreConf.getRqlDbType().equals("postgresql")) {
			db = new PostgresqlDB();
		} else if (sqlCoreConf.getRqlDbType().equals("mysql")) {
			db = new MysqlDB();
		}
		db.open(sqlCoreConf.getRqlDbHost(), sqlCoreConf.getRqlDbPort(), sqlCoreConf.getRqlDbName(),
				sqlCoreConf.getRqlDbAdminLogin(), sqlCoreConf.getRqlDbAdminPwd());
		System.out.println("\nconnexion etablie \n");
	}

	public void closeDB() throws IOException {
		db.close();
	}

	public DataBaseSchema getDataBaseSchema() throws SQLException {
		List<Table> tables = new ArrayList<Table>();
		Table myTable;
		List<String> tablesName = new ArrayList<String>();
		List<Column> colTabName = new ArrayList<Column>();
		String tablesNameQuery = "";
		if (sqlCoreConf.getRqlDbType().equals("oracle")) {
			db = new OracleDB();
			tablesNameQuery = "SELECT TABLE_NAME FROM ALL_TABLES WHERE OWNER =" + "'"
					+ sqlCoreConf.getRqlDbAdminLogin().toUpperCase() + "'" + " order by TABLE_NAME  asc";
		} else if (sqlCoreConf.getRqlDbType().equals("postgresql")) {
			db = new PostgresqlDB();
			tablesNameQuery = "SELECT TABLE_NAME FROM information_schema.tables WHERE table_schema = 'public' order by TABLE_NAME  asc";
		} else if (sqlCoreConf.getRqlDbType().equals("mysql")) {
			db = new MysqlDB();
			tablesNameQuery = "SELECT TABLE_NAME FROM information_schema.tables WHERE table_schema = 'public' order by TABLE_NAME  asc";
		}
		db.open(sqlCoreConf.getRqlDbHost(), sqlCoreConf.getRqlDbPort(), sqlCoreConf.getRqlDbName(),
				sqlCoreConf.getRqlDbAdminLogin(), sqlCoreConf.getRqlDbAdminPwd());

		ResultSet resultsName = db.executeQuery(tablesNameQuery);
		while (resultsName.next()) {
			if (!resultsName.getString(1).contains("$")) {
				System.out.println(resultsName.getString(1));
				tablesName.add(resultsName.getString(1));
			}
		}
		ListIterator<String> it = tablesName.listIterator();
		String colNameQuery = "";
		String actualTab = "";
		ResultSet resultCol = null;
		while (it.hasNext()) {
			myTable = new Table();
			colTabName = new ArrayList<Column>();
			actualTab = it.next();
			Column column = null;
			System.out.println(actualTab);
			if (sqlCoreConf.getRqlDbType().equals("oracle")) {
				colNameQuery = "SELECT column_name, data_type column_type FROM user_tab_cols WHERE table_name =" + "'"
						+ actualTab + "'";
			} else if (sqlCoreConf.getRqlDbType().equals("postgresql")) {
				colNameQuery = "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = 'public' and table_name ="
						+ "'" + actualTab + "'";
			} else if (sqlCoreConf.getRqlDbType().equals("mysql")) {
				colNameQuery = "SELECT column_name, data_type FROM information_schema.columns WHERE table_schema = 'public' and table_name ="
						+ "'" + actualTab + "'";
			}
			resultCol = db.executeQuery(colNameQuery);
			while (resultCol.next()) {
				column = new Column(resultCol.getString(1), resultCol.getString(2));
				colTabName.add(column);
			}
			myTable.setTableName(actualTab);
			myTable.setColumnsName(colTabName);
			tables.add(myTable);
		}
		return new DataBaseSchema(tables);
	}

	public List<String> getDataBaseTablesHeader() throws SQLException {
		List<String> tablesName = new ArrayList<String>();
		String tablesNameQuery = "";
		if (sqlCoreConf.getRqlDbType().equals("oracle")) {
			db = new OracleDB();
			tablesNameQuery = "SELECT TABLE_NAME FROM ALL_TABLES WHERE OWNER =" + "'"
					+ sqlCoreConf.getRqlDbAdminLogin().toUpperCase() + "'" + " order by TABLE_NAME  asc";
		} else if (sqlCoreConf.getRqlDbType().equals("postgresql")) {
			db = new PostgresqlDB();
			tablesNameQuery = "SELECT TABLE_NAME FROM information_schema.tables WHERE table_schema = 'public' order by TABLE_NAME  asc";
		} else if (sqlCoreConf.getRqlDbType().equals("mysql")) {
			db = new MysqlDB();
			tablesNameQuery = "SELECT TABLE_NAME FROM information_schema.tables WHERE table_schema = 'public' order by TABLE_NAME  asc";
		}
		db.open(sqlCoreConf.getRqlDbHost(), sqlCoreConf.getRqlDbPort(), sqlCoreConf.getRqlDbName(),
				sqlCoreConf.getRqlDbAdminLogin(), sqlCoreConf.getRqlDbAdminPwd());
		ResultSet resultsName = db.executeQuery(tablesNameQuery);
		while (resultsName.next()) {
			if (!resultsName.getString(1).contains("$")) {
				System.out.println(resultsName.getString(1));
				tablesName.add(resultsName.getString(1));
			}
		}
		return tablesName;
	}

	public ResultSet execute(String query) throws Exception {
		if (sqlCoreConf.getRqlDbType().equals("oracle")) {
			db = new OracleDB();
		} else if (sqlCoreConf.getRqlDbType().equals("postgresql")) {
			db = new PostgresqlDB();
		} else if (sqlCoreConf.getRqlDbType().equals("mysql")) {
			db = new MysqlDB();
		} else {
			throw new Exception();
		}
		db.open(sqlCoreConf.getRqlDbHost(), sqlCoreConf.getRqlDbPort(), sqlCoreConf.getRqlDbName(),
				sqlCoreConf.getRqlDbAdminLogin(), sqlCoreConf.getRqlDbAdminPwd());

		return db.executeQuery(query);
	}

	public List<String> getAttributesList(String id, String queryType) throws SQLException {
		List<String> colTabName = new ArrayList<String>();
		String colNameQuery = "";
		System.out.println("queryType:" + queryType);
		if (!queryType.equals("Metric Functional Dependencies")) {
			if (sqlCoreConf.getRqlDbType().equals("oracle")) {
				db = new OracleDB();
				colNameQuery = "SELECT column_name FROM user_tab_cols WHERE table_name =" + "'" + id + "'";

			} else if (sqlCoreConf.getRqlDbType().equals("postgresql")) {
				db = new PostgresqlDB();
				colNameQuery = "SELECT column_name FROM information_schema.columns WHERE table_schema = 'public' and table_name ="
						+ "'" + id + "'";
			} else if (sqlCoreConf.getRqlDbType().equals("mysql")) {
				db = new MysqlDB();
				colNameQuery = "SELECT column_name FROM information_schema.columns WHERE table_schema = 'public' and table_name ="
						+ "'" + id + "'";
			}
		} else {
			if (sqlCoreConf.getRqlDbType().equals("oracle")) {
				db = new OracleDB();
				colNameQuery = "SELECT column_name FROM user_tab_cols WHERE table_name =" + "'" + id
						+ "' and (data_type LIKE '%NUMBER%' or data_type LIKE '%FLOAT%' or data_type LIKE '%DOUBLE%')";

			} else if (sqlCoreConf.getRqlDbType().equals("postgresql")) {
				db = new PostgresqlDB();
				colNameQuery = "SELECT column_name FROM information_schema.columns WHERE table_schema = 'public' and table_name ="
						+ "'" + id + "'"
						+ " and (data_type = 'integer' or data_type = 'smallint' or data_type = 'bigint' or data_type = 'serial' or data_type = 'bigserial' or data_type LIKE 'number%' or data_type LIKE 'decimal%' or\r\n"
						+ "data_type LIKE 'real%' or data_type LIKE 'double precision%')";
			} else if (sqlCoreConf.getRqlDbType().equals("mysql")) {
				db = new MysqlDB();
				colNameQuery = "SELECT column_name FROM information_schema.columns WHERE table_schema = 'public' and table_name ="
						+ "'" + id + "'"
						+ " and (data_type LIKE '%int%' or data_type = 'serial' or data_type = 'bigserial' or data_type LIKE 'number%' or data_type LIKE 'double%' or data_type LIKE 'float%' or data_type LIKE 'decimal%' or\r\n"
						+ "data_type LIKE 'real%' or data_type LIKE 'double precision%')";
			}
		}
		db.open(sqlCoreConf.getRqlDbHost(), sqlCoreConf.getRqlDbPort(), sqlCoreConf.getRqlDbName(),
				sqlCoreConf.getRqlDbAdminLogin(), sqlCoreConf.getRqlDbAdminPwd());

		ResultSet resultCol = db.executeQuery(colNameQuery);
		while (resultCol.next()) {
			colTabName.add(resultCol.getString(1));
		}
		return colTabName;
	}

	public SQLResultsObject executeQuery(String sqlQuery)
			throws SQLException, JsonParseException, JsonMappingException, IOException {
		Boolean isSelect = false;
		this.sqlQuery = sqlQuery;
		if (sqlCoreConf.getRqlDbType().equals("oracle")) {
			db = new OracleDB();
		} else if (sqlCoreConf.getRqlDbType().equals("postgresql")) {
			db = new PostgresqlDB();
		} else if (sqlCoreConf.getRqlDbType().equals("mysql")) {
			db = new MysqlDB();
		}
		db.open(sqlCoreConf.getRqlDbHost(), sqlCoreConf.getRqlDbPort(), sqlCoreConf.getRqlDbName(),
				sqlCoreConf.getRqlDbAdminLogin(), sqlCoreConf.getRqlDbAdminPwd());
		System.out.println("\nconnexion etablie \n");
		System.out.println(sqlQuery);
		ResultSet resultsSql = db.executeQuery(sqlQuery);
		List<Object> rows = new ArrayList<Object>();
		ObjectMapper mapper = new ObjectMapper();

		if (resultsSql != null) {
			isSelect = true;
			ResultSetMetaData resultMeta = resultsSql.getMetaData();
			List<String> header = new ArrayList<String>();
			for (int i = 1; i <= resultMeta.getColumnCount(); i++) {
				header.add(resultMeta.getColumnName(i));
			}
			Set<String> set = new LinkedHashSet<>();
			for (String str : header) {
				if (!str.equals("")) {
					String value = str;
					for (int i1 = 1; !set.add(value); i1++) {
						value = str + i1;
					}
				}
			}
			header = new ArrayList<String>();
			header.addAll(set);

			while (resultsSql.next()) {
				JSONObject json = new JSONObject();

				for (int i = 1; i <= resultMeta.getColumnCount(); i++) {
					Object obj = resultsSql.getObject(i);
					if (obj == null) {

						json.put(resultMeta.getColumnName(i), "");
					} else {

						json.put(header.get(i - 1), obj.toString());
					}

				}
				Object obj = mapper.readValue(json.toString(), Object.class);
				rows.add(obj);
			}
			for (int i = 1; i <= resultMeta.getColumnCount(); i++) {
				listEntete.add(header.get(i - 1));
			}
		}
		return new SQLResultsObject(listEntete, rows, isSelect);
	}

	public void insertRow(String query) throws SQLException {
		ResultSet resultsSql = db.executeQuery(query);
	}

	public SQLResultsRow executeQuery2(String sqlQuery) throws SQLException {
		SQLRow sqlRow;
		Boolean isSelect = false;
		this.sqlQuery = sqlQuery;
		if (sqlCoreConf.getRqlDbType().equals("oracle")) {
			db = new OracleDB();
		} else if (sqlCoreConf.getRqlDbType().equals("postgresql")) {
			db = new PostgresqlDB();
		} else if (sqlCoreConf.getRqlDbType().equals("mysql")) {
			db = new MysqlDB();
		}
		db.open(sqlCoreConf.getRqlDbHost(), sqlCoreConf.getRqlDbPort(), sqlCoreConf.getRqlDbName(),
				sqlCoreConf.getRqlDbAdminLogin(), sqlCoreConf.getRqlDbAdminPwd());
		System.out.println("\nconnexion etablie \n");
		System.out.println(sqlQuery);
		ResultSet resultsSql = db.executeQuery(sqlQuery);
		if (resultsSql != null) {
			isSelect = true;
			ResultSetMetaData rsmd = resultsSql.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				listEntete.add(rsmd.getColumnName(i));
			}
			while (resultsSql.next()) {
				recurentList = new ArrayList<String>();
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					recurentList.add(resultsSql.getString(i));
				}
				sqlRow = new SQLRow(recurentList);
				finalList.add(sqlRow);
			}
		}
		return new SQLResultsRow(listEntete, finalList, isSelect);
	}
}

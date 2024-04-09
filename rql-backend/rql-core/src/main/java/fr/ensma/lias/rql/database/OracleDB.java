package fr.ensma.lias.rql.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fr.ensma.lias.rql.database.Database;
import oracle.jdbc.pool.OracleDataSource;

/**
 * @author Bilal REZKELLAH
 */
public class OracleDB implements Database {

	private OracleDataSource ora;

	private Connection cx = null;

	private Statement stmt = null;

	private ResultSet rs = null;

	@Override
	public ResultSet executeQuery(String query) throws SQLException {
		if (cx == null) {
			cx = ora.getConnection();
		}
		if (stmt == null) {
			stmt = cx.createStatement();
		}
		boolean isSelect = stmt.execute(query);
		if (isSelect) {
			rs = stmt.getResultSet();
		} else {
			rs = null;
		}
		return rs;
	}

	@Override
	public void open(String host, int port, String database, String user, String password) throws SQLException {
		ora = new OracleDataSource();
		ora.setDriverType("thin");
		// ora.setServerName("134.214.104.91");
		ora.setServerName(host);
		ora.setPortNumber(port);
		ora.setDatabaseName(database);
		ora.setUser(user);
		ora.setPassword(password);
		ora.setLoginTimeout(20);
		cx = ora.getConnection();
		stmt = cx.createStatement();
	}

	public void close() throws IOException {
		try {
			stmt.close();
			cx.close();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
}

package fr.ensma.lias.rql.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Bilal REZKELLAH
 */
public class MysqlDB implements Database {

	private Connection cx;

	private Statement stmt;

	public ResultSet executeQuery(String query) throws SQLException {
		ResultSet rs = null;
		boolean isSelect = stmt.execute(query);
		if (isSelect) {
			rs = stmt.getResultSet();
		}
		return rs;
	}

	public void open(String host, int port, String database, String user, String password) throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		cx = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
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

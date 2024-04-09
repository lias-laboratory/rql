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
public class PostgresqlDB implements Database {

	private Connection cx;
	private Statement stmt;

	@Override
	public void close() throws IOException {
		try {
			stmt.close();
			cx.close();
		} catch (SQLException e) {
			throw new IOException(e);
		}

	}

	@Override
	public ResultSet executeQuery(String query) throws SQLException {
		ResultSet rs = null;
		boolean isSelect = stmt.execute(query);
		if (isSelect) {
			rs = stmt.getResultSet();
		}
		return rs;
	}

	@Override
	public void open(String host, int port, String database, String user, String password) throws SQLException {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		cx = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database, user, password);

		stmt = cx.createStatement();
	}
}

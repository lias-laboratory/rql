package fr.ensma.lias.rql.database;

import java.io.Closeable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Bilal REZKELLAH
 */
public abstract interface Database extends Closeable {

	/**
	 * Executes a SQL query.
	 * 
	 * @param query a SQL query
	 * @return the result of the query
	 * 
	 * @throws SQLException SQL-related error
	 */
	public ResultSet executeQuery(String query) throws SQLException;

	/**
	 * Initializes the connection to the DBMS.
	 * 
	 * @throws SQLException SQL-related error
	 */
	public void open(String host, int port, String database, String user, String password) throws SQLException;
}

package fr.ensma.lias.rql.dao.postgressession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.inject.Singleton;

import fr.ensma.lias.rql.cfg.Configuration;


/**
 * @author Bilal REZKELLAH
 */
@Singleton
public class PostgresSession implements IPostgresSession {

	Configuration cfg = new Configuration();
	private Connection cx;

	public PostgresSession() throws SQLException {
		System.out.println(cfg.getConfiguration().rqlDbAdminLogin());
		cx = DriverManager.getConnection(
				"jdbc:postgresql://" + cfg.getConfiguration().rqlDbHost() + ":" + cfg.getConfiguration().rqlDbPort()
						+ "/" + "rql",
				cfg.getConfiguration().rqlDbAdminLogin(), cfg.getConfiguration().rqlDbAdminPwd());
	}

	@Override
	public Connection getConnection() {
		return cx;
	}

	public Connection getCx() {
		return cx;
	}

	public void setCx(Connection cx) {
		this.cx = cx;
	}

}

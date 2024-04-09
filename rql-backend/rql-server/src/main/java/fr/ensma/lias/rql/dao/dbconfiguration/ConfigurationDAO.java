package fr.ensma.lias.rql.dao.dbconfiguration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.inject.Inject;

import fr.ensma.lias.rql.dao.postgressession.IPostgresSession;
import fr.ensma.lias.rql.model.DataBaseConfig;

/**
 * @author Bilal REZKELLAH
 */
public class ConfigurationDAO implements IConfigurationDAO {

	@Inject
	IPostgresSession ips;

	@Override
	public DataBaseConfig getConfigByID(String id) throws SQLException, IOException {
		Connection conn = ips.getConnection();
		Statement stmt = conn.createStatement();
		String query = "SELECT * FROM DATABASE WHERE projectid = " + "'" + Integer.valueOf(id) + "'";
		System.out.println(query);
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		DataBaseConfig dbc = new DataBaseConfig();
		dbc.setDataBaseHost(rs.getString("host"));
		dbc.setDataBaseName(rs.getString("name"));
		dbc.setDataBasePassword(rs.getString("password"));
		dbc.setDataBasePort(rs.getInt("port"));
		dbc.setDataBaseType(rs.getString("type"));
		dbc.setDataBaseUser(rs.getString("userid"));
		return dbc;
	}

	@Override
	public String createConfig(DataBaseConfig dbc) throws SQLException {
		String SQL = "INSERT INTO DATABASE (projectid,host,name,password,port,type,userid) " + "VALUES(?,?,?,?,?,?,?)";
		long id = 0;
		Connection conn = ips.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
		pstmt.setInt(1, Integer.valueOf(dbc.getProjectid()));
		pstmt.setString(2, dbc.getDataBaseHost());
		pstmt.setString(3, dbc.getDataBaseName());
		pstmt.setString(4, dbc.getDataBasePassword());
		pstmt.setInt(5, dbc.getDataBasePort());
		pstmt.setString(6, dbc.getDataBaseType());
		pstmt.setString(7, dbc.getDataBaseUser());

		int affectedRows = pstmt.executeUpdate();
		// check the affected rows
		if (affectedRows > 0) {
			// get the ID back
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getLong(1);
			}
		}

		return Long.toString(id);
	}

}

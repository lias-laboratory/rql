package fr.ensma.lias.rql.dao.dbconfiguration;

import java.io.IOException;
import java.sql.SQLException;

import fr.ensma.lias.rql.model.DataBaseConfig;

/**
 * @author Bilal REZKELLAH
 */
public interface IConfigurationDAO {

	DataBaseConfig getConfigByID(String ProjectID) throws SQLException, IOException;

	String createConfig(DataBaseConfig dbc) throws SQLException;

}

package fr.ensma.lias.rql.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import fr.ensma.lias.rql.api.SchemaResource;
import fr.ensma.lias.rql.core.IRql;
import fr.ensma.lias.rql.dao.dbconfiguration.IConfigurationDAO;
import fr.ensma.lias.rql.dao.project.IProjectDAO;
import fr.ensma.lias.rql.dto.SchemaResult;
import fr.ensma.lias.rql.model.DataBaseConfig;

/**
 * @author Bilal REZKELLAH
 */
public class SchemaResourceImp implements SchemaResource {

	@Inject
	IRql irql;

	@Inject
	IConfigurationDAO iconfig;

	@Inject
	IProjectDAO iproject;

	@Override
	public SchemaResult getAllTables(String projectID) {
		DataBaseConfig dbc = null;
		try {
			dbc = iconfig.getConfigByID(projectID);
		} catch (SQLException | IOException e1) {
			e1.printStackTrace();
			throw new WebApplicationException("Connexion faild.", Status.BAD_REQUEST);

		}
		try {
			return irql.getDataBaseSchema(dbc);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			throw new WebApplicationException("Schema Base Error.", Status.BAD_REQUEST);
		}
	}

	@Override
	public List<String> getAllTablesHeader(String projectID) {
		DataBaseConfig dbc = null;
		try {
			dbc = iconfig.getConfigByID(projectID);
		} catch (SQLException | IOException e1) {
			e1.printStackTrace();
			throw new WebApplicationException("Connexion faild.", Status.BAD_REQUEST);

		}
		try {
			return irql.getDataBaseTablesHeader(dbc);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			throw new WebApplicationException("Schema Base Error.", Status.BAD_REQUEST);
		}
	}

	@Override
	public List<String> getAttributesList(String id, String queryType, String projectID) {
		DataBaseConfig dbc = null;
		try {
			dbc = iconfig.getConfigByID(projectID);
		} catch (SQLException | IOException e1) {
			e1.printStackTrace();
			throw new WebApplicationException("Connexion faild.", Status.BAD_REQUEST);

		}
		try {
			return irql.getTableAttributesList(id, queryType, dbc);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
			throw new WebApplicationException("Schema Base Error.", Status.BAD_REQUEST);
		}
	}
}

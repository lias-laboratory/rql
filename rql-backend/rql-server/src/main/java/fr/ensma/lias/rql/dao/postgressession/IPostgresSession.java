package fr.ensma.lias.rql.dao.postgressession;

import java.sql.Connection;

/**
 * @author Bilal REZKELLAH
 */
public interface IPostgresSession {

	Connection getConnection();

}

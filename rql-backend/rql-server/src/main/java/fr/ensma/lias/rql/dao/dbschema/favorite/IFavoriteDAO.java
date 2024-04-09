package fr.ensma.lias.rql.dao.dbschema.favorite;

import java.sql.SQLException;
import java.util.List;

import fr.ensma.lias.rql.dto.Favorite;

/**
 * @author Bilal REZKELLAH
 */
public interface IFavoriteDAO {

	String createFavorite(Favorite favorite) throws SQLException;

	boolean deleteFavorite(String favoriteID) throws SQLException;

	List<Favorite> getAllFavoriteByProject(String projectID) throws SQLException;

	List<Favorite> getAllFavoriteSQLQueries(String projectID) throws SQLException;
}

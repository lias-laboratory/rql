package fr.ensma.lias.rql.dao.dbschema.favorite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import fr.ensma.lias.rql.dao.postgressession.IPostgresSession;
import fr.ensma.lias.rql.dto.Favorite;

/**
 * @author Bilal REZKELLAH
 */
public class FavoriteDAO implements IFavoriteDAO {
	
	@Inject
	IPostgresSession ips;

	@Override
	public String createFavorite(Favorite favorite) throws SQLException {
		String SQL = "INSERT INTO favorite (query,creationdate,description,projectID,favoritename,type) "
				+ "VALUES(?,?,?,?,?,?)";
		long id = 0;
		Connection conn = ips.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
		pstmt.setString(1, favorite.getQuery());
		pstmt.setString(2, favorite.getCreationDate());
		pstmt.setString(3, favorite.getDescription());
		pstmt.setInt(4, Integer.valueOf(favorite.getProjectID()));
		pstmt.setString(5, favorite.getName());
		pstmt.setString(6, favorite.getType());
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

	@Override
	public boolean deleteFavorite(String favoriteID) throws SQLException {
		String SQL = "DELETE FROM FAVORITE WHERE favoriteid = ? ";
		Connection conn = ips.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
		pstmt.setInt(1, Integer.valueOf(favoriteID));
		pstmt.execute();
		return pstmt.execute();
	}

	@Override
	public List<Favorite> getAllFavoriteByProject(String projectID) throws SQLException {
		Favorite favorite;
		List<Favorite> favoriteList = new ArrayList<Favorite>();
		Connection conn = ips.getConnection();
		String query = "SELECT * FROM FAVORITE WHERE projectid = ? ";
		PreparedStatement pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, Integer.valueOf(projectID));
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			favorite = new Favorite();
			favorite.setQuery(rs.getString("query"));
			favorite.setCreationDate(rs.getString("creationdate"));
			favorite.setProjectID(String.valueOf(rs.getInt("projectid")));
			favorite.setId(rs.getString("favoriteid"));
			favorite.setDescription(rs.getString("description"));
			favorite.setName(rs.getString("favoritename"));
			favorite.setType(rs.getString("type"));
			favoriteList.add(favorite);
		}
		return favoriteList;
	}

	@Override
	public List<Favorite> getAllFavoriteSQLQueries(String projectID) throws SQLException {
		Favorite favorite;
		List<Favorite> favoriteList = new ArrayList<Favorite>();
		Connection conn = ips.getConnection();
		String query = "SELECT * FROM FAVORITE WHERE projectid = ? and type = 'SQL' ";
		PreparedStatement pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, Integer.valueOf(projectID));
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			favorite = new Favorite();
			favorite.setQuery(rs.getString("query"));
			favorite.setCreationDate(rs.getString("creationdate"));
			favorite.setProjectID(String.valueOf(rs.getInt("projectid")));
			favorite.setId(rs.getString("favoriteid"));
			favorite.setDescription(rs.getString("description"));
			favorite.setName(rs.getString("favoritename"));
			favorite.setType(rs.getString("type"));
			favoriteList.add(favorite);
		}
		return favoriteList;
	}

}

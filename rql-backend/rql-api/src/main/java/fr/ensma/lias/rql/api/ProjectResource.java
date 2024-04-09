package fr.ensma.lias.rql.api;

import static fr.ensma.lias.rql.api.ApiParameters.ID;
import static fr.ensma.lias.rql.api.ApiPaths.PROJECT;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import fr.ensma.lias.rql.dto.Favorite;
import fr.ensma.lias.rql.dto.Project;

/**
 * @author Bilal REZKELLAH
 */
@Path(PROJECT)
@Produces(MediaType.APPLICATION_JSON)
public interface ProjectResource {

	@Path("/allproject")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	List<Project> getAllProjectByUserID(@QueryParam("userID") String userID);

	@Path("/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	String createNewProject(Project project);

	@Path("{id}")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	Project getProjectById(@PathParam(ID) String id);

	@Path("{id}")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	boolean deleteProjectById(@PathParam(ID) String id);

	@Path("/update")
	@POST // à remplacer par un PUT
	@Consumes(MediaType.APPLICATION_JSON)
	boolean updateProject(Project project);

	@Path("{id}/favorite")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	List<Favorite> getAllFavoriteByProject(@PathParam(ID) String id);

	@Path("{id}/favorite/sql")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	List<Favorite> getAllFavoriteSqlQueries(@PathParam(ID) String id);

	@Path("/favorite/{id}")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	boolean deleteFavoriteById(@PathParam(ID) String id);

	@Path("/favorite/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	String createNewFavorite(Favorite favorite);

}

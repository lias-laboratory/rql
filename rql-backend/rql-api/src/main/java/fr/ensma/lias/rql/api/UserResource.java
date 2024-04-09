package fr.ensma.lias.rql.api;

import static fr.ensma.lias.rql.api.ApiPaths.USER;
import static fr.ensma.lias.rql.api.ApiParameters.ID;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataParam;

import fr.ensma.lias.rql.dto.User;

/**
 * @author Bilal REZKELLAH
 */
@Path(USER)
@Produces(MediaType.APPLICATION_JSON)
public interface UserResource {

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	List<User> getAllCollaborarors(@QueryParam(ID) String userid);

	@Path("/collaborator")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	boolean deleteCollaborator(@QueryParam("projectid") String projectid, @QueryParam("username") String username);

	@Path("/collaborator")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	String addCollaborator(@FormDataParam("projectid") String projectid, @FormDataParam("username") String username);

	@Path("/collaborator")
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	String getCollaborators(@QueryParam("projectid") String projectid, @QueryParam("username") String username);

}

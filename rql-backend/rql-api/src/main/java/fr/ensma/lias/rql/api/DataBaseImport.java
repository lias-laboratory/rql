package fr.ensma.lias.rql.api;

import static fr.ensma.lias.rql.api.ApiParameters.COLUMN_LIST;
import static fr.ensma.lias.rql.api.ApiParameters.ID;
import static fr.ensma.lias.rql.api.ApiParameters.TABLE_NAME;
import static fr.ensma.lias.rql.api.ApiParameters.SHEET_INDEX;
import static fr.ensma.lias.rql.api.ApiPaths.DB_IMPORT;
import static fr.ensma.lias.rql.api.ApiPaths.TABLE_IMPORT;
import static fr.ensma.lias.rql.api.ApiPaths.UPLOAD_XLSX;
import static fr.ensma.lias.rql.api.ApiPaths.SCRIPT_DOWNLOAD;
import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import fr.ensma.lias.rql.dto.FilePreview;
import fr.ensma.lias.rql.dto.TableImportResult;

/**
 * @author Bilal REZKELLAH
 */
@Path(DB_IMPORT)
@Produces(MediaType.APPLICATION_JSON)
public interface DataBaseImport {

	@TokenAuthenticated
	@Path(UPLOAD_XLSX)
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	FilePreview uploadFile(@FormDataParam("file") InputStream filestream,
			@FormDataParam("file") FormDataContentDisposition fileDetail);

	@TokenAuthenticated
	@Path("{id}" + TABLE_IMPORT)
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	TableImportResult tableImport(@PathParam(ID) String id, @QueryParam(SHEET_INDEX) Integer sheetIndex,
			@QueryParam(TABLE_NAME) String tableName, @QueryParam("projectID") String projectID,
			@QueryParam(COLUMN_LIST) String columnList);

	@TokenAuthenticated
	@Path("{id}" + SCRIPT_DOWNLOAD)
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("text/plain")
	Response scriptDownload(@PathParam(ID) String id);

}

package fr.ensma.lias.rql.service;

import java.io.IOException;
import java.sql.SQLException;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.ensma.lias.rql.api.QueryResource;
import fr.ensma.lias.rql.cfg.IConfiguration;
import fr.ensma.lias.rql.core.IRql;
import fr.ensma.lias.rql.dao.dbconfiguration.IConfigurationDAO;
import fr.ensma.lias.rql.dao.inmemory.InMemory;
import fr.ensma.lias.rql.dao.query.IQueryDAO;
import fr.ensma.lias.rql.dto.CheckRuleResult;
import fr.ensma.lias.rql.dto.RQLResult;
import fr.ensma.lias.rql.dto.SQLResult;
import fr.ensma.lias.rql.dto.TypeQuery;
import fr.ensma.lias.rql.rqlgrammar.ParseException;

/**
 * @author Rezkellah Bilal
 */
public class QueryResourceImp implements QueryResource {

    @Inject
    InMemory myInstance;

    @Inject
    IConfiguration cfg;

    @Inject
    IQueryDAO iquery;
    
    @Inject
    IConfigurationDAO iconfig;

    @Inject
    IRql irql;

    @Override
    public SQLResult getResultSet(String ID, String ProjectID) throws JsonParseException, JsonMappingException, IOException {
	if (ID == null || ID.isEmpty()) {
	    throw new WebApplicationException("Parameter is missing.", Status.NOT_FOUND);
	}
	System.out.println(ProjectID);
	String query = myInstance.getSqlQueryDB().get(ID);
	if (ID == null || ID.isEmpty()) {
	    throw new WebApplicationException("Parameter is missing.", Status.NOT_FOUND);
	}
	try {
	    SQLResult executeSqlQuery = irql.executeSqlQuery(query,iconfig.getConfigByID(ProjectID) );
	    return executeSqlQuery;
	} catch (SQLException e) {
	    Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
	    throw new WebApplicationException(response);
	}
    }

    @Override
    public TypeQuery createQuery(String query, String projectid) {
	if (irql.isRqlQuery(query)) {
	    try {
		return iquery.createRQLQuery(query, iconfig.getConfigByID(projectid));
	    } catch (Exception e) {
		Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
		throw new WebApplicationException(response);
	    }
	} else {
	    try {
		return iquery.createSQLQuery(query);
	    } catch (Exception e) {
		e.printStackTrace();
		throw new WebApplicationException("Query is bad.", Status.BAD_REQUEST);
	    }
	}
    }

    @Override
    public RQLResult getAllRulesID(String id, String support, String confidence) {
	try {
	    return irql.executeRqlQuery(id, Double.parseDouble(support), Double.parseDouble(confidence));
	} catch (Exception e) {
	    e.printStackTrace();
	    Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
	    throw new WebApplicationException(response);
	}
    }

    @Override
    public CheckRuleResult checkForThisRule(String id, String leftAttributes, String rightAttribute, String support,
	    String confidence, String projectID) {
	long debut = System.currentTimeMillis();

	try {
	    return irql.checkRule(debut, iconfig.getConfigByID(projectID), id, leftAttributes, rightAttribute, Double.parseDouble(support),
		    Double.parseDouble(confidence));
	} catch (ParseException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    throw new WebApplicationException("Query is bad.", Status.BAD_REQUEST);
	} catch (NumberFormatException e) {
	    e.printStackTrace();
	    throw new WebApplicationException("Query is bad.", Status.BAD_REQUEST);
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    throw new WebApplicationException("Connection faild", Status.BAD_REQUEST);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new WebApplicationException("Connection faild", Status.BAD_REQUEST);

	}
    }

    @Override
    public String isGoodConstructed(String query) {
	try {
	    irql.testQuery(query);
	} catch (Exception e) {
	    Response response = Response.status(Status.BAD_REQUEST).entity(e.getMessage()).build();
	    throw new WebApplicationException(response);
	}
	return "true";
    }
}

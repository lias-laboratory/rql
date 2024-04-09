package fr.ensma.lias.rql.dao.dbschema;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author Bilal REZKELLAH
 */
public interface ISchemaDAO {

	String createTable(XSSFSheet sheet, List<Integer> intList, String tableName)
			throws SQLException, JsonParseException, JsonMappingException, IOException;
}

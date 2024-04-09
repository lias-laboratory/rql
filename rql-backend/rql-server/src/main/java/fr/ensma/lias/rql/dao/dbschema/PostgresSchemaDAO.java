package fr.ensma.lias.rql.dao.dbschema;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.ensma.lias.rql.cfg.IConfiguration;
import fr.ensma.lias.rql.cfg.SqlCoreConf;
import fr.ensma.lias.rql.dao.dbconfiguration.IConfigurationDAO;
import fr.ensma.lias.rql.dbimport.NameNormalizer;
import fr.ensma.lias.rql.dbimport.PostgresNameNormalizer;
import fr.ensma.lias.rql.sql.SQLManager;


/**
 * @author Bilal REZKELLAH
 */
public class PostgresSchemaDAO extends AbstractSchemaDAO {

	@Inject
	IConfiguration cfg;

	@Inject
	IConfigurationDAO iconfig;

	public String cellTypeMap(CellType cellType) {
		if (cellType == CellType.STRING) {
			return "TEXT";
		} else if (cellType == CellType.NUMERIC) {
			return "BIGINT";
		} else if (cellType == CellType.BOOLEAN) {
			return "BOOLEAN";
		} else {
			return null;
		}
	}

	public String importTable(XSSFSheet sheet, List<Integer> intList, String tableName, String projectID)
			throws SQLException, JsonParseException, JsonMappingException, IOException {
		String scriptSQL = "";
		String createTable = null;
		List<CellType> colTypeList = new ArrayList<CellType>();
		System.out.println(cfg.getConfiguration().rqlDbName());

		NameNormalizer nNormalizer = new PostgresNameNormalizer();

		SQLManager sql = new SQLManager(
				new SqlCoreConf(cfg.getConfiguration().rqlDbName(), cfg.getConfiguration().rqlDbHost(),
						cfg.getConfiguration().rqlDbPort(), cfg.getConfiguration().rqlDbType(),
						cfg.getConfiguration().rqlDbAdminLogin(), cfg.getConfiguration().rqlDbAdminPwd()));

		sql.openDB();
		Row titleRow = sheet.getRow(0);
		createTable = "CREATE TABLE " + nNormalizer.colNameNormalizer(tableName) + " (";

		List<String> header = new ArrayList<String>();
		DataFormatter formatter = new DataFormatter();
		for (int j = 0; j < titleRow.getLastCellNum(); j++) {
			String val = formatter.formatCellValue(titleRow.getCell(j));
			header.add(val);
		}
		Set<String> set = new LinkedHashSet<>();
		for (String str : header) {
			if (!str.equals("")) {
				String value = str;
				for (int i1 = 1; !set.add(value); i1++) {
					value = str + i1;
				}
			}
		}
		header = new ArrayList<String>();
		header.addAll(set);
		for (int i = 0; i < intList.size(); i++) {
			if (i == 0) {
				createTable += nNormalizer.colNameNormalizer(header.get(intList.get(i))) + " "
						+ cellTypeMap(getColType(sheet, intList.get(i)));
			} else {
				createTable += ", " + nNormalizer.colNameNormalizer(header.get(intList.get(i))) + " "
						+ cellTypeMap(colTypeList.get(i));

			}
		}
		createTable += ")";
		scriptSQL += createTable;
		irql.executeSqlQuery(createTable, iconfig.getConfigByID(projectID));
		String insertRow = "";
		insertRow = "INSERT INTO " + nNormalizer.colNameNormalizer(tableName) + " (";
		for (int i = 0; i < intList.size(); i++) {
			if (i == 0) {
				insertRow += nNormalizer.colNameNormalizer(header.get(intList.get(i)));
			} else {
				insertRow += ", " + nNormalizer.colNameNormalizer(header.get(intList.get(i)));
			}
		}
		insertRow += ") ";
		String values = "";
		Iterator<Row> iteratorRow = sheet.iterator();
		if (iteratorRow.hasNext())
			iteratorRow.next();
		while (iteratorRow.hasNext()) {
			values = insertRow + "VALUES(";
			Row row = iteratorRow.next();
			for (int i = 0; i < intList.size(); i++) {
				if (colTypeList.get(i) == CellType.STRING) {
					String val = formatter.formatCellValue(row.getCell(intList.get(i)));
					if (i == 0) {
						values += "'" + val.replace("'", "") + "'";
					} else {
						values += ", " + "'" + val.replace("'", "") + "'";
					}
				} else if (colTypeList.get(i) == CellType.NUMERIC) {
					Integer val = -1;
					val = (int) row.getCell(intList.get(i)).getNumericCellValue();
					if (i == 0) {
						values += val;
					} else {
						values += ", " + val;
					}
				} else if (colTypeList.get(i) == CellType.BOOLEAN) {

				} else {
					boolean val = (boolean) row.getCell(intList.get(i)).getBooleanCellValue();
					if (i == 0) {
						values += val;
					} else {
						values += ", " + val;
					}
				}
			}
			values += ")";
			scriptSQL += values;
			sql.insertRow(values);
		}
		sql.closeDB();
		return scriptSQL;
	};
}

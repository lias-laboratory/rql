package fr.ensma.lias.rql.dao.dbschema;

import org.apache.poi.ss.usermodel.CellType;

/**
 * @author Bilal REZKELLAH
 */
public class MySqlSchemaDAO extends AbstractSchemaDAO {
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
}

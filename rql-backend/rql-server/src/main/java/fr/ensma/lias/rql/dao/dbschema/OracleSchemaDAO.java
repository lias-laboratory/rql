package fr.ensma.lias.rql.dao.dbschema;

import org.apache.poi.ss.usermodel.CellType;

/**
 * @author Bilal REZKELLAH
 */
public class OracleSchemaDAO extends AbstractSchemaDAO {

	public String cellTypeMap(CellType cellType) {
		if (cellType == CellType.STRING) {
			return "VARCHAR2(200)";
		} else if (cellType == CellType.NUMERIC) {
			return "NUMBER(20)";
		} else if (cellType == CellType.BOOLEAN) {
			return "BOOLEAN";
		} else {
			return null;
		}
	}
}

package fr.ensma.lias.rql.dao.dbschema;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import fr.ensma.lias.rql.core.IRql;

/**
 * @author Bilal REZKELLAH
 */
public class AbstractSchemaDAO implements ISchemaDAO {

	@Inject
	IRql irql;

	public CellType getColType(XSSFSheet sheet, Integer index) {
		Row rowi = sheet.getRow(1);
		CellType cellType = null;
		int rowIndex = 1;
		cellType = rowi.getCell(index).getCellTypeEnum();
		if (cellType == CellType.BLANK) {
			while (cellType == CellType.BLANK && rowIndex < sheet.getPhysicalNumberOfRows() - 1) {
				rowi = sheet.getRow(++rowIndex);
				cellType = rowi.getCell(index).getCellTypeEnum();
			}
			if (rowIndex == sheet.getPhysicalNumberOfRows() - 1) {
				cellType = CellType.STRING;
			}
		}
		if (cellType == CellType.NUMERIC) {
			rowIndex = 1;
			while ((cellType == CellType.NUMERIC || cellType == CellType.BLANK)
					&& rowIndex < sheet.getPhysicalNumberOfRows() - 1) {
				rowi = sheet.getRow(++rowIndex);
				cellType = rowi.getCell(index).getCellTypeEnum();
			}
			if (cellType != CellType.NUMERIC && cellType != CellType.BLANK) {
				cellType = CellType.STRING;
			} else {

				cellType = CellType.NUMERIC;
			}
		}
		return cellType;
	}

	public String cellTypeMap(CellType cellType) {
		return null;
	}

	@Override
	public String createTable(XSSFSheet sheet, List<Integer> intList, String tableName)
			throws SQLException, JsonParseException, JsonMappingException, IOException {
		return null;
	};

}

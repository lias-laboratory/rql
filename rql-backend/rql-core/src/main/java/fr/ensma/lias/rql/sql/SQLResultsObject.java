package fr.ensma.lias.rql.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class SQLResultsObject extends SQLResults {

	public SQLResultsObject(List<String> header, List<Object> rows, Boolean isSelect) {
		super(header, isSelect);
		this.rows = rows;
	}

	public List<Object> rows = new ArrayList<Object>();

	public List<Object> getRows() {
		return rows;
	}

	public void setRows(List<Object> rows) {
		this.rows = rows;
	}
}

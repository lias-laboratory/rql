package fr.ensma.lias.rql.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class SQLResultsRow extends SQLResults {

	public SQLResultsRow(List<String> header, List<SQLRow> rows, Boolean isSelect) {
		super(header, isSelect);
		this.rows = rows;
	}

	private List<SQLRow> rows = new ArrayList<SQLRow>();

	public List<SQLRow> getRows() {
		return rows;
	}

	public void setRows(List<SQLRow> rows) {
		this.rows = rows;
	}
}

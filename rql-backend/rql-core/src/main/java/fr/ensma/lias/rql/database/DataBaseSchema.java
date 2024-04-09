package fr.ensma.lias.rql.database;

import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class DataBaseSchema {

	private List<Table> tables;

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

	public DataBaseSchema(List<Table> tables) {
		super();
		this.tables = tables;
	}
}

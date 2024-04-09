package fr.ensma.lias.rql.dto;

import java.util.List;

import fr.ensma.lias.rql.database.Table;

/**
 * @author Bilal REZKELLAH
 */
public class SchemaResult {

	private List<Table> tables;

	public List<Table> getTables() {
		return tables;
	}

	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

	public SchemaResult(List<Table> tables) {
		super();
		this.tables = tables;
	}

}

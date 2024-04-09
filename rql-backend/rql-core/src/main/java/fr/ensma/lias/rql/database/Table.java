package fr.ensma.lias.rql.database;

import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class Table {

	private String tableName;

	private List<Column> columnsName;

	public Table(String tableName, List<Column> columnsName) {
		super();
		this.tableName = tableName;
		this.columnsName = columnsName;
	}

	public Table() {
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<Column> getColumnsName() {
		return columnsName;
	}

	public void setColumnsName(List<Column> columnsName) {
		this.columnsName = columnsName;
	}
}

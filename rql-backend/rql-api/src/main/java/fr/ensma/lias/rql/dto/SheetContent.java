package fr.ensma.lias.rql.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class SheetContent {

	private List<Object> rows = new ArrayList<Object>();

	private List<String> header = new ArrayList<String>();

	private String tableName;

	private Integer nbRow;

	public List<Object> getRows() {
		return rows;
	}

	public void setRows(List<Object> rows) {
		this.rows = rows;
	}

	public List<String> getHeader() {
		return header;
	}

	public void setHeader(List<String> header) {
		this.header = header;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public SheetContent(List<Object> rows, List<String> header, String tableName) {
		super();
		this.rows = rows;
		this.header = header;
		this.tableName = tableName;
	}

	public SheetContent() {

	}

	public Integer getNbRow() {
		return nbRow;
	}

	public void setNbRow(Integer nbRow) {
		this.nbRow = nbRow;
	}
}

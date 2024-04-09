package fr.ensma.lias.rql.dto;

/**
 * @author Bilal REZKELLAH
 */
public class TableImportResult {

	private String tableName;
	private Integer nbRow;
	private Integer nbCol;
	private boolean goNext2;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Integer getNbRow() {
		return nbRow;
	}

	public void setNbRow(Integer nbRow) {
		this.nbRow = nbRow;
	}

	public Integer getNbCol() {
		return nbCol;
	}

	public void setNbCol(Integer nbCol) {
		this.nbCol = nbCol;
	}

	public TableImportResult(String tableName, Integer nbRow, Integer nbCol, boolean goNext2) {
		super();
		this.tableName = tableName;
		this.nbRow = nbRow;
		this.nbCol = nbCol;
		this.goNext2 = goNext2;
	}

	public boolean isGoNext2() {
		return goNext2;
	}

	public void setGoNext2(boolean goNext2) {
		this.goNext2 = goNext2;
	}

}

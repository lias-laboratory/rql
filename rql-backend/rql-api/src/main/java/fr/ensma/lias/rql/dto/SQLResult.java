package fr.ensma.lias.rql.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class SQLResult {

	public List<Object> rows = new ArrayList<Object>();

	public List<String> header = new ArrayList<String>();

	boolean IsSelect;

	public boolean isIsSelect() {
		return IsSelect;
	}

	public void setIsSelect(boolean isSelect) {
		IsSelect = isSelect;
	}

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

	public SQLResult(List<String> header, List<Object> rows, Boolean isSelect) {
		this.header = header;
		this.rows = rows;
		this.IsSelect = isSelect;
	}

	public SQLResult() {

	}

}

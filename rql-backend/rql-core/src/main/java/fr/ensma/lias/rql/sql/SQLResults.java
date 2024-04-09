package fr.ensma.lias.rql.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class SQLResults {

	public List<String> header = new ArrayList<String>();

	private Boolean isSelect;

	public SQLResults(List<String> header, Boolean isSelect) {
		this.header = header;
		this.isSelect = isSelect;
	}

	public Boolean getIsSelect() {
		return isSelect;
	}

	public void setIsSelect(Boolean isSelect) {
		this.isSelect = isSelect;
	}

	public List<String> getHeader() {
		return header;
	}

	public void setHeader(List<String> header) {
		this.header = header;
	}

}

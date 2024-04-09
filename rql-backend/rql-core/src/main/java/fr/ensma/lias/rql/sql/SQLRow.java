package fr.ensma.lias.rql.sql;

import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class SQLRow {

	private List<String> cels;

	public List<String> getCels() {
		return cels;
	}

	public void setCels(List<String> cels) {
		this.cels = cels;
	}

	public SQLRow(List<String> cels) {
		this.cels = cels;
	}
}

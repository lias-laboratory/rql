package fr.ensma.lias.rql.dto;

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
		super();
		this.cels = cels;
	}
}

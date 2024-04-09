package fr.ensma.lias.rql;

import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class RQLResults {

	private List<Rule> exactRules;

	public List<Rule> getExactRules() {
		return exactRules;
	}

	public void setExactRules(List<Rule> exactRules) {
		this.exactRules = exactRules;
	}

	public RQLResults() {

	}

	public RQLResults(List<Rule> exactRules) {
		super();
		this.exactRules = exactRules;
	}
}

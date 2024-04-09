package fr.ensma.lias.rql.dto;

import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class RQLResult {

	private List<RQLRow> exactRule;

	private RuleGenerationSummary summary;

	public RuleGenerationSummary getSummary() {
		return summary;
	}

	public void setSummary(RuleGenerationSummary summary) {
		this.summary = summary;
	}

	public List<RQLRow> getexactRule() {
		return exactRule;
	}

	public RQLResult(List<RQLRow> exactRule, RuleGenerationSummary summary) {
		super();
		this.exactRule = exactRule;
		this.summary = summary;
	}

	public void setexactRule(List<RQLRow> exactRule) {
		this.exactRule = exactRule;
	}

	public RQLResult() {

	}

}

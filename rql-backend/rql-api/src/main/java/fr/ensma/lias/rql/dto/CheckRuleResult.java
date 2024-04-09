package fr.ensma.lias.rql.dto;

import fr.ensma.lias.rql.rulecheck.CheckResult;

/**
 * @author Bilal REZKELLAH
 */
public class CheckRuleResult {

	private CheckResult checkResult;

	private CheckRuleSummary summary;

	public CheckResult getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(CheckResult checkResult) {
		this.checkResult = checkResult;
	}

	public CheckRuleSummary getSummary() {
		return summary;
	}

	public CheckRuleResult(CheckResult checkResult, CheckRuleSummary summary) {
		super();
		this.checkResult = checkResult;
		this.summary = summary;
	}

	public void setSummary(CheckRuleSummary summary) {
		this.summary = summary;
	}
}

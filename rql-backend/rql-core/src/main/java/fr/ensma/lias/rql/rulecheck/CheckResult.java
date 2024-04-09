package fr.ensma.lias.rql.rulecheck;

import fr.ensma.lias.rql.sql.SQLResultsRow;

/**
 * @author Bilal REZKELLAH
 */
public class CheckResult {

	private String isTrue;

	private String support;

	private String confidence;

	private int counterExamplesNB;

	public SQLResultsRow counterExamples;

	public SQLResultsRow counterExamples2;

	public SQLResultsRow counterExamples3;

	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}

	public String getConfidence() {
		return confidence;
	}

	public int getCounterExamplesNB() {
		return counterExamplesNB;
	}

	public void setCounterExamplesNB(int counterExamplesNB) {
		this.counterExamplesNB = counterExamplesNB;
	}

	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}

	public CheckResult(String isTrue, String support, String confidence, int counterExamplesNB,
			SQLResultsRow counterExamples, SQLResultsRow counterExamples2, SQLResultsRow counterExamples3) {
		super();
		this.isTrue = isTrue;
		this.support = support;
		this.confidence = confidence;
		this.counterExamplesNB = counterExamplesNB;
		this.counterExamples = counterExamples;
		this.counterExamples2 = counterExamples2;
		this.counterExamples3 = counterExamples3;

	}

	public SQLResultsRow getCounterExamples2() {
		return counterExamples2;
	}

	public void setCounterExamples2(SQLResultsRow counterExamples2) {
		this.counterExamples2 = counterExamples2;
	}

	public SQLResultsRow getCounterExamples3() {
		return counterExamples3;
	}

	public void setCounterExamples3(SQLResultsRow counterExamples3) {
		this.counterExamples3 = counterExamples3;
	}

	public String getIsTrue() {
		return isTrue;
	}

	public void setIsTrue(String isTrue) {
		this.isTrue = isTrue;
	}

	public SQLResultsRow getCounterExamples() {
		return counterExamples;
	}

	public void setCounterExamples(SQLResultsRow counterExamples) {
		this.counterExamples = counterExamples;
	}

	public CheckResult() {
	}
}

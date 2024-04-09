package fr.ensma.lias.rql.dto;

import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class CheckRuleSummary extends ExecutionSummary {

	private int counterExampleNB;

	private int tupleNB;

	private String isTrue;

	private String support;

	private String confidence;

	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}

	public String getConfidence() {
		return confidence;
	}

	public void setConfidence(String confidence) {
		this.confidence = confidence;
	}

	public CheckRuleSummary(long timeExecution, List<String> attributesList, List<String> tuplesList,
			int counterExampleNB, int tupleNB, String isTrue, String support, String confidence) {
		super(timeExecution, attributesList, tuplesList);
		this.counterExampleNB = counterExampleNB;
		this.tupleNB = tupleNB;
		this.isTrue = isTrue;
		this.support = support;
		this.confidence = confidence;
	}

	public int getCounterExampleNB() {
		return counterExampleNB;
	}

	public void setCounterExampleNB(int counterExampleNB) {
		this.counterExampleNB = counterExampleNB;
	}

	public int getTupleNB() {
		return tupleNB;
	}

	public void setTupleNB(int tupleNB) {
		this.tupleNB = tupleNB;
	}

	public String getIsTrue() {
		return isTrue;
	}

	public void setIsTrue(String isTrue) {
		this.isTrue = isTrue;
	}

}

package fr.ensma.lias.rql;

import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class Rule {

	private List<String> leftAttributes;

	private String rightAttributes;

	private double support;

	private double confidence;

	private double lift;

	public Rule() {
	}

	public List<String> getLeftAttributes() {
		return leftAttributes;
	}

	public void setLeftAttributes(List<String> leftAttributes) {
		this.leftAttributes = leftAttributes;
	}

	public String getRightAttributes() {
		return rightAttributes;
	}

	public void setRightAttributes(String rightAttributes) {
		this.rightAttributes = rightAttributes;
	}

	public double getSupport() {
		return support;
	}

	public void setSupport(double support) {
		this.support = support;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	public double getLift() {
		return lift;
	}

	public void setLift(double lift) {
		this.lift = lift;
	}
}

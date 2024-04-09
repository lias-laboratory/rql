package fr.ensma.lias.rql.dto;

/**
 * @author Bilal REZKELLAH
 */
public class RQLRow {

	private String leftAttributes;

	private String rightAttribute;

	private double support;

	private double confidence;

	private double lift;

	public String getLeftAttributes() {
		return leftAttributes;
	}

	public void setLeftAttributes(String leftAttributes) {
		this.leftAttributes = leftAttributes;
	}

	public String getRightAttribute() {
		return rightAttribute;
	}

	public void setRightAttribute(String rightAttribute) {
		this.rightAttribute = rightAttribute;
	}

	public RQLRow(String leftAttributes, String rightAttribute) {
		super();
		this.leftAttributes = leftAttributes;
		this.rightAttribute = rightAttribute;
	}

	public RQLRow() {
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

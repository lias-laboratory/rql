package fr.ensma.lias.rql.baseresults;

/**
 * @author Bilal REZKELLAH
 */
public class BaseLine {

	private String count;

	private String ag;

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getAg() {
		return ag;
	}

	public void setAg(String ag) {
		this.ag = ag;
	}

	public BaseLine(String count, String ag) {
		this.count = count;
		this.ag = ag;
	}
}

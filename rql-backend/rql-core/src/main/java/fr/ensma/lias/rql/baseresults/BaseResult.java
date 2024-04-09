package fr.ensma.lias.rql.baseresults;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class BaseResult {

	private List<BaseLine> content = new ArrayList<BaseLine>();

	public void resultsToList(ResultSet result) throws SQLException {
		BaseLine ligne;
		while (result.next()) {
			String count = result.getString(1);
			String ag = result.getString(2);
			ligne = new BaseLine(count, ag);
			this.content.add(ligne);
		}
	}

	public List<BaseLine> getContent() {
		return content;
	}

	public void setContent(List<BaseLine> content) {
		this.content = content;
	}
}

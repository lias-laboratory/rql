package fr.ensma.lias.rql.queryconstruction;

/**
 * @author Bilal REZKELLAH
 */
public class QueryConstructor {

	static final String FINDRULES = "FINDRULES";
	static final String OVER = "OVER";
	static final String SCOPE = "SCOPE";
	static final String WHERE = "WHERE";
	static final String CONDITION_ON_A_IS = "CONDITION ON A IS";
	static final String NULL = " NULL";
	static final String FD = "Functional Dependencies";
	static final String SFD = "Sequential Functional Dependencies";
	static final String MFD = "Metric Functional Dependencies";
	static final String CAR = "Classic Association Rules";
	static final String NV = "Null Values";

	String constructedQuery = "";
	String attributesList = "[ATTRIBUTES LIST]";
	String dataSet = "[TABLE NAME]";
	String tableName = "[TABLE NAME]";
	String whereScope = "[CONDITION]";
	String conditionalWhere = "[CONDITION]";

	public QueryConstructor() {

	}

	public String constructquery(String QueryType, String isTable, String isALLData, String sqlQuerie, String tableName,
			String subsetWhere, String attributesList, boolean isConditional, String conditionalWhere,
			String tolerence) {

		constructedQuery = FINDRULES + "\n" + OVER + " ";

		if (!attributesList.equals("")) {
			this.attributesList = attributesList;
		}

		constructedQuery += this.attributesList;
		constructedQuery += "\n";
		constructedQuery += SCOPE;
		if (isTable.equals("table")) {
			if (isALLData.equals("Subset")) {
				if (!tableName.equals("")) {
					this.tableName = tableName;
				}
				if (!subsetWhere.equals("")) {
					this.whereScope = subsetWhere;
				}
				dataSet = " ( SELECT * FROM " + this.tableName + " " + WHERE + " " + this.whereScope.toUpperCase()
						+ " )";
			} else if (isALLData.equals("All")) {
				if (!tableName.equals("")) {
					this.tableName = tableName;
				}
				dataSet = this.tableName;
			}
		} else {
			dataSet = "( " + sqlQuerie + " )";
		}
		if (QueryType.equals(NV)) {

			constructedQuery += " t1 " + this.dataSet + "\n";
		} else {
			constructedQuery += " t1, t2 " + this.dataSet + "\n";
		}

		if (isConditional) {
			if (!conditionalWhere.equals("")) {
				this.conditionalWhere = conditionalWhere;
			}

			constructedQuery += WHERE + " " + this.conditionalWhere.toUpperCase() + "\n";

		}
		constructedQuery += CONDITION_ON_A_IS;
		if (QueryType.equals(NV)) {

			constructedQuery += " t1.A is " + NULL;
		} else if (QueryType.equals(FD)) {
			constructedQuery += " t1.A = t2.A";
		} else if (QueryType.equals(SFD)) {
			constructedQuery += " t1.A > t2.A";
		} else if (QueryType.equals(MFD)) {
			constructedQuery += " 2 * ABS(t1.A - t2.A) / (t1.A + t2.A) < " + tolerence;
		} else if (QueryType.equals(CAR)) {
			constructedQuery += " t1.A = 1 ";
		}
		return constructedQuery;

	}
}

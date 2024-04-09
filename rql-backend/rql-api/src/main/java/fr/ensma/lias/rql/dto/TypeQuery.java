package fr.ensma.lias.rql.dto;

import java.util.List;

import fr.ensma.lias.rql.baseresults.BaseLine;

/**
 * @author Bilal REZKELLAH
 */
public class TypeQuery {

	private QueryEnumType type;

	private String ruleParsed;

	private String queryKey;

	private List<BaseLine> baseContent;

	private List<String> attributesList;

	public List<String> getAttributesList() {
		return attributesList;
	}

	public void setAttributesList(List<String> attributesList) {
		this.attributesList = attributesList;
	}

	public TypeQuery(QueryEnumType type, String ruleParsed, String queryKey, List<BaseLine> baseContent,
			List<String> attributesList) {
		super();
		this.setType(type);
		this.ruleParsed = ruleParsed;
		this.queryKey = queryKey;
		this.baseContent = baseContent;
		this.attributesList = attributesList;
	}

	public String getQueryKey() {
		return queryKey;
	}

	public void setQueryKey(String queryKey) {
		this.queryKey = queryKey;
	}

	public List<BaseLine> getBaseContent() {
		return baseContent;
	}

	public void setBaseContent(List<BaseLine> baseContent) {
		this.baseContent = baseContent;
	}

	public String getRuleParsed() {
		return ruleParsed;
	}

	public void setRuleParsed(String ruleParsed) {
		this.ruleParsed = ruleParsed;
	}

	public QueryEnumType getType() {
		return type;
	}

	public void setType(QueryEnumType type) {
		this.type = type;
	}

}

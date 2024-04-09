package fr.ensma.lias.rql.dto;

import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class ExecutionSummary {

	private long timeExecution;

	private List<String> attributesList;

	private List<String> tuplesList;

	public long getTimeExecution() {
		return timeExecution;
	}

	public void setTimeExecution(long timeExecution) {
		this.timeExecution = timeExecution;
	}

	public List<String> getAttributesList() {
		return attributesList;
	}

	public void setAttributesList(List<String> attributesList) {
		this.attributesList = attributesList;
	}

	public List<String> getTuplesList() {
		return tuplesList;
	}

	public void setTuplesList(List<String> tuplesList) {
		this.tuplesList = tuplesList;
	}

	public ExecutionSummary(long timeExecution, List<String> attributesList, List<String> tuplesList) {
		super();
		this.timeExecution = timeExecution;
		this.attributesList = attributesList;
		this.tuplesList = tuplesList;
	}

}

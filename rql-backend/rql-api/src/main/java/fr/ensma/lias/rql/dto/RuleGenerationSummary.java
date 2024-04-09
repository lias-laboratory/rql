package fr.ensma.lias.rql.dto;

import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class RuleGenerationSummary extends ExecutionSummary {

	public RuleGenerationSummary(long timeExecution, List<String> attributesList, List<String> tuplesList) {
		super(timeExecution, attributesList, tuplesList);
	}
}

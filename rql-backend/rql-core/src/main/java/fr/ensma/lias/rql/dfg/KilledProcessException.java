package fr.ensma.lias.rql.dfg;

import java.util.concurrent.RejectedExecutionException;

/**
 * @author Gouriou Benjamin
 */
public class KilledProcessException extends RejectedExecutionException {

	private static final long serialVersionUID = -6689075757022563066L;

	KilledProcessException(String details) {
		super(details);
	}
}

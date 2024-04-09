package fr.ensma.lias.rql.dfg;

/**
 * @author Gouriou Benjamin
 */
public class ThreadRunException extends RuntimeException {

	private static final long serialVersionUID = 5598083182705789448L;

	private final Exception _parentException;

	private final int _centralGene;

	ThreadRunException(Exception ex, int gene) {
		super("DFGApp : Execution has failed for gene " + gene + "--> Generation Process aborted. Details:\n\t"
				+ ex.getMessage());
		_parentException = ex;
		_centralGene = gene;
	}

	public Exception getParentException() {
		return _parentException;
	}

	public int getCentralGene() {
		return _centralGene;
	}

}

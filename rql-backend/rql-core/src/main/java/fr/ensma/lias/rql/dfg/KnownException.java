package fr.ensma.lias.rql.dfg;

/**
 * @author Gouriou Benjamin
 */
public class KnownException extends Exception {

	private static final long serialVersionUID = -1052570947087566045L;

	KnownException(String details) {
		super(details);
	}

	KnownException(Exception exception) {
		super(exception);
	}

}

package adventure.security;

import br.gov.frameworkdemoiselle.security.AuthenticationException;

public class UnconfirmedUserException extends AuthenticationException {

	public UnconfirmedUserException() {
		super("Esta conta ainda não foi ativada.");
	}

	private static final long serialVersionUID = 1L;

}

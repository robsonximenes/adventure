package adventure.rest.service;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import adventure.entity.Usuario;
import br.gov.frameworkdemoiselle.security.Credentials;
import br.gov.frameworkdemoiselle.security.LoggedIn;
import br.gov.frameworkdemoiselle.security.SecurityContext;
import br.gov.frameworkdemoiselle.util.Beans;

@Path("/api/login")
@Produces(APPLICATION_JSON)
public class LoginService {

	@Inject
	private SecurityContext securityContext;

	@POST
	public void login(@FormParam("username") String username, @FormParam("password") String password) throws Exception {
		Credentials credentials = Beans.getReference(Credentials.class);
		credentials.setUsername(username);
		credentials.setPassword(password);

		securityContext.login();
	}

	@DELETE
	@LoggedIn
	public void logout() {
		securityContext.logout();
	}

	@GET
	@LoggedIn
	public Usuario getUser() {
		return (Usuario) securityContext.getUser();
	}
}

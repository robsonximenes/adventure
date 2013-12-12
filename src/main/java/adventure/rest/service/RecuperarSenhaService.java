package adventure.rest.service;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.resteasy.spi.validation.ValidateRequest;

import adventure.persistence.UsuarioDAO;
import adventure.persistence.ValidationException;
import br.gov.frameworkdemoiselle.util.Strings;

@ValidateRequest
@Path("/api/recuperar")
@Produces(APPLICATION_JSON)
public class RecuperarSenhaService {

	@Inject
	private UsuarioDAO dao;

	@GET
	@Path("/senha")
	public Response check(@NotEmpty @Email @QueryParam("email") String email) {
		ValidationException validation = new ValidationException();

		if (!Strings.isEmpty(email)) {
			// validation.addViolation("email", "campo obrigatório");
			// } else {
			if (dao.loadByEmail(email) == null) {
				validation.addViolation(null, "e-mail inexistente");
			}
		}

		if (!validation.getConstraintViolations().isEmpty()) {
			throw validation;
		}

		return Response.ok().build();
	}

}
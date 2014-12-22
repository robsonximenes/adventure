package adventure.service;

import static adventure.entity.Gender.MALE;

import java.util.Date;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import adventure.entity.Gender;
import adventure.entity.User;
import adventure.persistence.UserDAO;
import adventure.security.Passwords;
import adventure.validator.UniqueUserEmail;
import br.gov.frameworkdemoiselle.lifecycle.Startup;
import br.gov.frameworkdemoiselle.security.Credentials;
import br.gov.frameworkdemoiselle.security.LoggedIn;
import br.gov.frameworkdemoiselle.security.SecurityContext;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.Beans;
import br.gov.frameworkdemoiselle.util.ValidatePayload;

@Path("signup")
public class SignUpService {

	@Inject
	private UserDAO dao;

	@POST
	@Transactional
	@ValidatePayload
	@Produces("text/plain")
	@Consumes("application/json")
	public Long signUp(SignUpData data) throws Exception {
		User user = new User();
		BeanUtils.copyProperties(user, data);

		String password = user.getPassword();
		user.setPassword(Passwords.hash(password));
		Long result = dao.insert(user).getId();

		login(user.getEmail(), password);

		return result;
	}

	private void login(String email, String password) {
		Credentials credentials = Beans.getReference(Credentials.class);
		credentials.setUsername(email);
		credentials.setPassword(password);

		Beans.getReference(SecurityContext.class).login();
	}

	@DELETE
	@LoggedIn
	@Transactional
	public void quit() {
		SecurityContext securityContext = Beans.getReference(SecurityContext.class);
		User user = (User) securityContext.getUser();
		dao.delete(user.getId());
	}

	@Startup
	@Transactional
	public void cargarTemporariaInicial() {
		if (dao.findAll().isEmpty()) {
			User usuario;

			usuario = new User();
			usuario.setName("Urtzi Iglesias");
			usuario.setEmail("urtzi.iglesias@vidaraid.com");
			usuario.setPassword(Passwords.hash("abcde"));
			usuario.setBirthday(new Date());
			usuario.setGender(MALE);
			dao.insert(usuario);
		}
	}

	public static class SignUpData {

		@NotEmpty
		String name;

		@Email
		@NotEmpty
		@UniqueUserEmail
		String email;

		@NotEmpty
		String password;

		@Past
		@NotNull
		Date birthday;

		@NotNull
		Gender gender;
	}
}

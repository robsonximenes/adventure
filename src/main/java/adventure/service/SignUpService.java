package adventure.service;

import static adventure.entity.Gender.MALE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Date;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.resteasy.spi.validation.ValidateRequest;

import adventure.entity.Gender;
import adventure.entity.JSEntity;
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

@ValidateRequest
@Path("signup")
@Produces(APPLICATION_JSON)
public class SignUpService {

	@Inject
	private UserDAO dao;

	@POST
	// TODO @NotLoggedIn
	@Transactional
	public Long signUp(@NotNull @Valid SignUpForm form) throws Exception {
		User user = new User();
		BeanUtils.copyProperties(user, form);

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
		br.gov.frameworkdemoiselle.security.User user = securityContext.getUser();

		dao.delete((Long) user.getAttribute("id"));
	}

	@Startup
	@Transactional
	public void cargarTemporariaInicial() {
		if (dao.findAll().isEmpty()) {
			User usuario;

			usuario = new User();
			usuario.setFullName("Urtzi Iglesias");
			usuario.setEmail("urtzi.iglesias@vidaraid.com");
			usuario.setPassword(Passwords.hash("abcde"));
			usuario.setBirthday(new Date());
			usuario.setGender(MALE);
			dao.insert(usuario);
		}
	}

	@JSEntity
	public static class SignUpForm {

		@NotEmpty
		private String fullName;

		@Email
		@NotEmpty
		@UniqueUserEmail
		private String email;

		@NotEmpty
		private String password;

		@Past
		@NotNull
		private Date birthday;

		@NotNull
		private Gender gender;

		public String getFullName() {
			return fullName;
		}

		public void setFullName(String fullName) {
			this.fullName = fullName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public Date getBirthday() {
			return birthday;
		}

		public void setBirthday(Date birthday) {
			this.birthday = birthday;
		}

		public Gender getGender() {
			return gender;
		}

		public void setGender(Gender gender) {
			this.gender = gender;
		}
	}
}

package adventure.rest;

import static adventure.entity.GenderType.FEMALE;
import static adventure.entity.GenderType.MALE;
import static adventure.entity.StatusType.PENDENT;
import static adventure.util.Constants.NAME_SIZE;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.NotEmpty;

import adventure.entity.Category;
import adventure.entity.GenderType;
import adventure.entity.Period;
import adventure.entity.Race;
import adventure.entity.RaceCategory;
import adventure.entity.Registration;
import adventure.entity.TeamFormation;
import adventure.entity.User;
import adventure.persistence.MailDAO;
import adventure.persistence.PeriodDAO;
import adventure.persistence.RaceCategoryDAO;
import adventure.persistence.RaceDAO;
import adventure.persistence.RegistrationDAO;
import adventure.persistence.TeamFormationDAO;
import adventure.persistence.UserDAO;
import adventure.rest.LocationREST.CityData;
import adventure.rest.RegistrationREST.RaceData;
import adventure.rest.RegistrationREST.RegistrationData;
import br.gov.frameworkdemoiselle.NotFoundException;
import br.gov.frameworkdemoiselle.UnprocessableEntityException;
import br.gov.frameworkdemoiselle.security.LoggedIn;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.Strings;
import br.gov.frameworkdemoiselle.util.ValidatePayload;

@Path("race/{id}/registration")
public class RaceRegistrationREST {

	@POST
	@LoggedIn
	@Transactional
	@ValidatePayload
	@Produces("text/plain")
	@Consumes("application/json")
	public String submit(RaceRegistrationData data, @PathParam("id") Integer id, @Context UriInfo uriInfo)
			throws Exception {
		loadRace(id);
		Date date = new Date();

		RaceCategory raceCategory = loadRaceCategory(id, data.course, data.category);
		List<User> members = loadMembers(data.members);
		Period period = loadPeriod(raceCategory.getRace(), date);
		validate(raceCategory, members);

		Registration result = submit(data, raceCategory, members, date, period);

		URI baseUri = uriInfo.getBaseUri().resolve("..");
		MailDAO.getInstance().sendRegistrationCreation(result, members, baseUri);
		return result.getFormattedId();
	}

	private Registration submit(RaceRegistrationData data, RaceCategory raceCategory, List<User> members, Date date,
			Period period) {
		Registration result = null;
		Registration registration = new Registration();
		registration.setTeamName(data.teamName);
		registration.setRaceCategory(raceCategory);
		registration.setSubmitter(UserDAO.getInstance().load(User.getLoggedIn().getEmail()));
		registration.setStatus(PENDENT);
		registration.setDate(date);
		registration.setPeriod(period);
		result = RegistrationDAO.getInstance().insert(registration);

		for (User member : members) {
			User atachedMember = UserDAO.getInstance().load(member.getId());
			TeamFormation teamFormation = new TeamFormation(registration, atachedMember);
			TeamFormationDAO.getInstance().insert(teamFormation);
		}

		return result;
	}

	@GET
	@LoggedIn
	@Path("list")
	@Transactional
	@Produces("application/json")
	public List<RegistrationData> find(@PathParam("id") Integer id) throws Exception {
		Race race = loadRace(id);
		List<RegistrationData> result = new ArrayList<RegistrationData>();
		User loggedInUser = User.getLoggedIn();

		for (Registration registration : RegistrationDAO.getInstance().find(race)) {
			RegistrationData data = new RegistrationData();
			data.id = registration.getId();
			data.number = registration.getFormattedId();
			data.teamName = registration.getTeamName();
			data.status = registration.getStatus();
			data.race = new RaceData();
			data.race.id = registration.getRaceCategory().getRace().getId();
			data.race.name = registration.getRaceCategory().getRace().getName();
			data.race.date = registration.getRaceCategory().getRace().getDate();
			data.race.city = new CityData();
			data.race.city.id = registration.getRaceCategory().getRace().getCity().getId();
			data.race.city.name = registration.getRaceCategory().getRace().getCity().getName();
			data.race.city.state = registration.getRaceCategory().getRace().getCity().getState().getAbbreviation();

			result.add(data);
		}

		return result.isEmpty() ? null : result;
	}

	private Race loadRace(Integer id) throws Exception {
		Race result = RaceDAO.getInstance().loadForDetail(id);

		if (result == null) {
			throw new NotFoundException();
		}

		return result;
	}

	private RaceCategory loadRaceCategory(Integer raceId, Integer courseId, Integer categoryId) throws Exception {
		RaceCategory result = RaceCategoryDAO.getInstance().loadForRegistration(raceId, courseId, categoryId);

		if (result == null) {
			throw new UnprocessableEntityException().addViolation("category", "indisponível para esta prova");
		}

		return result;
	}

	private List<User> loadMembers(List<Integer> ids) throws Exception {
		List<User> result = new ArrayList<User>();
		UnprocessableEntityException exception = new UnprocessableEntityException();

		for (Integer id : ids) {
			User user = UserDAO.getInstance().loadBasics(id);

			if (user == null) {
				exception.addViolation("members", "usuário " + id + " inválido");
			} else if (result.contains(user)) {
				exception.addViolation("members", "usuário " + id + "duplicado");
			} else {
				result.add(user);
			}
		}

		if (!exception.getViolations().isEmpty()) {
			throw exception;
		}

		return result;
	}

	private Period loadPeriod(Race race, Date date) throws Exception {
		Period result = PeriodDAO.getInstance().load(race, date);

		if (result == null) {
			throw new UnprocessableEntityException().addViolation("Fora do período de inscrição.");
		}

		return result;
	}

	private void validate(RaceCategory raceCategory, List<User> members) throws Exception {
		int total = members.size();
		UnprocessableEntityException exception = new UnprocessableEntityException();
		Category category = raceCategory.getCategory();

		if (total > category.getTeamSize()) {
			exception.addViolation("members", "tem muita gente");
		} else if (total < category.getTeamSize()) {
			exception.addViolation("members", "equipe incompleta");
		}

		int male = count(members, MALE);
		if (category.getMinMaleMembers() != null && male < category.getMinMaleMembers()) {
			exception.addViolation("members", "falta homem");
		}

		int female = count(members, FEMALE);
		if (category.getMinFemaleMembers() != null && female < category.getMinFemaleMembers()) {
			exception.addViolation("members", "falta mulher");
		}

		for (User member : members) {
			TeamFormation formation = TeamFormationDAO.getInstance().loadForRegistrationSubmissionValidation(
					raceCategory.getRace(), member);

			if (formation != null) {
				exception.addViolation("members", parse(member) + " já faz parte da equipe "
						+ formation.getRegistration().getTeamName());
			}

			if (member.getProfile().getPendencies() > 0 || member.getHealth().getPendencies() > 0) {
				exception.addViolation("members", parse(member) + " possui pendências cadastrais");
			}
		}

		if (!exception.getViolations().isEmpty()) {
			throw exception;
		}
	}

	private String parse(User user) {
		String result = null;

		if (user.equals(User.getLoggedIn())) {
			result = "você";
		} else {
			result = Strings.firstToUpper(user.getName().split(" +")[0].toLowerCase());
		}

		return result;
	}

	private int count(List<User> members, GenderType gender) {
		int result = 0;

		for (User user : members) {
			if (user.getProfile().getGender() == gender) {
				result++;
			}
		}

		return result;
	}

	public static class RaceRegistrationData {

		@NotEmpty
		@Size(max = NAME_SIZE)
		public String teamName;

		@NotNull
		public Integer category;

		@NotNull
		public Integer course;

		@NotEmpty
		public List<Integer> members;
	}
}

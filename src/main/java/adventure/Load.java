package adventure;

import static adventure.entity.Gender.FEMALE;
import static adventure.entity.Gender.MALE;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import adventure.entity.Account;
import adventure.entity.Category;
import adventure.entity.Course;
import adventure.entity.Gender;
import adventure.entity.Health;
import adventure.entity.Period;
import adventure.entity.Profile;
import adventure.entity.Race;
import adventure.entity.RaceCategory;
import adventure.entity.Register;
import adventure.entity.TeamFormation;
import adventure.security.Passwords;
import br.gov.frameworkdemoiselle.lifecycle.Startup;
import br.gov.frameworkdemoiselle.transaction.Transactional;

@Transactional
public class Load {

	private static final DateFormat format = new SimpleDateFormat("dd/MM/yyyyy");

	@Inject
	private EntityManager em;

	private TeamFormation newTeamFormation(Register register, Account account, boolean confirmed) throws Exception {
		TeamFormation teamFormation = new TeamFormation(register, account);
		teamFormation.setConfirmed(confirmed);
		em.persist(teamFormation);
		return teamFormation;
	}

	private Register newRegister(String teamName, RaceCategory raceCategory, Account[] members) throws Exception {
		Register register = new Register();
		register.setTeamName(teamName);
		register.setRaceCategory(raceCategory);
		em.persist(register);

		for (int i = 0; i < members.length; i++) {
			newTeamFormation(register, members[i], i % 2 == 0);
		}

		return register;
	}

	private Race newRace(String name, String date) throws Exception {
		Race race = new Race();
		race.setName(name);
		race.setDate(format.parse(date));
		em.persist(race);
		return race;
	}

	private Category newCategory(String name, String description, Integer teamSize, Integer minMaleMembers,
			Integer minFemaleMembers) {
		Category category = new Category();
		category.setName(name);
		category.setDescription(description);
		category.setTeamSize(teamSize);
		category.setMinMaleMembers(minMaleMembers);
		category.setMinFemaleMembers(minFemaleMembers);
		em.persist(category);
		return category;
	}

	private Period newPeriod(Race race, String begining, String end, Double price) throws Exception {
		Period period = new Period(race);
		period.setBeginning(format.parse(begining));
		period.setEnd(format.parse(end));
		period.setPrice(BigDecimal.valueOf(price));
		em.persist(period);
		return period;
	}

	private Course newCourse(Race race, Integer length) {
		Course course = new Course(race);
		course.setLength(length);
		em.persist(course);
		return course;
	}

	private RaceCategory newRaceCategory(Race race, Course course, Category category) {
		RaceCategory availableCategory = new RaceCategory(race, course, category);
		em.persist(availableCategory);
		return availableCategory;
	}

	private Account newAccount(String username, String password, String name, Gender gender, boolean verified) {
		Account account = new Account();
		account.setEmail(username);
		account.setPassword(Passwords.hash(password, username));
		account.setCreation(new Date());
		account.setConfirmation(verified ? new Date() : null);
		em.persist(account);

		Profile profile = new Profile(account);
		profile.setName(name);
		profile.setGender(gender);
		em.persist(profile);

		Health health = new Health(account);
		em.persist(health);

		return account;
	}

	@Startup
	@SuppressWarnings("unused")
	public void perform() throws Exception {
		em.createQuery("delete from Register").executeUpdate();
		em.createQuery("delete from TeamFormation").executeUpdate();
		em.createQuery("delete from RaceCategory").executeUpdate();
		em.createQuery("delete from Category").executeUpdate();
		em.createQuery("delete from Course").executeUpdate();
		em.createQuery("delete from Period").executeUpdate();
		em.createQuery("delete from Race").executeUpdate();
		em.createQuery("delete from Health").executeUpdate();
		em.createQuery("delete from Profile").executeUpdate();
		em.createQuery("delete from Account").executeUpdate();

		Account[] accounts = new Account[50];
		for (int i = 0; i < accounts.length; i++) {
			String email = "guest_" + i + "@guest.com";
			String password = "guest";
			String name;
			Gender gender;
			boolean verified = true;

			if (i % 3 == 0) {
				name = "Male Guest " + i;
				gender = MALE;
			} else {
				name = "Female Guest " + i;
				gender = FEMALE;
			}

			if (i % 2 == 1) {
				verified = false;
			}

			accounts[i] = newAccount(email, password, name, gender, verified);
		}

		// newAccount("cleverson.sacramento@gmail.com", "123", "Cleverson Saramento", MALE, false);
		 newAccount("cleverson.sacramento@gmail.com", "123", "Cleverson Saramento", MALE, true);
		// newAccount("cleverson.sacramento@gmail.com", null, "Cleverson Saramento", MALE, false);

		Category quarteto = newCategory("Quarteto", "Quarteto contendo pelo menos uma mulher", 4, 1, 1);
		Category duplaMasc = newCategory("Dupla masculina", "Dupla composta apenas por homens", 2, 2, null);
		Category duplaFem = newCategory("Dupla feminina", "Dupla composta apenas por mulheres", 2, null, 2);
		Category duplaMista = newCategory("Dupla mista", "Dupla composta por um homem e uma mulher", 2, 1, 1);
		Category trioMasc = newCategory("Trio masculino", "Trio composto apenas por homens", 3, 3, null);
		Category trioMisto = newCategory("Trio misto", "Trio contendo pelo menos uma mulher", 3, 1, 1);
		Category solo = newCategory("Solo", "Único integrante independente do sexo", 1, null, null);

		newRace("Desafio dos Sertões", "16/08/2014");
		newRace("Laskpé", "08/11/2014");

		Race np = newRace("Noite do Perrengue 3", "21/03/2015");
		newPeriod(np, "07/01/2015", "15/01/2015", 60.00);
		newPeriod(np, "16/01/2015", "31/01/2015", 80.00);
		Course np40km = newCourse(np, 40);
		Course np100km = newCourse(np, 100);
		newRaceCategory(np, np40km, duplaMasc);
		newRaceCategory(np, np40km, duplaFem);
		newRaceCategory(np, np40km, duplaMista);
		newRaceCategory(np, np100km, duplaMasc);
		newRaceCategory(np, np100km, duplaMista);
		RaceCategory npQuarteto100km = newRaceCategory(np, np100km, quarteto);

		newRace("CARI - Sol do Salitre", "11/04/2015");
		newRace("CICA - Mandacaru", "18/04/2015");
		newRace("CICA - Peleja", "13/06/2015");
		newRace("CARI - Casco de Peba", "20/06/2015");
		newRace("Corrida do CT", "07/07/2015");
		newRace("CARI - Laskpé", "15/08/2015");
		newRace("CICA - Cangaço", "30/08/2015");
		newRace("CARI - Desafio dos Sertões", "10/10/2015");
		newRace("CARI - Integração", "05/12/2015");

		newRegister("Quarteto Exemplo", npQuarteto100km, new Account[] { accounts[0], accounts[2], accounts[6],
				accounts[12] });
	}
}

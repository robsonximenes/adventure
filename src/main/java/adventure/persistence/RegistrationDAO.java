package adventure.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import adventure.entity.Race;
import adventure.entity.Registration;
import adventure.entity.TeamFormation;
import adventure.entity.User;
import br.gov.frameworkdemoiselle.template.JPACrud;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.Beans;

@Transactional
public class RegistrationDAO extends JPACrud<Registration, Long> {

	private static final long serialVersionUID = 1L;

	public static RegistrationDAO getInstance() {
		return Beans.getReference(RegistrationDAO.class);
	}

	public Registration loadForDetails(Long id) {
		StringBuffer jpql = new StringBuffer();
		jpql.append(" select ");
		jpql.append("    new Registration( ");
		jpql.append("        re.id, ");
		jpql.append("        re.date, ");
		jpql.append("        re.teamName, ");
		jpql.append("        re.paymentTransaction, ");
		jpql.append("        su.id, ");
		jpql.append("        su.email, ");
		jpql.append("        pr.name, ");
		jpql.append("        re.status, ");
		jpql.append("        ra.id, ");
		jpql.append("        ra.name, ");
		jpql.append("        ra.date, ");
		jpql.append("        ra.paymentAccount, ");
		jpql.append("        ra.paymentToken, ");
		jpql.append("        pe.id, ");
		jpql.append("        pe.price, ");
		jpql.append("        ci.id, ");
		jpql.append("        ci.name, ");
		jpql.append("        st.id, ");
		jpql.append("        st.name, ");
		jpql.append("        st.abbreviation, ");
		jpql.append("        ca.id, ");
		jpql.append("        ca.name, ");
		jpql.append("        co.id, ");
		jpql.append("        co.length ");
		jpql.append("        ) ");
		jpql.append("   from Registration re ");
		jpql.append("   join re.submitter su ");
		jpql.append("   join re.raceCategory rc ");
		jpql.append("   join rc.race ra ");
		jpql.append("   join rc.course co ");
		jpql.append("   join rc.category ca ");
		jpql.append("   join re.period pe ");
		jpql.append("   left join ra.city ci ");
		jpql.append("   left join ci.state st, ");
		jpql.append("        Profile pr ");
		jpql.append("  where su.id = pr.id ");
		jpql.append("    and re.id = :id");

		TypedQuery<Registration> query = getEntityManager().createQuery(jpql.toString(), Registration.class);
		query.setParameter("id", id);

		Registration result;
		try {
			result = query.getSingleResult();
		} catch (NoResultException cause) {
			result = null;
		}
		return result;
	}

	public List<Registration> findToOrganizer(Race race) {
		StringBuffer jpql = new StringBuffer();
		jpql.append(" select ");
		jpql.append(" 	 new TeamFormation( ");
		jpql.append(" 	     u.id, ");
		jpql.append(" 	     u.email, ");
		jpql.append(" 	     p.name, ");
		jpql.append(" 	     p.mobile, ");
		jpql.append(" 	     tf.racePrice, ");
		jpql.append(" 	     tf.annualFee, ");
		jpql.append(" 	     re.id, ");
		jpql.append(" 	     re.status, ");
		jpql.append(" 	     re.teamName, ");
		jpql.append(" 	     re.date, ");
		jpql.append(" 	     ra.id, ");
		jpql.append(" 	     ca.id, ");
		jpql.append(" 	     ca.name, ");
		jpql.append(" 	     co.id, ");
		jpql.append(" 	     co.length ");
		jpql.append(" 	     ) ");
		jpql.append("   from TeamFormation tf ");
		jpql.append("   join tf.user u ");
		jpql.append("   join tf.registration re ");
		jpql.append("   join re.raceCategory rc ");
		jpql.append("   join rc.race ra ");
		jpql.append("   join rc.category ca ");
		jpql.append("   join rc.course co, ");
		jpql.append("        Profile p ");
		jpql.append("  where u = p.user ");
		jpql.append("    and ra = :race ");
		jpql.append("  order by ");
		jpql.append("        re.status, ");
		jpql.append("        re.date, ");
		jpql.append("        ra.id ");

		TypedQuery<TeamFormation> query = getEntityManager().createQuery(jpql.toString(), TeamFormation.class);
		query.setParameter("race", race);

		Registration registration = null;
		List<Registration> result = new ArrayList<Registration>();
		for (TeamFormation teamFormation : query.getResultList()) {
			if (!teamFormation.getRegistration().equals(registration)) {
				registration = teamFormation.getRegistration();
				registration.setTeamFormations(new ArrayList<TeamFormation>());
				result.add(registration);
			}

			teamFormation.setRegistration(null);
			registration.getTeamFormations().add(teamFormation);
		}

		return result;
	}

	public List<Registration> find(User loggedInUser) {
		StringBuffer jpql = new StringBuffer();
		jpql.append(" select  ");
		jpql.append("    new Registration( ");
		jpql.append("        re.id, ");
		jpql.append("        re.status, ");
		jpql.append("        re.teamName, ");
		jpql.append("        ra.id, ");
		jpql.append("        ra.name, ");
		jpql.append("        ra.date, ");
		jpql.append("        ci.id, ");
		jpql.append("        ci.name, ");
		jpql.append("        st.abbreviation ");
		jpql.append("    ) ");
		jpql.append("   from TeamFormation tf ");
		jpql.append("   join tf.registration re ");
		jpql.append("   join re.raceCategory rc ");
		jpql.append("   join rc.race ra ");
		jpql.append("   left join ra.city ci ");
		jpql.append("   left join ci.state st ");
		jpql.append("  where tf.user = :user ");
		jpql.append("  order by ");
		jpql.append("        re.date ");

		TypedQuery<Registration> query = getEntityManager().createQuery(jpql.toString(), Registration.class);
		query.setParameter("user", loggedInUser);

		return query.getResultList();
	}

	public List<Registration> findToUser(Race race) {
		StringBuffer jpql = new StringBuffer();
		jpql.append(" select  ");
		jpql.append("    new Registration( ");
		jpql.append("        re.id, ");
		jpql.append("        re.status, ");
		jpql.append("        re.teamName, ");
		jpql.append("        ra.id, ");
		jpql.append("        ra.name, ");
		jpql.append("        ra.date, ");
		jpql.append("        ci.id, ");
		jpql.append("        ci.name, ");
		jpql.append("        st.abbreviation ");
		jpql.append("    ) ");
		jpql.append("   from Registration re ");
		jpql.append("   join re.raceCategory rc ");
		jpql.append("   join rc.race ra ");
		jpql.append("   left join ra.city ci ");
		jpql.append("   left join ci.state st ");
		jpql.append("  where ra = :race ");
		jpql.append("  order by ");
		jpql.append("        re.date ");

		TypedQuery<Registration> query = getEntityManager().createQuery(jpql.toString(), Registration.class);
		query.setParameter("race", race);

		return query.getResultList();
	}
}

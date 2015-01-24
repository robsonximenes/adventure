package adventure.persistence;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import adventure.entity.Registration;
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
		jpql.append("        su.id, ");
		jpql.append("        su.email, ");
		jpql.append("        pr.name, ");
		jpql.append("        re.status, ");
		jpql.append("        ra.id, ");
		jpql.append("        ra.name, ");
		jpql.append("        ra.date, ");
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
}

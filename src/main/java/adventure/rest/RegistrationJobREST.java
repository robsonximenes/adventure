package adventure.rest;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import adventure.entity.Period;
import adventure.entity.Registration;
import adventure.entity.TeamFormation;
import adventure.persistence.MailDAO;
import adventure.persistence.PeriodDAO;
import adventure.persistence.RegistrationDAO;
import adventure.persistence.TeamFormationDAO;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.Beans;
import br.gov.frameworkdemoiselle.util.NameQualifier;

@Path("registration")
public class RegistrationJobREST {

	private transient Logger logger;

	@POST
	@Transactional
	@Path("period/update")
	public void updatePeriod(@Context UriInfo uriInfo) throws Exception {
		getLogger().info("Iniciando a busca por inscrições expiradas...");

		RegistrationDAO registrationDAO = RegistrationDAO.getInstance();
		List<Registration> expired = registrationDAO.findForUpdatePeriod();
		TeamFormationDAO teamFormationDAO = TeamFormationDAO.getInstance();
		PeriodDAO periodDAO = PeriodDAO.getInstance();
		MailDAO mailDAO = MailDAO.getInstance();

		for (Registration registration : expired) {
			Period period = periodDAO.loadCurrent(registration.getRaceCategory().getRace());

			for (TeamFormation teamFormation : teamFormationDAO.find(registration)) {
				TeamFormation persisted = teamFormationDAO.load(registration, teamFormation.getUser());
				persisted.setRacePrice(period.getPrice());
				teamFormationDAO.update(persisted);
			}

			Registration persisted = registrationDAO.load(registration.getId());
			persisted.setPeriod(period);
			persisted.setPaymentCode(null);
			registrationDAO.update(persisted);

			getLogger().info("Os valores da inscrição #" + registration.getFormattedId() + " foram atualizados.");

			URI baseUri = uriInfo.getBaseUri().resolve("..");
			mailDAO.sendRegistrationPeriodChanging(registration, period.getBeginning(), period.getEnd(), baseUri);
		}
	}

	private Logger getLogger() {
		if (this.logger == null) {
			this.logger = Beans.getReference(Logger.class, new NameQualifier(RegistrationJobREST.class.getName()));
		}

		return this.logger;
	}
}

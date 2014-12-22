package adventure.persistence;

import adventure.entity.Event;
import br.gov.frameworkdemoiselle.template.JPACrud;
import br.gov.frameworkdemoiselle.transaction.Transactional;

@Transactional
public class EventDAO extends JPACrud<Event, Long> {

	private static final long serialVersionUID = 1L;
}

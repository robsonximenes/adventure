package adventure.service;

import static java.lang.Integer.MAX_VALUE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.validation.Valid;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.spi.validation.ValidateRequest;

import adventure.entity.Atleta;
import adventure.entity.Telefone;
import br.gov.frameworkdemoiselle.lifecycle.Startup;
import br.gov.frameworkdemoiselle.util.Strings;

@Path("/atleta")
@Produces(APPLICATION_JSON)
@ValidateRequest
public class AtletaService {

	private static Map<Long, Atleta> database = Collections.synchronizedMap(new HashMap<Long, Atleta>());

	@POST
	@ValidateRequest
	public Long create(@Valid Atleta atleta) {
		Random generator = new Random();
		Long id = Long.valueOf(generator.nextInt(MAX_VALUE));

		atleta.setId(id);
		database.put(id, atleta);

		return id;
	}

	// @PUT
	// @Path("/{id}")
	// public void update(Atleta atleta) {
	// database.put(atleta.getId(), atleta);
	// }

	@DELETE
	@Path("/{id}")
	public void delete(@PathParam("id") Long id) {
		database.remove(id);
	}

	// @GET
	// @Path("/{id}")
	// public Atleta load(@PathParam("id") Long id) {
	// return database.get(id);
	// }

	@GET
	public List<Atleta> search(@QueryParam("email") String email) {
		List<Atleta> result = new ArrayList<Atleta>();

		if (Strings.isEmpty(email)) {
			result.addAll(database.values());

		} else {
			for (Atleta atleta : database.values()) {
				if (atleta.getEmail().equals(email)) {
					result.add(atleta);
				}
			}
		}

		return result;
	}

	@Startup
	public void cargarTemporariaInicial() {
		Atleta atleta;

		atleta = new Atleta();
		atleta.setNome("Urtzi Iglesias");
		atleta.setEmail("urtzi.iglesias@vidaraid.com");

		Calendar calendar = Calendar.getInstance();
		calendar.set(1980, 01, 01);
		atleta.setNascimento(calendar.getTime());

		atleta.setRg(null);
		atleta.setCpf(null);
		atleta.setTelefoneCelular(new Telefone("61", "1234-4567", null));
		atleta.setTelefoneResidencial(new Telefone("61", "1234-4567", null));

		create(atleta);
	}
}

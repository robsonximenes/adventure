//package adventure;
//
//import static adventure.entity.Sexo.MASCULINO;
//import static javax.servlet.http.HttpServletResponse.SC_PRECONDITION_FAILED;
//import static junit.framework.Assert.assertEquals;
//import static org.junit.Assert.assertNull;
//import static org.junit.Assert.fail;
//
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.List;
//
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.junit.Arquillian;
//import org.jboss.arquillian.test.api.ArquillianResource;
//import org.jboss.resteasy.client.ClientResponseFailure;
//import org.jboss.resteasy.client.ProxyFactory;
//import org.jboss.resteasy.util.GenericType;
//import org.jboss.shrinkwrap.api.spec.WebArchive;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import test.Tests;
//import adventure.entity.Atleta;
//import adventure.persistence.ValidationException.Violation;
//
//@RunWith(Arquillian.class)
//public class AtletaServiceTest {
//
//	@ArquillianResource
//	private URL url;
//
//	@Deployment(testable = false)
//	public static WebArchive createDeployment() {
//		return Tests.createDeployment();
//	}
//
//	private AtletaServiceClient createClient() {
//		return ProxyFactory.create(AtletaServiceClient.class, url.toString());
//	}
//
//	@Test
//	public void crudBemSucedidoApenasComCamposObrigatorios() {
//		Atleta atletaLocal = new Atleta();
//		atletaLocal.setNome("Cleverson Sacramento");
//		atletaLocal.setEmail("cleverson.sacramento@gmail.com");
//		atletaLocal.setSexo(MASCULINO);
//		atletaLocal.setTelefoneCelular("71 8888-9999");
//		atletaLocal.setNascimento(new Date());
//		atletaLocal.setCpf("123456789-00");
//		atletaLocal.setRg("11223344-55");
//
//		Atleta atletaRemote;
//		atletaRemote = obterPorEmail(atletaLocal.getEmail());
//
//		if (atletaRemote != null) {
//			fail("O atleta já está cadastrado e não deveria");
//		}
//
//		AtletaServiceClient client = createClient();
//		client.criar(atletaLocal);
//
//		atletaRemote = obterPorEmail(atletaLocal.getEmail());
//		assertEquals(atletaLocal.getEmail(), atletaRemote.getEmail());
//
//		client.excluir(atletaRemote.getId());
//		atletaRemote = obterPorEmail(atletaLocal.getEmail());
//		assertNull(atletaRemote);
//	}
//
//	@Test
//	public void falhaAoTentarInserirComCamposObrigatoriosNulos() {
//		AtletaServiceClient client = createClient();
//
//		Atleta atleta = new Atleta();
//
//		try {
//			client.criar(atleta);
//			fail("Deveria ter ocorrido erro ao tentar inserir");
//
//		} catch (ClientResponseFailure cause) {
//			assertEquals(SC_PRECONDITION_FAILED, cause.getResponse().getStatus());
//
//			@SuppressWarnings("unchecked")
//			List<Violation> validations = (List<Violation>) cause.getResponse().getEntity(
//					new GenericType<List<Violation>>() {
//					});
//
//			List<Violation> expected = new ArrayList<Violation>();
//			expected.add(new Violation("nome", "Não pode ser vazio."));
//			expected.add(new Violation("email", "Não pode ser vazio."));
//			expected.add(new Violation("cpf", "Não pode ser vazio."));
//			expected.add(new Violation("rg", "Não pode ser vazio."));
//			expected.add(new Violation("nascimento", "Não pode ser nulo."));
//			expected.add(new Violation("sexo", "Não pode ser nulo."));
//			expected.add(new Violation("telefoneCelular", "Não pode ser vazio."));
//
//			assertEquals(new HashSet<Violation>(expected), new HashSet<Violation>(validations));
//		}
//	}
//
//	private Atleta obterPorEmail(String email) {
//		Atleta result = null;
//		AtletaServiceClient client = createClient();
//
//		for (Atleta aux : client.obterTodos()) {
//			if (email.equals(aux.getEmail())) {
//				if (result != null) {
//					throw new IllegalStateException("E-mail duplicado: " + email);
//				} else {
//					result = aux;
//				}
//			}
//		}
//
//		return result;
//	}
//}

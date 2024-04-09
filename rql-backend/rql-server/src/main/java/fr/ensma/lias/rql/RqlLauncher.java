package fr.ensma.lias.rql;

import java.net.URI;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import fr.ensma.lias.rql.cfg.IConfiguration;
import fr.ensma.lias.rql.service.AuthenticationResourceImpl;
import fr.ensma.lias.rql.service.DataBaseImportImp;
import fr.ensma.lias.rql.service.ProjectResourceImp;
import fr.ensma.lias.rql.service.QueryConstructionImp;
import fr.ensma.lias.rql.service.QueryResourceImp;
import fr.ensma.lias.rql.service.SchemaResourceImp;
import fr.ensma.lias.rql.service.UserResourceImp;
import fr.ensma.lias.rql.service.XlsxResourceImp;

/**
 * @author Bilal REZKELLAH
 */
public class RqlLauncher {

	private static URI getBaseURI(Integer value) {
		return UriBuilder.fromUri("http://0.0.0.0/").port(value).build();
	}

	public static void main(String[] args) {
		Logger l = Logger.getLogger("org.glassfish.grizzly.http.server.HttpHandler");
		l.setLevel(Level.FINE);
		l.setUseParentHandlers(false);
		ConsoleHandler ch = new ConsoleHandler();
		ch.setLevel(Level.ALL);
		l.addHandler(ch);

		ResourceConfig rc = new ResourceConfig();
		rc.registerClasses(UserResourceImp.class, ProjectResourceImp.class, AuthenticationResourceImpl.class,
				MultiPartFeature.class, DataBaseImportImp.class, QueryResourceImp.class, SchemaResourceImp.class,
				XlsxResourceImp.class, QueryConstructionImp.class, CrossDomainFilter.class);
		rc.property("jersey.config.server.wadl.disableWadl", "false");

		Weld weld = new Weld();
		final WeldContainer initialize = weld.initialize();
		final IConfiguration refRQLConfiguration = initialize.instance().select(IConfiguration.class).get();
		try {
			HttpServer server = GrizzlyHttpServerFactory.createHttpServer(
					getBaseURI(Integer.parseInt(refRQLConfiguration.getConfiguration().rqlServerPort())), rc);
			server.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

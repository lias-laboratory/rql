package fr.ensma.lias.rql.service;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response.Status;

import fr.ensma.lias.rql.AuthToken;
import fr.ensma.lias.rql.RqlConstant;
import fr.ensma.lias.rql.RqlUtil;
import fr.ensma.lias.rql.api.TokenAuthenticated;
import fr.ensma.lias.rql.cfg.IConfiguration;
import fr.ensma.lias.rql.cfg.RqlConfig;

/**
 * @author Bilal REZKELLAH
 */
@TokenAuthenticated
public class BearerTokenFilter implements ContainerRequestFilter {

	@Inject
	IConfiguration refConfiguration;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String token = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		final RqlConfig configuration = refConfiguration.getConfiguration();

		final AuthToken decryptToken = RqlUtil.decryptToken(token, configuration.encryptPassword(),
				configuration.encryptNoise());

		if (decryptToken != null) {
			final Date startDate = decryptToken.getStartDate();

			if (startDate != null) {
				final long diff = new Date().getTime() - startDate.getTime();
				if ((diff / 1000) < getSessionTimeOut(configuration.sessionTimeout())) {
					if (!(decryptToken.getIdentifiant().equals(configuration.identificationUser()))) {
						throw new WebApplicationException("Token is wrong.", Status.UNAUTHORIZED);
					}
				} else {
					throw new WebApplicationException("Session timeout.", Status.UNAUTHORIZED);
				}
			} else {
				throw new WebApplicationException("Token is wrong.", Status.UNAUTHORIZED);
			}
		} else {
			throw new WebApplicationException("User is not unauthorized.", Status.UNAUTHORIZED);
		}
	}

	public static int getSessionTimeOut(String configurationValue) {
		try {
			return 60 * Integer.parseInt(configurationValue);
		} catch (NumberFormatException e) {
			return RqlConstant.SESSION_TIMEOUT_DEFAULT;
		}
	}
}

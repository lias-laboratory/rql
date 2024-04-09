package fr.ensma.lias.rql.cfg;

import javax.inject.Singleton;

import org.aeonbits.owner.ConfigFactory;

/**
 * @author Bilal REZKELLAH
 */
@Singleton
public class Configuration implements IConfiguration {

	protected RqlConfig config;

	public Configuration() {
		config = ConfigFactory.create(RqlConfig.class);
	}

	@Override
	public RqlConfig getConfiguration() {
		return config;
	}
}

package fr.ensma.lias.rql.cfg;

import org.aeonbits.owner.Config;

/**
 * @author Bilal REZKELLAH
 */
public interface RqlConfig extends Config {

	@DefaultValue("postgres")
	String rqlDbName();

	@DefaultValue("db")
	String rqlDbHost();

	@DefaultValue("business_db")
	String rqlDbHostMetier();

	@DefaultValue("5432")
	Integer rqlDbPort();

	@DefaultValue("postgresql")
	String rqlDbType();

	@DefaultValue("postgres")
	String rqlDbAdminLogin();

	@DefaultValue("postgres")
	String rqlDbAdminPwd();

	@Key("rql.identification.user")
	@DefaultValue("admin")
	String identificationUser();

	@Key("rql.identification.password")
	@DefaultValue("adminadmin")
	String identificationPassword();

	@Key("rql.encrypt.password")
	@DefaultValue("!thisismypassword!")
	String encryptPassword();

	@Key("rql.encrypt.noise")
	@DefaultValue("@BCDEFGHIJKLMNPQRSTUVWXZ&bcdefghijklmnopqrstuvwxyz0123456789")
	String encryptNoise();

	@Key("rql.session.timeout")
	@DefaultValue("60")
	String sessionTimeout();

	@DefaultValue("../shd31/shd")
	String pathToShd();

	@DefaultValue("./target/tmpFiles")
	String tmpResultsDir();

	@Key("rql.server.port")
	@DefaultValue("9992")
	String rqlServerPort();

}

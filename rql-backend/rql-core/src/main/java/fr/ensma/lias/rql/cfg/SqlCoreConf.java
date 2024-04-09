package fr.ensma.lias.rql.cfg;

/**
 * @author Bilal REZKELLAH
 */
public class SqlCoreConf extends CoreConfig {

	public SqlCoreConf() {
		super();
	}

	public SqlCoreConf(String rqlDbName, String rqlDbHost, Integer rqlDbPort, String rqlDbType, String rqlDbAdminLogin,
			String rqlDbAdminPwd) {
		super(rqlDbName, rqlDbHost, rqlDbPort, rqlDbType, rqlDbAdminLogin, rqlDbAdminPwd);

	}
}

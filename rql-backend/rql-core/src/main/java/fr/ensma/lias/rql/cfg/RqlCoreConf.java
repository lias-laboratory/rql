package fr.ensma.lias.rql.cfg;

/**
 * @author Bilal REZKELLAH
 */
public class RqlCoreConf extends CoreConfig {

	private String pathToShd;
	private String tmpAgFile;

	public RqlCoreConf(String rqlDbName, String rqlDbHost, Integer rqlDbPort, String rqlDbType, String rqlDbAdminLogin,
			String rqlDbAdminPwd, String pathToShd, String tmpAgFile) {
		super(rqlDbName, rqlDbHost, rqlDbPort, rqlDbType, rqlDbAdminLogin, rqlDbAdminPwd);
		this.pathToShd = pathToShd;
		this.tmpAgFile = tmpAgFile;
	}

	public RqlCoreConf() {
		super();
	};

	public String getPathToShd() {
		return pathToShd;
	}

	public void setPathToShd(String pathToShd) {
		this.pathToShd = pathToShd;
	}

	public String getTmpAgFile() {
		return tmpAgFile;
	}

	public void setTmpAgFile(String tmpAgFile) {
		this.tmpAgFile = tmpAgFile;
	}
}

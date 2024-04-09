package fr.ensma.lias.rql.cfg;

/**
 * @author Bilal REZKELLAH
 */
public class CoreConfig {

	private String rqlDbName;

	private String rqlDbHost;

	private String rqlDbType;

	private Integer rqlDbPort;

	private String rqlDbAdminLogin;

	private String rqlDbAdminPwd;

	public String getRqlDbName() {
		return rqlDbName;
	}

	public void setRqlDbName(String rqlDbName) {
		this.rqlDbName = rqlDbName;
	}

	public CoreConfig() {
	}

	public CoreConfig(String rqlDbName, String rqlDbHost, Integer rqlDbPort, String rqlDbType, String rqlDbAdminLogin,
			String rqlDbAdminPwd) {
		this.rqlDbName = rqlDbName;
		this.rqlDbHost = rqlDbHost;
		this.rqlDbType = rqlDbType;
		this.rqlDbPort = rqlDbPort;
		this.rqlDbAdminLogin = rqlDbAdminLogin;
		this.rqlDbAdminPwd = rqlDbAdminPwd;
	}

	public String getRqlDbHost() {
		return rqlDbHost;
	}

	public void setRqlDbHost(String rqlDbHost) {
		this.rqlDbHost = rqlDbHost;
	}

	public String getRqlDbType() {
		return rqlDbType;
	}

	public void setRqlDbType(String rqlDbType) {
		this.rqlDbType = rqlDbType;
	}

	public Integer getRqlDbPort() {
		return rqlDbPort;
	}

	public void setRqlDbPort(Integer rqlDbPort) {
		this.rqlDbPort = rqlDbPort;
	}

	public String getRqlDbAdminLogin() {
		return rqlDbAdminLogin;
	}

	public void setRqlDbAdminLogin(String rqlDbAdminLogin) {
		this.rqlDbAdminLogin = rqlDbAdminLogin;
	}

	public String getRqlDbAdminPwd() {
		return rqlDbAdminPwd;
	}

	public void setRqlDbAdminPwd(String rqlDbAdminPwd) {
		this.rqlDbAdminPwd = rqlDbAdminPwd;
	}
}

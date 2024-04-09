package fr.ensma.lias.rql.model;

/**
 * @author Bilal REZKELLAH
 */
public class DataBaseConfig {

	private String projectid;

	private String dataBaseName;

	private String dataBaseHost;

	private Integer dataBasePort;

	private String dataBaseUser;

	private String dataBasePassword;

	private String dataBaseType;

	public String getDataBaseName() {
		return dataBaseName;
	}

	public void setDataBaseName(String dataBaseName) {
		this.dataBaseName = dataBaseName;
	}

	public DataBaseConfig(String projectid, String dataBaseName, String dataBaseHost, Integer dataBasePort,
			String dataBaseUser, String dataBasePassword, String dataBaseType) {
		super();
		this.projectid = projectid;
		this.dataBaseName = dataBaseName;
		this.dataBaseHost = dataBaseHost;
		this.dataBasePort = dataBasePort;
		this.dataBaseUser = dataBaseUser;
		this.dataBasePassword = dataBasePassword;
		this.dataBaseType = dataBaseType;
	}

	public DataBaseConfig() {
	}

	public String getDataBaseHost() {
		return dataBaseHost;
	}

	public void setDataBaseHost(String dataBaseHost) {
		this.dataBaseHost = dataBaseHost;
	}

	public Integer getDataBasePort() {
		return dataBasePort;
	}

	public void setDataBasePort(Integer dataBasePort) {
		this.dataBasePort = dataBasePort;
	}

	public String getDataBaseUser() {
		return dataBaseUser;
	}

	public void setDataBaseUser(String dataBaseUser) {
		this.dataBaseUser = dataBaseUser;
	}

	public String getDataBasePassword() {
		return dataBasePassword;
	}

	public void setDataBasePassword(String dataBasePassword) {
		this.dataBasePassword = dataBasePassword;
	}

	public String getDataBaseType() {
		return dataBaseType;
	}

	public void setDataBaseType(String dataBaseType) {
		this.dataBaseType = dataBaseType;
	}

	public String getProjectid() {
		return projectid;
	}

	public void setProjectid(String projectid) {
		this.projectid = projectid;
	}

}

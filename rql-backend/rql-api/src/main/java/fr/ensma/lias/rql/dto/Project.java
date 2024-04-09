package fr.ensma.lias.rql.dto;

import java.util.List;

/**
 * @author Bilal REZKELLAH
 */
public class Project {

	private String id;
	private String name;
	private String description;
	private String userid;
	private String creationDate;
	private String dbType;
	private List<User> collaborators;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public Project(String id, String name, String userid, String string) {
		super();
		this.id = id;
		this.name = name;
		this.userid = userid;
		this.creationDate = string;
	}

	public Project() {
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public List<User> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(List<User> collaborators) {
		this.collaborators = collaborators;
	}

}

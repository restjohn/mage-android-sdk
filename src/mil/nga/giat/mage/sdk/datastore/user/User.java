package mil.nga.giat.mage.sdk.datastore.user;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User {

	@DatabaseField(generatedId = true)
	private Long pk_id;

	@DatabaseField
	private String email;

	@DatabaseField
	private String firstname;

	@DatabaseField
	private String lastname;

	@DatabaseField(canBeNull = false)
	private String username;

	@DatabaseField(canBeNull = false)
	private Boolean isCurrentUser = Boolean.FALSE;
	
	// TODO : add last modified

	@DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true)
	private Role role;

	public User() {
		// ORMLite needs a no-arg constructor
	}

	public User(String email, String firstname, String lastname, String username, Role role) {
		super();
		this.email = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.username = username;
		this.role = role;
	}

	public Long getPk_id() {
		return pk_id;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}
	
	public String getUsername() {
		return username;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Boolean isCurrentUser() {
		return isCurrentUser;
	}

	public void setCurrentUser(Boolean isCurrentUser) {
		this.isCurrentUser = isCurrentUser;
	}

}
package thuonghth.users;

/**
 * @author thuonghth
 */

public class AccountDTO {
	private String email, password;
	private int statusId;

	public AccountDTO(String email, String password) {
		this.email = email;
		this.password = password;

	}

	public AccountDTO(String email, String password, int statusId) {
		this.email = email;
		this.password = password;
		this.statusId = statusId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHashedPassword() {
		return password;
	}

	public void setHashedPassword(String password) {
		this.password = password;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

}

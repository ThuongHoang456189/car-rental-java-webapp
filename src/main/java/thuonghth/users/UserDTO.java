package thuonghth.users;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Date;

/**
 * @author thuonghth
 */
public class UserDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1088529517428859380L;

	private String name, phone, address;
	private Date createDate;
	private int roleID, statusID;
	private AccountDTO account;
	public UserDTO() {
	}

	public UserDTO(String email, String hashedPassword, String name, String phone, String address,
			int roleID, int statusID) {
		this.account = new AccountDTO(email, hashedPassword);
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.createDate = null;
		this.roleID = roleID;
		this.statusID = statusID;
	}
	
	public UserDTO(String email, String hashedPassword, String name, String phone, String address, Date createDate,
			int roleID, int statusID) {
		this.account = new AccountDTO(email, hashedPassword);
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.createDate = createDate;
		this.roleID = roleID;
		this.statusID = statusID;
	}
	
	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public int getRoleID() {
		return roleID;
	}

	public void setRoleID(int roleID) {
		this.roleID = roleID;
	}

	public int getStatusID() {
		return statusID;
	}

	public void setStatusID(int statusID) {
		this.statusID = statusID;
	}

}

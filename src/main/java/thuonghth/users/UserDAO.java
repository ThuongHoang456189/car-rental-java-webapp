package thuonghth.users;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.NamingException;

import thuonghth.utils.DBHelper;
import thuonghth.utils.MyConstants;
import thuonghth.utils.PBKDF2PasswordHasher;

/**
 * @author thuonghth
 */
public class UserDAO {
	public boolean registerNewUser(UserDTO user) throws SQLException, NamingException {
		boolean success = false;
		Connection conn = null;
		try {
			conn = DBHelper.getConnect();
			CallableStatement cstmt = conn.prepareCall("{? = call dbo.insertNewUserProc(?, ?, ?, ?, ?, ?, ?)}");
			cstmt.registerOutParameter(1, java.sql.Types.INTEGER);
			cstmt.setString(2, user.getAccount().getEmail());
			cstmt.setString(3, user.getAccount().getHashedPassword());
			cstmt.setString(4, user.getName());
			cstmt.setString(5, user.getPhone());
			cstmt.setString(6, user.getAddress());
			cstmt.setShort(7, (short) user.getRoleID());
			cstmt.setShort(8, (short) user.getStatusID());
			cstmt.execute();
			success = cstmt.getBoolean(1);
			cstmt.close();
		} finally {
			if (conn != null)
				conn.close();
		}
		return success;
	}

	public AccountDTO login(AccountDTO account) throws SQLException, NamingException {
		AccountDTO found = null;
		Connection conn = null;
		try {
			conn = DBHelper.getConnect();
			String query = "SELECT email, hashed_password ,account_status_id FROM user_account WHERE email like ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, account.getEmail());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				found = new AccountDTO(rs.getString("email"), rs.getString("hashed_password"),
						rs.getInt("account_status_id"));
			}
			rs.close();
			pstmt.close();
		} finally {
			if (conn != null)
				conn.close();
		}
		return found;
	}

	public boolean matchAccount(AccountDTO inputAccount, AccountDTO queryAccount) {
		if (queryAccount == null)
			return false;
		PBKDF2PasswordHasher hasher = new PBKDF2PasswordHasher();
		boolean matches = hasher.checkPassword(inputAccount.getHashedPassword(), queryAccount.getHashedPassword());
		return matches;
	}

	public boolean isAccountActive(AccountDTO queryAccount) {
		return queryAccount.getStatusId() == MyConstants.STATUS_ACTIVE;
	}

	public boolean isAccountNew(AccountDTO queryAccount) {
		return queryAccount.getStatusId() == MyConstants.STATUS_NEW;
	}

	public int getRoleIdOfUser(String email) throws NamingException, SQLException {
		Connection conn = null;
		int roleId = -1;
		try {
			conn = DBHelper.getConnect();
			String query = "SELECT role_id FROM user_info WHERE email LIKE ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, email);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				roleId = rs.getInt("role_id");
			}
			rs.close();
			pstmt.close();
		} finally {
			if (conn != null)
				conn.close();
		}
		return roleId;
	}

	public boolean isAdmin(String email) throws NamingException, SQLException {
		return getRoleIdOfUser(email) == MyConstants.ROLE_ADMIN;
	}

	public boolean isUser(String email) throws NamingException, SQLException {
		return getRoleIdOfUser(email) == MyConstants.ROLE_CUSTOMER_USER;
	}

	public boolean activateAccount(String email) throws NamingException, SQLException {
		boolean success = false;
		Connection conn = null;
		try {
			conn = DBHelper.getConnect();
			String query = "UPDATE user_account SET account_status_id = ? WHERE email like ?";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setShort(1, (short) MyConstants.STATUS_ACTIVE);
			pstmt.setString(2, email);
			success = pstmt.executeUpdate() > 0;
			pstmt.close();
		} finally {
			if (conn != null)
				conn.close();
		}
		return success;
	}
}

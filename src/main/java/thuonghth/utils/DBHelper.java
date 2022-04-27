package thuonghth.utils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * @author thuonghth
 */
public class DBHelper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3107065742953827596L;

	public static Connection getConnect() throws NamingException, SQLException {
		Context context = new InitialContext();
		Context tomcatContext = (Context) context.lookup("java:comp/env");
		DataSource ds = (DataSource) tomcatContext.lookup("CarRentalDB");
		Connection conn = ds.getConnection();
		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		return conn;
	}
}

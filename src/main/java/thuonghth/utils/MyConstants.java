package thuonghth.utils;

import java.io.Serializable;

/**
 * @author thuonghth
 */

public class MyConstants implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// paging
	public static final int TOTAL_ITEM_IN_PAGE = 3;
	// Role
	public static final int ROLE_ADMIN = 3, ROLE_CUSTOMER_USER = 4;
	// Status
	public static final int STATUS_NEW = 3, STATUS_ACTIVE = 4, STATUS_INACTIVE = 5, STATUS_ACCEPT = 6,
			STATUS_DELETE = 7;
	// Recaptcha google
	public static final String SITE_KEY = "6Les7AweAAAAANF8pQrk6c5xZ21JKEwUhxWOKHgI";
	public static final String SECRET_KEY = "6Les7AweAAAAALOANGydouHe9a13LrcW_b0CQYKI";

	// Send Email
	public static final int SMTP_PORT = 465;
	public static final String HOST_EMAIL = "chuchunya123@gmail.com";
	public static final String HOST_EMAIL_PWD = "wgrecdkquudfsqfe";

	public static final int DEFAULT_RENTAL_INTERVAL = 10;

	public static final int SESSION_VALID_TIME_IN_MINUTE = 30;
}

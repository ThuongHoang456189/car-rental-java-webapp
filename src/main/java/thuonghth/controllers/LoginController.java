package thuonghth.controllers;

import java.io.IOException;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import thuonghth.users.AccountDTO;
import thuonghth.users.UserDAO;
import thuonghth.utils.VerifyRecaptchaUtil;
import thuonghth.utils.input_validators.AccountValidator;

/**
 * @author thuonghth
 */

// This servlet handles the Login action
public class LoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(LoginController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		request.getRequestDispatcher("login.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	private final String SEARCH_CONTROLLER = "SearchController";
	private final String LOGIN_PAGE = "login.jsp";
	private final String CONFIRM_EMAIL_PAGE = "confirmEmail.jsp";
	private final String INVALID_CAPTCHA_ERROR = "Captcha invalid! ";
	private final String INVALID_INPUT_ERROR = "Invalid input. ";
	private final String FAILED_LOGIN_ERROR = "Email or Password invalid! ";

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String inputEmail = request.getParameter("email");
		String inputPwd = request.getParameter("password");
		
		String url = LOGIN_PAGE;
		
		boolean sendCode = false;
		try {
			boolean valid = true;
			String errorString = null;

			String recaptchaResponse = request.getParameter("g-recaptcha-response");
			// Verify CAPTCHA.
			valid = VerifyRecaptchaUtil.verify(recaptchaResponse);
			if (!valid) {
				LOGGER.error(INVALID_CAPTCHA_ERROR);
				request.setAttribute("actionFeedback", INVALID_CAPTCHA_ERROR);
				request.setAttribute("email", inputEmail);
			} else {
				// Validate input
				AccountValidator validator = new AccountValidator(inputEmail, inputPwd);
				TreeMap<String, String> fieldsMsg = validator.validateFields();
				if (!validator.isFieldsValid()) {
					LOGGER.error(INVALID_INPUT_ERROR);
					request.setAttribute("actionFeedback", INVALID_INPUT_ERROR);
					request.setAttribute("fieldsMsg", fieldsMsg);
					request.setAttribute("email", inputEmail);
				} else {
					AccountDTO inputAccount = validator.getAccountDTO();
					UserDAO dao = new UserDAO();
					// Login
					AccountDTO queryAccount = dao.login(inputAccount);
					if (dao.matchAccount(inputAccount, queryAccount)) {
						if (dao.isAccountActive(queryAccount)) {
							HttpSession session = request.getSession();
							session.setAttribute("user",
									dao.isAdmin(queryAccount.getEmail()) ? "admin" : queryAccount.getEmail());
							response.sendRedirect(SEARCH_CONTROLLER);
							return;
						} else if (dao.isAccountNew(queryAccount)) {
							sendCode = true;
							request.getSession().setAttribute("newUser", queryAccount.getEmail());
							url = CONFIRM_EMAIL_PAGE;
							request.getRequestDispatcher(url).forward(request, response);
							return;
						}
					} else {
						request.setAttribute("actionFeedback", FAILED_LOGIN_ERROR);
					}
				}

			}
			request.getRequestDispatcher(url).forward(request, response);
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

}

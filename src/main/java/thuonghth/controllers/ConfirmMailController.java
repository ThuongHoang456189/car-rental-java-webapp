package thuonghth.controllers;

import java.io.IOException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.users.UserDAO;
import thuonghth.utils.MyConstants;
import thuonghth.utils.RandomGeneratorUtil;
import thuonghth.utils.SendMailUtil;

/**
 * @author thuonghth
 */

// This servlet is used for activating new user account
public class ConfirmMailController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ConfirmMailController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ConfirmMailController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	private final String LOGIN_PAGE = "login.jsp";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Set up request, response character encoding to UTF-8
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		// Fetch newUser info if they have logged in or have registered.
		String newUser = (String) request.getSession().getAttribute("newUser");
		
		// Check to make sure the new user has registered or logged in. 
		// If they did the thing above, check for verification code is still valid, 
		// if not, regenerate a new verification code.
		if (newUser == null || newUser.isBlank()) {
			request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
			return;
		} else {
			String confirmCode = (String) request.getSession().getAttribute("new-user-confirm-code");
			if (confirmCode == null || confirmCode.isBlank()) {
				String newConfirmCode = RandomGeneratorUtil.generateActivateCode();
				request.getSession().setAttribute("new-user-confirm-code", newConfirmCode);
				SendMailUtil.sendMail(newUser, newConfirmCode);
				request.setAttribute("actionFeedback",
						"We have just sent you the verification code. Please check your email and fill the code below.");
				LOGGER.info("Verfication code sent.");
			}
			request.getRequestDispatcher("confirmEmail.jsp").forward(request, response);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	private final String SEARCH_CONTROLLER = "SearchController";

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String newUser = (String) request.getSession().getAttribute("newUser");
		if (newUser == null || newUser.isBlank()) {
			request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
			return;
		} else {
			String confirmCode = (String) request.getSession().getAttribute("new-user-confirm-code");
			if (confirmCode == null || confirmCode.isBlank()) {
				String newConfirmCode = RandomGeneratorUtil.generateActivateCode();
				request.getSession().setAttribute("new-user-confirm-code", newConfirmCode);
				SendMailUtil.sendMail(newUser, newConfirmCode);
				request.setAttribute("actionFeedback",
						"We have just sent you the verification code. Note that the verification code is valid in "
								+ MyConstants.SESSION_VALID_TIME_IN_MINUTE
								+ " minutes. Please check your email and fill the code below.");
				LOGGER.info("Verfication code sent.");
				request.getRequestDispatcher("confirmEmail.jsp").forward(request, response);
				return;
			} else {
				String inputConfirmCode = request.getParameter("confirmCode").trim();
				if (inputConfirmCode.equals(confirmCode)) {
					UserDAO dao = new UserDAO();
					try {
						if (dao.activateAccount(newUser)) {
							request.getSession().removeAttribute("newUser");
							request.getSession().removeAttribute("new-user-confirm-code");
							response.sendRedirect(LOGIN_PAGE);
						} else {
							request.setAttribute("actionFeedback", "Fail to activate your account.");
							LOGGER.error("Fail to activate account.");
							request.getRequestDispatcher("confirmEmail.jsp").forward(request, response);
						}
					} catch (NamingException e) {
						LOGGER.error(e);
					} catch (SQLException e) {
						LOGGER.error(e);
					}
				} else {
					request.setAttribute("actionFeedback", "Invalid verification code. Please try again.");
					request.getRequestDispatcher("confirmEmail.jsp").forward(request, response);
				}
			}
		}
	}

}

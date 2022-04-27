package thuonghth.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TreeMap;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.users.UserDAO;
import thuonghth.users.UserDTO;
import thuonghth.utils.input_validators.UserRegistrationInfoValidator;

/**
 * @author thuonghth
 */
// Register Controller
public class RegisterController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(RegisterController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterController() {
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
		request.getRequestDispatcher("user-register-form.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	private final String SUCCESS_REGISTRATION_MSG = "You have successfully registerd. ";
	private final String FAIL_REGISTRATION_ERROR = "Oop. This email is already existed or something wrong happened. ";
	private final String INVALID_REGISTRATION_INFO_ERROR = "Your information form has some invalid input. ";
	private final String CONFIRM_EMAIL_CONTROLLER = "ConfirmMailController";

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String inputEmail = request.getParameter("email");
		String inputPwd = request.getParameter("password");
		String inputConfirmPwd = request.getParameter("confirmPassword");
		String inputPhone = request.getParameter("phone");
		String inputName = request.getParameter("name");
		String inputAddress = request.getParameter("address");
		
		UserRegistrationInfoValidator validator = new UserRegistrationInfoValidator(inputEmail, inputPwd,
				inputConfirmPwd, inputName, inputPhone, inputAddress);
		TreeMap<String, String> fieldsMsg = validator.validateUser();
		if (!validator.isUserInfoValid()) {
			LOGGER.error(INVALID_REGISTRATION_INFO_ERROR);
			request.setAttribute("fields", validator);
			request.setAttribute("actionFeedback", INVALID_REGISTRATION_INFO_ERROR);
			request.setAttribute("fieldsMsg", fieldsMsg);
			request.getRequestDispatcher("user-register-form.jsp").forward(request, response);
			return;
		} else {
			UserDAO userDAO = new UserDAO();
			UserDTO userDTO = validator.getUserDTO();
			boolean isSuccessful = false;
			try {
				isSuccessful = userDAO.registerNewUser(userDTO);
				if (isSuccessful) {
					LOGGER.info(SUCCESS_REGISTRATION_MSG);
					request.setAttribute("actionFeedback", SUCCESS_REGISTRATION_MSG);
					request.getSession().setAttribute("newUser", userDTO.getAccount().getEmail());
					request.getRequestDispatcher(CONFIRM_EMAIL_CONTROLLER).forward(request, response);
					return;
				} else {
					LOGGER.error(FAIL_REGISTRATION_ERROR);
					request.setAttribute("fields", validator);
					request.setAttribute("actionFeedback", FAIL_REGISTRATION_ERROR);
					request.getRequestDispatcher("user-register-form.jsp").forward(request, response);
					return;
				}
			} catch (SQLException | NamingException e) {
				LOGGER.error(e);
				request.getRequestDispatcher("error-page.jsp").forward(request, response);
				return;
			}
		}
	}

}

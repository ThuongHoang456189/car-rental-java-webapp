package thuonghth.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.utils.RandomGeneratorUtil;
import thuonghth.utils.SendMailUtil;

/**
 * @author thuonghth
 */
public class SendCodeAgainController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(SendCodeAgainController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SendCodeAgainController() {
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
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String newUser = (String) request.getSession().getAttribute("newUser");
		if (newUser == null || newUser.isBlank()) {
			request.getRequestDispatcher(LOGIN_PAGE).forward(request, response);
			return;
		} else {
			String newConfirmCode = RandomGeneratorUtil.generateActivateCode();
			request.getSession().setAttribute("new-user-confirm-code", newConfirmCode);
			SendMailUtil.sendMail(newUser, newConfirmCode);
			LOGGER.info("Verfication code sent.");
			request.setAttribute("actionFeedback",
					"We have just sent you the verification code. Please check your email and fill the code below.");
			request.getRequestDispatcher("confirmEmail.jsp").forward(request, response);
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

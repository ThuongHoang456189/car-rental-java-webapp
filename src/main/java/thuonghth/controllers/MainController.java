package thuonghth.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author thuonghth
 */
public class MainController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private final String LOGIN_CONTROLLER = "LoginController";
	private final String LOGOUT_CONTROLLER = "LogoutController";
	private final String REGISTER_CONTROLLER = "RegisterController";
	private final String CONFIRM_EMAIL_CONTROLLER = "ConfirmMailController";
	private final String SEND_CODE_AGAIN_CONTROLLER = "SendCodeAgainController";
	private final String SEARCH_CONTROLLER = "SearchController";
	private final String CART_INSERTION_CONTROLLER = "/addToCart";
	private final String CART_STATUS_CONTROLLER = "/changeCartStatus";
	private final String CART_SHOWING_CONTROLLER = "/viewCart";
	private final String CART_REMOVING_CONTROLLER = "/removeFromCart";
	private final String ORDERING_CONTROLLER = "/order";
	private final String CONFIRMATION_CONTROLLER = "/confirm";
	private final String ORDER_SEARCHING_CONTROLLER = "/viewOrderHistory";
	private final String ORDER_REMOVING_CONTROLLER = "/deleteOrder";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MainController() {
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
		response.setContentType("text/html;charset=UTF-8");
		String action = request.getParameter("btnAction");
		String url = SEARCH_CONTROLLER;
		try {
			if (action == null) {
				url = SEARCH_CONTROLLER;
			} else {
				String user = (String) (request.getSession()).getAttribute("user");
				String newUser = (String) (request.getSession()).getAttribute("newUser");
				if (user == null && newUser == null) {
					if (action.equals("Login")) {
						url = LOGIN_CONTROLLER;
					} else if (action.equals("Register")) {
						url = REGISTER_CONTROLLER;
					} else {
						url = SEARCH_CONTROLLER;
					}
				} else {
					if (newUser != null && user == null) {
						if (action.equals("Login")) {
							url = CONFIRM_EMAIL_CONTROLLER;
						} else if (action.equals("Confirm")) {
							url = CONFIRM_EMAIL_CONTROLLER;
						} else if (action.equals("send code again")) {
							url = SEND_CODE_AGAIN_CONTROLLER;
						} else {
							url = SEARCH_CONTROLLER;
						}
					} else {
						// Sua khuc nay
						if (action.equals("Logout")) {
							url = LOGOUT_CONTROLLER;
						} else if (action.equals("addToCart")) {
							url = CART_INSERTION_CONTROLLER;
						} else if (action.equals("changeCartStatus")) {
							url = CART_STATUS_CONTROLLER;
						} else if (action.equals("viewCart")) {
							url = CART_SHOWING_CONTROLLER;
						} else if (action.equals("removeFromCart")) {
							url = CART_REMOVING_CONTROLLER;
						} else if (action.equals("order")) {
							url = ORDERING_CONTROLLER;
						} else if (action.equals("confirm")) {
							url = CONFIRMATION_CONTROLLER;
						} else if (action.equals("viewOrderHistory")) {
							url = ORDER_SEARCHING_CONTROLLER;
						} else if (action.equals("deleteOrder")) {
							url = ORDER_REMOVING_CONTROLLER;
						} else {
							url = SEARCH_CONTROLLER;
						}
					}
				}
			}
		} finally {
			request.getRequestDispatcher(url).forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		String action = request.getParameter("btnAction");
		String url = SEARCH_CONTROLLER;
		try {
			if (action == null) {
				url = SEARCH_CONTROLLER;
			} else {
				String user = (String) (request.getSession()).getAttribute("user");
				String newUser = (String) (request.getSession()).getAttribute("newUser");
				if (user == null && newUser == null) {
					if (action.equals("Login")) {
						url = LOGIN_CONTROLLER;
					} else if (action.equals("Register")) {
						url = REGISTER_CONTROLLER;
					} else {
						url = SEARCH_CONTROLLER;
					}
				} else {
					if (newUser != null && user == null) {
						if (action.equals("Login")) {
							url = CONFIRM_EMAIL_CONTROLLER;
						} else if (action.equals("Confirm")) {
							url = CONFIRM_EMAIL_CONTROLLER;
						} else if (action.equals("send code again")) {
							url = SEND_CODE_AGAIN_CONTROLLER;
						} else {
							url = SEARCH_CONTROLLER;
						}
					} else {
						// Sua khuc nay
						if (action.equals("Logout")) {
							url = LOGOUT_CONTROLLER;
						} else if (action.equals("addToCart")) {
							url = CART_INSERTION_CONTROLLER;
						} else if (action.equals("changeCartStatus")) {
							url = CART_STATUS_CONTROLLER;
						} else if (action.equals("viewCart")) {
							url = CART_SHOWING_CONTROLLER;
						} else if (action.equals("removeFromCart")) {
							url = CART_REMOVING_CONTROLLER;
						} else if (action.equals("order")) {
							url = ORDERING_CONTROLLER;
						} else if (action.equals("confirm")) {
							url = CONFIRMATION_CONTROLLER;
						} else if (action.equals("viewOrderHistory")) {
							url = ORDER_SEARCHING_CONTROLLER;
						} else if (action.equals("deleteOrder")) {
							url = ORDER_REMOVING_CONTROLLER;
						} else {
							url = SEARCH_CONTROLLER;
						}
					}
				}
			}
		} finally {
			request.getRequestDispatcher(url).forward(request, response);
		}
	}

}

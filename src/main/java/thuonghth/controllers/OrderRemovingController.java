package thuonghth.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.orders.OrderDAO;
import thuonghth.utils.input_validators.OrderSearchFeedback;

/**
 * Servlet implementation class OrderRemovingController
 */
public class OrderRemovingController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(OrderRemovingController.class);

	private boolean isUser(HttpServletRequest request) {
		return request.getSession().getAttribute("user") != null;
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrderRemovingController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (isUser(request)) {
			String orderID = request.getParameter("orderID");
			if (orderID != null) {
				OrderSearchFeedback orderSearchFeedback = new OrderSearchFeedback();
				if (orderSearchFeedback.isOrderIDValid(orderID)) {
					try {
						boolean isSuccess = new OrderDAO().removeOrder(Long.parseLong(orderID));
						if (isSuccess) {
							request.setAttribute("actionFeedback", "Deleted successfully.");
						} else {
							request.setAttribute("actionFeedback", "Deleted failed!");
						}
						request.getRequestDispatcher("/viewOrderHistory").forward(request, response);
						return;
					} catch (Exception e) {
						LOGGER.error(e);
						response.sendRedirect("error-page.jsp");
						return;
					}
				} else {
					LOGGER.error("The orderID is invalid. ");
					response.sendRedirect("error-page.jsp");
					return;
				}
			} else {
				LOGGER.debug("The orderID is null. ");
				response.sendRedirect("error-page.jsp");
				return;
			}
		} else {
			response.sendRedirect("MainController?btnAction=Login");
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

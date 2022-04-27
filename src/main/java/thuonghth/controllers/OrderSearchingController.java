package thuonghth.controllers;

import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.orders.OrderDAO;
import thuonghth.orders.OrderDTO;
import thuonghth.utils.input_validators.OrderSearchFeedback;
import thuonghth.utils.input_validators.OrderSearchInfo;

/**
 * Servlet implementation class OrderSearchingController
 */
public class OrderSearchingController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(OrderSearchingController.class);

	private boolean isUser(HttpServletRequest request) {
		return request.getSession().getAttribute("user") != null;
	}

	private boolean isActiveAdmin(HttpServletRequest request) {
		return request.getSession().getAttribute("user") != null
				&& ((String) request.getSession().getAttribute("user")).equals("admin");
	}

	private String getCustomer(HttpServletRequest request) {
		return (String) request.getSession().getAttribute("user");
	}

	private OrderSearchFeedback getDefaultSearchInfoForRequest(String sortOption) {
		return new OrderSearchFeedback("", "", sortOption);
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrderSearchingController() {
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
			String sortOption = request.getParameter("sortOption");
			OrderSearchFeedback defaultOrderSearchFeedback = getDefaultSearchInfoForRequest(sortOption);
			OrderSearchInfo defaultSearchInfo = defaultOrderSearchFeedback.getOrderSearchInfo();
			OrderDAO orderDAO = new OrderDAO();
			LinkedList<OrderDTO> orderList = new LinkedList<>();
			try {
				if (isActiveAdmin(request)) {
					orderList = orderDAO.getOrderList(defaultSearchInfo);
				} else {
					orderList = orderDAO.getCustomerOrderList(getCustomer(request), defaultSearchInfo);
				}
				request.setAttribute("orderSearchInfo", defaultOrderSearchFeedback);
				request.setAttribute("orders", orderList);
				request.getRequestDispatcher("order-history.jsp").forward(request, response);
				return;
			} catch (Exception e) {
				LOGGER.error(e);
				e.printStackTrace();
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
		if (isUser(request)) {
			String carName = request.getParameter("carName");
			String orderDate = request.getParameter("orderDate");
			String sortOption = request.getParameter("sortOption");
			OrderSearchFeedback validator = new OrderSearchFeedback(carName, orderDate, sortOption);
			TreeMap<String, String> fieldsMsg = validator.validateOrderSearchInfo();
			if (validator.isOrderSearchInfoValid()) {
				OrderSearchInfo orderSearchInfo = validator.getOrderSearchInfo();
				OrderDAO orderDAO = new OrderDAO();
				LinkedList<OrderDTO> orderList = new LinkedList<>();
				try {
					if (isActiveAdmin(request)) {
						orderList = orderDAO.getOrderList(orderSearchInfo);
					} else {
						orderList = orderDAO.getCustomerOrderList(getCustomer(request), orderSearchInfo);
					}
					request.setAttribute("orderSearchInfo", validator);
					request.setAttribute("orders", orderList);
				} catch (Exception e) {
					LOGGER.error(e);
					response.sendRedirect("error-page.jsp");
					return;
				}
			} else {
				request.setAttribute("orderSearchInfo", validator);
				request.setAttribute("fieldsMsg", fieldsMsg);
			}
			request.getRequestDispatcher("order-history.jsp").forward(request, response);
			return;
		} else {
			response.sendRedirect("MainController?btnAction=Login");
			return;
		}
	}

}

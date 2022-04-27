package thuonghth.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.carts.CartDAO;
import thuonghth.carts.CartDTO;
import thuonghth.carts.CartRecord;
import thuonghth.utils.input_validators.CarSearchInfo;

/**
 * Servlet implementation class CartShowingController
 */
public class CartShowingController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(CartShowingController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CartShowingController() {
		super();
		// TODO Auto-generated constructor stub
	}

	private boolean isActiveCustomer(HttpServletRequest request) {
		return request.getSession().getAttribute("user") != null
				&& !((String) request.getSession().getAttribute("user")).equals("admin");
	}

	private boolean isActiveAdmin(HttpServletRequest request) {
		return request.getSession().getAttribute("user") != null
				&& ((String) request.getSession().getAttribute("user")).equals("admin");
	}

	private CartDTO getCart(HttpServletRequest request) {
		return (CartDTO) request.getSession().getAttribute("cart");
	}

	private String getCartStatus(CartDTO cart) {
		return cart.getStatus();
	}

	private Date getRentalDate(HttpServletRequest request) {
		CarSearchInfo searchInfo = (CarSearchInfo) request.getSession().getAttribute("searchInfo");
		return searchInfo.getRentalDate();
	}

	private Date getReturnDate(HttpServletRequest request) {
		CarSearchInfo searchInfo = (CarSearchInfo) request.getSession().getAttribute("searchInfo");
		return searchInfo.getReturnDate();
	}

	private void resetFieldsMsg(CartDTO cart) {
		for (CartRecord record : cart.getCart().values()) {
			record.getCar().setFieldsMsg(null);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (isActiveCustomer(request)) {
			CartDTO cart = getCart(request);
			try {
				if (cart != null) {
					resetFieldsMsg(cart);
					if (!new CartDAO(cart).displayValidateCart("", getRentalDate(request), getReturnDate(request), null))
						cart.setTotal(BigDecimal.ZERO);
					String btnAction = getCartStatus(cart).equals(CartDTO.ORDERING_STATUS) ? "confirm" : "order";
					request.setAttribute("btnAction", btnAction);
				}

				request.getRequestDispatcher("cart.jsp").forward(request, response);
				return;
			} catch (NamingException | SQLException e) {
				LOGGER.error(e);
				response.sendRedirect("error-page.jsp");
				return;
			}
		} else {
			if (isActiveAdmin(request)) {
				LOGGER.error("Only customer can rent car(s). ");
				response.sendRedirect("error-page.jsp");
				return;
			} else {
				response.sendRedirect("MainController?btnAction=Login");
				return;
			}
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

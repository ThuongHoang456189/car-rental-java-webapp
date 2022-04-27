package thuonghth.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.TreeMap;

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
 * Servlet implementation class OrderingController
 */
public class OrderingController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(OrderingController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrderingController() {
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

	private boolean setUpAmount(HttpServletRequest request) {
		boolean isInputValid = true;
		CartDTO cart = getCart(request);
		if (cart == null)
			isInputValid = false;
		else {
			for (CartRecord record : cart.getCart().values()) {
				record.getCar().setFieldsMsg(new TreeMap<String, String>());
				TreeMap<String, String> fieldsMsg = record.getCar().getFieldsMsg();
				try {
					String amountString = request.getParameter("amount" + record.getCar().getInputObject().getCarId());
					int amount = Integer.parseInt(amountString);
					if (amount <= 0) {
						fieldsMsg.put("quantity", "The amount field must be at least 1. ");
						isInputValid = isInputValid && false;
					}
					record.setAmount(amount);
					record.setSubTotal(
							record.getCar().getInputObject().getPrice().multiply(new BigDecimal(record.getAmount())));
				} catch (NumberFormatException e) {
					fieldsMsg.put("quantity", "The amount field must be an integer number. ");
					isInputValid = isInputValid && false;
				}
			}
		}
		return isInputValid;
	}

	private String getDiscountCode(HttpServletRequest request) {
		return request.getParameter("discountCode");
	}

	private Date getRentalDate(HttpServletRequest request) {
		CarSearchInfo searchInfo = (CarSearchInfo) request.getSession().getAttribute("searchInfo");
		return searchInfo.getRentalDate();
	}

	private Date getReturnDate(HttpServletRequest request) {
		CarSearchInfo searchInfo = (CarSearchInfo) request.getSession().getAttribute("searchInfo");
		return searchInfo.getReturnDate();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (isActiveCustomer(request)) {
			boolean isCartValid = true;
			isCartValid = isCartValid && setUpAmount(request);
			CartDTO cart = getCart(request);
			if (cart != null) {
				CartDAO cartDAO = new CartDAO(cart);
				String discountCode = getDiscountCode(request);
				try {
					boolean isDiscountValid = cartDAO.validateDiscount(
							discountCode != null ? discountCode : cart.getDiscount().getInputObject().getDiscountCode())
							|| discountCode.isBlank();
					if (!isDiscountValid)
						request.setAttribute("discountFeedback", "Invalid discount. ");
					isCartValid = isDiscountValid
							&& cartDAO.validateCart(discountCode, getRentalDate(request), getReturnDate(request), null);
					if (isCartValid) {
						cartDAO.updateCartTotal();
						cart.setStatus(CartDTO.ORDERING_STATUS);
						request.setAttribute("btnAction", "confirm");
						request.getRequestDispatcher("/viewCart").forward(request, response);
						return;
					}
				} catch (Exception e) {
					LOGGER.error(e);
					e.printStackTrace();
					response.sendRedirect("error-page.jsp");
					return;
				}
			}
			request.setAttribute("btnAction", "order");
			request.getRequestDispatcher("/viewCart").forward(request, response);
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

}

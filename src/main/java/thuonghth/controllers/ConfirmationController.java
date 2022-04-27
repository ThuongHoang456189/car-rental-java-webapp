package thuonghth.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.carts.CartDAO;
import thuonghth.carts.CartDTO;
import thuonghth.carts.CartRecord;
import thuonghth.discounts.DiscountDTO;
import thuonghth.orders.OrderDAO;
import thuonghth.utils.DBHelper;
import thuonghth.utils.input_validators.CarSearchInfo;

/**
 * Servlet implementation class ConfirmationController
 */
public class ConfirmationController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(ConfirmationController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ConfirmationController() {
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

	private String getCustomer(HttpServletRequest request) {
		return (String) request.getSession().getAttribute("user");
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
			boolean isSuccess = false;
			OrderDAO orderDAO = null;
			boolean isCartValid = true;
			boolean isDiscountValid = true;
			isCartValid = isCartValid && setUpAmount(request);
			CartDTO cart = getCart(request);
			CartDAO cartDAO = new CartDAO(cart);
			if (cart != null) {
				String discountCode = getDiscountCode(request);
				Connection con = null;
				try {
					if (discountCode != null && !discountCode.isBlank()) {
						isDiscountValid = cartDAO.validateDiscount(discountCode);
						isCartValid = isCartValid && cartDAO.validateCart(discountCode, getRentalDate(request),
								getReturnDate(request), null) && isDiscountValid;
						if (!isDiscountValid) {
							request.setAttribute("discountFeedback", "Invalid discount. ");
							cartDAO.updateCartTotal();
							cart.setStatus(CartDTO.INITED_STATUS);
							request.setAttribute("btnAction", "order");
							request.getRequestDispatcher("/viewCart").forward(request, response);
							return;
						} else if (isCartValid) {
							cartDAO.updateCartTotal();
							cart.setStatus(CartDTO.ORDERING_STATUS);
							request.setAttribute("btnAction", "confirm");
							request.getRequestDispatcher("/viewCart").forward(request, response);
							return;
						} else {
							response.sendRedirect("error-page.jsp");
							return;
						}
					} else {

						con = DBHelper.getConnect();
						con.setAutoCommit(false);
						isDiscountValid = cartDAO.validateDiscount(discountCode);
						isCartValid = cartDAO.validateCart(discountCode, getRentalDate(request), getReturnDate(request),
								con) && isDiscountValid;
						if (isCartValid) {
							cart.setStatus(CartDTO.CONFIRMED_STATUS);
							cartDAO.updateCartTotal();
							orderDAO = new OrderDAO();
							isSuccess = orderDAO.insertOrder(cart, getRentalDate(request), getReturnDate(request),
									getCustomer(request), con);
							if (!isSuccess)
								con.rollback();
						}
						con.commit();
					}
				} catch (Exception e) {
					try {
						if (con != null)
							con.rollback();
					} catch (SQLException e1) {
						LOGGER.error(e1);
					}
					e.printStackTrace();
					LOGGER.error(e);
					response.sendRedirect("error-page.jsp");
					return;
				} finally {
					if (con != null)
						try {
							con.setAutoCommit(true);
							con.close();
						} catch (SQLException e2) {
							LOGGER.error(e2);
						}
				}
				if (isSuccess) {
					request.setAttribute("order", orderDAO.getOrder());
					request.getSession().removeAttribute("cart");
					request.getRequestDispatcher("receipt.jsp").forward(request, response);
					return;
				} else {
					if (!isCartValid) {
						if (!isDiscountValid) {
							cart.getDiscount().setInputObject(new DiscountDTO(""));
							cartDAO.updateCartTotal();
							request.setAttribute("discountFeedback", "Invalid discount. ");
						}
						cart.setStatus(CartDTO.INITED_STATUS);
						request.setAttribute("btnAction", "order");
						request.getRequestDispatcher("/viewCart").forward(request, response);
						return;
					}
					request.setAttribute("actionFeedback", "Ordered failed !!!");
					request.getRequestDispatcher("error-page.jsp").forward(request, response);
					return;
				}
			} else {
				request.setAttribute("actionFeedback", "Your car information is lost!");
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

}

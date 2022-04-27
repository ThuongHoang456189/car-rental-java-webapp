package thuonghth.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.carts.CartDAO;
import thuonghth.carts.CartDTO;
import thuonghth.discounts.DiscountDTO;
import thuonghth.utils.input_validators.WrapperInputObject;

/**
 * Servlet implementation class CartRemovingController
 */
public class CartRemovingController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(CartRemovingController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CartRemovingController() {
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

	private String getCarIdString(HttpServletRequest request) {
		String carId = request.getParameter("carId");
		return carId == null ? "" : carId.trim();
	}

	private void removeCarFromCart(HttpServletRequest request) {
		try {
			int carId = Integer.parseInt(getCarIdString(request));
			CartDTO cart = getCart(request);
			if (cart != null) {
				cart.getCart().remove(carId);
				CartDAO cartDAO = new CartDAO(cart);
				cartDAO.updateCartTotal();
				if(cart.getCart().values().size() <= 0)
					cart.setDiscount(new WrapperInputObject<DiscountDTO>());
			}
		} catch (NumberFormatException e) {
			LOGGER.error("Removing TourID is not valid. " + e);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (isActiveCustomer(request)) {
			removeCarFromCart(request);
			response.sendRedirect("MainController?btnAction=viewCart");

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

package thuonghth.controllers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.carts.CartDAO;
import thuonghth.carts.CartDTO;
import thuonghth.props.CarDTO;
import thuonghth.utils.input_validators.CarSearchInfo;
import thuonghth.utils.input_validators.CarSearchValidator;

/**
 * Servlet implementation class CartInsertionController
 */
public class CartInsertionController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(CartInsertionController.class);

	private CartDTO cart;

	private long page = 0;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CartInsertionController() {
		super();
	}

	private boolean isActiveCustomer(HttpServletRequest request) {
		return request.getSession().getAttribute("user") != null
				&& !((String) request.getSession().getAttribute("user")).equals("admin");
	}

	private boolean isActiveAdmin(HttpServletRequest request) {
		return request.getSession().getAttribute("user") != null
				&& ((String) request.getSession().getAttribute("user")).equals("admin");
	}

	private CarSearchInfo getCartSearchInfo(HttpServletRequest request) {
		return (CarSearchInfo) request.getSession().getAttribute("searchInfo");
	}

	private void initialCart(HttpServletRequest request) {
		CartDTO cartInSession = (CartDTO) request.getSession().getAttribute("cart");
		if (cartInSession == null) {
			cart = new CartDTO();

			request.getSession().setAttribute("cart", cart);
		}
		// Bo sung rentalDate, returnDate
		CarSearchInfo searchInfo = getCartSearchInfo(request);
		if (searchInfo != null) {
			cart.setRentalDate(searchInfo.getRentalDate());
			cart.setReturnDate(searchInfo.getReturnDate());
		}
	}

	private CarDTO getCarDTOFromRequest(HttpServletRequest request) {
		CarDTO car = null;
		// Nhung thong tin can lay gom car name, car type, price
		String carId = request.getParameter(CarDTO.CAR_ID_FIELD);
		String carName = request.getParameter(CarDTO.CAR_NAME_FIELD);
		String carType = request.getParameter(CarDTO.CAR_TYPE_FIELD);
		String price = request.getParameter(CarDTO.PRICE_FIELD);
		String quantity = request.getParameter(CarDTO.QUANTITY_FIELD);
		CarSearchValidator validator = new CarSearchValidator();
		validator.setupValidator(carId, carName, carType, price, quantity);
		validator.validateCarFromRequest();
		if (validator.isCarFromRequestValid())
			car = validator.getCarFromRequest();
		return car;
	}

	private CartDTO getCart(HttpServletRequest request) {
		return (CartDTO) request.getSession().getAttribute("cart");
	}

	private void updatePage(HttpServletRequest request) {
		String page = request.getParameter("page");
		try {
			this.page = Long.parseLong(page);
		} catch (NumberFormatException e) {
			this.page = 1;
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (isActiveCustomer(request)) {
			updatePage(request);

			CarDTO car = getCarDTOFromRequest(request);
			if (car != null) {
				initialCart(request);
				CartDTO cart = getCart(request);
				CartDAO cartDAO = new CartDAO(cart);
				cartDAO.updateCart(car);
				cart.setStatus(CartDTO.INITED_STATUS);
			}

			response.sendRedirect("MainController?btnAction=Search&page=" + this.page);
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

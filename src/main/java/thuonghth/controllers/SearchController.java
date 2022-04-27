package thuonghth.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import thuonghth.props.CarDAO;
import thuonghth.props.CarDTO;
import thuonghth.utils.DateTimeUtil;
import thuonghth.utils.MyConstants;
import thuonghth.utils.input_validators.CarSearchInfo;
import thuonghth.utils.input_validators.CarSearchValidator;

/**
 * @author thuonghth
 */
public class SearchController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(SearchController.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchController() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	private final String INDEX_PAGE = "index.jsp";

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		String page = request.getParameter("page");
		CarSearchInfo searchInfo = null;
		CarDAO dao = new CarDAO();
		LinkedList<CarDTO> carsList = null;
		
		try {
			if (page == null || request.getSession().getAttribute("searchInfo") == null || page.equals("")
					|| request.getSession().getAttribute("searchInfo").equals("")) {
				searchInfo = new CarSearchInfo("", "", DateTimeUtil.getCurrentDate(), DateTimeUtil
						.getNextPeriodDays(DateTimeUtil.getCurrentDate(), MyConstants.DEFAULT_RENTAL_INTERVAL));
				carsList = dao.getCarsList(searchInfo, 1);
				int numOfAvailableCars = dao.getNumOfAvailableCars();
				int maxPages = dao.getNumOfPages();
				
				request.getSession().setAttribute("searchInfo", searchInfo);
				request.getSession().setAttribute("numOfAvailableCars", numOfAvailableCars);
				request.getSession().setAttribute("maxPages", maxPages);
			} else {
				searchInfo = (CarSearchInfo) request.getSession().getAttribute("searchInfo");
				int pageValue = Integer.parseInt(page);
				carsList = dao.getCarsList(searchInfo, pageValue);
			}
			request.setAttribute("carsList", carsList);
		} catch (NamingException e) {
			LOGGER.error(e);
		} catch (SQLException e) {
			LOGGER.error(e);
		}
		request.getRequestDispatcher(INDEX_PAGE).forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String carName = request.getParameter("carName");
		String carType = request.getParameter("carType");
		String rentalDateStr = request.getParameter("rentalDate");
		String returnDateStr = request.getParameter("returnDate");
		CarSearchValidator validator = new CarSearchValidator(carName, carType, rentalDateStr, returnDateStr);
		TreeMap<String, String> fieldsMsg = validator.validateCarSearchInfo();
		if (!validator.isCarSearchInfoValid()) {
			CarSearchInfo searchInfo = validator.getWrongCarSearchInfo();
			request.setAttribute("searchInfo", searchInfo);
			request.setAttribute("fieldsMsg", fieldsMsg);
		} else {
			CarSearchInfo searchInfo = validator.getCarSearchInfo();
			CarDAO dao = new CarDAO();
			try {
				LinkedList<CarDTO> carsList = dao.getCarsList(searchInfo, 1);
				int numOfAvailableCars = dao.getNumOfAvailableCars();
				int maxPages = dao.getNumOfPages();
				request.getSession().setAttribute("searchInfo", searchInfo);
				request.setAttribute("fieldsMsg", fieldsMsg);
				request.setAttribute("carsList", carsList);
				request.getSession().setAttribute("numOfAvailableCars", numOfAvailableCars);
				request.getSession().setAttribute("maxPages", maxPages);
			} catch (NamingException e) {
				LOGGER.error(e);
			} catch (SQLException e) {
				LOGGER.error(e);
			}
		}
		request.getRequestDispatcher(INDEX_PAGE).forward(request, response);
	}

}

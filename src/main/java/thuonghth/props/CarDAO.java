package thuonghth.props;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.naming.NamingException;

import thuonghth.carts.CartRecord;
import thuonghth.utils.DBHelper;
import thuonghth.utils.MyConstants;
import thuonghth.utils.input_validators.CarSearchInfo;

/**
 * @author thuonghth
 */

public class CarDAO {
	private int numOfAvailableCars = 0;

	private int calculateOffset(int page) {
		return (page - 1) * MyConstants.TOTAL_ITEM_IN_PAGE;
	}

	public LinkedList<CarDTO> getCarsList(CarSearchInfo searchInfo, int page) throws NamingException, SQLException {
		LinkedList<CarDTO> carsList = new LinkedList<>();
		Connection conn = null;
		try {
			conn = DBHelper.getConnect();
			CallableStatement cstmt = conn.prepareCall("{? = call dbo.getAvailableCarsProc(?, ?, ?, ?, ?, ?)}");
			cstmt.registerOutParameter(1, java.sql.Types.INTEGER);
			cstmt.setDate(2, searchInfo.getRentalDate());
			cstmt.setDate(3, searchInfo.getReturnDate());
			cstmt.setString(4, searchInfo.getCarName());
			cstmt.setString(5, searchInfo.getCarType());
			cstmt.setInt(6, calculateOffset(page));
			cstmt.setInt(7, MyConstants.TOTAL_ITEM_IN_PAGE);
			ResultSet rs = cstmt.executeQuery();
			while (rs.next()) {
				CarDTO car = new CarDTO(rs.getInt("car_id"), rs.getString("car_name"), rs.getInt("year"),
						rs.getString("car_type_description"), rs.getBigDecimal("price"), rs.getInt("quantity"));
				carsList.add(car);
			}
			numOfAvailableCars = cstmt.getInt(1);
			cstmt.close();
		} finally {
			if (conn != null)
				conn.close();
		}
		return carsList;
	}

	public int getNumOfAvailableCars() {
		return this.numOfAvailableCars;
	}

	public int getNumOfPages() {
		return (int) (this.numOfAvailableCars + MyConstants.TOTAL_ITEM_IN_PAGE - 1) / MyConstants.TOTAL_ITEM_IN_PAGE;
	}

	public CarDTO getCar(int carId, Date rentalDate, Date returnDate, Connection con)
			throws NamingException, SQLException {
		CarDTO foundCar = null;
		boolean isPrivateConnection = con == null;
		try {
			if (isPrivateConnection)
				con = DBHelper.getConnect();
			CallableStatement cstmt = con.prepareCall("{call dbo.uspGetCar(?,?,?)}");
			cstmt.setInt(1, carId);
			cstmt.setDate(2, rentalDate);
			cstmt.setDate(3, returnDate);
			ResultSet rs = cstmt.executeQuery();
			if (rs.next()) {
				foundCar = new CarDTO(rs.getInt("car_id"), rs.getNString("car_name"),
						rs.getNString("car_type_description"), rs.getBigDecimal("price"), rs.getInt("quantity"));
			}
		} finally {
			if (isPrivateConnection && con != null)
				con.close();
		}
		return foundCar;
	}

	private void updateCartRecord(boolean isNotMatched, CarDTO carInCart, CarDTO carFromDB,
			TreeMap<String, String> fieldsMsg, String fieldName, String msg) {
		if (isNotMatched) {
			fieldsMsg.put(fieldName, msg);
			switch (fieldName) {
			case CarDTO.CAR_NAME_FIELD:
				carInCart.setCarName(carFromDB == null ? "N/A" : carFromDB.getCarName());
				break;
			case CarDTO.CAR_TYPE_FIELD:
				carInCart.setCarType(carFromDB == null ? "N/A" : carFromDB.getCarType());
				break;
			case CarDTO.QUANTITY_FIELD:
				carInCart.setQuantity(carFromDB == null ? 0 : carFromDB.getQuantity());
				break;
			case CarDTO.PRICE_FIELD:
				carInCart.setPrice(carFromDB == null ? BigDecimal.ZERO : carFromDB.getPrice());
				break;
			default:
				break;
			}
		}
	}

	public boolean validateCar(CartRecord carRecord, Date rentalDate, Date returnDate, Connection con)
			throws NamingException, SQLException {
		boolean isMatched = false;
		CarDTO car = carRecord.getCar().getInputObject();
		int carId = car.getCarId();
		CarDTO carFromDB = getCar(carId, rentalDate, returnDate, con);
		TreeMap<String, String> fieldsMsg = carRecord.getCar().getFieldsMsg();
		if (car.equals(carFromDB)) {
			if (fieldsMsg == null) {
				fieldsMsg = new TreeMap<>();
				carRecord.getCar().setFieldsMsg(fieldsMsg);
			}
			if (carRecord.getAmount() > carFromDB.getQuantity())
				updateCartRecord(carFromDB != null && carRecord.getAmount() > carFromDB.getQuantity(), car, carFromDB,
						fieldsMsg, CarDTO.QUANTITY_FIELD, "The available quantity is not enough! ");
			else
				isMatched = true;
		}
		carRecord.getCar().setValid(isMatched);
		carRecord.getCar().setFieldsMsg(fieldsMsg);
		return isMatched;
	}

	public boolean displayValidateCar(CartRecord carRecord, Date rentalDate, Date returnDate, Connection con)
			throws NamingException, SQLException {
		boolean isMatched = false;
		CarDTO car = carRecord.getCar().getInputObject();
		int carId = car.getCarId();
		CarDTO carFromDB = getCar(carId, rentalDate, returnDate, con);
		TreeMap<String, String> fieldsMsg = carRecord.getCar().getFieldsMsg();
		if (!car.equals(carFromDB)) {
			carRecord.getCar().setValid(false);
			if (fieldsMsg == null) {
				fieldsMsg = new TreeMap<>();
				carRecord.getCar().setFieldsMsg(fieldsMsg);
			}

			updateCartRecord(carFromDB == null, car, carFromDB, fieldsMsg, CarDTO.CAR_NAME_FIELD,
					"Car " + car.getCarName() + " is not available! ");
			updateCartRecord(carFromDB == null, car, carFromDB, fieldsMsg, CarDTO.CAR_TYPE_FIELD,
					car.getCarName().equals("N/A") ? "N/A" : "Car " + car.getCarName() + " is not available! ");
			updateCartRecord(carFromDB == null, car, carFromDB, fieldsMsg, CarDTO.QUANTITY_FIELD,
					car.getCarName().equals("N/A") ? "N/A" : "Car " + car.getCarName() + " is not available! ");
			updateCartRecord(carFromDB == null, car, carFromDB, fieldsMsg, CarDTO.PRICE_FIELD,
					car.getCarName().equals("N/A") ? "N/A" : "Car " + car.getCarName() + " is not available! ");

			if (carFromDB != null) {
				updateCartRecord(!car.getCarName().equalsIgnoreCase(carFromDB.getCarName()), car, carFromDB, fieldsMsg,
						CarDTO.CAR_NAME_FIELD, car != null ? "Replaced " + car.getCarName() : "N/A");
				updateCartRecord(!car.getCarType().equalsIgnoreCase(carFromDB.getCarType()), car, carFromDB, fieldsMsg,
						CarDTO.CAR_TYPE_FIELD, car != null ? "Replaced " + car.getCarType() : "N/A");
				updateCartRecord(car.getQuantity() != carFromDB.getQuantity(), car, carFromDB, fieldsMsg,
						CarDTO.QUANTITY_FIELD,
						car != null
								? "The available quantity changed from " + car.getQuantity() + " to "
										+ carFromDB.getQuantity()
								: "N/A");
				updateCartRecord(carRecord.getAmount() > carFromDB.getQuantity(), car, carFromDB, fieldsMsg,
						CarDTO.QUANTITY_FIELD, car != null ? "The available quantity is not enough! " : "N/A");
				updateCartRecord(car.getPrice().compareTo(carFromDB.getPrice()) != 0, car, carFromDB, fieldsMsg,
						CarDTO.PRICE_FIELD,
						car != null ? "Replaced " + carFromDB.getPrice().setScale(3, RoundingMode.CEILING).toString()
								: "N/A");
			}

		} else {
			if (fieldsMsg == null) {
				fieldsMsg = new TreeMap<>();
				carRecord.getCar().setFieldsMsg(fieldsMsg);
			}
			if (carRecord.getAmount() > carFromDB.getQuantity())
				updateCartRecord(carFromDB != null && carRecord.getAmount() > carFromDB.getQuantity(), car, carFromDB,
						fieldsMsg, CarDTO.QUANTITY_FIELD, "The available quantity is not enough! ");
			else
				isMatched = true;
		}
		carRecord.getCar().setValid(isMatched);
		carRecord.getCar().setFieldsMsg(fieldsMsg);
		return isMatched;
	}
}

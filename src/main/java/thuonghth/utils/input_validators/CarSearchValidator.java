package thuonghth.utils.input_validators;

import java.math.BigDecimal;
import java.util.TreeMap;

import thuonghth.props.CarDTO;
import thuonghth.utils.DateTimeUtil;

/**
 * @author thuonghth
 */

public class CarSearchValidator {
	public static final String CAR_ID_FIELD = "carId";
	public static final String PRICE_FIELD = "price";
	public static final String QUANTITY_FIELD = "quantity";
	public static final String CAR_NAME_FIELD = "carName";
	public static final String CAR_TYPE_FIELD = "carType";
	public static final String RENTAL_DATE_FIELD = "rentalDate";
	public static final String RETURN_DATE_FIELD = "returnDate";
	public static final int CAR_NAME_MAX_LENGTH = 50;
	public static final int CAR_TYPE_MAX_LENGTH = 50;
	public static final String DATE_FORMAT_ERROR = " field must be a valid date. ";
	private String carId;
	private String carName;
	private String carType;
	private String price;
	private String rentalDate;
	private String returnDate;
	private String quantity;
	private TreeMap<String, String> fieldsMsg;

	public CarSearchValidator() {

	}

	public CarSearchValidator(String carName, String carType, String rentalDate, String returnDate) {
		this.carName = carName.trim();
		this.carType = carType.trim();
		this.rentalDate = rentalDate.trim();
		this.returnDate = returnDate.trim();
		this.fieldsMsg = new TreeMap<>();
	}

	public void setupValidator(String carId, String carName, String carType, String price, String quantity) {
		this.carId = carId;
		this.carName = carName;
		this.carType = carType;
		this.price = price;
		this.quantity = quantity;
		this.fieldsMsg = new TreeMap<>();
	}

	public String validateRequiredString(String str, String fieldName) {
		return str.isEmpty() ? "The " + fieldName + " field must not be blank. " : "";
	}

	public String validateMaxStringLength(String str, String fieldName, int maxLength) {
		return str.length() > maxLength
				? "The " + fieldName + " is over the maximum length of " + maxLength + " characters. "
				: "";
	}

	private String validateCarIdFromRequest(String carId) {
		carId = carId.trim();
		String errorMsg = "";
		errorMsg += validateRequiredString(carId, CAR_ID_FIELD);
		try {
			int carIDValue = Integer.parseInt(carId);

			if (carIDValue <= 0)
				errorMsg += "The " + CAR_ID_FIELD + " field must be a positive integer number. ";
		} catch (NumberFormatException e) {
			errorMsg += "The " + CAR_ID_FIELD + " field must be a integer number. ";
		}
		return errorMsg;
	}

	private String validateQuantityFromRequest(String quantity) {
		quantity = carId.trim();
		String errorMsg = "";
		errorMsg += validateRequiredString(quantity, QUANTITY_FIELD);
		try {
			int tourIDValue = Integer.parseInt(quantity);

			if (tourIDValue <= 0)
				errorMsg += "The " + QUANTITY_FIELD + " field must be a positive integer number. ";
		} catch (NumberFormatException e) {
			errorMsg += "The " + QUANTITY_FIELD + " field must be a integer number. ";
		}
		return errorMsg;
	}

	private String validateCarNameFromRequest(String carName) {
		String errorMsg = "";
		carName = carName.trim();
		errorMsg += validateRequiredString(carName, CAR_NAME_FIELD);
		errorMsg += validateMaxStringLength(carName, CAR_NAME_FIELD, CAR_NAME_MAX_LENGTH);
		return errorMsg;
	}

	private String validateCarTypeFromRequest(String carType) {
		String errorMsg = "";
		carType = carType.trim();
		errorMsg += validateRequiredString(carType, CAR_TYPE_FIELD);
		errorMsg += validateMaxStringLength(carType, CAR_TYPE_FIELD, CAR_TYPE_MAX_LENGTH);
		return errorMsg;
	}

	public String validatePriceFromRequest(String price) {
		String errorMsg = "";
		price = price.trim();
		errorMsg += validateRequiredString(price, PRICE_FIELD);
		try {
			BigDecimal priceValue = new BigDecimal(price);

			if (priceValue.compareTo(BigDecimal.ZERO) < 0)
				errorMsg += "The " + PRICE_FIELD + " field must be a non-negative real number. ";
		} catch (NumberFormatException e) {
			errorMsg += "The " + PRICE_FIELD + " field must be a real number" + ". ";
		}
		return errorMsg;
	}

	public String getMaxLengthError(int length) {
		return "This field must not be exceed " + length + " characters. ";
	}

	public String validateCarName(String carName) {
		carName = carName.trim();
		String errorMsg = "";
		errorMsg += carName.length() <= CAR_NAME_MAX_LENGTH ? "" : getMaxLengthError(CAR_NAME_MAX_LENGTH);
		return errorMsg;
	}

	public String validateCarType(String carType) {
		carType = carType.trim();
		String errorMsg = "";
		errorMsg += carType.length() <= CAR_TYPE_MAX_LENGTH ? "" : getMaxLengthError(CAR_TYPE_MAX_LENGTH);
		return errorMsg;
	}

	public String validateRentalDate(String rentalDate) {
		rentalDate = rentalDate.trim();
		String errorMsg = "";
		if (DateTimeUtil.isValidDate(rentalDate)) {
			if (!DateTimeUtil.isAfterToday(rentalDate)) {
				errorMsg += "Rental Date must be today " + DateTimeUtil.getCurrentDateStr() + " or later. ";
			}
		} else {
			errorMsg += "Rental Date" + DATE_FORMAT_ERROR;
		}
		return errorMsg;
	}

	public String validateReturnDate(String returnDate) {
		returnDate = returnDate.trim();
		String errorMsg = "";
		if (DateTimeUtil.isValidDate(returnDate)) {
			if (this.rentalDate.isBlank()) {
				this.rentalDate = DateTimeUtil.getCurrentDateStr();
			}
			if (!DateTimeUtil.isAfterToday(returnDate)) {
				errorMsg += "Renturn Date must be today " + DateTimeUtil.getCurrentDateStr() + " or later. ";
			} else if (DateTimeUtil.isValidDate(rentalDate)
					&& !DateTimeUtil.isValidDateInterval(rentalDate, returnDate)) {
				errorMsg += "Return Date must be after Rental Date. ";
			}
		} else {
			errorMsg += "Return Date" + DATE_FORMAT_ERROR;
		}
		return errorMsg;
	}

	public TreeMap<String, String> validateCarSearchInfo() {
		fieldsMsg = new TreeMap<>();
		fieldsMsg.put(CAR_NAME_FIELD, validateCarName(this.carName));
		fieldsMsg.put(CAR_TYPE_FIELD, validateCarType(this.carType));
		fieldsMsg.put(RENTAL_DATE_FIELD, validateRentalDate(this.rentalDate));
		fieldsMsg.put(RETURN_DATE_FIELD, validateReturnDate(this.returnDate));
		return fieldsMsg;
	}

	public boolean isCarSearchInfoValid() {
		boolean isValid = true;
		isValid = isValid && fieldsMsg.get(CAR_NAME_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(CAR_TYPE_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(RENTAL_DATE_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(RETURN_DATE_FIELD).equals("");
		return isValid;
	}

	public CarSearchInfo getWrongCarSearchInfo() {
		return new CarSearchInfo(carName, carType, rentalDate, returnDate);
	}

	public CarSearchInfo getCarSearchInfo() {
		return new CarSearchInfo(carName, carType, DateTimeUtil.getDate(rentalDate), DateTimeUtil.getDate(returnDate));
	}

	public TreeMap<String, String> validateCarFromRequest() {
		fieldsMsg = new TreeMap<>();
		fieldsMsg.put(CAR_ID_FIELD, carId == null ? "null" : validateCarIdFromRequest(carId));
		fieldsMsg.put(CAR_NAME_FIELD, carName == null ? "null" : validateCarNameFromRequest(carName));
		fieldsMsg.put(CAR_TYPE_FIELD, carType == null ? "null" : validateCarTypeFromRequest(carType));
		fieldsMsg.put(PRICE_FIELD, price == null ? "null" : validatePriceFromRequest(price));
		fieldsMsg.put(QUANTITY_FIELD, quantity == null ? "null" : validateQuantityFromRequest(quantity));
		return fieldsMsg;
	}

	public boolean isCarFromRequestValid() {
		boolean isValid = true;
		isValid = isValid && fieldsMsg.get(CAR_ID_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(CAR_NAME_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(CAR_TYPE_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(PRICE_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(QUANTITY_FIELD).equals("");
		return isValid;
	}

	public CarDTO getCarFromRequest() {
		return new CarDTO(Integer.parseInt(carId), carName, carType, new BigDecimal(price), Integer.parseInt(quantity));
	}
}

package thuonghth.utils.input_validators;

import java.util.TreeMap;

import thuonghth.utils.DateTimeUtil;

public class OrderSearchFeedback {
	private String carName;
	private String orderDate;
	private String sortOption;
	private CarSearchValidator carSearchValidator;
	private static final String CAR_NAME_FIELD = "Car Name";
	private static final String ORDER_DATE_FIELD = "Order Date";
	private TreeMap<String, String> fieldsMsg;

	public OrderSearchFeedback(String carName, String orderDate, String sortOption) {
		this.carName = carName;
		this.orderDate = orderDate;
		this.sortOption = sortOption;
		this.carSearchValidator = new CarSearchValidator();
	}

	public OrderSearchFeedback() {
		this.carSearchValidator = new CarSearchValidator();
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getSortOption() {
		return sortOption;
	}

	public void setSortOption(String sortOption) {
		this.sortOption = sortOption;
	}

	private String validateCarName(String carName) {
		carName = carName.trim();
		String errorMsg = "";
		errorMsg += carName.length() <= CarSearchValidator.CAR_NAME_MAX_LENGTH ? ""
				: carSearchValidator.getMaxLengthError(CarSearchValidator.CAR_NAME_MAX_LENGTH);
		return errorMsg;
	}

	private String validateOrderDate(String date) {
		date = date.trim();
		String errorMsg = "";
		if (!date.isBlank()) {
			if (!DateTimeUtil.isValidDate(date))
				errorMsg += ORDER_DATE_FIELD + CarSearchValidator.DATE_FORMAT_ERROR;
			else if (DateTimeUtil.isFromAfterToday(date))
				errorMsg += ORDER_DATE_FIELD + " must not be after today. ";
		}
		return errorMsg;
	}

	public TreeMap<String, String> validateOrderSearchInfo() {
		fieldsMsg = new TreeMap<>();
		fieldsMsg.put(CAR_NAME_FIELD, validateCarName(this.carName));
		fieldsMsg.put(ORDER_DATE_FIELD, validateOrderDate(this.orderDate));
		return fieldsMsg;
	}

	public boolean isOrderSearchInfoValid() {
		boolean isValid = true;
		isValid = isValid && fieldsMsg.get(CAR_NAME_FIELD).equals("");
		isValid = isValid && fieldsMsg.get(ORDER_DATE_FIELD).equals("");
		return isValid;
	}

	public boolean isOrderIDValid(String orderID) {
		try {
			int carIDValue = Integer.parseInt(orderID);
			if (carIDValue <= 0)
				return false;
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public OrderSearchInfo getOrderSearchInfo() {
		if (this.carName.isBlank() && this.orderDate.isBlank())
			if (this.sortOption != null && !this.sortOption.isBlank())
				return new OrderSearchInfo(sortOption);
			else
				return new OrderSearchInfo();
		else
			return new OrderSearchInfo(carName,
					this.orderDate == null || this.orderDate.isBlank() ? null : DateTimeUtil.getDate(this.orderDate),
					sortOption);
	}

}

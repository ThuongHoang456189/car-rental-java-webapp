package thuonghth.utils.input_validators;

import java.sql.Date;

import thuonghth.utils.DateTimeUtil;

/**
 * @author thuonghth
 */

public class CarSearchInfo {
	private String carName;
	private String carType;
	private Date rentalDate;
	private Date returnDate;
	private String rentalDateStr;
	private String returnDateStr;

	public CarSearchInfo(String carName, String carType, Date rentalDate, Date returnDate) {
		this.carName = carName;
		this.carType = carType;
		this.rentalDate = rentalDate;
		this.returnDate = returnDate;
		this.rentalDateStr = DateTimeUtil.getDateStr(rentalDate);
		this.returnDateStr = DateTimeUtil.getDateStr(returnDate);
	}
	
	public CarSearchInfo(String carName, String carType, String rentalDateStr, String returnDateStr) {
		this.carName = carName;
		this.carType = carType;
		this.rentalDate = null;
		this.returnDate = null;
		this.rentalDateStr = rentalDateStr;
		this.returnDateStr = returnDateStr;
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public Date getRentalDate() {
		return rentalDate;
	}

	public void setRentalDate(Date rentalDate) {
		this.rentalDate = rentalDate;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public String getRentalDateStr() {
		return rentalDateStr;
	}

	public void setRentalDateStr(String rentalDateStr) {
		this.rentalDateStr = rentalDateStr;
	}

	public String getReturnDateStr() {
		return returnDateStr;
	}

	public void setReturnDateStr(String returnDateStr) {
		this.returnDateStr = returnDateStr;
	}

}

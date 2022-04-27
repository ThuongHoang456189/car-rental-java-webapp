package thuonghth.props;

import java.math.BigDecimal;

/**
 * @author thuonghth
 */

public class CarDTO {
	private int carId;
	private String carName;
	private int year;
	private String carType;
	private BigDecimal price;
	private int quantity;

	public static final String CAR_ID_FIELD = "carId";
	public static final String QUANTITY_FIELD = "quantity";
	public static final String CAR_NAME_FIELD = "carName";
	public static final String CAR_TYPE_FIELD = "carType";
	public static final String PRICE_FIELD = "price";

	public CarDTO() {

	}

	public CarDTO(int carId, String carName, int year, String carType, BigDecimal price, int quantity) {
		this.carId = carId;
		this.carName = carName;
		this.year = year;
		this.carType = carType;
		this.price = price;
		this.quantity = quantity;
	}

	public CarDTO(int carId, String carName, String carType, BigDecimal price) {
		this.carId = carId;
		this.carName = carName;
		this.carType = carType;
		this.price = price;
	}

	public CarDTO(int carId, String carName, String carType, BigDecimal price, int quantity) {
		this.carId = carId;
		this.carName = carName;
		this.carType = carType;
		this.price = price;
		this.quantity = quantity;
	}

	public int getCarId() {
		return carId;
	}

	public void setCarId(int carId) {
		this.carId = carId;
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		boolean isEqual = true;
		CarDTO car = (CarDTO) obj;
		isEqual = isEqual && this.carId == car.carId;
		isEqual = isEqual && this.carName.equals(car.carName);
		isEqual = isEqual && this.carType.equals(car.carType);
		isEqual = isEqual && this.quantity == car.quantity;
		isEqual = isEqual && this.price.compareTo(car.price) == 0;
		return isEqual;
	}
}

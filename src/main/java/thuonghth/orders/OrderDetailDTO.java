package thuonghth.orders;

import java.math.BigDecimal;

import thuonghth.props.CarDTO;

public class OrderDetailDTO {
	private long orderId;
	private int carId;
	private int amount;
	private BigDecimal price;
	private BigDecimal subTotal;
	private CarDTO car;

	public OrderDetailDTO(long orderId, int carId, int amount, BigDecimal price) {
		this.orderId = orderId;
		this.carId = carId;
		this.amount = amount;
		this.price = price;
		this.subTotal = price.multiply(new BigDecimal(amount));
	}

	public OrderDetailDTO(int carId, int amount, BigDecimal price) {
		this.carId = carId;
		this.amount = amount;
		this.price = price;
		this.subTotal = price.multiply(new BigDecimal(amount));
	}

	public OrderDetailDTO(int amount, BigDecimal price, CarDTO car) {
		this.amount = amount;
		this.price = price;
		this.car = car;
		this.subTotal = price.multiply(new BigDecimal(amount));
	}

	public long getOrderId() {
		return orderId;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}

	public int getCarId() {
		return carId;
	}

	public void setCarId(int carId) {
		this.carId = carId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public CarDTO getCar() {
		return car;
	}

	public void setCar(CarDTO car) {
		this.car = car;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

}

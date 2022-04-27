package thuonghth.carts;

import java.math.BigDecimal;

import thuonghth.props.CarDTO;
import thuonghth.utils.input_validators.WrapperInputObject;

public class CartRecord {
	private WrapperInputObject<CarDTO> car;
	private int amount;
	private BigDecimal subTotal;

	public CartRecord() {
		this.car = new WrapperInputObject<>();
		this.amount = 0;
		this.subTotal = BigDecimal.ZERO;
	}

	public WrapperInputObject<CarDTO> getCar() {
		return car;
	}

	public void setCar(WrapperInputObject<CarDTO> car) {
		this.car = car;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public BigDecimal getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}

}

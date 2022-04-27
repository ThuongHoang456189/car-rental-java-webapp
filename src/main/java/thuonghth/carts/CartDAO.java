package thuonghth.carts;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.TreeMap;

import javax.naming.NamingException;

import thuonghth.discounts.DiscountDAO;
import thuonghth.discounts.DiscountDTO;
import thuonghth.props.CarDAO;
import thuonghth.props.CarDTO;
import thuonghth.utils.input_validators.WrapperInputObject;

public class CartDAO {
	private CartDTO cart;

	public CartDAO(CartDTO cart) {
		this.cart = cart;
	}

	public CartDTO getCart() {
		return cart;
	}

	public void setCart(CartDTO cart) {
		this.cart = cart;
	}

	private BigDecimal getSubtotal(long amount, BigDecimal price) {
		return price.multiply(new BigDecimal(amount));
	}

	private CartRecord initCartRecord(CarDTO car) {
		CartRecord cartRecord = new CartRecord();
		WrapperInputObject<CarDTO> carWrapper = cartRecord.getCar();
		carWrapper.setInputObject(car);
		cartRecord.setAmount(1);
		cartRecord.setSubTotal(getSubtotal(cartRecord.getAmount(), car.getPrice()));
		return cartRecord;
	}

	private void updateCartRecord(CartRecord cartRecord) {
		WrapperInputObject<CarDTO> carWrapper = cartRecord.getCar();
		cartRecord.setAmount(cartRecord.getAmount() + 1);
		cartRecord.setSubTotal(getSubtotal(cartRecord.getAmount(), carWrapper.getInputObject().getPrice()));
	}

	public BigDecimal getDiscountPercent() {
		BigDecimal discountPercent = BigDecimal.ZERO;
		WrapperInputObject<DiscountDTO> discountWrapper = cart.getDiscount();
		if (discountWrapper != null) {
			DiscountDTO discount = discountWrapper.getInputObject();
			if (discount != null) {
				discountPercent = discount.getDiscountPercent();
				if (discountPercent == null) {
					discountPercent = BigDecimal.ZERO;
				}
			}
		}
		return discountPercent;
	}

	public void updateCartTotal() {
		BigDecimal total = BigDecimal.ZERO;
		for (CartRecord record : cart.getCart().values()) {
			total = total.add(record.getSubTotal());
		}
		total = total.subtract(getDiscountPercent().multiply(total).divide(new BigDecimal(100)));
		cart.setTotal(total);
	}

	public void updateCart(CarDTO car) {
		HashMap<Integer, CartRecord> cartList = cart.getCart();

		CartRecord cartRecord = cartList.get(car.getCarId());

		if (cartRecord == null) {
			cartRecord = initCartRecord(car);
		} else {
			updateCartRecord(cartRecord);
		}
		cartList.put(car.getCarId(), cartRecord);
		updateCartTotal();
	}

	public boolean validateDiscount(String discountCode) throws NamingException, SQLException {
		if (discountCode == null) {
			if (isDiscountExisting())
				discountCode = this.cart.getDiscount().getInputObject().getDiscountCode();
			else
				return true;
		}
		boolean isValid = true;
		discountCode = discountCode.trim();
		DiscountDTO discount = new DiscountDTO(discountCode);
		WrapperInputObject<DiscountDTO> discountWrapper = new WrapperInputObject<>();
		discountWrapper.setFieldsMsg(null);
		TreeMap<String, String> fieldsMsg = discountWrapper.getFieldsMsg() == null ? new TreeMap<String, String>()
				: discountWrapper.getFieldsMsg();
		if (discountCode.isBlank() || discountCode.length() > 10) {
			discountWrapper.setInputObject(discount);
			discountWrapper.setValid(false);
			if (discountCode.length() > 10) {

				if (fieldsMsg == null) {
					fieldsMsg = new TreeMap<>();
					discountWrapper.setFieldsMsg(fieldsMsg);
				}
				fieldsMsg.put(DiscountDTO.DISCOUNT_CODE_FIELD,
						"The Discount Code field must not exceed 10 characters. ");
				discountWrapper.setFieldsMsg(fieldsMsg);
				isValid = false;
			}
		} else {
			// Lay duoc thong tin ve discount
			DiscountDAO discountDAO = new DiscountDAO(discount);
			discountDAO.loadDiscount();
			discountWrapper = discountDAO.getWrapperDiscount();
			isValid = discountDAO.isDiscountCodeValid();
		}
		if (!isValid) {
			fieldsMsg.put(DiscountDTO.DISCOUNT_CODE_FIELD, "The Discount Code is invalid. ");
		}
		cart.setDiscount(discountWrapper);
		return isValid;
	}

	// Can ham xac dinh xem cart da xai discount chua
	// return boolean
	public boolean isDiscountExisting() {
		boolean isExisting = false;
		try {
			isExisting = cart.getDiscount().getInputObject().getDiscountID() != -1;
		} catch (NullPointerException e) {
		}
		return isExisting;
	}

	public boolean validateCart(String discountCode, Date rentalDate, Date returnDate, Connection con)
			throws NamingException, SQLException {
		boolean isValid = true;
		HashMap<Integer, CartRecord> cartList = cart.getCart();
		CarDAO carDAO = new CarDAO();
		for (CartRecord record : cartList.values()) {
			isValid = isValid && carDAO.validateCar(record, rentalDate, returnDate, con);
		}
		if (!isDiscountExisting())
			isValid = isValid && validateDiscount(discountCode);
		return isValid;
	}

	public boolean displayValidateCart(String discountCode, Date rentalDate, Date returnDate, Connection con)
			throws NamingException, SQLException {
		boolean isValid = true;
		HashMap<Integer, CartRecord> cartList = cart.getCart();
		CarDAO carDAO = new CarDAO();
		for (CartRecord record : cartList.values()) {
			boolean isRecordValid = carDAO.displayValidateCar(record, rentalDate, returnDate, con);
			isValid = isValid && isRecordValid;
			if (!isRecordValid)
				record.setSubTotal(BigDecimal.ZERO);
		}
		if (!isDiscountExisting())
			isValid = isValid && validateDiscount(discountCode);
		return isValid;
	}
}

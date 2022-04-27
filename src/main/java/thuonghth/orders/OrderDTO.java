package thuonghth.orders;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedList;

import thuonghth.discounts.DiscountDTO;

public class OrderDTO implements Comparable<OrderDTO> {
	private long orderID;
	private Date orderDate;
	private String customerEmail;
	private Date rentalDate;
	private Date returnDate;
	private DiscountDTO discount;
	private BigDecimal total;

	private LinkedList<OrderDetailDTO> orderDetailDTOs;

	public OrderDTO(long orderID, Date orderDate, String customerEmail, Date rentalDate, Date returnDate,
			DiscountDTO discount, BigDecimal total, LinkedList<OrderDetailDTO> orderDetailDTOs) {
		this.orderID = orderID;
		this.orderDate = orderDate;
		this.customerEmail = customerEmail;
		this.rentalDate = rentalDate;
		this.returnDate = returnDate;
		this.discount = discount;
		this.total = total;
		this.orderDetailDTOs = orderDetailDTOs;
	}

	public OrderDTO(String customerEmail, Date rentalDate, Date returnDate, DiscountDTO discount, BigDecimal total,
			LinkedList<OrderDetailDTO> orderDetailDTOs) {
		this.customerEmail = customerEmail;
		this.rentalDate = rentalDate;
		this.returnDate = returnDate;
		this.discount = discount;
		this.total = total;
		this.orderDetailDTOs = orderDetailDTOs;
	}

	public OrderDTO(Date rentalDate, Date returnDate, DiscountDTO discount, BigDecimal total,
			LinkedList<OrderDetailDTO> orderDetailDTOs) {
		this.rentalDate = rentalDate;
		this.returnDate = returnDate;
		this.discount = discount;
		this.total = total;
		this.orderDetailDTOs = orderDetailDTOs;
	}

	public OrderDTO(Date orderDate, String customerEmail, Date rentalDate, Date returnDate, DiscountDTO discount,
			BigDecimal total) {
		this.orderDate = orderDate;
		this.customerEmail = customerEmail;
		this.rentalDate = rentalDate;
		this.returnDate = returnDate;
		this.discount = discount;
		this.total = total;
		this.orderDetailDTOs = new LinkedList<>();
	}

	public long getOrderID() {
		return orderID;
	}

	public void setOrderID(long orderID) {
		this.orderID = orderID;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
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

	public DiscountDTO getDiscount() {
		return discount;
	}

	public void setDiscount(DiscountDTO discount) {
		this.discount = discount;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public LinkedList<OrderDetailDTO> getOrderDetailDTOs() {
		return orderDetailDTOs;
	}

	public void setOrderDetailDTOs(LinkedList<OrderDetailDTO> orderDetailDTOs) {
		this.orderDetailDTOs = orderDetailDTOs;
	}

	@Override
	public int compareTo(OrderDTO o) {
		return this.orderDate.compareTo(o.getOrderDate());
	}

}

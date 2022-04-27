package thuonghth.orders;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.LinkedList;

import javax.naming.NamingException;

import thuonghth.carts.CartDTO;
import thuonghth.carts.CartRecord;
import thuonghth.discounts.DiscountDTO;
import thuonghth.props.CarDTO;
import thuonghth.utils.DBHelper;
import thuonghth.utils.DateTimeUtil;
import thuonghth.utils.input_validators.OrderSearchInfo;

public class OrderDAO {
	private OrderDTO order;
	private long currentOrderId = -1;
	private OrderDTO currentOrder = null;

	private OrderDetailDTO convertCartRecordToOrderDetail(CartRecord cartRecord) {
		CarDTO car = cartRecord.getCar().getInputObject();
		int amount = cartRecord.getAmount();
		BigDecimal price = car.getPrice();
		return new OrderDetailDTO(amount, price, car);
	}

	public OrderDTO convertCartToOrder(Date rentalDate, Date returnDate, CartDTO cart) {
		LinkedList<OrderDetailDTO> orderDetails = new LinkedList<>();
		for (CartRecord record : cart.getCart().values()) {
			orderDetails.add(convertCartRecordToOrderDetail(record));
		}
		DiscountDTO discount = cart.getDiscount() == null || cart.getDiscount().getInputObject() == null ? null
				: cart.getDiscount().getInputObject();
		BigDecimal total = cart.getTotal();
		return new OrderDTO(rentalDate, returnDate, discount, total, orderDetails);
	}

	private boolean insertOrderDetail(OrderDetailDTO detail, Connection con) throws SQLException {
		CallableStatement cstmt = con.prepareCall("{? = call dbo.uspInsertOrderDetail(?,?,?,?)}");
		cstmt.registerOutParameter(1, Types.BIT);
		cstmt.setLong(2, detail.getOrderId());
		cstmt.setInt(3, detail.getCar().getCarId());
		cstmt.setInt(4, detail.getAmount());
		cstmt.setBigDecimal(5, detail.getPrice());
		cstmt.execute();
		return cstmt.getBoolean(1);
	}

	public boolean insertOrder(CartDTO cart, Date rentalDate, Date returnDate, String customer, Connection con)
			throws SQLException {
		order = convertCartToOrder(rentalDate, returnDate, cart);
		boolean success = false;
		if (con != null) {
			CallableStatement cstmt = con.prepareCall("{call dbo.uspInsertOrder(?,?,?,?,?)}");
			cstmt.setLong(1, order.getDiscount() == null ? -1 : order.getDiscount().getDiscountID());
			cstmt.setBigDecimal(2, order.getTotal());
			cstmt.setNString(3, customer);
			cstmt.setDate(4, rentalDate);
			cstmt.setDate(5, returnDate);
			ResultSet rs = cstmt.executeQuery();
			if (rs.next()) {
				long orderID = rs.getLong(1);
				order.setOrderID(orderID);
				order.setOrderDate(DateTimeUtil.getDateFromTimestamp(rs.getTimestamp(2)));
				for (OrderDetailDTO detail : order.getOrderDetailDTOs()) {
					detail.setOrderId(orderID);
					success = insertOrderDetail(detail, con);
					if (!success)
						break;
				}
			}
		}
		return success;
	}

	public OrderDTO getOrder() {
		return order;
	}

	public void setOrder(OrderDTO order) {
		this.order = order;
	}

	private OrderDetailDTO getOrderDetailFromResultSet(ResultSet rs) throws SQLException {
		CarDTO car = new CarDTO();
		car.setCarName(rs.getNString("car_name"));
		return new OrderDetailDTO(rs.getInt("amount"), rs.getBigDecimal("price"), car);
	}

	private OrderDTO getOrderFromResultSet(ResultSet rs) throws SQLException {
		DiscountDTO discount = null;
		long discountID = rs.getLong("discountID");
		if (!rs.wasNull()) {
			discount = new DiscountDTO(discountID, rs.getBigDecimal("discountPercent"));
		}
		return new OrderDTO(rs.getDate("order_date"), rs.getNString("customer_email"), rs.getDate("rental_date"),
				rs.getDate("return_date"), discount, rs.getBigDecimal("total"));
	}

	public LinkedList<OrderDTO> getCustomerOrderList(String customerEmail, OrderSearchInfo orderSearchInfo)
			throws NamingException, SQLException {
		LinkedList<OrderDTO> orderList = new LinkedList<>();
		String sql = "{call dbo.uspGetCustomerOrder(?,?,?)}";
		Connection con = null;
		try {
			con = DBHelper.getConnect();
			CallableStatement cstmt = con.prepareCall(sql);
			cstmt.setNString(1, orderSearchInfo.getCarName());
			cstmt.setDate(2, orderSearchInfo.getOrderDate());
			cstmt.setNString(3, customerEmail);
			ResultSet rs = cstmt.executeQuery();
			while (rs.next()) {
				long orderID = rs.getLong("order_id");
				if (orderID != currentOrderId) {
					OrderDTO order = getOrderFromResultSet(rs);
					order.setOrderID(orderID);
					orderList.add(order);
					this.currentOrderId = orderID;
					this.currentOrder = order;
				}
				OrderDetailDTO orderDetail = getOrderDetailFromResultSet(rs);
				orderDetail.setOrderId(orderID);
				this.currentOrder.getOrderDetailDTOs().add(orderDetail);
			}
		} finally {
			if (con != null)
				con.close();
		}
		if (orderSearchInfo.getSortOption().equals("asc"))
			Collections.sort(orderList);
		else
			Collections.sort(orderList, Collections.reverseOrder());
		return orderList;
	}

	public LinkedList<OrderDTO> getOrderList(OrderSearchInfo orderSearchInfo) throws NamingException, SQLException {
		LinkedList<OrderDTO> orderList = new LinkedList<>();
		String sql = "{call dbo.uspGetOrder(?,?)}";
		Connection con = null;
		try {
			con = DBHelper.getConnect();
			CallableStatement cstmt = con.prepareCall(sql);
			cstmt.setNString(1, orderSearchInfo.getCarName());
			cstmt.setDate(2, orderSearchInfo.getOrderDate());
			ResultSet rs = cstmt.executeQuery();
			while (rs.next()) {
				long orderID = rs.getLong("order_id");
				if (orderID != currentOrderId) {
					OrderDTO order = getOrderFromResultSet(rs);
					order.setOrderID(orderID);
					orderList.add(order);
					this.currentOrderId = orderID;
					this.currentOrder = order;
				}
				OrderDetailDTO orderDetail = getOrderDetailFromResultSet(rs);
				orderDetail.setOrderId(orderID);
				this.currentOrder.getOrderDetailDTOs().add(orderDetail);
			}
		} finally {
			if (con != null)
				con.close();
		}
		if (orderSearchInfo.getSortOption().equals("asc"))
			Collections.sort(orderList);
		else
			Collections.sort(orderList, Collections.reverseOrder());
		return orderList;
	}

	public boolean removeOrder(long orderID) throws NamingException, SQLException {
		boolean isSuccess = false;
		String sql = "update car_rental_order set is_active = 0 where order_id = ?";
		Connection con = null;
		try {
			con = DBHelper.getConnect();
			PreparedStatement pstmt = con.prepareStatement(sql);
			pstmt.setLong(1, orderID);
			isSuccess = pstmt.executeUpdate() > 0;
		} finally {
			if (con != null)
				con.close();
		}
		return isSuccess;
	}
	
//	public String getTemp() throws NamingException, SQLException {
//		Connection con = null;
//		String msg = "";
//		try {
//			con = DBHelper.getConnect();
//			CallableStatement cstmt = con.prepareCall("{call dbo.temp(?)}");
//			cstmt.setDate(1, DateTimeUtil.getCurrentDate());
//			ResultSet rs = cstmt.executeQuery();
//			if(rs.next()) {
//				msg = rs.getString(1);
//			}
//		} finally {
//			if (con != null)
//				con.close();
//		}
//		return msg;
//	}
}

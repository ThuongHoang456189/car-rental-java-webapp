package thuonghth.utils.input_validators;

import java.sql.Date;

public class OrderSearchInfo {
	private String carName;
	private Date orderDate;
	private String sortOption;

	public OrderSearchInfo(String carName, Date orderDate, String sortOption) {
		this.carName = carName;
		this.orderDate = orderDate;
		this.sortOption = sortOption;
	}

	public OrderSearchInfo() {
		this.carName = "";
		this.orderDate = null;
		this.sortOption = "asc";
	}

	public OrderSearchInfo(String sortOption) {
		this.carName = "";
		this.orderDate = null;
		this.sortOption = sortOption;
	}

	public String getCarName() {
		return carName;
	}

	public void setCarName(String carName) {
		this.carName = carName;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public String getSortOption() {
		return sortOption;
	}

	public void setSortOption(String sortOption) {
		this.sortOption = sortOption;
	}
}

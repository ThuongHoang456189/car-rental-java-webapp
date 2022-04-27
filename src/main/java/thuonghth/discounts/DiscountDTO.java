package thuonghth.discounts;

import java.math.BigDecimal;

public class DiscountDTO {
	private long discountID;
	private String discountCode;
	private BigDecimal discountPercent;

	public static final String DISCOUNT_CODE_FIELD = "discountCode";

	public DiscountDTO(String discountCode) {
		this.discountID = -1;
		this.discountCode = discountCode;
		this.discountPercent = BigDecimal.ZERO;
	}

	public DiscountDTO(long discountID, BigDecimal discountPercent) {
		this.discountID = discountID;
		this.discountPercent = discountPercent;
	}

	public long getDiscountID() {
		return discountID;
	}

	public void setDiscountID(long discountID) {
		this.discountID = discountID;
	}

	public String getDiscountCode() {
		return discountCode;
	}

	public void setDiscountCode(String discountCode) {
		this.discountCode = discountCode;
	}

	public BigDecimal getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(BigDecimal discountPercent) {
		this.discountPercent = discountPercent;
	}
}

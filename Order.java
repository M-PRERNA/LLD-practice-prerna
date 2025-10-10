package collections;

public class Order implements Comparable<Order>{
	private String orderId;
	private boolean isExpress;
	
	public Order(String orderId, boolean isExpress) {
		super();
		this.orderId = orderId;
		this.isExpress = isExpress;
	}
	
	
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public boolean isExpress() {
		return isExpress;
	}
	public void setExpress(boolean isExpress) {
		this.isExpress = isExpress;
	}


	@Override
	public int compareTo(Order o) {
		 // If both are either express or both are not express, compare by orderId
	    if ((this.isExpress() && o.isExpress()) || (!this.isExpress() && !o.isExpress())) {
	        return this.getOrderId().compareTo(o.getOrderId());
	    }

	    // Otherwise, put express orders before normal ones
	    if (this.isExpress() && !o.isExpress()) {
	        return -1; // this comes before o
	    } else {
	        return 1;  // this comes after o
	    }	}
	
}

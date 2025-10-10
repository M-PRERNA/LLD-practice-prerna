package collections;

import java.util.PriorityQueue;

public class OrderProcessor {
	 PriorityQueue<Order> orders;
	 
	 OrderProcessor(){
		 orders = new PriorityQueue<>();
	 }
	 
	 public void addOrder(Order order) {
		 orders.add(order);
	 }
	 
	 public void processOrder() {
		 
	 }
	 
	 public int getSize() {
		 return orders.size();
	 }

}

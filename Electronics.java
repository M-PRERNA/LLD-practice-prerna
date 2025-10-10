package collections;

public class Electronics extends Item{
	 int warranty;

	    public Electronics(String id, String name, int price, int quantity, int warranty) {
	        super(id, name, price, quantity);
	        this.warranty = warranty;
	    }
}

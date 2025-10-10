package collections;

public class Clothing extends Item{

	int size;
	 

	    public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}


		public Clothing(String id, String name, int price, int quantity, int size) {
	        super(id, name, price, quantity);
	        this.size = size;
	    }
}

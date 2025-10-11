package collections;

import java.util.Comparator;

public class ItemQuantityComparator implements Comparator<Item>{

	@Override
	public int compare(Item o1, Item o2) {
		// TODO Auto-generated method stub
		if(o1.getQuantity() > o2.getQuantity()) {
			return -1;
		}
		else if(o1.getQuantity() < o2.getQuantity()) {
			return 1;
		}
		else return 0;
	}

}

package collections;

import java.util.ArrayList;
import java.util.LinkedList;

public class RecentlyViewItems {
	LinkedList<Item> items;
	int MAX_SIZE = 3;
	
	RecentlyViewItems(){
		items = new LinkedList<>();
	}
	
	public void addRecentlyViewedItem(Item item) {
		items.remove(item);
		items.addFirst(item);
		if(items.size()>3) {
			items.removeLast();
		}
	}
	
	public ArrayList<Item> getRecentlyViewedItems() {
		return  new ArrayList<>(items);
	}
}

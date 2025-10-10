package collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Inventory <T extends Item>{
	HashMap<String, T> store;
	
	Inventory(){
		store = new HashMap<>();
		
	}
	
	public void add(T item) {
		  // 1️⃣ Check quantity
		try {
        if (item.getQuantity() < 0) {
            throw new Exception(
                "Quantity cannot be negative for item ID: " + item.getId()
            );
        }

        // 2️⃣ Check duplicate ID
        if (store.containsKey(item.getId())) {
            throw new Exception(
                "Item with ID " + item.getId() + " already exists"
            );
        }

        // 3️⃣ Add item
        store.put(item.getId(), item);
        System.out.println("Item added successfully: " + item.getName());
		
		}
	
	public void remove(T item) {
		store.remove(item.getId());
	}
	
	public void get(String id) {
		store.get(id);
	}
	public List<T> getAll() {
		return new ArrayList<>(store.values());
	}
}

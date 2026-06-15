package collections;

import java.util.List;

public class Demo {
    public static void main(String[] args) {
        System.out.println("=== Inventory & Order Management Demo ===\n");

        Inventory<Item> inventory = new Inventory<>();

        Book book = new Book("B1", "Effective Java", 45, 10, "Joshua Bloch");
        Electronics laptop = new Electronics("E1", "Laptop", 1200, 5, 24);
        Clothing shirt = new Clothing("C1", "T-Shirt", 20, 0, 42);
        Book dupBook = new Book("B1", "Duplicate", 10, 1, "Nobody");
        Electronics badQty = new Electronics("E2", "Broken", 100, -3, 12);

        System.out.println("-- Adding items --");
        inventory.add(book);
        inventory.add(laptop);
        inventory.add(shirt);
        System.out.println("-- Adding duplicate ID (should fail) --");
        inventory.add(dupBook);
        System.out.println("-- Adding negative quantity (should fail) --");
        inventory.add(badQty);

        System.out.println("\n-- All items in inventory: " + inventory.getAll().size() + " --");

        System.out.println("\n-- Filter by price [0, 100] --");
        inventory.filterByPrice(0, 100);

        System.out.println("\n-- Filter by availability (quantity > 0) --");
        inventory.filterByAvailability();

        System.out.println("\n-- Sort by name --");
        List<Item> byName = inventory.sortItems(new ItemNameComparator());
        for (Item i : byName) System.out.println("  " + i.getName());

        System.out.println("\n-- Sort by quantity (desc) --");
        List<Item> byQty = inventory.sortItems(new ItemQuantityComparator());
        for (Item i : byQty) System.out.println("  " + i.getName() + " (qty=" + i.getQuantity() + ")");

        System.out.println("\n-- Recently viewed (cap 3) --");
        RecentlyViewItems recent = new RecentlyViewItems();
        recent.addRecentlyViewedItem(book);
        recent.addRecentlyViewedItem(laptop);
        recent.addRecentlyViewedItem(shirt);
        recent.addRecentlyViewedItem(book);
        for (Item i : recent.getRecentlyViewedItems()) System.out.println("  " + i.getName());

        System.out.println("\n-- Order processing (express prioritized) --");
        OrderProcessor processor = new OrderProcessor();
        processor.addOrder(new Order("ORD-100", false));
        processor.addOrder(new Order("ORD-101", true));
        processor.addOrder(new Order("ORD-102", false));
        System.out.println("  Queue size: " + processor.getSize());
        System.out.println("  Next order (head): " + processor.orders.peek().getOrderId()
                + " express=" + processor.orders.peek().isExpress());

        System.out.println("\n=== Demo finished successfully ===");
    }
}

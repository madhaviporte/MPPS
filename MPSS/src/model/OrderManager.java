package model;

import java.util.*;

/**
 * Traceability: Class Diagram -> OrderManager class
 * Sequence Diagram -> OrderManager participates in end-of-day order flow
 */
public class OrderManager {
    private Date         orderDate;
    private List<String> orderedItems;

    public OrderManager() {
        this.orderedItems = new ArrayList<>();
        this.orderDate    = new Date();
    }

    /**
     * Traceability: Class Diagram -> OrderManager::checkLowStock()
     * Sequence Diagram -> step 2: checkLowStock()
     * Returns list of parts whose currentStock < thresholdValue
     */
    public List<MotorPart> checkLowStock(Inventory inventory) {
        List<MotorPart> lowStock = new ArrayList<>();
        for (MotorPart p : inventory.getPartsList()) {
            if (p.getState() == MotorPart.PartState.LOW_STOCK
             || p.getState() == MotorPart.PartState.OUT_OF_STOCK) {
                lowStock.add(p);
            }
        }
        return lowStock;
    }

    /**
     * Traceability: Class Diagram -> OrderManager::calcRequiredQty()
     * Required qty = threshold * 2 - currentStock (restock to double threshold)
     */
    public int calcRequiredQty(MotorPart part) {
        int required = (int)(part.getThresholdValue() * 2) - part.getCurrentStock();
        return Math.max(required, 1);
    }

    /**
     * Traceability: Class Diagram -> OrderManager::generateOrderList()
     * Sequence Diagram -> steps 2-7: generates order list with vendor details
     * @return list of order-line strings ready for display
     */
    public List<String> generateOrderList(Inventory inventory, Map<String, Vendor> vendors) {
        orderedItems.clear();
        orderDate = new Date();
        List<MotorPart> lowStockParts = checkLowStock(inventory);
        for (MotorPart part : lowStockParts) {
            Vendor vendor = vendors.get(part.getVendorId());
            int reqQty    = calcRequiredQty(part);
            String line   = String.format(
                "Part: %-20s | Rack: %s | Stock: %3d | Threshold: %4.1f | Order Qty: %3d | Vendor: %s | Contact: %s",
                part.getPartName(), part.getRackNumber(),
                part.getCurrentStock(), part.getThresholdValue(), reqQty,
                vendor != null ? vendor.getVendorName() : "N/A",
                vendor != null ? vendor.getContactNumber() : "N/A");
            orderedItems.add(line);
            part.placeOrder();
        }
        return Collections.unmodifiableList(orderedItems);
    }

    /** Traceability: Class Diagram -> OrderManager::printOrderDetails() */
    public String printOrderDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ORDER LIST — ").append(orderDate).append(" ===\n");
        for (String item : orderedItems) sb.append(item).append("\n");
        return sb.toString();
    }

    public Date         getOrderDate()   { return orderDate; }
    public List<String> getOrderedItems(){ return Collections.unmodifiableList(orderedItems); }
}

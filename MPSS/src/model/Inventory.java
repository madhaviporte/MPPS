package model;

import java.util.*;

/**
 * Traceability: Class Diagram -> Inventory
 * Automatically computes thresholds from 4-week sales history.
 */
public class Inventory {
    private List<MotorPart> partsList;
    private Date lastUpdated;

    public Inventory() {
        partsList   = new ArrayList<>();
        lastUpdated = new Date();
    }

    public void updateStock(String partNumber, int newQty) {
        MotorPart part = getPartByNumber(partNumber);
        if (part != null) { part.setCurrentStock(newQty); lastUpdated = new Date(); }
    }

    public MotorPart getPartByNumber(String partNumber) {
        for (MotorPart p : partsList)
            if (p.getPartNumber().equals(partNumber)) return p;
        return null;
    }

    /**
     * Traceability: Inventory::getAvgWeeklySales()
     *
     * Algorithm:
     *   1. Find the most recent sale date for this part.
     *   2. Look back exactly 4 weeks (28 days) from that date.
     *   3. Sum all units sold in that window.
     *   4. Divide by 4 to get the average units sold per week.
     *
     * If fewer than 4 weeks of history exist, we divide by the actual
     * number of weeks covered (minimum 1) so new parts still get a
     * meaningful threshold.
     */
    public float getAvgWeeklySales(String partNumber, List<SaleTransaction> transactions) {
        // Collect all transactions for this part
        List<SaleTransaction> partTx = new ArrayList<>();
        for (SaleTransaction t : transactions)
            if (t.getPartNumber().equals(partNumber)) partTx.add(t);

        if (partTx.isEmpty()) return 0f;

        // Find the most recent sale date
        long latestMs = Long.MIN_VALUE;
        for (SaleTransaction t : partTx)
            if (t.getSaleDate().getTime() > latestMs) latestMs = t.getSaleDate().getTime();

        // 4-week window ending at the most recent sale
        long windowMs = 28L * 24 * 60 * 60 * 1000;   // 28 days in milliseconds
        long windowStart = latestMs - windowMs;

        int totalUnits = 0;
        long earliestInWindow = latestMs;
        for (SaleTransaction t : partTx) {
            long txMs = t.getSaleDate().getTime();
            if (txMs >= windowStart) {
                totalUnits += t.getQuantitySold();
                if (txMs < earliestInWindow) earliestInWindow = txMs;
            }
        }

        // How many weeks does the window actually cover?
        long coveredMs    = latestMs - earliestInWindow;
        float weeksActual = Math.max(1f, coveredMs / (7f * 24 * 60 * 60 * 1000));
        float weeks       = Math.min(4f, weeksActual);   // cap at 4

        return totalUnits / weeks;
    }

    /**
     * Recalculate thresholds for ALL parts using current transaction history.
     * Call this after every sale is recorded so thresholds stay up to date.
     */
    public void recalcAllThresholds(List<SaleTransaction> transactions) {
        for (MotorPart p : partsList) {
            float avg = getAvgWeeklySales(p.getPartNumber(), transactions);
            p.calcThreshold(avg);   // threshold = avg * 1.5
        }
    }

    /** Monthly sales totals for graph (Class Diagram -> Inventory::generateSalesGraph) */
    public Map<String, Integer> generateSalesGraph(List<SaleTransaction> transactions) {
    Map<String, Integer> monthly = new LinkedHashMap<>();

    // Last 12 months
    for (int i = 11; i >= 0; i--) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -i);

        String key = new java.text.SimpleDateFormat("MMM yyyy")
                .format(cal.getTime());

        monthly.put(key, 0);
    }

    // Fill data
    for (SaleTransaction t : transactions) {
        String key = new java.text.SimpleDateFormat("MMM yyyy")
                .format(t.getSaleDate());

        if (monthly.containsKey(key)) {
            monthly.put(key, monthly.get(key) + t.getQuantitySold());
        }
    }

    return monthly;
}
    public void addPart(MotorPart part) { partsList.add(part); lastUpdated = new Date(); }
    public boolean removePart(String pn){ return partsList.removeIf(p -> p.getPartNumber().equals(pn)); }
    public List<MotorPart> getPartsList(){ return Collections.unmodifiableList(partsList); }
    public Date getLastUpdated()         { return lastUpdated; }
}

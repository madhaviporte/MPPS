package model;

import java.util.*;

/**
 * Traceability: Class Diagram -> ReportGenerator class
 */
public class ReportGenerator {
    private Date   reportDate;
    private String reportType;

    public ReportGenerator() {
        this.reportDate = new Date();
        this.reportType = "DAILY";
    }

    /**
     * Traceability: Class Diagram -> ReportGenerator::generateDailyReport()
     * Sequence Diagram -> step 8: calcDailyRevenue()
     */
    public String generateDailyReport(List<SaleTransaction> transactions, List<String> orderLines) {
        reportDate = new Date();
        reportType = "DAILY";
        double totalRevenue = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("====== DAILY REPORT ======\n");
        sb.append("Date: ").append(reportDate).append("\n\n");
        sb.append("--- Sales Transactions ---\n");
        for (SaleTransaction t : transactions) {
            sb.append(String.format("  TxID: %-10s  Part: %-15s  Qty: %3d  Price: %8.2f  Revenue: %8.2f%n",
                    t.getTransactionId(), t.getPartNumber(),
                    t.getQuantitySold(), t.getSalePrice(), t.calcRevenue()));
            totalRevenue += t.calcRevenue();
        }
        sb.append(String.format("%nTotal Daily Revenue: Rs. %.2f%n", totalRevenue));
        sb.append("\n--- End-of-Day Order List ---\n");
        if (orderLines.isEmpty()) {
            sb.append("  No orders required today.\n");
        } else {
            for (String line : orderLines) sb.append("  ").append(line).append("\n");
        }
        return sb.toString();
    }

    /**
     * Traceability: Class Diagram -> ReportGenerator::generateMonthGraph()
     * Returns monthly data for rendering in the Swing chart panel.
     */
    public Map<String, Integer> generateMonthGraph(Inventory inventory, List<SaleTransaction> transactions) {
        reportType = "MONTHLY";
        return inventory.generateSalesGraph(transactions);
    }

    /** Traceability: Class Diagram -> ReportGenerator::printOrderReport() */
    public String printOrderReport(List<String> orderLines) {
        StringBuilder sb = new StringBuilder();
        sb.append("====== ORDER REPORT ======\n");
        sb.append("Date: ").append(reportDate).append("\n\n");
        if (orderLines.isEmpty()) {
            sb.append("No items to order.\n");
        } else {
            orderLines.forEach(l -> sb.append(l).append("\n"));
        }
        return sb.toString();
    }

    public Date   getReportDate() { return reportDate; }
    public String getReportType() { return reportType; }
}

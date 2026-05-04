package model;

import java.util.Date;

/**
 * Traceability: Class Diagram -> SaleTransaction class
 */
public class SaleTransaction {
    private String transactionId;
    private Date   saleDate;
    private int    quantitySold;
    private double salePrice;
    private String partNumber;

    public SaleTransaction(String transactionId, Date saleDate, int quantitySold, double salePrice, String partNumber) {
        this.transactionId = transactionId;
        this.saleDate      = saleDate;
        this.quantitySold  = quantitySold;
        this.salePrice     = salePrice;
        this.partNumber    = partNumber;
    }

    /** Traceability: Class Diagram -> SaleTransaction::recordSale() */
    public void recordSale() {
        System.out.printf("Sale recorded: TxID=%s, Part=%s, Qty=%d, Price=%.2f%n",
                transactionId, partNumber, quantitySold, salePrice);
    }

    /** Traceability: Class Diagram -> SaleTransaction::calcRevenue() */
    public double calcRevenue() {
        return quantitySold * salePrice;
    }

    /** Traceability: Class Diagram -> SaleTransaction::getDailyRevenue() */
    public double getDailyRevenue() {
        return calcRevenue();
    }

    public String getTransactionId() { return transactionId; }
    public Date   getSaleDate()      { return saleDate; }
    public int    getQuantitySold()  { return quantitySold; }
    public double getSalePrice()     { return salePrice; }
    public String getPartNumber()    { return partNumber; }
}

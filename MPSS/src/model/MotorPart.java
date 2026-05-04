package model;

/**
 * Traceability: Class Diagram -> MotorPart
 * State Chart  -> PartState lifecycle
 */
public class MotorPart {
    private String partNumber;
    private String partName;
    private int    currentStock;
    private float  thresholdValue;   // AUTO-CALCULATED, never set by user
    private String rackNumber;
    private String vendorId;

    public enum PartState { IN_STOCK, LOW_STOCK, ORDER_PENDING, OUT_OF_STOCK, RESTOCKED, DISCONTINUED }
    private PartState state;

    public MotorPart(String partNumber, String partName, int currentStock, String rackNumber, String vendorId) {
        this.partNumber     = partNumber;
        this.partName       = partName;
        this.currentStock   = currentStock;
        this.thresholdValue = 0;   // will be computed once transactions exist
        this.rackNumber     = rackNumber;
        this.vendorId       = vendorId;
        updateState();
    }

    public String getPartDetails() {
        return String.format("Part#: %s | Name: %s | Stock: %d | Threshold: %.1f | Rack: %s",
                partNumber, partName, currentStock, thresholdValue, rackNumber);
    }

    /**
     * Traceability: Class Diagram -> MotorPart::calcThreshold()
     * Threshold = average weekly sales * 1.5  (1.5x safety factor for JIT)
     * Called automatically by Inventory; never called by the user directly.
     */
    public float calcThreshold(float avgWeeklySales) {
        this.thresholdValue = avgWeeklySales * 1.5f;
        updateState();
        return this.thresholdValue;
    }

    public void updateState() {
        if (state == PartState.DISCONTINUED) return;
        if (currentStock == 0)                    state = PartState.OUT_OF_STOCK;
        else if (thresholdValue > 0 && currentStock < thresholdValue) state = PartState.LOW_STOCK;
        else                                      state = PartState.IN_STOCK;
    }

    public void placeOrder()          { if (state==PartState.LOW_STOCK||state==PartState.OUT_OF_STOCK) state=PartState.ORDER_PENDING; }
    public void receiveStock(int qty) { currentStock += qty; state=PartState.RESTOCKED; updateState(); }
    public void discontinue()         { state = PartState.DISCONTINUED; }

    public String    getPartNumber()  { return partNumber; }
    public String    getPartName()    { return partName; }
    public int       getCurrentStock(){ return currentStock; }
    public void      setCurrentStock(int s){ this.currentStock=s; updateState(); }
    public float     getThresholdValue(){ return thresholdValue; }
    public String    getRackNumber()  { return rackNumber; }
    public void      setRackNumber(String r){ this.rackNumber=r; }
    public String    getVendorId()    { return vendorId; }
    public void      setVendorId(String v){ this.vendorId=v; }
    public PartState getState()       { return state; }
    public void      setState(PartState s){ this.state=s; }
}

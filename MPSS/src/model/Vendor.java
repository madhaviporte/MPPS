package model;

/**
 * Traceability: Class Diagram -> Vendor class
 */
public class Vendor {
    private String vendorId;
    private String vendorName;
    private String address;
    private String contactNumber;

    public Vendor(String vendorId, String vendorName, String address, String contactNumber) {
        this.vendorId      = vendorId;
        this.vendorName    = vendorName;
        this.address       = address;
        this.contactNumber = contactNumber;
    }

    /** Traceability: Class Diagram -> Vendor::getAddress() */
    public String getAddress() { return address; }

    /** Traceability: Class Diagram -> Vendor::getVendorInfo() */
    public String getVendorInfo() {
        return String.format("ID: %s | Name: %s | Contact: %s | Address: %s",
                vendorId, vendorName, contactNumber, address);
    }

    public String getVendorId()       { return vendorId; }
    public String getVendorName()     { return vendorName; }
    public String getContactNumber()  { return contactNumber; }
    public void setVendorName(String v){ this.vendorName = v; }
    public void setAddress(String a)  { this.address = a; }
    public void setContactNumber(String c){ this.contactNumber = c; }
}

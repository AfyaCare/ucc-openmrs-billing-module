package org.openmrs.module.billingservices.model.sale.quote.raw;

public class SaleQuoteLineRaw {
    private int quoteLineId;
    private int saleQuote;
    private int item;
    private int serviceType;
    private int itemPrice;
    private int quantity;
    private String unit;
    private double quotedAmount;
    private double payableAmount;
    private int status;
    private String dateCreated;
    private Integer changedBy;
    private String dateChanged;
    private String uuid;


    public int getQuoteLineId() {
        return quoteLineId;
    }

    public void setQuoteLineId(int quoteLineId) {
        this.quoteLineId = quoteLineId;
    }

    public int getSaleQuote() {
        return saleQuote;
    }

    public void setSaleQuote(int saleQuote) {
        this.saleQuote = saleQuote;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getQuotedAmount() {
        return quotedAmount;
    }

    public void setQuotedAmount(double quotedAmount) {
        this.quotedAmount = quotedAmount;
    }

    public double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(Integer changedBy) {
        this.changedBy = changedBy;
    }

    public String getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(String dateChanged) {
        this.dateChanged = dateChanged;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

package org.openmrs.module.billingservices.model.sale.order.raw;

public class OrderByQuoteLineRaw {
    private int soqlNo;
    private int saleOrderQuote;
    private int quoteLine;
    private double paidAmount;
    private double debtAmount;
    private String dateCreated;
    private String uuid;

    public int getSoqlNo() {
        return soqlNo;
    }

    public void setSoqlNo(int soqlNo) {
        this.soqlNo = soqlNo;
    }

    public int getSaleOrderQuote() {
        return saleOrderQuote;
    }

    public void setSaleOrderQuote(int saleOrderQuote) {
        this.saleOrderQuote = saleOrderQuote;
    }

    public int getQuoteLine() {
        return quoteLine;
    }

    public void setQuoteLine(int quoteLine) {
        this.quoteLine = quoteLine;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getDebtAmount() {
        return debtAmount;
    }

    public void setDebtAmount(double debtAmount) {
        this.debtAmount = debtAmount;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

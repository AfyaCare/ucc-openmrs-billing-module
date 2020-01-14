package org.openmrs.module.billingservices.model.sale.payment.raw;

public class OrderPayInstallmentRaw {
    private int entryNo;
    private int installmentNo;
    private int orderByQuoteLine;
    private double paidAmount;
    private String receiptNo;
    private int receivedBy;
    private String dateCreated;
    private String uuid;

    public int getEntryNo() {
        return entryNo;
    }

    public void setEntryNo(int entryNo) {
        this.entryNo = entryNo;
    }

    public int getInstallmentNo() {
        return installmentNo;
    }

    public void setInstallmentNo(int installmentNo) {
        this.installmentNo = installmentNo;
    }

    public int getOrderByQuoteLine() {
        return orderByQuoteLine;
    }

    public void setOrderByQuoteLine(int orderByQuoteLine) {
        this.orderByQuoteLine = orderByQuoteLine;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public int getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(int receivedBy) {
        this.receivedBy = receivedBy;
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

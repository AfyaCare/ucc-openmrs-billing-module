package org.openmrs.module.billingservices.model.sale.payment;

import org.openmrs.module.billingservices.model.misc.User;
import org.openmrs.module.billingservices.model.sale.order.OrderByQuoteLine;

/**
 @Name   : OrderPayInstallment
 @Author : Eric Mwailunga
 October,2019
*/
public class OrderPayInstallment {
    private int entryNo;
    private int installmentNo;
    private OrderByQuoteLine orderByQuoteLine;
    private double paidAmount;
    private String receiptNo;
    private User receivedBy;
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

    public OrderByQuoteLine getOrderByQuoteLine() {
        return orderByQuoteLine;
    }

    public void setOrderByQuoteLine(OrderByQuoteLine orderByQuoteLine) {
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

    public User getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(User receivedBy) {
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

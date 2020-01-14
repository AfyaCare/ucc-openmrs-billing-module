package org.openmrs.module.billingservices.model.sale.order;

import org.openmrs.module.billingservices.model.misc.User;
import org.openmrs.module.billingservices.model.sale.quote.SaleQuoteLine;

/**
  @Name   : OrderByQuoteLine
  @Author : Eric Mwailunga
  October,2019
*/
public class OrderByQuoteLine {
    private int orderByQuoteLineId;
    private OrderByQuote orderByQuote;
    private SaleQuoteLine saleQuoteLine;
    private double paidAmount;
    private double debtAmount;
    private User receivedBy;
    private String dateCreated;
    private String uuid;

    public int getOrderByQuoteLineId() {
        return orderByQuoteLineId;
    }

    public void setOrderByQuoteLineId(int orderByQuoteLineId) {
        this.orderByQuoteLineId = orderByQuoteLineId;
    }

    public OrderByQuote getOrderByQuote() {
        return orderByQuote;
    }

    public void setOrderByQuote(OrderByQuote orderByQuote) {
        this.orderByQuote = orderByQuote;
    }

    public SaleQuoteLine getSaleQuoteLine() {
        return saleQuoteLine;
    }

    public void setSaleQuoteLine(SaleQuoteLine saleQuoteLine) {
        this.saleQuoteLine = saleQuoteLine;
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

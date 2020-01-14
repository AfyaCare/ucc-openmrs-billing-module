package org.openmrs.module.billingservices.model.sale.order;

import org.openmrs.module.billingservices.model.misc.User;
import org.openmrs.module.billingservices.model.sale.quote.SaleQuote;

import java.util.List;

/**
  @Name   : OrderByQuote
  @Author : Eric Mwailunga
  October,2019
*/
public class OrderByQuote {
    private int orderByQuoteId;
    private String datedSaleId;
    private SaleQuote saleQuote;
    private String paymentMethod;
    private double payableAmount;
    private double paidAmount;
    private double debtAmount;
    private int installmentFrequency;
    private User creator;
    private String dateCreated;
    private String uuid;
    private List<OrderByQuoteLine> orderByQuoteLines;

    public int getOrderByQuoteId() {
        return orderByQuoteId;
    }

    public void setOrderByQuoteId(int orderByQuoteId) {
        this.orderByQuoteId = orderByQuoteId;
    }

    public String getDatedSaleId() {
        return datedSaleId;
    }

    public void setDatedSaleId(String datedSaleId) {
        this.datedSaleId = datedSaleId;
    }

    public SaleQuote getSaleQuote() {
        return saleQuote;
    }

    public void setSaleQuote(SaleQuote saleQuote) {
        this.saleQuote = saleQuote;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(double payableAmount) {
        this.payableAmount = payableAmount;
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

    public int getInstallmentFrequency() {
        return installmentFrequency;
    }

    public void setInstallmentFrequency(int installmentFrequency) {
        this.installmentFrequency = installmentFrequency;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
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

    public List<OrderByQuoteLine> getOrderByQuoteLines() {
        return orderByQuoteLines;
    }

    public void setOrderByQuoteLines(List<OrderByQuoteLine> orderByQuoteLines) {
        this.orderByQuoteLines = orderByQuoteLines;
    }
}

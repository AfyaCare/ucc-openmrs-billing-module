package org.openmrs.module.billingservices.model.sale.quote;

import org.openmrs.module.billingservices.model.misc.Patient;
import org.openmrs.module.billingservices.model.misc.User;
import org.openmrs.module.billingservices.model.misc.Visit;
import org.openmrs.module.billingservices.model.sale.payment.PaymentCategory;

import java.util.List;

/**
  @Name   : SaleQuote
  @Author : Eric Mwailunga
  October,2019
*/
public class SaleQuote {
    private int quoteId;
    private Patient patient;
    private Visit visit;
    private PaymentCategory paymentCategory;
    private PaymentCategory paymentSubCategory;
    private User orderedBy;
    private double totalQuotedAmount;
    private double totalDiscountedAmount;
    private double totalPayableAmount;
    private QuoteStatus quoteStatus;
    private int discounted;
    private String dateOrdered;
    private User changedBy;
    private String dateChanged;
    private String uuid;
    private List<SaleQuoteLine> saleQuoteLineList;


    public int getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(int quoteId) {
        this.quoteId = quoteId;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Visit getVisit() {
        return visit;
    }

    public void setVisit(Visit visit) {
        this.visit = visit;
    }

    public PaymentCategory getPaymentCategory() {
        return paymentCategory;
    }

    public void setPaymentCategory(PaymentCategory paymentCategory) {
        this.paymentCategory = paymentCategory;
    }

    public PaymentCategory getPaymentSubCategory() {
        return paymentSubCategory;
    }

    public void setPaymentSubCategory(PaymentCategory paymentSubCategory) {
        this.paymentSubCategory = paymentSubCategory;
    }

    public User getOrderedBy() {
        return orderedBy;
    }

    public void setOrderedBy(User orderedBy) {
        this.orderedBy = orderedBy;
    }

    public double getTotalQuotedAmount() {
        return totalQuotedAmount;
    }

    public void setTotalQuotedAmount(double totalQuotedAmount) {
        this.totalQuotedAmount = totalQuotedAmount;
    }

    public double getTotalDiscountedAmount() {
        return totalDiscountedAmount;
    }

    public void setTotalDiscountedAmount(double totalDiscountedAmount) {
        this.totalDiscountedAmount = totalDiscountedAmount;
    }

    public double getTotalPayableAmount() {
        return totalPayableAmount;
    }

    public void setTotalPayableAmount(double totalPayableAmount) {
        this.totalPayableAmount = totalPayableAmount;
    }

    public QuoteStatus getQuoteStatus() {
        return quoteStatus;
    }

    public void setQuoteStatus(QuoteStatus quoteStatus) {
        this.quoteStatus = quoteStatus;
    }

    public int getDiscounted() {
        return discounted;
    }

    public void setDiscounted(int discounted) {
        this.discounted = discounted;
    }

    public String getDateOrdered() {
        return dateOrdered;
    }

    public void setDateOrdered(String dateOrdered) {
        this.dateOrdered = dateOrdered;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
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

    public List<SaleQuoteLine> getSaleQuoteLineList() {
        return saleQuoteLineList;
    }

    public void setSaleQuoteLineList(List<SaleQuoteLine> saleQuoteLineList) {
        this.saleQuoteLineList = saleQuoteLineList;
    }
}
